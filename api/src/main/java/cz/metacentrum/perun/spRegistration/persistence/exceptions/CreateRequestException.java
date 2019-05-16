package cz.metacentrum.perun.spRegistration.persistence.exceptions;

/**
 * Exception represents that active requests exists for the given service.
 * Only one request can be active at the same time for one service.
 *
 * @author Dominik Frantisek Bucik &lt;bucik@ics.muni.cz&gt;
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
