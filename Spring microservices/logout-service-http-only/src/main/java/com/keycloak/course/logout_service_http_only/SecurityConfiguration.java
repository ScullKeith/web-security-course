package com.keycloak.course.logout_service_http_only;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity(debug = true)
@Configuration
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.addFilterBefore(
						new HttpOnlyCookieTokenAuthenticationFilter(
								new OrRequestMatcher(new AntPathRequestMatcher("/logout/**")), authenticationManager()),
						AuthorizationFilter.class)
				.addFilterBefore(new CsrfCookieFilter(), AuthorizationFilter.class);

		http.csrf(csrf -> csrf.disable());

		http.sessionManagement((
				SessionManagementConfigurer<HttpSecurity> httpSecuritySessionManagementConfigurer) -> httpSecuritySessionManagementConfigurer
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.cors(cors -> Customizer.withDefaults());
		http.logout(logout -> logout.disable()); // Disable the default logout handling !IMPORTANT!!!
			
		return http.build();
	}

	private JwtDecoder jwtDecoder() {
		JwtDecoder jwtDecoder = NimbusJwtDecoder
				.withJwkSetUri("https://localhost:8443/realms/ApiRealm/protocol/openid-connect/certs").build();

		return jwtDecoder;
	}

	private AuthenticationManager authenticationManager() {

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

		JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder());
		authenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("https://localhost:3000") // Your frontend URL
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}

	@Bean
	public org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceManager(
			ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientService authorizedClientService) {

		OAuth2AuthorizedClientProvider authorizerClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
				.clientCredentials().build();

		AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
				clientRegistrationRepository, authorizedClientService);

		authorizedClientManager.setAuthorizedClientProvider(authorizerClientProvider);

		return authorizedClientManager;

	}
}
