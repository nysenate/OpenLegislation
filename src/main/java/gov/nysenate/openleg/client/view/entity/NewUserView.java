package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.client.view.base.ViewObject;

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
