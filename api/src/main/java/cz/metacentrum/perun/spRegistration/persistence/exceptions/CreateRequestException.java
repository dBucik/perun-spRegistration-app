package cz.metacentrum.perun.spRegistration.persistence.exceptions;

/**
 * Exception representing that active requests exist when wanted to create another request
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class CreateRequestException extends Exception {

	public CreateRequestException() {
		super();
	}

	public CreateRequestException(String s) {
		super(s);
	}

	public CreateRequestException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public CreateRequestException(Throwable throwable) {
		super(throwable);
	}

	protected CreateRequestException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
