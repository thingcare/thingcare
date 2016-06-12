package io.thingcare.modules.security.user.exception;


public class UserLoginAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 7943603726214255042L;

	public UserLoginAlreadyExistsException(String msg) {
		super(msg);
	}
}
