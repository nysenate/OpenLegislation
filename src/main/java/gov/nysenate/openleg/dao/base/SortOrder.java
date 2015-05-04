package gov.nysenate.openleg.dao.base;

/**
 * A simple enumeration indicating the available sort orders.
 * NONE refers to an arbitrary ordering.
 */
public enum SortOrder
{
    ASC,
    DESC,
    NONE,
    ;

    public static SortOrder getOpposite(SortOrder order) {
        if (ASC.equals(order)) {
            return DESC;
        } else if (DESC.equals(order)) {
            return ASC;
        }
        return NONE;
    }
}
