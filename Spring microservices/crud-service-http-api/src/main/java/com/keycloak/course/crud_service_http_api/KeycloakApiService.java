package com.keycloak.course.crud_service_http_api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.account.UserRepresentation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KeycloakApiService {

	private AuthorizedClientServiceOAuth2AuthorizedClientManager cliMgr;
	private RestTemplate restTemplate;

	public KeycloakApiService(AuthorizedClientServiceOAuth2AuthorizedClientManager cliMgr) {
		this.cliMgr = cliMgr;

		this.restTemplate = new RestTemplate();
	}

	// authorizedClient.getAccessToken() will return the existing token if it's
	// still valid. If the token is expired, the OAuth2AuthorizedClientManager will
	// manage the process of obtaining a new token, and
	// authorizedClient.getAccessToken() will return the new token.
	private String getToken() {
		OAuth2AuthorizeRequest authorizedRequest = OAuth2AuthorizeRequest.withClientRegistrationId("api-backend")
				.principal("api-backend").build();

		OAuth2AuthorizedClient authorizedClient = this.cliMgr.authorize(authorizedRequest);

		OAuth2AccessToken token = authorizedClient.getAccessToken();
		System.out.println("token VLAUE: " + token.getTokenValue());
		return token.getTokenValue();
	}

	private HttpHeaders getHeaders() {

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		return headers;
	}

	public UserDTO getUserByUsername(String username) {

		String url = "http://localhost:8080/admin/realms/ApiRealm/users?search=" + username;

		HttpHeaders headers = getHeaders();

		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<List<UserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<List<UserDTO>>() {
				});

		List<UserDTO> usersResponse = responseEntity.getBody();

		if (usersResponse == null || usersResponse.size() > 1 || usersResponse.size() <= 0)
			throw new IllegalArgumentException(
					"Expected exactly one user, but got: " + (usersResponse == null ? 0 : usersResponse.size()));

		UserDTO userInDB = responseEntity.getBody().get(0);

		System.out.println("GOT USER! " + userInDB.getEmail() + " " + userInDB.getUsername() + " "
				+ userInDB.getFirstName() + " " + userInDB.getLastName() + " ");

		HttpStatusCode statusCode = responseEntity.getStatusCode();

		if (statusCode != HttpStatus.OK)
			System.out.println("BAD STATUS CODE! " + statusCode);
		return userInDB;
	}

	public List<UserDTO> getUsers(int min, int max, int first) {

		HttpHeaders headers = getHeaders();
		String url = "http://localhost:8080/admin/realms/ApiRealm/users?first=" + first + "&min=" + min + "&max=" + max;

		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<List<UserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<List<UserDTO>>() {
				});

		List<UserDTO> userResponse = responseEntity.getBody();

		if (userResponse == null || userResponse.size() <= 0)
			throw new IllegalArgumentException("No users retrieved");

		return userResponse;
	}

	private UserRepresentation getUserRepresentationByUsername(String username) {

		String url = "http://localhost:8080/admin/realms/ApiRealm/users?search=" + username;

		HttpHeaders headers = getHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<List<UserRepresentation>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
				requestEntity, new ParameterizedTypeReference<List<UserRepresentation>>() {
				});

		List<UserRepresentation> userResponse = responseEntity.getBody();

		if (userResponse == null || userResponse.size() <= 0)
			throw new IllegalArgumentException("No users retrieved");

		return userResponse.get(0);
	}

	public void updateUser(UserUpdateDTO userUpdateDTO, String userId, String username) {

		try {
			UserRepresentation userInDB = getUserRepresentationByUsername(username);

			ObjectMapper objectMapper = new ObjectMapper();

			// Configure ObjectMapper to ignore null values
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			// Prevent failures for unknown properties
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			// Update userInDB with non-null values from userUpdateDTO
			objectMapper.updateValue(userInDB, userUpdateDTO);

			Map<String, List<String>> attributesUpdate = new HashMap<>();
			attributesUpdate.putAll(userInDB.getAttributes());

			for (Map.Entry<String, List<String>> entry : userUpdateDTO.getAttributes().entrySet()) {
				if (entry.getValue() != null) {
					attributesUpdate.put(entry.getKey(), entry.getValue());
				}
			}

			attributesUpdate.forEach((k, v) -> {

				System.out.println(k + ": " + v.get(0));
			});

			userInDB.setAttributes(attributesUpdate);

			HttpHeaders header = getHeaders();

			String url = "https://localhost:8443/admin/realms/ApiRealm/users/" + userId;

			HttpEntity<UserRepresentation> requestEntity = new HttpEntity<UserRepresentation>(userInDB, header);

			ResponseEntity<Object> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,
					Object.class);

			if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT)
				throw new IllegalArgumentException("Unable to update user!");

		} catch (JsonMappingException e) {
			e.printStackTrace();
		}
	}

	public void deleteUser(String userId) {

		HttpHeaders headers = getHeaders();

		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		String url = "http://localhost:8080/admin/realms/ApiRealm/users/" + userId;
		
		restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Object.class);

	}

}
