package com.keycloak.course.crud_service_keycloak_adapter;

import javax.ws.rs.QueryParam;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public class CrudController {

	@Autowired
	KeycloakAdapterService keycloakAdapterService;

	@PutMapping("/authenticated-test")
	public String testAuthentication(@AuthenticationPrincipal Jwt principal) {
		return "Hello, ";
	}

	@GetMapping("/private")
	public PrivateUserDTO getPrivateUser(@AuthenticationPrincipal Jwt principal) {

		String userId = principal.getClaim("sub");

		if (userId == null || userId.isBlank()) {
			// TODO: handle errors
		}

		PrivateUserDTO user = keycloakAdapterService.getPrivateUser(userId);
		return user;
	}

	@GetMapping("/{pid}")
	public PublicUserDTO getPublicUser(@PathVariable("pid") String pid) {
		PublicUserDTO user = keycloakAdapterService.getPublicUser(pid);
		return user;
	}

	@GetMapping("/public")
	public PublicUsersPageResponseDTO getUsers(@QueryParam("pageNumber") Integer pageNumber,
			@QueryParam("pageSize") Integer pageSize, @AuthenticationPrincipal Jwt principal) {

		String userId = principal.getClaim("sub");
		var page = keycloakAdapterService.getUsers(pageNumber, pageSize, userId);

		return page;

	}

	// no authorization (@PreAuthorize) needed, because we are using the ID from the
	// claims in the JWT, which is stored in an http only cookie client-side:
	@PutMapping()
	public PublicUserDTO updateUser(@RequestBody UserUpdateDTO userUpdateDTO, @AuthenticationPrincipal Jwt principal) {

		System.out.println("principal keycloak userId " + principal.getClaim("sub"));

		UserRepresentation updatedUserInDB = keycloakAdapterService.updateUser(userUpdateDTO,
				principal.getClaim("sub"));

		if (updatedUserInDB == null)
			return null;

		PublicUserDTO updatedUser = new PublicUserDTO(updatedUserInDB);

		return updatedUser;
	}

}
