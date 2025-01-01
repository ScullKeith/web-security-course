package com.keycloak.course.friends_service;

import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
public class AdminTokenService {

	private AuthorizedClientServiceOAuth2AuthorizedClientManager cliMgr;

	public AdminTokenService(AuthorizedClientServiceOAuth2AuthorizedClientManager cliMgr) {
		this.cliMgr = cliMgr;
	}

	public String getToken() {

		OAuth2AuthorizeRequest authorizedRequest = OAuth2AuthorizeRequest.withClientRegistrationId("api-backend")
				.principal("api-backend").build();

		OAuth2AuthorizedClient authorizedClient = this.cliMgr.authorize(authorizedRequest);
		
		OAuth2AccessToken token = authorizedClient.getAccessToken();

		System.out.println("Admin token value " + token);
		
		return token.getTokenValue();
	}
}
