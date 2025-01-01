package com.keycloak.course.logout_service_http_only;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CsrfCookieFilter extends OncePerRequestFilter {

	private static String csrfSecret = "DS4mcrc3PMJ2hgBm4jyCsQ==";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		System.out.println("[CsrfCookieFilter]");

		String accessToken = "";

		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {

			if (cookie.getName().equals("access_token"))
				accessToken = cookie.getValue();
		}

		String csrfToken = request.getHeader("csrf_token");

		System.out.println("[filter]: access_token\n" + accessToken);
		System.out.println("\n\n[filter: csrf_token\n" + csrfToken);

		if (csrfToken == null || csrfToken.isBlank() || accessToken == null || accessToken.isBlank()) {
			System.out.println("CSRF token invalid. Sending 401 response.");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid CSRF token");
			return; // Stop further filter chain execution
		}

		try {
			var csrfTokenValid = validateCsrfToken(csrfToken, accessToken);

			if (!csrfTokenValid) {
				System.out.println("CSRF token invalid. Sending 401 response.");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid CSRF token");
				return; // Stop further filter chain execution
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		filterChain.doFilter(request, response);
	}

	private boolean validateCsrfToken(String csrfToken, String accessToken) throws Exception {

		String[] parts = csrfToken.split("\\.");
		if (parts.length != 2) {
			System.out.println("csrf token INVALID because legnth not 2");
			return false;
		}
		String receivedHmac = parts[0];
		String message = parts[1];

		String accessTokenHash = hashAccessToken(accessToken);

		if (!message.startsWith(accessTokenHash + "!")) {
			System.out.println("csrf token INVALID because accessTokenHash is for another access token and/or user");
			return false;
		}

		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(csrfSecret.getBytes(), "HmacSHA256");
		mac.init(secretKeySpec);
		byte[] hmacBytes = mac.doFinal(message.getBytes());

		String computedHmac = Hex.encodeHexString(hmacBytes);

		return computedHmac.equals(receivedHmac);
	}

	private String hashAccessToken(String accessToken) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(accessToken.getBytes());
		// actually hashes the hash which holds the accessToken
		return Hex.encodeHexString(hash);
	}
}
