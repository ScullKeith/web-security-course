package com.keycloak.course.authenticator.password;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

public class PasswordRegistrationAuthenticator implements Authenticator {

	@Override
	public void close() {
	}

	@Override
	public void authenticate(AuthenticationFlowContext context) {

		AuthenticationSessionModel authSessionModel = context.getAuthenticationSession();

		String firstName = authSessionModel.getAuthNote("first_name");
		context.challenge(createForm(context, firstName));

		return;
	}

	@Override
	public void action(AuthenticationFlowContext context) {

		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

		String password = formData.getFirst("password");
		String repeatPassword = formData.getFirst("repeat_password");

		if (!passwordValid(password, repeatPassword)) {

		}

		AuthenticationSessionModel authSessionModel = context.getAuthenticationSession();

		authSessionModel.setAuthNote("password", password);
		authSessionModel.setAuthNote("repeatPassword", (repeatPassword));

		context.success();
	}

	@Override
	public boolean requiresUser() {
		return false;
	}

	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		return false;
	}

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
	}

	private Response createForm(AuthenticationFlowContext context, String firstName) {

		LoginFormsProvider form = context.form().setAttribute("first_name", firstName);

		return form.createForm("password-registration-form.ftl");
	}

	private boolean passwordValid(String password, String repeatPassword) {
		return true;
	}

}
