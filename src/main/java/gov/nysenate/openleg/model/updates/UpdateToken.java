package gov.nysenate.openleg.model.updates;

import java.time.LocalDateTime;

public class UpdateToken<ContentId> {
    
    protected ContentId id;
    protected LocalDateTime updatedDateTime;

    /** --- Constructors --- */

    public UpdateToken(ContentId id, LocalDateTime updatedDateTime) {
        this.id = id;
        this.updatedDateTime = updatedDateTime;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateToken)) return false;

        UpdateToken that = (UpdateToken) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (updatedDateTime != null ? !updatedDateTime.equals(that.updatedDateTime) : that.updatedDateTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (updatedDateTime != null ? updatedDateTime.hashCode() : 0);
        return result;
    }

    /** --- Getters --- */

    public ContentId getId() {
        return id;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }
}
