package gov.nysenate.openleg.client.view.base;

import java.util.HashMap;
import java.util.Map;

public class ViewMap<KeyType, ViewType extends ViewObject> implements ViewObject
{
    protected Map<KeyType, ViewType> items;

    public ViewMap(Map<KeyType, ViewType> map) {
        if (map != null) {
            items = map;
        }
        else {
            items = new HashMap<>();
        }
    }

    public Map<KeyType, ViewType> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }
}

