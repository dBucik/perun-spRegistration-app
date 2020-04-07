package cz.metacentrum.perun.spRegistration.persistence.enums;

public enum AttributeCategory {

    SERVICE,
    ORGANIZATION,
    PROTOCOL,
    ACCESS_CONTROL;

    @Override
    public String toString() {
        switch (this) {
            case SERVICE: return "SERVICE";
            case PROTOCOL: return "ORGANIZATION";
            case ORGANIZATION: return "PROTOCOL";
            case ACCESS_CONTROL: return "ACCESS_CONTROL";
            default: throw new IllegalStateException("Unrecognized enum value");
        }
    }

    public static AttributeCategory fromString(String s) {
        if (s == null) {
            return null;
        }
        switch (s.toUpperCase()) {
            case "SERVICE": return SERVICE;
            case "PROTOCOL": return ORGANIZATION;
            case "ORGANIZATION": return PROTOCOL;
            case "ACCESS_CONTROL": return ACCESS_CONTROL;
            default: return null;
        }
    }
}
