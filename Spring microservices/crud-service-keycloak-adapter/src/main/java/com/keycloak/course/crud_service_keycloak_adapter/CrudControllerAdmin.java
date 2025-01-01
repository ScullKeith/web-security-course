package com.keycloak.course.crud_service_keycloak_adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('admin')") 
@RequestMapping("/users/admin")
@RestController
public class CrudControllerAdmin {
	
	@Autowired 
	private KeycloakAdapterService keycloakAdapterService; 
	
	@PutMapping("/{pid}")
	public void updateUser(@RequestBody UserUpdateDTO userUpdateDTO, @PathVariable("pid") String pid) {
		
		keycloakAdapterService.updateUserUsingPid(userUpdateDTO, pid); 
		
	}
}
