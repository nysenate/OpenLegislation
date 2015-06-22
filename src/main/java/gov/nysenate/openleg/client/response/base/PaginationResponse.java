package gov.nysenate.openleg.client.response.base;

import gov.nysenate.openleg.dao.base.LimitOffset;

public abstract class PaginationResponse extends BaseResponse
{
    protected int total;
    protected int offsetStart;
    protected int offsetEnd;
    protected int limit;

    public PaginationResponse(int total, int offsetStart, int offsetEnd, int limit) {
        this.total = total;
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
        this.limit = limit;
    }

    public PaginationResponse(int total, LimitOffset limitOffset) {
        this(total, limitOffset.getOffsetStart(), Math.min(limitOffset.getOffsetEnd(), total), limitOffset.getLimit());
    }

    public int getTotal() {
        return total;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public int getLimit() {
        return limit;
    }
}
