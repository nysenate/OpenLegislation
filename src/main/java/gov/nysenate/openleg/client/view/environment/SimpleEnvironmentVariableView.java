package gov.nysenate.openleg.client.view.environment;

import gov.nysenate.openleg.client.view.base.ViewObject;

public class SimpleEnvironmentVariableView implements ViewObject{
    String name;
    Object value;

    public SimpleEnvironmentVariableView(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public SimpleEnvironmentVariableView(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String getViewType() {
        return "environment variable";
    }
}
