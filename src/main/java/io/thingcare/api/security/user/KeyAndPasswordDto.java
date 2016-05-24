package io.thingcare.api.security.user;

import io.thingcare.api.Dto;
import lombok.Value;

@Value
public class KeyAndPasswordDto extends Dto {
	private static final long serialVersionUID = 4085970859096012461L;

	private String key;

	private String newPassword;

}
