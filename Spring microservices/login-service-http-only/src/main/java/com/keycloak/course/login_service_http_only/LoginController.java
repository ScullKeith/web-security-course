package com.keycloak.course.login_service_http_only;

import static com.keycloak.course.login_service_http_only.CookieService.createCookie;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class LoginController {

	@Autowired
	CsrfTokenService csrfService;

	@GetMapping("/success")
	public void loginSuccess(@AuthenticationPrincipal OidcUser user,
			@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		try {
			// manually remove all cookies that may have been sent by frontend in original
			// request. This prevents the browser from sending the cookies during subsequent
			// requests:
			for (Cookie cookie : request.getCookies()) {
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}

			// JSessionId is automatically stored in HttpResponse, because our
			// SecurityFilterChain cannot be stateless in this case. We must manually
			// invalidate it:
			Cookie cookie = new Cookie("JSESSIONID", null);
			cookie.setPath("/");
			cookie.setMaxAge(0);
			response.addCookie(cookie);

			// Invalidates "Session" object in this Spring application. Session object uses
			// JSessionId if included in HttpRequest.
			request.getSession().invalidate();

			// extract access, id && refresh tokens, add cookies to HttpResponse:
			String accessToken = client.getAccessToken().getTokenValue();
			String idToken = user.getIdToken().getTokenValue();
			String refreshToken = client.getRefreshToken().getTokenValue();
			String csrfToken = csrfService.generateCsrfToken(accessToken);

			Cookie access_token_cookie = createCookie("access_token", accessToken, true, 300);
			Cookie id_token_cookie = createCookie("id_token", idToken, false, 300);
			Cookie refresh_token_cookie = createCookie("refresh_token", refreshToken, true, 1_800);

			// csrf tokens shouldn't be stored long term in cookies due to
			// xss vulnerability. Frontend should store csrf token in DOM
			
			Cookie csrf_token_cookie = createCookie("csrf_token", csrfToken, false, 20);

			response.addCookie(access_token_cookie);
			response.addCookie(id_token_cookie);
			response.addCookie(refresh_token_cookie);
			response.addCookie(csrf_token_cookie);

			System.out.println("csrf_token " + csrfToken);

			response.sendRedirect("https://localhost:3000/post-sign-in");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/postLogin")
	public String postLogin() {
		return "logged in!";
	}

}
