package com.keycloak.course.crud_service_keycloak_adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(Jwt source) {

		List<String> claimRoles = source.getClaim("roles");

		if (claimRoles == null)
			return new ArrayList<GrantedAuthority>();

		claimRoles.forEach(r -> System.out.println("[KeycloakRoleConverter]: " + r));

		List<GrantedAuthority> userRoles = claimRoles.stream().map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return userRoles;
	}

}
