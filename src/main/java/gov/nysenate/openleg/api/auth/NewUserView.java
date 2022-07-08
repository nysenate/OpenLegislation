package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.ViewObject;

import java.util.Set;

public record NewUserView(String name, String email, Set<String> subscriptions)
        implements ViewObject {
    @Override
    public String getViewType() {
        return "new-user";
    }
}
