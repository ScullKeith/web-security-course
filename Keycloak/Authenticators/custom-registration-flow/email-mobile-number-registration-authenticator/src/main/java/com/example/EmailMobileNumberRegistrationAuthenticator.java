package com.example;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

public class EmailMobileNumberRegistrationAuthenticator implements Authenticator {

	@Override
	public void close() {
	}

	@Override
	public void authenticate(AuthenticationFlowContext context) {
		context.challenge(createForm(context, null));
	}

	@Override
	public void action(AuthenticationFlowContext context) {

		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

		String email = formData.getFirst("email");
		String mobileNumber = formData.getFirst("mobile_number");

		if (!isValidEmail(email)) {
			context.challenge(
					createForm(context, form -> form.addError(new FormMessage("email", "Invalid email address"))));
			return;
		}

		if (userExistsByEmail(context.getSession(), context.getRealm(), email)) {
			context.challenge(createForm(context, form -> form.addError(
					new FormMessage("email", "An account already exists with email address `" + email + "`"))));
			return;
		}

		AuthenticationSessionModel authSessionModel = context.getAuthenticationSession();

		authSessionModel.setAuthNote("email", email);
		authSessionModel.setAuthNote("mobile_number", mobileNumber);

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

	private Response createForm(AuthenticationFlowContext context, Consumer<LoginFormsProvider> formConsumer) {

		LoginFormsProvider form = context.form();
		form.setAttribute("first_name", context.getAuthenticationSession().getAuthNote("first_name"));

		if (formConsumer != null)
			formConsumer.accept(form);

		return form.createForm("email-mobile-number-registration-form.ftl");
	}

	private boolean isValidEmail(String email) {

		String emailRgx = "^[a-zA-Z\\d!@#$%^&*(){}<>?]+@[a-zA-Z\\d!@#$%^&*(){}<>?]+\\.[a-zA-Z]{3}";
		Pattern p = Pattern.compile(emailRgx);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	private boolean userExistsByEmail(KeycloakSession session, RealmModel realm, String email) {
		UserProvider userProvider = session.users();
		UserModel user = userProvider.getUserByEmail(realm, email);
		return user != null;
	}
}
