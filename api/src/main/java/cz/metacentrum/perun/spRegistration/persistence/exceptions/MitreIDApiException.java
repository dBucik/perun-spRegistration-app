package cz.metacentrum.perun.spRegistration.persistence.exceptions;

/**
 * Exception represents problem while working with MitreID API.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class MitreIDApiException extends Exception {

	public MitreIDApiException() {
		super();
	}

	public MitreIDApiException(String s) {
		super(s);
	}

	public MitreIDApiException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public MitreIDApiException(Throwable throwable) {
		super(throwable);
	}

	protected MitreIDApiException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
