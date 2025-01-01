package com.keycloak.course.logout_service_http_only;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class LogoutController {

	@Autowired
	LogoutService logoutService;

	@PostMapping("/logout")
	public String logout(HttpServletRequest request) {

		String accessToken = getAccessToken(request.getCookies());

		String userId = extractSubClaim(accessToken, "sub");

		logoutService.logoutKeycloakAdmin(userId);

		return "logout!";
	}

	private String getAccessToken(Cookie[] cookies) {

		String accessToken = "";

		for (Cookie cookie : cookies)
			if (cookie.getName().equals("access_token"))
				accessToken = cookie.getValue();

		return accessToken;
	}

	private String extractSubClaim(String accessToken, String claim) {

		String[] parts = accessToken.split("\\.");
		String payload = new String(Base64.getDecoder().decode(parts[1]));

		String claimKey = "\"" + claim + "\":\"";
		int startIndex = payload.indexOf(claimKey) + claimKey.length();
		int endIndex = payload.indexOf("\"", startIndex);

		return payload.substring(startIndex, endIndex);
	}

}
