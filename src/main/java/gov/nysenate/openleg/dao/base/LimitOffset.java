package gov.nysenate.openleg.dao.base;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * The LimitOffset class is intended to be used for limiting the number of results
 * returned by query methods.
 */
public class LimitOffset
{
    /** Use this reference when no limit is desired. */
    public static final LimitOffset ALL = new LimitOffset(0,0);

    /** Some references for convenience. */
    public static final LimitOffset ONE = new LimitOffset(1);
    public static final LimitOffset TEN = new LimitOffset(10);
    public static final LimitOffset TWENTY_FIVE = new LimitOffset(25);
    public static final LimitOffset FIFTY = new LimitOffset(50);
    public static final LimitOffset HUNDRED = new LimitOffset(100);
    public static final LimitOffset THOUSAND = new LimitOffset(1000);

    /** Number of elements to limit the result set to. */
    private final int limit;

    /** The offset position used in conjunction with the limit. The offset starts from 1
     *  which is the same as not offsetting the results. */
    private final int offset;

    /** --- Constructors --- */

    public LimitOffset(int limit) {
        this(limit, 1);
    }

    public LimitOffset(int limit, int offset) {
        this.limit = limit;
        this.offset = (offset > 1) ? offset : 1;
    }

    /** --- Methods --- */

    /**
     * If the 'limitOffset' is valid, return a new sub-list according to the given 'limitOffset'.
     *
     * @param list List<T> - The original list.
     * @param limOff LimitOffset - The limit/offset to trim the list to.
     * @return List<T> - The trimmed list.
     */
    public static <T> List<T> limitList(List<T> list, LimitOffset limOff) {
        if (limOff != null && limOff.hasLimit()) {
            int start = limOff.getOffsetStart() - 1;
            int end = start + limOff.getLimit();
            end = (end > list.size()) ? list.size() : end;
            return new ArrayList<>(list.subList(start, end));
        }
        return list;
    }

    /**
     * Using the given LimitOffset object, return a new instance that has it's offset value set as
     * directly after the end of given LimitOffset. This method can be used for pagination such that
     * the offset does not have to be manually incremented by the limit each time.
     *
     * @return LimitOffset
     */
    public LimitOffset next() {
        int nextOffset = this.getOffsetEnd();
        nextOffset = (nextOffset == Integer.MAX_VALUE) ? Integer.MAX_VALUE : nextOffset + 1;
        return new LimitOffset(this.getLimit(), nextOffset);
    }

    /** --- Functional Getters/Setters --- */

    public boolean hasLimit() {
        return (this.limit > 0);
    }

    public boolean hasOffset() {
        return (this.offset > 1);
    }

    public int getOffsetEnd() {
        if (!hasLimit()) return Integer.MAX_VALUE;
        return this.limit + this.offset - 1;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LimitOffset)) return false;
        LimitOffset that = (LimitOffset) o;
        if (limit != that.limit) return false;
        if (offset != that.offset) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = limit;
        result = 31 * result + offset;
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("limit", limit)
                .append("offset", offset)
                .toString();
    }

    /** --- Basic Getters/Setters --- */

    public int getLimit() {
        return limit;
    }

    public int getOffsetStart() {
        return offset;
    }
}