package com.keycloak.course.resource_server_example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor 
public class UserVm {

	private String username; 
	private String email;
	private String firstName;
	private String lastName;
	
}
