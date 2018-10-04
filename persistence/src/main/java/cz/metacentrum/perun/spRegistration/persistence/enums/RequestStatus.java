package cz.metacentrum.perun.spRegistration.persistence.enums;

/**
 * Status of the request.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public enum RequestStatus {

	NEW,
	APPROVED,
	REJECTED,
	WFA,
	WFC,
	CANCELED;

	/**
	 * Convert Enum value to INTEGER representation (used in DB).
	 * @return Positive Int value [1-6], -1 in case of error.
	 */
	public int getAsInt() {
		switch (this) {
			case NEW: return 1;
			case APPROVED: return 2;
			case REJECTED: return 3;
			case WFA: return 4;
			case WFC: return 5;
			case CANCELED: return 6;
		}

		return -1;
	}

	/**
	 * Convert INTEGER value (from DB) to ENUM value.
	 * @param i value to be converted (accepted values are in range [1-6])
	 * @return Enum value, null n case of error.
	 */
	public static RequestStatus resolve(int i) {
		switch (i) {
			case 1: return NEW;
			case 2: return APPROVED;
			case 3: return REJECTED;
			case 4: return WFA;
			case 5: return WFC;
			case 6: return CANCELED;
		}

		return null;
	}
}
