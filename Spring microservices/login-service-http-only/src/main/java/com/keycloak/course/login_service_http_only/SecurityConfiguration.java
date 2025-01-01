package com.keycloak.course.login_service_http_only;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity(debug = true)
@Configuration
public class SecurityConfiguration {

	@Autowired
	ClientRegistrationRepository clientRegistrationRepository;

	/*
	 * With this SecurityFilterChain, HTTP requests can only be authenticated via
	 * authorization_code/PKCE enhanced USER LOGIN, token in headers not supported
	 */

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated()).oauth2Login(Customizer.withDefaults())

				.oauth2Login(oauth2 -> oauth2
						.authorizationEndpoint(config -> config
								.authorizationRequestResolver(pkceResolver(clientRegistrationRepository))
								.baseUri("/login/oauth2"))

						.defaultSuccessUrl("/success", true))
//				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
//				}))
				.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
				// this endpoint is unprotected and doesn't require authentication. So, we've
				// implemented our own custom logoout service entirely (separtate app)

				// ensure only "/login" accepted for login endpoint:
				// The authenticationEntryPoint executes when Spring Security detects an
				// unauthenticated user trying to access a protected resource.
				// This means 404 will be returned even when we hit an endpoint which DOES
				// exist, but user there is no Spring Security Principal.

				// Keycloak realm disabled cookies, so new login required when users
				// reach login endpoint
				.exceptionHandling(handler -> handler.authenticationEntryPoint((request, response, authException) -> {

					if (request.getRequestURI().equals("/login"))
						response.sendRedirect("/oauth2/authorization/api-frontend-login"); // this is endpoint which
																							// initiates oauth2 login
					else if (request.getRequestURI().equals("/register"))
						// redirect to our registration-service
						response.sendRedirect("https:8400/register");
//								"https://localhost:8443/realms/ApiRealm/protocol/openid-connect/registrations?client_id=api-frontend&response_type=code&scope=openid&redirect_uri=http://localhost:8443/postlogin&code_challenge=amnAFuXk5QOOFX1InQmHfOyj3Cl-_rVaztIsoYLqnt0&code_challenge_method=S256");
					else
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}));

		return http.build();
	}

	private OAuth2AuthorizationRequestResolver pkceResolver(ClientRegistrationRepository repo) {

		var resolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
		resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
		return resolver;

	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("https://localhost:3000")); // Allow frontend on localhost:3000
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Apply CORS settings to all endpoints
		return source;
	}

}
