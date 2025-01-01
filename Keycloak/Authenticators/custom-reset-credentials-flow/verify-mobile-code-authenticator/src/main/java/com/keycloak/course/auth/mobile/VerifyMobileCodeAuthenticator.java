package com.keycloak.course.auth.mobile;

import java.util.function.Consumer;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import jakarta.ws.rs.core.Response;

public class VerifyMobileCodeAuthenticator implements Authenticator {

	private MockSMSGateway smsGateway = new MockSMSGateway();

	@Override
	public void close() {
	}

	@Override
	public void authenticate(AuthenticationFlowContext context) {

		// no need to check for IDP for reset credentials, since users authenticated via
		// IDP (IE Gitlabs, Spring Auth. Server) have credentials which are
		// stored/managed by the IDP itself. Reset-credentials flow is irrelevant to
		// IDP's.

		int mobileCode = smsGateway.generateAndSendMockSMSCode();
		context.getAuthenticationSession().setAuthNote("mobile_code", String.valueOf(mobileCode));
		System.out.println("mobileCode sent is::::::::: " + mobileCode);

		context.challenge(createForm(context, null));
	}

	private Response createForm(AuthenticationFlowContext context, Consumer<LoginFormsProvider> formConsumer) {

		LoginFormsProvider form = context.form();
		form.setAttribute("mobile_number", context.getUser().getFirstAttribute("mobile_number"));

		if (formConsumer != null)
			formConsumer.accept(form);

		return form.createForm("verify-mobile-code.ftl");
	}

	@Override
	public void action(AuthenticationFlowContext context) {

		String action = context.getHttpRequest().getDecodedFormParameters().getFirst("action");

		boolean resendCodeRequested = "resend".equals(action);

		if (resendCodeRequested) {

			System.out.println("resending code...");

			// resend code (additional attempt)
			int mobileCode = smsGateway.generateAndSendMockSMSCode();
			context.getAuthenticationSession().setAuthNote("mobile_code", String.valueOf(mobileCode));
			System.out.println("mobileCode sent is::::::::: " + mobileCode);

			// present form again to user/user agent
			context.challenge(createForm(context, null));
			return;

		}

		String mobileCodeEntered = context.getHttpRequest().getDecodedFormParameters().getFirst("mobile_code");
		String mobileCodeSent = context.getAuthenticationSession().getAuthNote("mobile_code");

		if (mobileCodeEntered == null || !mobileCodeEntered.equals(mobileCodeSent)) {
			context.challenge(
					createForm(context, form -> form.addError(new FormMessage("mobile_number", "Incorrect code"))));
		} else
			context.success();
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
