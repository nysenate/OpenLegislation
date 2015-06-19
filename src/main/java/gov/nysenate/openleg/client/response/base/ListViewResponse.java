package gov.nysenate.openleg.client.response.base;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.dao.base.LimitOffset;

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
