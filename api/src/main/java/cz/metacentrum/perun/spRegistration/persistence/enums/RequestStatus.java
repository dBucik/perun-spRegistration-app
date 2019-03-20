package cz.metacentrum.perun.spRegistration.persistence.enums;

/**
 * Status of the request.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public enum RequestStatus {

	APPROVED,
	REJECTED,
	WFA,
	WFC;

	/**
	 * Convert Enum value to INTEGER representation (used in DB).
	 * @return Positive Int value [1-4], -1 in case of error.
	 */
	public int getAsInt() {
		switch (this) {
			case APPROVED: return 1;
			case REJECTED: return 2;
			case WFA: return 3;
			case WFC: return 4;
			default: return -1;
		}
	}

	/**
	 * Convert INTEGER value (from DB) to ENUM value.
	 * @param i value to be converted (accepted values are in range [1-4])
	 * @return Enum value, null n case of error.
	 */
	public static RequestStatus resolve(int i) {
		switch (i) {
			case 1: return APPROVED;
			case 2: return REJECTED;
			case 3: return WFA;
			case 4: return WFC;
			default: return null;
		}
	}
}
