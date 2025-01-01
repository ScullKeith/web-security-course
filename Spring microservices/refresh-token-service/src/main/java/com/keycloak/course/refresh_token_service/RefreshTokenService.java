package com.keycloak.course.refresh_token_service;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RefreshTokenService {

	private RestTemplate restTemplate = new RestTemplate();

	public Map<String, Object> sendRefreshTokenRequest(String refreshToken) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "refresh_token");
		formData.add("refresh_token", refreshToken);
		formData.add("client_id", "api-frontend");

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

		ResponseEntity<Map<String, Object>> responseEntity;

		try {
			responseEntity = restTemplate.exchange(
					"https://localhost:8443/realms/ApiRealm/protocol/openid-connect/token", HttpMethod.POST,
					requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {
					});

		} catch (Exception e) {
			throw e;
		}

		Map<String, Object> response = responseEntity.getBody();

		return response;
	}
}
