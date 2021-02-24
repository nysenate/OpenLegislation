package gov.nysenate.openleg.processors.law;

/**
 * A simple enum to represent methods that come along with LawDocuments.
 */
public enum LawMethod {
    MASTER, REPEAL, DELETE, UPDATE, UNKNOWN;

    public static LawMethod stringToMethod(String method) {
        if (method.isEmpty())
            return UPDATE;
        try {
            return valueOf(method.replace("*", ""));
        }
        catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
