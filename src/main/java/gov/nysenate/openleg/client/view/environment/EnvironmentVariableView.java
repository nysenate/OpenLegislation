package gov.nysenate.openleg.client.view.environment;

public class EnvironmentVariableView extends SimpleEnvironmentVariableView {

    private String type;
    private boolean mutable;

    public EnvironmentVariableView(String name, Object value, Class<?> type, boolean mutable) {
        super(name, value);
        this.type = type != null ? type.getSimpleName() : null;
        this.mutable = mutable;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isMutable() {
        return mutable;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getViewType() {
        return "environment variable";
    }
}