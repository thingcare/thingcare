package io.thingcare.api.security.user;

import java.time.ZonedDateTime;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ManagedUserDto extends UserDto {
	private static final long serialVersionUID = 776119785342966103L;
	private String id;
	private ZonedDateTime createdDate;
	private String lastModifiedBy;
	private ZonedDateTime lastModifiedDate;
	@NotNull
	@Size(min = UserCommonConstants.PASSWORD_MIN_LENGTH, max = UserCommonConstants.PASSWORD_MAX_LENGTH)
	private String password;

	public ManagedUserDto(String id, String login, String password, String firstName, String lastName, String email,
			boolean activated, String langKey, Set<String> authorities, ZonedDateTime createdDate,
			String lastModifiedBy, ZonedDateTime lastModifiedDate) {
		super(login, firstName, lastName, email, activated, langKey, authorities);
		this.id = id;
		this.createdDate = createdDate;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = lastModifiedDate;
		this.password = password;
	}
}
