package com.keycloak.course.logout_service_http_only;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LogoutService {

	private AuthorizedClientServiceOAuth2AuthorizedClientManager cliMgr;
	private RestTemplate restTemplate;

	public LogoutService(AuthorizedClientServiceOAuth2AuthorizedClientManager cliMgr) {
		this.cliMgr = cliMgr;
		this.restTemplate = new RestTemplate();
	}

	private String getToken() {

		OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("api-backend")
				.principal("api-backend").build();

		OAuth2AuthorizedClient authorizedClient = this.cliMgr.authorize(authorizeRequest);

		OAuth2AccessToken token = authorizedClient.getAccessToken();

		return token.getTokenValue();
	}

	private HttpHeaders getHeaders() {

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		return headers;
	}

	public void logoutKeycloakAdmin(String userId) {

		String adminLogoutUrl = "https://localhost:8443/admin/realms/ApiRealm/users/" + userId + "/logout";
		HttpHeaders headers = getHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<Object> responseEntity = restTemplate.exchange(adminLogoutUrl, HttpMethod.POST, requestEntity,
				Object.class);

		System.out.println("logoutKeyxcloakAdmin response " + responseEntity.getStatusCode());
	}

}
