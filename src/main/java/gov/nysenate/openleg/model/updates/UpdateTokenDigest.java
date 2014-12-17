package gov.nysenate.openleg.model.updates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UpdateTokenDigest<ContentId> extends UpdateToken<ContentId> {

    protected List<UpdateDigest<ContentId>> digests = new ArrayList<>();

    /** --- Constructors --- */

    public UpdateTokenDigest(ContentId id, LocalDateTime updatedDateTime) {
        super(id, updatedDateTime);
    }

    public UpdateTokenDigest(UpdateToken<ContentId> updateToken) {
        this(updateToken.getId(), updateToken.getUpdatedDateTime());
    }

    /** --- Basic Getters / Setters --- */

    public void addUpdateDigest(UpdateDigest<ContentId> digest) {
        digests.add(digest);
    }

    public List<UpdateDigest<ContentId>> getDigests() {
        return digests;
    }

    public void setDigests(List<UpdateDigest<ContentId>> digests) {
        this.digests = digests;
    }
}
