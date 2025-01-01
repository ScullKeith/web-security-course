package com.keycloak.course.crud_service_keycloak_adapter;

import org.keycloak.representations.idm.UserRepresentation;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PrivateUserDTO {

	private String firstName;
	private String lastName;
	private String username;

	private String displayName;

	private String email;
	private String userId;
	private String pid;

	public PrivateUserDTO(UserRepresentation userRep) {

		this.firstName = userRep.getFirstName();
		this.lastName = userRep.getLastName();

		this.username = userRep.getUsername();
		this.email = userRep.getEmail();
		this.userId = userRep.getId();

		if (userRep.getAttributes() != null && userRep.getAttributes().size() > 0) {

			if (userRep.getAttributes().containsKey("display_name"))
				this.displayName = userRep.getAttributes().get("display_name").get(0);

			if (userRep.getAttributes().containsKey("pid"))
				this.pid = userRep.getAttributes().get("pid").get(0);

		}

	}
}