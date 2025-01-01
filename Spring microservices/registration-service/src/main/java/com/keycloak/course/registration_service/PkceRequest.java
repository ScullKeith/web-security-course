package com.keycloak.course.registration_service;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class PkceRequest {
	
	private String codeChallenge;
	private String codeChallengeMethod;
	private String codeVerifier;
}
