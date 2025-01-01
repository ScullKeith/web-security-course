package com.keycloak.course.crud_service_keycloak_adapter;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PublicUsersPageResponseDTO {

	private int pageNumber;
	private int pageSize;

	private boolean lastPage;
	
	private List<PublicUserDTO> users;
	
}
