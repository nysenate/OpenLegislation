package gov.nysenate.openleg.client.view.base;

public class ModelView<T> implements ViewObject
{
    private T result;

    public ModelView(T result) {
        this.result = result;
    }

    @Override
    public String getViewType() {
        return result.getClass().getName() + "-view";
    }

    public T getResult() {
        return result;
    }
}
