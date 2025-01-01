package com.keycloak.course.authenticator.password;

import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

public class PasswordRegistrationAuthenticatorFactory implements AuthenticatorFactory {
	
	private static final String PROVIDER_ID = "password-registration-reg-auth";

	@Override
	public Authenticator create(KeycloakSession session) {
		return new PasswordRegistrationAuthenticator();
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
		return "Password Registration Authenticator";
	}

	@Override
	public String getReferenceCategory() {
		return "code";
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
		return "Authenticator used for user to provide their password during registration flow";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return ProviderConfigurationBuilder.create().build();
	}

}
