package com.keycloak.course.logout_service_http_only;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpOnlyCookieTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public HttpOnlyCookieTokenAuthenticationFilter(RequestMatcher requiresAuth,
			AuthenticationManager authenticationManger) {
		super(requiresAuth);
		this.setAuthenticationManager(authenticationManger);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		String token = extractAccessTokenFromCookie(request);
		
		if(token == null || token.isEmpty()) { 
			 throw new AuthenticationServiceException("Token is null or empty"); 
		}
		
		BearerTokenAuthenticationToken authToken = new BearerTokenAuthenticationToken(token);

		// ask AuthenticationManager to authenticate token:
		return getAuthenticationManager().authenticate(authToken);
	}

	private String extractAccessTokenFromCookie(HttpServletRequest request) {

		Cookie[] cookies = request.getCookies();

		if (cookies != null && cookies.length > 1) { 
			for (Cookie cookie : cookies) {
				System.out.println("nomnnom cookie " + cookie.getName());
				if ("access_token".equals(cookie.getName()))
					return cookie.getValue();
			}
		} else {
			System.out.println("NULL COOKIES OR NO COOKIES");
		}
		return null;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		SecurityContextHolder.getContext().setAuthentication(authResult);
		chain.doFilter(request, response);
	}
}
