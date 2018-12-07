package cz.metacentrum.perun.spRegistration.service.exceptions;

public class SpRegistrationApiException extends Exception {

	public SpRegistrationApiException() {
		super();
	}

	public SpRegistrationApiException(String s) {
		super(s);
	}

	public SpRegistrationApiException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public SpRegistrationApiException(Throwable throwable) {
		super(throwable);
	}

	protected SpRegistrationApiException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
