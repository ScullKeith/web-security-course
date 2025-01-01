package com.keycloak.course.authenticator.names;

import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

public class NamesRegistrationAuthenticatorFactory implements AuthenticatorFactory {
	
	private final static String PROVIDER_ID = "names-reg-auth";

	@Override
	public Authenticator create(KeycloakSession session) {
		return new NamesRegistrationAuthenticator();
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
	public String getDisplayType() {
		return "Names Registration Authenticator";
	}

	@Override
	public String getReferenceCategory() {
		return "authenticator";
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

	@Override
	public Requirement[] getRequirementChoices() {
		return new Requirement[] { AuthenticationExecutionModel.Requirement.REQUIRED };
	}

	@Override
	public boolean isUserSetupAllowed() {
		return false;
	}
	
	@Override
	public String getHelpText() {
		return "Authenticator for users to enter their username and display name";
	}
	
	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return ProviderConfigurationBuilder.create().build();
	}

}
