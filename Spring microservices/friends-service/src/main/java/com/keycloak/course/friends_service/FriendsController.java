package com.keycloak.course.friends_service;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/friends")
@RestController
public class FriendsController {

	@Autowired
	private FriendsService friendsService;

	@Autowired
	KeycloakAdapterService keycloakService;

	@Autowired
	ApiService apiService;

	@PostMapping("/{responderPid}")
	public FriendshipStatus addFriendRequest(@PathVariable("responderPid") String responderPid,
			@AuthenticationPrincipal Jwt principal) {

		String requesterId = principal.getClaim("sub");

		String requesterPid = keycloakService.getPidById(requesterId); // get from queryParam"?

		String responderId = keycloakService.getUserIdByPid(responderPid);

		friendsService.addFriendRequest(requesterPid, requesterId, responderPid, responderId);

		return FriendshipStatus.PENDING_OTHER_USER;
	}

	@GetMapping("/status/{pid}")
	public FriendshipStatus getFriendshipStatus(@PathVariable("pid") String otherUserPid,
			@AuthenticationPrincipal Jwt principal) {

		String callingUserId = principal.getClaim("sub");
		FriendshipStatus friendshipStatus = friendsService.getFriendshipStatus(callingUserId, otherUserPid);

		if (friendshipStatus == null) {
			// TODO: reject
		}

		return friendshipStatus;
	}

	@PutMapping("/{pid}")
	public FriendshipStatus acceptFriendRequest(@PathVariable("pid") String requesterPid,
			@AuthenticationPrincipal Jwt principal) {

		String respsonderId = principal.getClaim("sub");
		friendsService.acceptFriendship(requesterPid, respsonderId);

		return FriendshipStatus.FRIENDS;
	}

	@GetMapping("/list")
	public FriendsPageResponseDTO getFriends(@QueryParam("pageNumber") Integer pageNumber,
			@QueryParam("pageSize") Integer pageSize, @AuthenticationPrincipal Jwt jwt) {

		System.out.println("pageNumber and pageSize " + pageNumber + " " + pageSize);

		String callerId = jwt.getClaim("sub");
		System.out.println("callerId " + callerId);

		Page<String> friendsPage = friendsService.getFriendIds(callerId, pageNumber, pageSize);
		List<String> friendIds = friendsPage.getContent();

		if (friendIds == null || friendIds.size() <= 0)
			return new FriendsPageResponseDTO(0, 0, true, Collections.EMPTY_LIST);

		System.out.println("friendIds " + friendIds.size());

		List<PublicUserDTO> friendsList = apiService.getFriends(friendIds);

		FriendsPageResponseDTO friends = new FriendsPageResponseDTO(friendsPage.getNumber(), friendsPage.getSize(),
				friendsPage.getTotalPages() == pageNumber + 1, friendsList);

		return friends;
	}

	@DeleteMapping("/{responderPid}")
	public FriendshipStatus deleteFriendship(@PathVariable("responderPid") String responderPid,
			@AuthenticationPrincipal Jwt principal) {

		String requesterId = principal.getClaim("sub");

		friendsService.deleteFriendship(requesterId, responderPid);
		return FriendshipStatus.NOT_FRIENDS;
	}

}