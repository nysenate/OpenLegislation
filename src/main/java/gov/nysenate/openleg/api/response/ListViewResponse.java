package gov.nysenate.openleg.api.response;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.common.dao.LimitOffset;

import java.util.List;

public class ListViewResponse<ViewType> extends PaginationResponse
{
    protected ListView<ViewType> result;

    protected ListViewResponse(ListView<ViewType> result, int total, LimitOffset limitOffset) {
        super(total, limitOffset);
        this.result = result;
        if (result != null) {
            success = true;
            this.responseType = result.getViewType();
        }
    }

    protected ListViewResponse(ListView<ViewType> result) {
        this(result, result.getSize(), LimitOffset.ALL);
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(List<ViewType> items) {
        return new ListViewResponse<>(ListView.of(items));
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(List<ViewType> items, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.of(items), total, limitOffset);
    }

    public static ListViewResponse<String> ofStringList(List<String> items, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.ofStringList(items), total, limitOffset);
    }

    public static ListViewResponse<Integer> ofIntList(List<Integer> items, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.ofIntList(items), total, limitOffset);
    }

    public ListView<ViewType> getResult() {
        return result;
    }
}
