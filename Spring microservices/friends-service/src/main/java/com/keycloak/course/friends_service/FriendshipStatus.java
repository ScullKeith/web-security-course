package com.keycloak.course.friends_service;

import lombok.Getter;

@Getter 
public enum FriendshipStatus {
	
	NOT_FRIENDS, PENDING_CALLER, PENDING_OTHER_USER,  FRIENDS
}
