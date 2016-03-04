package gov.nysenate.openleg.dao.base;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The paginated list is a wrapper for associating lists with a total count and
 * the current limit offset value. This is useful for database result set pagination.
 * @param <T> The type of the elements within the stored list.
 */
public class PaginatedList<T>
{
    protected int total;
    protected LimitOffset limOff;
    protected List<T> results;

    /** --- Constructors --- */

    public PaginatedList(int total, LimitOffset limOff, List<T> results) {
        this.total = total;
        this.limOff = limOff;
        this.results = results;
    }

    /** --- Functional Getters --- */

    public Stream<T> stream() {
        return Optional.ofNullable(results)
                .orElse(Collections.emptyList())
                .stream();
    }

    /** --- Basic Getters --- */

    public int getTotal() {
        return total;
    }

    public LimitOffset getLimOff() {
        return limOff;
    }

    public List<T> getResults() {
        return results;
    }
}
