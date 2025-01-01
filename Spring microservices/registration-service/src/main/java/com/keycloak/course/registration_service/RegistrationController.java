package com.keycloak.course.registration_service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import static com.keycloak.course.registration_service.CookieService.*;

@RestController
public class RegistrationController {

	@Autowired
	PkceRequestService pkceService;

	@Autowired
	CsrfTokenService csrfTokenService;

	@GetMapping("/register")
	public void Register(HttpServletRequest request, HttpServletResponse response) throws IOException {

		var pkceRequest = pkceService.getPkceRequest();

		String registrationUrl = "https://localhost:8443/realms/ApiRealm/protocol/openid-connect/registrations?client_id=api-frontend&response_type=code&scope=openid&redirect_uri=https://localhost:8400/register/postRegistration&code_challenge="
				+ pkceRequest.getCodeChallenge() + "&code_challenge_method=" + pkceRequest.getCodeChallengeMethod();

		HttpSession session = request.getSession();
		session.setAttribute("code_verifier", pkceRequest.getCodeVerifier());

		response.sendRedirect(registrationUrl);
	}

	@GetMapping("/register/postRegistration")
	public void postRegistration(@RequestParam("code") String code, HttpServletRequest request,
			HttpServletResponse response) {

		System.out.println("AUTHORIZATION CODE: " + code);
		String codeVerifier = request.getSession().getAttribute("code_verifier").toString();

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

		formData.add("grant_type", "authorization_code");
		formData.add("client_id", "api-frontend");
		formData.add("redirect_uri", "https://localhost:8400/register/postRegistration");
		formData.add("scope", "read openid");
		formData.add("code_verifier", codeVerifier);
		formData.add("code", code);

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(formData, headers);

		String tokenEndpoint = "https://localhost:8443/realms/ApiRealm/protocol/openid-connect/token";
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenEndpoint, tokenRequest, String.class);

		// Parse the JSON response to extract tokens
		String responseBody = responseEntity.getBody();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			JsonNode jsonNode = objectMapper.readTree(responseBody);
			String accessToken = jsonNode.get("access_token").asText();
			String idToken = jsonNode.get("id_token").asText();
			String refreshToken = jsonNode.get("refresh_token").asText();
			String csrfToken = csrfTokenService.generateCsrfToken(accessToken);

			System.out.println("accessToken : " + accessToken);
			System.out.println("refreshToken: " + refreshToken);
			System.out.println("idToken: " + idToken);

			Cookie access_token_cookie = createCookie("access_token", accessToken, true, 300);
			Cookie id_token_cookie = createCookie("id_token", idToken, false, 300);
			Cookie refresh_token_cookie = createCookie("refresh_token", refreshToken, false, 20);
			Cookie csrf_token_cookie = createCookie("csrf_token", csrfToken, false, 20);

			response.addCookie(access_token_cookie);
			response.addCookie(id_token_cookie);
			response.addCookie(refresh_token_cookie);
			response.addCookie(csrf_token_cookie);

			response.sendRedirect("https://localhost:3000/post-sign-in");

		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
