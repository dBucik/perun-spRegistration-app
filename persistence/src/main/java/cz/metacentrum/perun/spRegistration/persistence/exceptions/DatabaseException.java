package cz.metacentrum.perun.spRegistration.persistence.exceptions;

/**
 * Exception represents problem while working with database.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class DatabaseException extends RuntimeException {

	public DatabaseException() {
		super();
	}

	public DatabaseException(String s) {
		super(s);
	}

	public DatabaseException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public DatabaseException(Throwable throwable) {
		super(throwable);
	}

	protected DatabaseException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
