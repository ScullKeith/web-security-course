package com.keycloak.course.listener.login;

import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class IdpEmailVerificationListenerProviderFactory implements EventListenerProviderFactory {

	@Override
	public EventListenerProvider create(KeycloakSession session) {
		return new IdpEmailVerificationListenerProvider(session); 
	}
	
	@Override
	public void init(Scope config) {
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
	}

	@Override
	public void close() {
	}
	
	@Override
	public String getId() {
		return "idp-authenetication-event-listener_V2"; 
	}
	
}
