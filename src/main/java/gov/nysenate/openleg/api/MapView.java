package gov.nysenate.openleg.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapView<KeyType, ViewType> implements ViewObject {
    protected ImmutableMap<KeyType, ViewType> items;

    public static <KeyType, ViewType extends ViewObject> MapView<KeyType, ViewType> of(Map<KeyType, ViewType> items) {
        return new MapView<>(items);
    }

    public static <KeyType> MapView<KeyType, Integer> ofIntMap(Map<KeyType, Integer> items) {
        return new MapView<>(items);
    }

    protected MapView() {}

    public MapView(Map<KeyType, ViewType> map) {
        if (map != null) {
            items = ImmutableMap.copyOf(map);
        }
        else {
            items = ImmutableMap.of();
        }
    }

    public Map<KeyType, ViewType> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }

    @Override
    public String getViewType() {
        if (items.isEmpty()) {
            return "empty map";
        }
        String keyViewType = ViewObject.getViewTypeOf(items.keySet().iterator().next());
        String valueViewType = ViewObject.getViewTypeOf(items.values().iterator().next());
        return keyViewType + "," + valueViewType + " map";
    }
}
