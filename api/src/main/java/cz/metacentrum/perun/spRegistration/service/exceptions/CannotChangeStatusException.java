package cz.metacentrum.perun.spRegistration.service.exceptions;

/**
 * Exception thrown when status of Request cannot be changed due to unmet requirements.
 *
 * @author Dominik Frantisek Bucik &lt;bucik@ics.muni.cz&gt;
 */
public class CannotChangeStatusException extends Exception {

	public CannotChangeStatusException() {
		super();
	}

	public CannotChangeStatusException(String s) {
		super(s);
	}

	public CannotChangeStatusException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public CannotChangeStatusException(Throwable throwable) {
		super(throwable);
	}

	protected CannotChangeStatusException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
