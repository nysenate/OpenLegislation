package gov.nysenate.openleg.api.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.openleg.api.ViewObject;

public class AuthedUser implements ViewObject {
    private boolean isAuthed;
    private boolean isAdmin;

    public AuthedUser() {
    }

    public AuthedUser(boolean isAuthed, boolean isAdmin) {
        this.isAuthed = isAuthed;
        this.isAdmin = isAdmin;
    }

    @JsonProperty("isAuthed")
    public boolean isAuthed() {
        return isAuthed;
    }

    @JsonProperty("isAdmin")
    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String getViewType() {
        return "authed-user";
    }
}
