package com.keycloak.course.friends_service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {

	private AdminTokenService adminService;

	private RestTemplate restTemplate;

	public ApiService(AdminTokenService adminService) {
		this.adminService = adminService;
		this.restTemplate = new RestTemplate();
	}

	private HttpHeaders getHeaders() {

		HttpHeaders headers = new HttpHeaders();

		// Setting the token as an HTTP-only cookie named "access_token"
		String tokenValue = adminService.getToken(); // Get your token
		headers.add(HttpHeaders.COOKIE, "access_token=" + tokenValue);

		System.out.println("ACccess token for api-backend " + tokenValue);

		headers.setContentType(MediaType.APPLICATION_JSON);

		return headers;
	}

	public List<PublicUserDTO> getFriends(List<String> friendIds) {

		HttpHeaders headers = getHeaders();

		IdsRequest request = new IdsRequest();
		request.setFriendPids(friendIds);
		HttpEntity<IdsRequest> requestEntity = new HttpEntity<IdsRequest>(request, headers);
		
		String url = "https://localhost:8443/realms/ApiRealm/custom-user-query-endpoint-V1.1/friends/list";

		ResponseEntity<List<PublicUserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<List<PublicUserDTO>>() {
				});

		List<PublicUserDTO> friends = responseEntity.getBody();

		return friends;
	}

	// Request body class for JSON
	public static class IdsRequest {
		private List<String> friendPids;

		public List<String> getFriendPids() {
			return friendPids;
		}

		public void setFriendPids(List<String> friendPids) {
			this.friendPids = friendPids;
		}
	}
}
