package com.keycloak.course.crud_service_keycloak_adapter;

import org.keycloak.representations.idm.UserRepresentation;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PublicUserDTO {

	private String firstName;
	private String lastName;

	private String pid;
	private String displayName;

	public PublicUserDTO(UserRepresentation userRep) {
		this.firstName = userRep.getFirstName();
		this.lastName = userRep.getLastName();

		boolean userHasAttributes = userRep.getAttributes() != null && userRep.getAttributes().size() > 0;  
		
		if (userHasAttributes && userRep.getAttributes().containsKey("pid"))
			this.pid = userRep.getAttributes().get("pid").get(0);
		
		if (userHasAttributes && userRep.getAttributes().containsKey("display_name"))
			this.displayName = userRep.getAttributes().get("display_name").get(0);

	}
}
