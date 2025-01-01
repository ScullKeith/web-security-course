package com.keycloak.course.authenticator.persistence;

import java.util.UUID;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserModel.RequiredAction;
import org.keycloak.sessions.AuthenticationSessionModel;

public class UserPersistenceRegistrationAuthenticator implements Authenticator {

	@Override
	public void close() {
	}

	@Override
	public void authenticate(AuthenticationFlowContext context) {

		RealmModel realm = context.getRealm();
		KeycloakSession session = context.getSession();

		AuthenticationSessionModel authSessionModel = context.getAuthenticationSession();

		String firstName = authSessionModel.getAuthNote("first_name");
		String lastName = authSessionModel.getAuthNote("last_name");
		String username = authSessionModel.getAuthNote("username");
		String password = authSessionModel.getAuthNote("password");
		String displayName = authSessionModel.getAuthNote("display_name");

		String email = authSessionModel.getAuthNote("email");
		String mobileNumber = authSessionModel.getAuthNote("mobile_number");

		System.out.println("[user-persistence-registration-authenticator]: auth notes retrieved: " + firstName + "\n"
				+ lastName + "\n" + username + "\n" + password + "\n" + email + "\n" + mobileNumber);

		UserModel user = session.users().addUser(realm, username);

		user.setEnabled(true);
		user.setEmail(email);
		user.setUsername(username);
		user.setEmailVerified(false);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setSingleAttribute("mobile_number", mobileNumber);
		user.setSingleAttribute("display_name", displayName);
		user.setSingleAttribute("pid", UUID.randomUUID().toString());

		UserCredentialModel userCredential = UserCredentialModel.password(password);
		user.credentialManager().updateCredential(userCredential);

		user.addRequiredAction(RequiredAction.VERIFY_EMAIL);
		user.addRequiredAction("verify-mobile-code");

		context.setUser(user);

		context.success();
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
