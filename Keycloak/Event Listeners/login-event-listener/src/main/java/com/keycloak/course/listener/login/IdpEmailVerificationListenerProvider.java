package com.keycloak.course.listener.login;

import java.util.Arrays;
import java.util.UUID;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserModel.RequiredAction;

// Created for configuring and correcting attributes and removing required actions for users logged in via social/IDPs 
public class IdpEmailVerificationListenerProvider implements EventListenerProvider {

	private final KeycloakSession session;

	public IdpEmailVerificationListenerProvider(KeycloakSession session) {
		this.session = session;
	}

	@Override
	public void close() {
	}

	@Override
	public void onEvent(Event event) {

		if (event.getType() == EventType.IDENTITY_PROVIDER_FIRST_LOGIN || event.getType() == EventType.REGISTER) {

			RealmModel realm = session.getContext().getRealm();
			UserModel user = session.users().getUserById(realm, event.getUserId());

			// Check for known federated identities (social IDP's)
			session.users().getFederatedIdentitiesStream(realm, user).forEach(identity -> {

				boolean isIDP = isProviderIDP(identity.getIdentityProvider(), realm, user);

				System.out.println("identity provider: " + identity.getIdentityProvider());
				System.out.println(IdentityProviders.GOOGLE.name());

				if (isIDP) {
					user.setEmailVerified(true);
					user.removeRequiredAction(RequiredAction.VERIFY_EMAIL);
					System.out.println("REMOVED VERIFY EMAIL FROM USER");
					configureIDPAttributes(identity.getIdentityProvider(), user);
				}
			});
		}
	}

	private boolean isProviderIDP(String identity, RealmModel realm, UserModel user) {
		return Arrays.asList(Idp.values()).stream()
				.anyMatch(idp -> idp.getProviderName().equals(identity.toLowerCase()));
	}

	private void configureIDPAttributes(String identity, UserModel user) {

		String email = user.getEmail();
		String updatedDisplayName = email.substring(0, email.indexOf("@"));
		user.setSingleAttribute("display_name", updatedDisplayName);
		user.setSingleAttribute("pid", UUID.randomUUID().toString());

		if (identity.toLowerCase().equals(IdentityProviders.GOOGLE.name().toLowerCase()))
			user.setUsername(updatedDisplayName);

	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {
	}

}
