package gov.nysenate.openleg.api.config;

import gov.nysenate.openleg.api.ViewObject;

public record EnvironmentVariableView(String name, Object value, String type, boolean mutable)
        implements ViewObject {

    public EnvironmentVariableView(String name, Object value, Class<?> type, boolean mutable) {
        this(name, value, type != null ? type.getSimpleName() : null, mutable);
    }

    @Override
    public String getViewType() {
        return "environment variable";
    }
}