package com.keycloak.course.endpoint.friends.list;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class FriendsListQueryEndpointProviderFactory implements RealmResourceProviderFactory {

	public static final String ID = "custom-user-query-endpoint-V1.1";

	@Override
	public RealmResourceProvider create(KeycloakSession session) {
		return new FriendsListQueryEndpointProvider(session);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void init(org.keycloak.Config.Scope config) {
	}

	@Override
	public void postInit(org.keycloak.models.KeycloakSessionFactory factory) {
	}

	@Override
	public void close() {
	}
}
