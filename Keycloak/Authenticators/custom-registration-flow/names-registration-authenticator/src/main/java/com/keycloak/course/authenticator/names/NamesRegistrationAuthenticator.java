package com.keycloak.course.authenticator.names;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

public class NamesRegistrationAuthenticator implements Authenticator {

	@Override
	public void close() {
	}

	@Override
	public void authenticate(AuthenticationFlowContext context) {
		context.challenge(createForm(context, null, null));
	}

	private Response createForm(AuthenticationFlowContext context, Consumer<LoginFormsProvider> formConsumer,
			Map<String, String> formAttributes) {

		LoginFormsProvider form = context.form();

		if (formAttributes != null)
			formAttributes.entrySet().forEach(attr -> form.setAttribute(attr.getKey(), attr.getValue()));

		if (formConsumer != null)
			formConsumer.accept(form);

		return form.createForm("name-registration-form.ftl");
	}

	@Override
	public void action(AuthenticationFlowContext context) {

		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

		String fullName = formData.getFirst("full_name");
		String username = formData.getFirst("username");
		String displayName = formData.getFirst("display_name");

		String regex = "^[\\s]*([a-zA-Z]{2,30})[\\s]+([a-zA-Z]{2,30})[\\s]*$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(fullName);

		if (!m.matches()) {
			Map<String, String> attributes = Map.of("full_name", "", "username", username, "display_name", displayName);
			context.challenge(createForm(context,
					form -> form.addError(new FormMessage("full_name", "Please enter your first and last name")),
					attributes));
			return;
		}

		if (userExistsByUsername(context.getSession(), context.getRealm(), username)) {
			Map<String, String> attributes = Map.of("full_name", fullName, "username", "", "display_name", displayName);
			context.challenge(createForm(context,
					form -> form.addError(new FormMessage("username", "Username already in use")), attributes));
			return;
		}

		String firstName = m.group(1);
		String lastName = m.group(2);

		if (userExistsByAttribute(context.getSession(), context.getRealm(), "display_name", displayName)) {
			Map<String, String> attributes = Map.of("full_name", fullName, "username", username, "display_name", "");
			context.challenge(createForm(context,
					form -> form.addError(new FormMessage("display_name", "Display name already in use")), attributes));
			return;
		}

		System.out.println("firstName: " + firstName);
		System.out.println("lastName: " + lastName);

		AuthenticationSessionModel authSessionModel = context.getAuthenticationSession();

		authSessionModel.setAuthNote("first_name", firstName);
		authSessionModel.setAuthNote("last_name", lastName);
		authSessionModel.setAuthNote("username", username);
		authSessionModel.setAuthNote("display_name", displayName);

		context.success();
	}

	private boolean userExistsByUsername(KeycloakSession session, RealmModel realm, String username) {
		UserProvider userProvider = session.users();
		UserModel user = userProvider.getUserByUsername(realm, username);
		return user != null;
	}

	private boolean userExistsByAttribute(KeycloakSession session, RealmModel realm, String attrName,
			String attrValue) {

		UserProvider userProvider = session.users();
		Stream<UserModel> usersStream = userProvider.searchForUserByUserAttributeStream(realm, attrName, attrValue);
		return usersStream.count() > 0;

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