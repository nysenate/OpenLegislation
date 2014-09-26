package gov.nysenate.openleg.client.view.base;

import java.util.HashMap;
import java.util.Map;

public class ViewMap<KeyType, ViewType extends ViewObject> implements ViewObject
{
    protected Map<KeyType, ViewType> viewMap;

    public ViewMap(Map<KeyType, ViewType> map) {
        if (map != null) {
            viewMap = map;
        }
        else {
            viewMap = new HashMap<>();
        }
    }

    public Map<KeyType, ViewType> getViewMap() {
        return viewMap;
    }

    public int getSize() {
        return viewMap.size();
    }
}

