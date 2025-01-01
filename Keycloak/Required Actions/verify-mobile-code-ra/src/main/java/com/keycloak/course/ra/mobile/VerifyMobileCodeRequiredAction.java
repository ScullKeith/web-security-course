package com.keycloak.course.ra.mobile;

import java.util.Arrays;
import java.util.function.Consumer;

import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import jakarta.ws.rs.core.Response;

public class VerifyMobileCodeRequiredAction implements RequiredActionProvider {

	public static final String PROVIDER_ID = "verify-mobile-code";

	private MockSMSGateway smsGateway = new MockSMSGateway();

	@Override
	public void close() {
	}

	@Override
	public void evaluateTriggers(RequiredActionContext context) {

		smsGateway = new MockSMSGateway();

		UserModel user = context.getUser();
		KeycloakSession session = context.getSession();
		RealmModel realm = context.getRealm();

		for (FederatedIdentityModel identity : session.users().getFederatedIdentitiesStream(realm, user).toList()) {
			
			if (isProviderIDP(identity.getIdentityProvider(), realm, user)) {
				System.out.println("removing " + PROVIDER_ID);
				user.removeRequiredAction(PROVIDER_ID);
				return;
			}
		}

		String mobileNumberVerified = user.getFirstAttribute("mobile_number_verified");

		if (mobileNumberVerified == null || !mobileNumberVerified.equals("true")) {
			user.addRequiredAction(PROVIDER_ID);
		}
	}

	private boolean isProviderIDP(String identity, RealmModel realm, UserModel user) {
		return Arrays.asList(Idp.values()).stream()
				.anyMatch(idp -> idp.getProviderName().equals(identity.toLowerCase()));
	}

	@Override
	public void requiredActionChallenge(RequiredActionContext context) {

		System.out.println("requireActionChallenge method");
		// send mobile code (1st attempt)
		int mobileCode = smsGateway.generateAndSendMockSMSCode();
		context.getAuthenticationSession().setAuthNote("mobile_code", String.valueOf(mobileCode));
		System.out.println("mobileCode sent is::::::::: " + mobileCode);

		context.challenge(createForm(context, null));
	}

	private Response createForm(RequiredActionContext context, Consumer<LoginFormsProvider> formConsumer) {

		LoginFormsProvider form = context.form();
		form.setAttribute("mobile_number", context.getUser().getFirstAttribute("mobile_number"));

		if (formConsumer != null)
			formConsumer.accept(form);

		return form.createForm("verify-mobile-code.ftl");
	}

	@Override
	public void processAction(RequiredActionContext context) {

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
		} else {
			UserModel user = context.getUser();
			user.setSingleAttribute("mobile_number_verified", "true");
			context.getAuthenticationSession().removeRequiredAction(PROVIDER_ID);
			context.success();
		}
	}

}
