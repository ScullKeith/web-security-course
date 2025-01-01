package com.keycloak.course.refresh_token_service;

import static com.keycloak.course.refresh_token_service.CookieService.createCookie;
import static com.keycloak.course.refresh_token_service.CookieService.getCookieValue;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/refresh")
@RestController
public class RefreshTokenController {

	@Autowired
	private RefreshTokenService refreshService;

	@Autowired
	CsrfTokenService csrfService;

	@PutMapping
	public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("received request " + request.getCookies());
		try {

			String refreshToken = getCookieValue(request.getCookies(), "refresh_token");

			System.out.println("refresh token found? " + refreshToken);

			if (refreshToken == null || refreshToken.isEmpty())
				return ResponseEntity.status(401).body(null);

			Map<String, Object> tokenResponse = refreshService.sendRefreshTokenRequest(refreshToken);

			// Extract new access token, id token
			String accessToken = (String) tokenResponse.get("access_token");
			String idToken = (String) tokenResponse.get("id_token");

			// create csrf token, associated with new access token
			String csrfToken = csrfService.generateCsrfToken(accessToken);

			// add new access token to http only cookie
			Cookie accessTokenCookie = createCookie("access_token", accessToken, true, 300);

			response.addCookie(accessTokenCookie);

			// add new id token && csrf token to http response body
			// Return the new CSRF token and ID token in the response body
			Map<String, String> responseBody = new HashMap<>();
			responseBody.put("csrf_token", csrfToken);
			responseBody.put("id_token", idToken);

			return ResponseEntity.ok(responseBody);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
