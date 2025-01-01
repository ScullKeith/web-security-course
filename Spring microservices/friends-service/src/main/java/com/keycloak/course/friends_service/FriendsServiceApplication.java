package com.keycloak.course.friends_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FriendsServiceApplication {

	public static void main(String[] args) {

		// Specify the path to your trust store
		String trustStorePath = "C:\\Users\\kscull\\Pictures\\personal\\Video Tutorial Series\\Web Security with OAuth2 and Keycloak for Spring Microservices Developers\\pre-videos-project\\ssl certificates (for https)\\microservices truststores\\customTrustStore.jks"; // Update
		String trustStorePassword = "password"; // Update this password

		// Set the trust store properties
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

		SpringApplication.run(FriendsServiceApplication.class, args);
	}

}
