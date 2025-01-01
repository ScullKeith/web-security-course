package com.keycloak.course.login_service_http_only;

import jakarta.servlet.http.Cookie;

public class CookieService {
	
	public static Cookie createCookie(String key, String value, boolean httpOnly, int maxAge) {

		Cookie cookie = new Cookie(key, value);
		cookie.setHttpOnly(httpOnly);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setAttribute("SameSite", "none");
		cookie.setMaxAge(maxAge);

		return cookie;

	}
}
