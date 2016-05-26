package io.thingcare.modules.security.user;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.thingcare.modules.security.authority.Authority;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.thingcare.api.AuditingEntity;
import io.thingcare.core.config.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A user.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "security.user")
public class User extends AuditingEntity {
	private static final long serialVersionUID = -8683910611333118104L;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 50)
	private String login;

	@JsonIgnore
	@NotNull
	@Size(min = 60, max = 60)
	private String password;

	@Size(max = 50)
	@Field("first_name")
	private String firstName;

	@Size(max = 50)
	@Field("last_name")
	private String lastName;

	@Email
	@Size(max = 100)
	private String email;

	private boolean activated = false;

	@Size(min = 2, max = 5)
	@Field("lang_key")
	private String langKey;

	@Size(max = 20)
	@Field("activation_key")
	@JsonIgnore
	private String activationKey;

	@Size(max = 20)
	@Field("reset_key")
	private String resetKey;

	@Field("reset_date")
	private ZonedDateTime resetDate = null;

	@JsonIgnore
	private Set<Authority> authorities = new HashSet<>();

}
