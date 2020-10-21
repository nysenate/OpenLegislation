package gov.nysenate.openleg.api.config;

import gov.nysenate.openleg.api.ViewObject;

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
