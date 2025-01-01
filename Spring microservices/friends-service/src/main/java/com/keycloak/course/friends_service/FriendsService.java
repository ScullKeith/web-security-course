package com.keycloak.course.friends_service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FriendsService {

	private FriendsRepository friendsRepo;

	public FriendsService(FriendsRepository friendsRepo) {
		this.friendsRepo = friendsRepo;
	}

	public void addFriendRequest(String requesterPid, String requesterId, String responderPid, String responderId) {
		friendsRepo.requestFriendship(requesterPid, requesterId, responderPid, responderId);
	}

	public FriendshipStatus getFriendshipStatus(String callerId, String otherUserPid) {

		Friends friendsResponse = friendsRepo.getFriendshipStatus(callerId, otherUserPid);

		if (friendsResponse == null) {
			// Not friends
			return FriendshipStatus.NOT_FRIENDS;
		}

		if (friendsResponse.getRequestConfirmed()) {
			// Friends
			return FriendshipStatus.FRIENDS;
		}

		if (friendsResponse.getRequesterId().equals(callerId)) {
			// calling user is requester
			return FriendshipStatus.PENDING_OTHER_USER;
		}

		if (friendsResponse.getRequesterPid().equals(otherUserPid)) {
			// calling user is responder
			return FriendshipStatus.PENDING_CALLER;
		}
		return null;
	}

	public void acceptFriendship(String requesterPid, String responderId) {
		System.out.println("acceptFriendship\nrequesterPid: " + requesterPid + "\nresponderId: " + responderId);
		friendsRepo.acceptFriendship(requesterPid, responderId);
	}

	public List<Friends> getFriends(String pid) {
		return null;
	}

	public Page<String> getFriendIds(String callerId, Integer pageNumber, Integer pageSize) {

		if (pageNumber == null)
			pageNumber = 0;

		if (pageSize == null || pageSize == 0)
			pageSize = 1;

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		Page<String> friendIdsPage = friendsRepo.getFriendIds(callerId, pageable);

		return friendIdsPage;
	}

	public void deleteFriendship(String requesterId, String responderPid) {
		System.out.println("Delting where requster id : " + requesterId + " and responderpid: " + responderPid);
		friendsRepo.deleteFriendship(requesterId, responderPid);
	}
}
