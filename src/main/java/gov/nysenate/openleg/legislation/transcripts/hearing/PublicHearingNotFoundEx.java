package gov.nysenate.openleg.legislation.transcripts.hearing;

import java.io.Serial;

public class PublicHearingNotFoundEx extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6022069225508859290L;

    protected PublicHearingId publicHearingId;

    public PublicHearingNotFoundEx(PublicHearingId publicHearingId, Throwable ex) {
        super(
                publicHearingId != null
                        ? "Public hearing " + publicHearingId + " could not be retrieved."
                        : "Public hearing could not be retrieved since the given public hearing id was null",
                ex
        );
        this.publicHearingId = publicHearingId;
    }

    public PublicHearingId getPublicHearingId() {
        return publicHearingId;
    }
}
