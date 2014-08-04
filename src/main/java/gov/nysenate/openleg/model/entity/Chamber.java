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

    public static Chamber getValue(String value) {
        if (value != null) {
            return Chamber.valueOf(value.toUpperCase());
        }
        throw new IllegalArgumentException("Supplied value cannot be null when mapping to Chamber.");
    }
}
