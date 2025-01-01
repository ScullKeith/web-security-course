package com.keycloak.course.friends_service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "friends")
public class Friends {
	
	public Friends(String requesterPid, String requesterId, String responderPid, String responderId) {
		
		this.requesterPid = requesterPid;
		this.requesterId = requesterId;
		this.responderPid = responderPid; 
		this.responderId = responderId;
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "requester_pid")
	private String requesterPid;

	@Column(name = "requester_id")
	private String requesterId;

	@Column(name = "responder_pid")
	private String responderPid;

	@Column(name = "responder_id")
	private String responderId;
	
	@Column(name = "request_confirmed")
	private Boolean requestConfirmed;
}
