package com.keycloak.course.endpoint.friends.list;

import java.security.Key;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.crypto.KeyUse;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.models.KeyManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resource.RealmResourceProvider;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class FriendsListQueryEndpointProvider implements RealmResourceProvider {

	private final KeycloakSession session;

	public FriendsListQueryEndpointProvider(KeycloakSession session) {
		this.session = session;
	}

	@POST
	@Path("/friends/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFriendsByPids(@Context HttpHeaders httpHeaders, IdsRequest idsRequest) {

		// Get cookies from the HttpHeaders
		Map<String, Cookie> cookies = httpHeaders.getCookies();
		Cookie token = cookies.get("access_token");

		if (token == null)
			return Response.status(Response.Status.UNAUTHORIZED).entity("JWT cookie not found").build();

		String tokenValue = token.getValue();

		if (!validateToken(tokenValue))
			return Response.status(Response.Status.UNAUTHORIZED).entity("Token invalid").build();

		List<String> friendPids = idsRequest.getFriendPids();

		if (friendPids == null || friendPids.isEmpty()) {
			System.out.println("friendPids invalid");
			return Response.status(Response.Status.BAD_REQUEST).entity("No user IDs provided").build();
		}

		// MySQL connection details (use proper environment variables in production)
		String jdbcUrl = "jdbc:mysql://keycloak_mysql_db:3306/keycloak_mysql_db_2";
		String dbUser = "scullkeith"; // Replace with your DB user
		String dbPassword = "password"; // Replace with your DB password

		String sql = "SELECT UA1.VALUE AS DISPLAY_NAME, UA2.VALUE AS PID, U.FIRST_NAME, U.LAST_NAME "
				+ "FROM USER_ENTITY U "
				+ "INNER JOIN USER_ATTRIBUTE UA1 ON U.ID = UA1.USER_ID AND UA1.NAME = 'display_name' "
				+ "INNER JOIN USER_ATTRIBUTE UA2 ON U.ID = UA2.USER_ID AND UA2.NAME = 'pid' " + "WHERE U.ID IN ("
				+ String.join(",", friendPids.stream().map(id -> "?").toArray(String[]::new)) + ");";

		try {
			try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
					PreparedStatement statement = connection.prepareStatement(sql)) {

				for (int i = 0; i < friendPids.size(); i++) {
					statement.setString(i + 1, friendPids.get(i));
				}

				ResultSet resultSet = statement.executeQuery();

				List<PublicUserDTO> users = new ArrayList<>();

				while (resultSet.next()) {
					PublicUserDTO user = new PublicUserDTO();
					user.setFirstName(resultSet.getString("FIRST_NAME"));
					user.setPid(resultSet.getString("PID"));
					user.setLastName(resultSet.getString("LAST_NAME"));
					user.setDisplayName(resultSet.getString("DISPLAY_NAME"));
					users.add(user);
				}

				return Response.ok(users).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + e.getMessage())
					.build();
		}
	}

	private boolean tokenIssuedViaApiBackend(AccessToken accessToken) {

		Map<String, Object> otherClaims = accessToken.getOtherClaims();

		if (otherClaims.containsKey("client_id")) {

			String clientId = otherClaims.get("client_id").toString();
			if (clientId.equals("api-backend"))
				return true; // caller has access to api-backend (secret key via client credentials flow)
								// This also ensures that no random user with token from 'api-frontend' can
								// reach this end-point directly

			return false;
		} else {
			// token doesn't have client_id claim
			return false;
		}
	}

	private boolean validateToken(String token) {

		try {
			KeyManager keyManager = session.keys();
			KeyWrapper keyWrapper = keyManager.getActiveKey(session.getContext().getRealm(), KeyUse.SIG, "RS256");

			Key key = keyWrapper.getPublicKey();

			if (key instanceof PublicKey) {

				PublicKey publicKey = (PublicKey) key;

				TokenVerifier<AccessToken> verifier = TokenVerifier.create(token, AccessToken.class)
						.publicKey(publicKey);

				AccessToken accessToken = verifier.verify().getToken();
				System.out.println("Token is valid. Claims: " + accessToken.getOtherClaims());

				if (tokenIssuedViaApiBackend(accessToken))
					return true;
			}

		} catch (VerificationException e) {
			System.err.println("Token validation failed: " + e.getMessage());
			return false;
		}
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public Object getResource() {
		return this;
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
