package gov.nysenate.openleg.client.response.base;

import gov.nysenate.openleg.client.view.base.ViewList;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.dao.base.LimitOffset;

import java.util.List;

public class ViewListResponse<ViewType extends ViewObject> extends PaginationResponse
{
    protected ViewList<ViewType> result;

    public ViewListResponse(ViewList<ViewType> result, int total, int offsetStart, int offsetEnd, int count) {
        super(total, offsetStart, offsetEnd, count);
        this.result = result;
        if (result != null) {
            success = true;
        }
    }

    public ViewListResponse(List<ViewType> result, int total, LimitOffset limitOffset) {
        this(new ViewList<>(result), total,
                limitOffset.getOffsetStart(), limitOffset.getOffsetEnd(), limitOffset.getLimit());
    }

    public ViewList<ViewType> getResult() {
        return result;
    }
}
