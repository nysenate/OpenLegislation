package gov.nysenate.openleg.client.view.environment;

import gov.nysenate.openleg.client.view.base.ViewObject;

public class EnvironmentVariableView implements ViewObject {

    private String name;
    private Object value;
    private boolean mutable;

    public EnvironmentVariableView(String name, Object value, boolean mutable) {
        this.name = name;
        this.value = value;
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

    @Override
    public String getViewType() {
        return "environment variable";
    }
}