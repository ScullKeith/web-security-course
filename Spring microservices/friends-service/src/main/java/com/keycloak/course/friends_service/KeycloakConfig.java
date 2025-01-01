package com.keycloak.course.friends_service;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

	@Bean
	public Keycloak keycloak() {
		
		return KeycloakBuilder.builder().
				serverUrl("https://localhost:8443").
				realm("ApiRealm")
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS).
				clientId("api-backend")
				.clientSecret("bsn2pqZhZwR2uZTxlhq1nZhy6HvA1t6c")
				.build();
	}
}
