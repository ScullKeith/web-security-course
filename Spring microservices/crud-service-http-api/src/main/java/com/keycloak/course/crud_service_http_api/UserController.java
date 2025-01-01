package com.keycloak.course.crud_service_http_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private KeycloakApiService keycloakApiService;

	@GetMapping("/authenticated-test")
	public String authenticated() {
		return "authenticated successfully!";
	}
	
	@PreAuthorize("hasRole('application.admin') or #principal.getClaim('preferred_username') == #username")
	@GetMapping("/{username}")
	public UserDTO getUserByUsername(@PathVariable("username") String username,
			@AuthenticationPrincipal Jwt principal) {
		
		return keycloakApiService.getUserByUsername(username);
	}

	@GetMapping
	public List<UserDTO> getUsers(@RequestParam(name = "min", defaultValue = "2") int min,
			@RequestParam(name = "max", defaultValue = "2") int max,
			@RequestParam(name = "first", defaultValue = "0") int first) {

		return keycloakApiService.getUsers(min, max, first);
	}

	@PutMapping("/{userId}")
//	@PreAuthorize("#principal.getClaim('sub') == #userId")
	public void updateUser(@RequestBody UserUpdateDTO userUpdateDTO, @AuthenticationPrincipal Jwt principal,
			@PathVariable("userId") String userId) {
		
		System.out.println("comparing: " + principal.getClaim("sub") + " " + userId);
		keycloakApiService.updateUser(userUpdateDTO, userId, principal.getClaim("preferred_username"));
	}
	
	@PreAuthorize("hasRole('application.admin') or #userId == #jwt.getClaim('sub')")
	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable("userId") String userId, @AuthenticationPrincipal Jwt jwt) {
		
		keycloakApiService.deleteUser(userId);
	}
}