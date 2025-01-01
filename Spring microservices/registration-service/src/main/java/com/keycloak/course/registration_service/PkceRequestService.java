package com.keycloak.course.registration_service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class PkceRequestService {

	public PkceRequest getPkceRequest() {

		PkceRequest request = new PkceRequest();
		
		try {
			
			request.setCodeChallengeMethod("S256");
			var codeVerifier = generateCodeVerifier();

			request.setCodeVerifier(codeVerifier);

			var codeChallenge = generateCodeChallenge(codeVerifier);

			request.setCodeChallenge(codeChallenge);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return request; 
	}

	private static String generateCodeVerifier() {

		SecureRandom secureRandom = new SecureRandom();
		byte[] codeVerifier = new byte[32];

		// fill array with 32 random bytes
		secureRandom.nextBytes(codeVerifier);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
	}

	private static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(codeVerifier.getBytes());
		return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
	}

}
