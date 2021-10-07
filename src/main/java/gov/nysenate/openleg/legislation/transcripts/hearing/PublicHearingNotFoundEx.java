package gov.nysenate.openleg.legislation.transcripts.hearing;

import java.io.Serial;

public class PublicHearingNotFoundEx extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6022069225508859290L;

    private final PublicHearingId id;
    private final String filename;

    public PublicHearingNotFoundEx(PublicHearingId id, Throwable ex) {
        this(id, null, ex);
    }

    public PublicHearingNotFoundEx(String filename, Throwable ex) {
        this(null, filename, ex);
    }

    public PublicHearingNotFoundEx(PublicHearingId id, String filename, Throwable ex) {
        super("Public hearing " + (id == null ? filename : id.getId()) + " could not be retrieved.", ex);
        this.id = id;
        this.filename = filename;
    }

    public PublicHearingId getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }
}
