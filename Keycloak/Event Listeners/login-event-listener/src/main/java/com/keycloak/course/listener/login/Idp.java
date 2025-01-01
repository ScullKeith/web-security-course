package com.keycloak.course.listener.login;

public enum Idp {

	google("google"), gitlab("gitlab"), auth_server("oidc");

	Idp(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderName() {
		return providerName;
	}

	private String providerName;
}
