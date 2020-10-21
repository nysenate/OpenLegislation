package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.ViewObject;

import java.util.Set;

public class NewUserView implements ViewObject {

    protected String name;
    protected String email;
    protected Set<String> subscriptions;

    protected NewUserView() {}

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public String getViewType() {
        return "new-user";
    }
}
