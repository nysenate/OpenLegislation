package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.ViewObject;

import java.util.HashSet;
import java.util.Set;

public class NewUserView implements ViewObject {

    private String name;
    private String email;
    private Set<String> subscriptions = new HashSet<>();

    public NewUserView() {}

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public String getViewType() {
        return "new-user";
    }
}
