package com.keycloak.course.ra.mobile;

import java.util.Random;

public class MockSMSGateway {
	
	public int generateAndSendMockSMSCode() {
		
		// generate mobile code
		int code = 1000 + new Random().nextInt(9000); 
		
		// send SMS (Twilio, etc.)... 
		
		return code; 
	}
}
