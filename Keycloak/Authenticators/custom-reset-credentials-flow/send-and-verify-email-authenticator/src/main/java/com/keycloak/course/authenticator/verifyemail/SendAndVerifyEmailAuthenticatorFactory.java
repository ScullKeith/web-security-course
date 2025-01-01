package com.keycloak.course.authenticator.verifyemail;

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

public class SendAndVerifyEmailAuthenticatorFactory implements AuthenticatorFactory {
	
	private static final String PROVIDER_ID = "send-and-verify-email-auth";

	@Override
	public Authenticator create(KeycloakSession session) {
		return new SendAndVerifyEmailAuthenticator();
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
		return "Reset Email Authenticator V1.4";
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
		return "Sends email with verification token for email verification. If token already present, validates token before moving to next authenticator.";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return ProviderConfigurationBuilder.create().build();
	}

}
