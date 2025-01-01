package com.keycloak.course.authenticator.verifyemail;

import javax.crypto.SecretKey;

import org.keycloak.TokenVerifier;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.actiontoken.resetcred.ResetCredentialsActionToken;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.Time;
import org.keycloak.crypto.KeyUse;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeyManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.representations.AccessToken;
import org.keycloak.sessions.AuthenticationSessionCompoundId;
import org.keycloak.sessions.AuthenticationSessionModel;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

/* 
 * This authenticator exists so that we can manually send an email with an HS512 hashed token in the body of the email. This is done during reset credentials flow. 
 * Why do we need a custom authenticator to send the reset password email when the default flow already does this?: Because we want to add mobile code authenticator as well during this flow, but if we use 
 * Keycloak's default send email authenticator, the mobile code authenticator executes concurrently to the verify-email verification form, causing confusion in the reset-credentials flow: 
*/

public class SendAndVerifyEmailAuthenticator implements Authenticator {

	@Override
	public void close() {
	}

	@Override
	public void authenticate(AuthenticationFlowContext context) {

		// check for "key" URL query parameter
		String key = context.getHttpRequest().getUri().getQueryParameters().getFirst("key");

		System.out.println("token (key) in url query param is: " + key);

		if (key == null || key.isBlank()) {
			System.out.println("reset password token key NOT detected, sending email");
			// reset email not yet sent, reset password link not yet clicked
			try {
				sendResetPasswordEmail(context);
			} catch (EmailException e) {
				e.printStackTrace();
			}
			// Render the custom FTL file
			LoginFormsProvider form = context.form();
			Response response = form.createForm("custom-reset-email.ftl");
			context.challenge(response);

		} else {
			System.out.println("reset password token key IS detected, validating token");
			// reset password link clicked, validate reset passwork JWT in URL query params
			boolean tokenValid = validateResetPasswordToken(context, key);

			if (tokenValid) {
				System.out.println("token valid, calling context.success()");
				context.success();
			} else {
				System.out.println("token invalid calling context.failure()");
				context.failure(null);
			}

		}
	}

	private boolean validateResetPasswordToken(AuthenticationFlowContext context, String token) {
		KeyManager keyManager = context.getSession().keys();
		// Get the active key for the realm
		KeyWrapper keyWrapper = keyManager.getActiveKey(context.getRealm(), KeyUse.SIG, "HS512");

		SecretKey secretKey = keyWrapper.getSecretKey();

		try {
			// Validate the token using Keycloak's TokenVerifier
			TokenVerifier<AccessToken> verifier = TokenVerifier.create(token, AccessToken.class).secretKey(secretKey);

			AccessToken accessToken = verifier.verify().getToken();
			System.out.println("Token is valid. Claims: " + accessToken.getOtherClaims());

			return true;
		} catch (VerificationException e) {
			System.err.println("Token validation failed: " + e.getMessage());
			return false;
		}
	}

	private void sendResetPasswordEmail(AuthenticationFlowContext context) throws EmailException {

		UserModel user = context.getUser();

		// Generate a client session code
		int validityInSecs = context.getRealm()
				.getActionTokenGeneratedByUserLifespan(ResetCredentialsActionToken.TOKEN_TYPE);
		int absoluteExpirationInSecs = Time.currentTime() + validityInSecs;

		AuthenticationSessionModel authenticationSession = context.getAuthenticationSession();

		String authSessionEncodedId = AuthenticationSessionCompoundId.fromAuthSession(authenticationSession)
				.getEncodedId();
		ResetCredentialsActionToken token = new ResetCredentialsActionToken(user.getId(), user.getEmail(),
				absoluteExpirationInSecs, authSessionEncodedId, authenticationSession.getClient().getClientId());

		String link = UriBuilder
				.fromUri(context.getActionTokenUrl(
						token.serialize(context.getSession(), context.getRealm(), context.getUriInfo())))
				.build().toString();

		context.getSession().getProvider(EmailTemplateProvider.class).setRealm(context.getRealm()).setUser(user)
				.setAuthenticationSession(authenticationSession).sendPasswordReset(link, 100L);

		EventBuilder event = context.getEvent();
		String username = authenticationSession.getAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME);
		System.out.println("Attempted username: " + username);

		// This logs the activity in Keycloak’s event history. System administrators can
		// view this event in the Keycloak admin console under the "Events" section:
		event.clone().event(EventType.SEND_RESET_PASSWORD).user(user).detail(Details.USERNAME, username)
				.detail(Details.EMAIL, user.getEmail())
				.detail(Details.CODE_ID, authenticationSession.getParentSession().getId()).success();
		
//		This tells Keycloak to continue processing in a "forked" execution flow while displaying a success message to the user: 
		context.forkWithSuccessMessage(new FormMessage("custom email for reset password"));

	}

	@Override
	public void action(AuthenticationFlowContext context) {

	}

	@Override
	public boolean requiresUser() {
		return false;
	}

	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		return true;
	}

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
	}

}
