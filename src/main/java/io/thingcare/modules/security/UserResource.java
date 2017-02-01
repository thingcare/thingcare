package io.thingcare.modules.security;

import io.thingcare.api.security.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@Slf4j
@RestController
@RequestMapping("/api")
public class UserResource {

	@RequestMapping(value = "/currentUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDto> currentUser(Principal principal)  {
		AccessToken token = ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getToken();
		return new ResponseEntity<>(UserDto.builder().login(token.getPreferredUsername()).firstName(token.getGivenName()).lastName(token.getFamilyName()).email(token.getEmail()).build(),HttpStatus.OK);
	}
}
