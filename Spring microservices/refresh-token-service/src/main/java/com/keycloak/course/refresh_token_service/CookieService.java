package com.keycloak.course.refresh_token_service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;

@Service
public class CookieService {

	public static String getCookieValue(Cookie[] cookies, String keyName) {

		if (cookies == null)
			throw new RuntimeException("Unauthorized");
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(keyName))
				return cookie.getValue();
		}
		return null;
	}

	public static Cookie createCookie(String key, String value, boolean httpOnly, int maxAge) {

		Cookie cookie = new Cookie(key, value);
		cookie.setHttpOnly(httpOnly);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);

		return cookie;

	}
}
