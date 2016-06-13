package io.thingcare.modules.security.user.exception;

public class UserEmailAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 5866773467989588365L;

	public UserEmailAlreadyExistsException(String msg) {
		super(msg);
	}
}
