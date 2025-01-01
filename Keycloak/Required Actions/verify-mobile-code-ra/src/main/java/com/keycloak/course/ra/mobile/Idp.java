package com.keycloak.course.ra.mobile;

import lombok.Getter;

@Getter
public enum Idp {
	
	google("google"), gitlab("gitlab"), auth_server("oidc");
	
	 Idp(String providerName) {
		 this.providerName = providerName;
	}
	
	private String providerName; 
}
