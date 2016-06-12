package io.thingcare.api.security.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ManagedUserDto extends UserDto {

	private static final long serialVersionUID = 776119785342966103L;

	private String id;
	private ZonedDateTime createdDate;
	private String lastModifiedBy;
	private ZonedDateTime lastModifiedDate;
	@NotNull
	@Size(min = UserCommonConstants.PASSWORD_MIN_LENGTH, max = UserCommonConstants.PASSWORD_MAX_LENGTH)
	private String password;

}
