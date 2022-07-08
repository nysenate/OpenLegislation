package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.ViewObject;

public record AuthedUser(boolean isAuthed, boolean isAdmin) implements ViewObject {
    @Override
    public String getViewType() {
        return "authed-user";
    }
}
