package gov.nysenate.openleg.model.entity;

/**
 * Enumeration of the different legislative chambers.
 */
public enum Chamber
{
    SENATE,
    ASSEMBLY;

    public String asSqlEnum() {
        return this.toString().toLowerCase();
    }

    /**
     * Returns the chamber that is opposite to this one.
     */
    public Chamber opposite() {
        if (this.equals(SENATE)) return ASSEMBLY;
        return SENATE;
    }

    public static Chamber getValue(String value) {
        if (value != null) {
            return Chamber.valueOf(value.toUpperCase());
        }
        throw new IllegalArgumentException("Supplied value cannot be null when mapping to Chamber.");
    }
}
