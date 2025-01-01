package com.keycloak.course.friends_service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

public interface FriendsRepository extends CrudRepository<Friends, Long> {

	@Query(value = "SELECT f AS friend_id FROM friends f WHERE f.requesterPid = ?1 UNION SELECT f AS friend_id FROM friends f WHERE f.pid_2 = ?1;", nativeQuery = true)
	List<String> findFriendsByPID(String pid);

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO friends (requester_pid, requester_id, responder_pid, responder_id, request_confirmed)\r\n"
			+ "SELECT ?1, ?2, ?3, ?4, FALSE\r\n" + "FROM dual\r\n" + "WHERE NOT EXISTS (\r\n" + "    SELECT 1\r\n"
			+ "    FROM friends\r\n" + "    WHERE (requester_id = ?2 AND responder_id = ?4)\r\n"
			+ "       OR (requester_id = ?4 AND responder_id = ?2)\r\n" + ");", nativeQuery = true)
	void requestFriendship(String requesterPid, String requesterId, String responderPid, String responderId);

	@Query(value = "SELECT * FROM friends f WHERE f.requester_id = ?1 " + "AND f.responder_pid = ?2 "
			+ "UNION SELECT * FROM friends f WHERE f.requester_pid = ?2 "
			+ "AND responder_id = ?1;", nativeQuery = true)
	Friends getFriendshipStatus(String requesterId, String responderPid);

	@Transactional
	@Modifying
	@Query(value = "UPDATE friends set request_confirmed = true WHERE requester_pid = ?1 AND responder_id = ?2 ", nativeQuery = true)
	void acceptFriendship(String requesterPid, String responderId);

	@Query(value = "SELECT friend_id FROM (" + "SELECT f.responder_id AS friend_id "
			+ "FROM friends f WHERE f.requester_id = ?1 AND f.request_confirmed = true " + "UNION "
			+ "SELECT f.requester_id AS friend_id "
			+ "FROM friends f WHERE f.responder_id = ?1 AND f.request_confirmed = true" + ") AS derived_friends " // Alias
																													// for
																													// the
																													// subquery
			+ "ORDER BY friend_id", // Apply ordering for consistent pagination

			countQuery = "SELECT COUNT(*) FROM ("
					+ "SELECT f.responder_id FROM friends f WHERE f.requester_id = ?1 AND f.request_confirmed = true "
					+ "UNION "
					+ "SELECT f.requester_id FROM friends f WHERE f.responder_id = ?1 AND f.request_confirmed = true"
					+ ") AS derived_friends", // Alias for the subquery in count query
			nativeQuery = true)
	Page<String> getFriendIds(String callerId, Pageable pageable);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM FRIENDS F WHERE (f.requester_id = ?1 AND f.responder_pid = ?2)" + "OR"
			+ "(f.responder_id = ?1 AND f.requester_pid = ?2)", nativeQuery = true)
	void deleteFriendship(String requesterId, String responderPid);

}
