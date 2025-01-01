package com.keycloak.course.friends_service;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
public class KeycloakAdapterService {

	private Keycloak keycloak;

	public KeycloakAdapterService(Keycloak keycloak) {
		this.keycloak = keycloak;
	}

	private UsersResource getUsersResource() {
		return keycloak.realm("ApiRealm").users();
	}

	public String getPidById(String id) {

		UserResource user = getUsersResource().get(id);

		if (user == null) {
			// TODO: reject
		}

		return user.toRepresentation().getAttributes().get("pid").get(0);
	}

	public String getUserIdByPid(String pid) {
		List<UserRepresentation> users = getUsersResource().searchByAttributes("pid:" + pid);

		UserRepresentation userRep = null;

		if (users == null || users.size() != 1) {
			// TODO: return bad request
		}

		for (UserRepresentation u : users)
			if (u.getAttributes().get("pid").get(0).equals(pid)) {
				userRep = u;
				break;
			}
		
		String userId = userRep.getId();
		return userId;

	}

}
