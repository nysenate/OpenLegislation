package gov.nysenate.openleg.client.view.base;

public class ModelView<T> implements ViewObject
{
    private T item;

    public ModelView(T item) {
        this.item = item;
    }

    @Override
    public String getViewType() {
        return item.getClass().getSimpleName() + "-view";
    }

    public T getItem() {
        return item;
    }
}
