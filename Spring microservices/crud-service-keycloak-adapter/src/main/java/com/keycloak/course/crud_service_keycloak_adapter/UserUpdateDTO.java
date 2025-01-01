package com.keycloak.course.crud_service_keycloak_adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateDTO {

	private String firstName;
	private String lastName;
	private String displayName;

}
