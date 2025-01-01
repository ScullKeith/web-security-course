package com.keycloak.course.listener.deletion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class UserDeletionListenerProvider implements EventListenerProvider {

	private KeycloakSession session;

	@Override
	public void close() {
	}

	public UserDeletionListenerProvider(KeycloakSession session) {
		this.session = session;
	}

	@Override
	public void onEvent(Event event) {
		System.out.println("on event userldeletionlistenerprovider");
	}
	
	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {

		OperationType operation = event.getOperationType();
		System.out.println("Admin operation type: " + operation.name());

		if (operation == OperationType.DELETE) {
			// remove from friends db as well to prevent stale DB table rows
			
			String jdbcUrl = "jdbc:mysql://friends_mysql_db:3306/friends_db";
			String dbUser = "scullkeith";
			String dbPassword = "password";

			// to allow us to execute deletion query without using where cause which refers
			// to primary key column
			String sql_safe_update_disable = "set sql_safe_updates = 0;";
			String sql_delete_friend = "DELETE FROM friends WHERE requester_id = ? OR responder_id = ?;";

			// Declare connection outside of try-with-resources to use rollback if needed
			try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
				// Start a transaction
				connection.setAutoCommit(false); // Disable autocommit for transaction

				try (PreparedStatement statementSafeUpdate = connection.prepareStatement(sql_safe_update_disable);
						PreparedStatement statementDeleteFriend = connection.prepareStatement(sql_delete_friend)) {

					// Disable safe updates (this is the first query in the transaction)
					statementSafeUpdate.executeUpdate();

					// Get user and perform deletion (second query in the transaction)
					String userId = event.getResourcePath().replace("users/", "");
					System.out.println("userId after replace? " + userId);
					
					statementDeleteFriend.setString(1, userId);
					statementDeleteFriend.setString(2, userId);

					statementDeleteFriend.executeUpdate();

					// Commit the transaction if everything was successful
					connection.commit();

				} catch (SQLException e) {
					// Rollback if any exception occurs
					connection.rollback();
					e.printStackTrace(); // Log the error
				}
			} catch (SQLException e) {
				// Handle connection-level exceptions
				e.printStackTrace();

			}

		}
	}

}
