package com.keycloak.course.crud_service_keycloak_adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KeycloakAdapterService {

	private Keycloak keycloak;

	public KeycloakAdapterService(Keycloak keycloak) {
		this.keycloak = keycloak;
	}

	private UsersResource getUsersResource() {
		return keycloak.realm("ApiRealm").users(); // does not immediately load all users from the database
	}

	public PrivateUserDTO getPrivateUser(String userId) {

		PrivateUserDTO userDto;

		// Retrieve user by userId
		UserRepresentation matchingUser = getUsersResource().get(userId).toRepresentation();

		if (matchingUser == null)
			throw new IllegalArgumentException("No user found for specified user id");

		userDto = new PrivateUserDTO(matchingUser);

		return userDto;
	}

	private UserRepresentation getUserByPidAttribute(String pid) {
		// get user by custom attribute, "pid"
		// List to hold the users matching the given PID

		UserRepresentation userRep = null;

		try {
			// Fetch all users (or you can specify a limit if needed)
			List<UserRepresentation> users = getUsersResource().searchByAttributes("pid:" + pid);
			if (users == null || users.size() <= 0) {
				return null;
			}

			/*
			 * Sometimes, results returned by "searchByAttributes" method may be using
			 * "contains" regex rather than "matches." Therefore, narrow results down to
			 * matches by iterating through results and finding the user with exact/matching
			 * attr. value:
			 */

			for (UserRepresentation u : users)
				if (u.getAttributes().get("pid").get(0).equals(pid)) {
					userRep = u;
					break;
				}

			if (userRep == null) {
				// todo handle null scenario
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return userRep;
	}

	public PublicUserDTO getPublicUser(String pid) {

		try {

			UserRepresentation userRep = getUserByPidAttribute(pid);
			PublicUserDTO user = new PublicUserDTO(userRep);

			return user;

		} catch (

		Exception e) {
			throw new RuntimeException("Error retrieving user by PID", e);
		}
	}

	public PublicUsersPageResponseDTO getUsers(int pageNumber, int pageSize, String userId) {

		UsersResource usersResource = getUsersResource();

		int usersSize = usersResource.count();
		int totalPages = usersSize / pageSize;
		System.out.println("usersSize " + usersSize);
		System.out.println("totalPages " + totalPages);

		List<UserRepresentation> userRepresentations = usersResource.list(pageNumber, pageSize);
		userRepresentations.removeIf(user -> user.getId().equals(userId));

		List<PublicUserDTO> users = userRepresentations.stream().map(PublicUserDTO::new).collect(Collectors.toList());

		PublicUsersPageResponseDTO page = new PublicUsersPageResponseDTO(pageNumber, pageSize,
				totalPages <= pageNumber + 1, users);

		return page;
	}

	public UserRepresentation updateUser(UserUpdateDTO userUpdateDTO, String userId) {

		UserRepresentation userInDB = null;

		try {

			UserResource usersResource = getUsersResource().get(userId);
			userInDB = usersResource.toRepresentation();

			ObjectMapper objectMapper = new ObjectMapper();

			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			// allow users to update only some data but not other
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			objectMapper.updateValue(userInDB, userUpdateDTO);

			Map<String, List<String>> attributesUpdate = new HashMap<>();
			attributesUpdate.putAll(userInDB.getAttributes());

			if (userUpdateDTO.getDisplayName() != null && !userUpdateDTO.getDisplayName().isEmpty()) {
				attributesUpdate.get("display_name").remove(0);
				attributesUpdate.put("display_name", List.of(userUpdateDTO.getDisplayName()));
			}

			userInDB.setAttributes(attributesUpdate);

			usersResource.update(userInDB);

		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInDB;
	}

	public void updateUserUsingPid(UserUpdateDTO userUpdateDTO, String pid) {

		// get user by custom attribute, "pid"
		try {
			UserRepresentation userInDB = getUserByPidAttribute(pid);

			ObjectMapper objectMapper = new ObjectMapper();

			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			// allow users to update only some data but not other
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			objectMapper.updateValue(userInDB, userUpdateDTO);

			Map<String, List<String>> attributesUpdate = new HashMap<>();
			attributesUpdate.putAll(userInDB.getAttributes());

			if (userUpdateDTO.getDisplayName() != null && !userUpdateDTO.getDisplayName().isEmpty()) {
				attributesUpdate.get("display_name").remove(0);
				attributesUpdate.put("display_name", List.of(userUpdateDTO.getDisplayName()));
			}

			userInDB.setAttributes(attributesUpdate);

			UserResource userResource = getUsersResource().get(userInDB.getId());
			userResource.update(userInDB);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
