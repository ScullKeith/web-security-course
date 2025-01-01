package com.keycloak.course.ra.mobile;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class VerifyMobileCodeRequiredActionFactory implements RequiredActionFactory {
	
	public static final String PROVIDER_ID = "verify-mobile-code";
	
	@Override
	public RequiredActionProvider create(KeycloakSession session) {
		return new VerifyMobileCodeRequiredAction();
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
		return PROVIDER_ID;
	}

	@Override
	public String getDisplayText() {
		return "Verify Mobile Code RA-V1.5";
	}

}
