package gov.nysenate.openleg.dao.base;

import com.google.common.collect.ImmutableMap;

/**
 * OrderBy associates column names with a sort order. This should be used within
 * the dao layer to append order by clauses to sql queries.
 */
public class OrderBy
{
    private static OrderBy NO_ORDER = new OrderBy();

    /** An immutable mapping of column names to sort order. */
    private ImmutableMap<String, SortOrder> sortColumns = ImmutableMap.of();

    /** --- Constructors --- */

    public OrderBy() {}

    public OrderBy(ImmutableMap<String, SortOrder> sortColumns) {
        this.sortColumns = sortColumns;
    }

    public OrderBy(String k1, SortOrder v1) {
        this.sortColumns = ImmutableMap.of(k1, v1);
    }

    public OrderBy(String k1, SortOrder v1, String k2, SortOrder v2) {
        this.sortColumns = ImmutableMap.of(k1, v1, k2, v2);
    }

    public OrderBy(String k1, SortOrder v1, String k2, SortOrder v2, String k3, SortOrder v3) {
        this.sortColumns = ImmutableMap.of(k1, v1, k2, v2, k3, v3);
    }

    /** --- Basic Getters/Setters --- */

    public ImmutableMap<String, SortOrder> getSortColumns() {
        return sortColumns;
    }
}
