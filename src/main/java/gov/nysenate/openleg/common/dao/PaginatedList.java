package gov.nysenate.openleg.common.dao;

import java.util.List;

/**
 * The paginated list is a wrapper for associating lists with a total count and
 * the current limit offset value. This is useful for database result set pagination.
 *
 * @param <T> The type of the elements within the stored list.
 */
public record PaginatedList<T>(int total, LimitOffset limOff, List<T> results) {}
