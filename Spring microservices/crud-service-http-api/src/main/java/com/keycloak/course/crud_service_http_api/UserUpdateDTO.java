package com.keycloak.course.crud_service_http_api;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Setter;

@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateDTO {

	private String email;
	private String firstName;
	private String lastName;

	private Map<String, List<String>> attributes;

}
