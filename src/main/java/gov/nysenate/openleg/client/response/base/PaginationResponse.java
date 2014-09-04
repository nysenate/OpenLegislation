package gov.nysenate.openleg.client.response.base;

public class PaginationResponse extends BaseResponse
{
    protected int total;
    protected int offsetStart;
    protected int offsetEnd;
    protected int count;

    public int getTotal() {
        return total;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public int getCount() {
        return count;
    }
}
