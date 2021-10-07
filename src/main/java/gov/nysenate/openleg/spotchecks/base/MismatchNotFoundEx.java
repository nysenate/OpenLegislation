package gov.nysenate.openleg.spotchecks.base;

public class MismatchNotFoundEx extends RuntimeException {

    private int mismatchId;

    public MismatchNotFoundEx(int mismatchId) {
        super("Mismatch with id = " + mismatchId + " could not be retrieved.");
    }

    public int getMismatchId() {
        return mismatchId;
    }
}
