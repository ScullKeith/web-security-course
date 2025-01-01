package com.keycloak.course.resource_server_example;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	@GetMapping()
	public String unauthenticatedEndpoint() {
		return "Welcome!";
	}

	// returns a UserVm object containing only the authenticated user's own data:
	@GetMapping("/authenticated-user")
	public UserVm getAuthenticatedUser(@AuthenticationPrincipal Jwt principal) {
		
		UserVm authenticatedUserVm = new UserVm();
		
		authenticatedUserVm.setEmail(principal.getClaim("email"));
		authenticatedUserVm.setUsername(principal.getClaim("preferred_username"));
		authenticatedUserVm.setFirstName(principal.getClaim("given_name"));
		authenticatedUserVm.setLastName(principal.getClaim("family_name"));
		
		return authenticatedUserVm;
	}
	
	// mock method: we have no way of getting any user's data other than
	// authenticated user in this application.
	// This is shown only to demonstrate roles in a resource server
	@PreAuthorize("hasRole('application.admin') or #email == #principal.getClaim('email')")
	@GetMapping("/{email}") 
	public String getSpecifiedUserMock(@PathVariable("email") String email, @AuthenticationPrincipal Jwt principal) {
		return "authorized!";
	}
}
