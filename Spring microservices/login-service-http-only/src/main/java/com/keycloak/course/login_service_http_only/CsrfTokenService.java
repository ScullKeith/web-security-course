package com.keycloak.course.login_service_http_only;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

@Service
public class CsrfTokenService {

	private static String csrfSecret = "DS4mcrc3PMJ2hgBm4jyCsQ==";

	public String generateCsrfToken(String accessToken) throws Exception {

		// 1. randomValue
		SecureRandom secureRandom = new SecureRandom();
		byte[] randomBytes = new byte[32]; // 256 bit token
		secureRandom.nextBytes(randomBytes);

		String randomValue = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

		// 2. access_token SHA256 hash
		String accessTokenHash = hashAccessToken(accessToken);

		// 1. Message: "[accessTokenHash]![randomValue]"
		String message = accessTokenHash + "!" + randomValue;

		// Use Hmac to hash the message into the Hmac value. Hmac combines SHA256 for
		// hashing witha secret key
		// Hmac has only 1 private key, no public key, so it's symmetric hashing
		// function
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(csrfSecret.getBytes(), "HmacSHA256");
		mac.init(secretKeySpec);

		// 2. hmac: Hmac hashed value of the message, using secret key

		byte[] hmacBytes = mac.doFinal(message.getBytes());
		// convert byte array (binary) into more compact hexadecimal (base 16)
		// characters, just like accessTokenhash
		String hmac = Hex.encodeHexString(hmacBytes);

		String csrfToken = hmac + "." + message;

		return csrfToken;
	}

	private static String hashAccessToken(String accessToken) throws NoSuchAlgorithmException {
		// Use SHA256 hashing function to hash access token:
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		// Hashes the access token value. The hashed value's default form is a
		// byte array of binary digits, AKA base 2, using digits 0,1
		byte[] hash = digest.digest(accessToken.getBytes());

		// Converts the hashed byte array (currently binary, AKA base 2) into a string
		// where each byte is represented by its corresponding hexadecimal value (base
		// 16 using 0-9, A-F)
		// This is done because hexadecimal is more compact, reduces # of characters by
		// 4 times. (8 digits to represent 8 bits per byte in binary, only 2 digits to
		// represent
		// 8 bits in hexadecimal)
		return Hex.encodeHexString(hash); // requires
	}
}
