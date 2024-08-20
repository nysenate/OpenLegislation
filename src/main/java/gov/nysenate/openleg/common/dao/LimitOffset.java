package gov.nysenate.openleg.common.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for limiting the number of resultList returned by query methods.
 */
public record LimitOffset(int limit, int offsetStart) {
    /** Use this reference when no limit is desired. */
    public static final LimitOffset ALL = new LimitOffset(0),

    /** Some references for convenience. */
            ONE = new LimitOffset(1), TEN = new LimitOffset(10),
            TWENTY_FIVE = new LimitOffset(25), FIFTY = new LimitOffset(50),
            HUNDRED = new LimitOffset(100), THOUSAND = new LimitOffset(1000);

    /** --- Constructors --- */

    public LimitOffset(int limit) {
        this(limit, 1);
    }

    public LimitOffset(int limit, int offsetStart) {
        this.limit = limit;
        this.offsetStart = Math.max(offsetStart, 1);
    }

    /* --- Methods --- */

    /**
     * If the 'limitOffset' is valid, return a new sub-list according to the given 'limitOffset'.
     *
     * @param list List<T> - The original list.
     * @param limOff LimitOffset - The limit/offset to trim the list to.
     * @return List<T> - The trimmed list.
     */
    public static <T> List<T> limitList(List<T> list, LimitOffset limOff) {
        if (limOff != null && limOff.hasLimit()) {
            int start = limOff.offsetStart() - 1;
            int end = start + limOff.limit();
            end = Math.min(end, list.size());
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
        return new LimitOffset(this.limit(), nextOffset);
    }

    /** --- Functional Getters/Setters --- */

    public boolean hasLimit() {
        return this.limit > 0;
    }

    public boolean hasOffset() {
        return this.offsetStart > 1;
    }

    public int getOffsetEnd() {
        return hasLimit() ?
                this.limit + this.offsetStart - 1 : Integer.MAX_VALUE;
    }
}