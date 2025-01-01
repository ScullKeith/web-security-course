package com.keycloak.course.friends_service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
public class FriendsPageResponseDTO {
	
	private int pageNumber;
	private int pageSize;
	
	private boolean lastPage; 
	
	private List<PublicUserDTO> users; 
}
