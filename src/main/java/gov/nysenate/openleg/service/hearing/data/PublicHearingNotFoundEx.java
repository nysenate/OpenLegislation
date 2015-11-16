package gov.nysenate.openleg.service.hearing.data;

import gov.nysenate.openleg.model.hearing.PublicHearingId;

public class PublicHearingNotFoundEx extends RuntimeException {
    private static final long serialVersionUID = 6022069225508859290L;

    protected PublicHearingId publicHearingId;

    public PublicHearingNotFoundEx(PublicHearingId publicHearingId, Throwable ex) {
        super(
                publicHearingId != null
                        ? "Public hearing " + publicHearingId.toString() + " could not be retrieved."
                        : "Public hearing could not be retrieved since the given public hearing id was null",
                ex
        );
        this.publicHearingId = publicHearingId;
    }

    public PublicHearingId getPublicHearingId() {
        return publicHearingId;
    }
}
