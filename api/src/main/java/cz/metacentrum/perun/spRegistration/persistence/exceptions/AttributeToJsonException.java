package cz.metacentrum.perun.spRegistration.persistence.exceptions;

/**
 * Exception representing problem while parsing Perun attribute to/from JSON format.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class AttributeToJsonException extends RuntimeException {

	public AttributeToJsonException() {
		super();
	}

	public AttributeToJsonException(String s) {
		super(s);
	}

	public AttributeToJsonException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public AttributeToJsonException(Throwable throwable) {
		super(throwable);
	}

	protected AttributeToJsonException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
