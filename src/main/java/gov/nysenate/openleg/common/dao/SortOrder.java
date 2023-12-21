package gov.nysenate.openleg.common.dao;

/**
 * A simple enumeration indicating the available sort orders.
 * NONE refers to an arbitrary ordering.
 */
public enum SortOrder {
    ASC, DESC, NONE;

    public static SortOrder getOpposite(SortOrder order) {
        if (ASC == order)
            return DESC;
        else if (DESC == order)
            return ASC;
        return NONE;
    }
}
