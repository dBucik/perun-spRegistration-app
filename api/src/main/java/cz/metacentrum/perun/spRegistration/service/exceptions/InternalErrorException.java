package cz.metacentrum.perun.spRegistration.service.exceptions;

public class InternalErrorException extends Exception {

	public InternalErrorException() {
		super();
	}

	public InternalErrorException(String s) {
		super(s);
	}

	public InternalErrorException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public InternalErrorException(Throwable throwable) {
		super(throwable);
	}

	protected InternalErrorException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
