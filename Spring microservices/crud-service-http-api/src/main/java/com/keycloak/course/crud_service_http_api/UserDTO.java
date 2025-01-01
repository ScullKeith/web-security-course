package com.keycloak.course.crud_service_http_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

	private String username;
	private String email;
	private String firstName;
	private String lastName;

	@JsonProperty("attributes")
	private void unpackAttributes(JsonNode attributes) {
		if (attributes.has("display_name")) {
			JsonNode displayNameNode = attributes.get("display_name");
			if (displayNameNode.isArray() && displayNameNode.size() > 0) {
				this.displayName = displayNameNode.get(0).asText();
			}
		}
	}
	
	private String displayName;

}
