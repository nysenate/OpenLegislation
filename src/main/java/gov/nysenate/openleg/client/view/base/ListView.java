package gov.nysenate.openleg.client.view.base;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class ListView<ViewType> implements ViewObject
{
    protected ImmutableList<ViewType> items;

    public static <ViewType extends ViewObject> ListView<ViewType> of(List<ViewType> items) {
        return new ListView<>(items);
    }
    public static ListView<String> ofStringList(List<String> items) {
        return new ListView<>(items);
    }
    public static ListView<Integer> ofIntList(List<Integer> items) {
        return new ListView<>(items);
    }

    protected ListView() {}

    private ListView(List<ViewType> items) {
        if (items != null) {
            this.items = ImmutableList.copyOf(items);
        }
        else {
            this.items = ImmutableList.of();
        }
    }

    public ImmutableList<ViewType> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }

    @Override
    public String getViewType() {
        String listContentType = items.size() > 0 ? ViewObject.getViewTypeOf(items.get(0)) : "empty";
        return listContentType + " list";
    }
}
