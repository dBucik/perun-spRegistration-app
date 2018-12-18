package cz.metacentrum.perun.spRegistration.persistence.enums;

/**
 * Enum representing type of Attribute from Perun.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public enum AttributeType {

	STRING,
	BOOLEAN,
	INTEGER,
	ARRAY,
	MAP;

	/**
	 * Parse type retrieved from Perun as an Enum value.
	 * @param perunType Type from Perun as String.
	 * @return Parsed Enum value.
	 */
	public static AttributeType fromPerunType(String perunType) {
		switch (perunType) {
			case "java.lang.Integer" :
				return INTEGER;
			case "java.lang.Boolean" :
				return BOOLEAN;
			case "java.util.ArrayList" :
			case "java.lang.LargeArrayList" :
				return ARRAY;
			case "java.lang.LinkedHashMap" :
				return MAP;
			case "java.lang.String" :
			case "java.lang.LargeString":
			default:
				return STRING;
		}
	}

}
