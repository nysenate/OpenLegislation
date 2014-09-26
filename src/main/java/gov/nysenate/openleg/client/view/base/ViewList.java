package gov.nysenate.openleg.client.view.base;

import java.util.ArrayList;
import java.util.List;

public class ViewList<ViewType extends ViewObject> implements ViewObject
{
    protected List<ViewType> items;

    public ViewList(List<ViewType> items) {
        if (items != null) {
            this.items = items;
        }
        else {
            this.items = new ArrayList<>();
        }
    }

    public List<ViewType> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }
}
