package gov.nysenate.openleg.legislation.transcripts.hearing;

import java.io.Serial;

public class HearingNotFoundEx extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6022069225508859290L;

    private final HearingId id;
    private final String filename;

    public HearingNotFoundEx(HearingId id, Throwable ex) {
        this(id, null, ex);
    }

    public HearingNotFoundEx(String filename, Throwable ex) {
        this(null, filename, ex);
    }

    private HearingNotFoundEx(HearingId id, String filename, Throwable ex) {
        super("Hearing " + (id == null ? filename : id.id()) + " could not be retrieved.", ex);
        this.id = id;
        this.filename = filename;
    }

    public HearingId getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }
}
