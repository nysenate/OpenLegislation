package gov.nysenate.openleg.spotchecks.model;

public enum SpotCheckMismatchTracked {
    TRACKED,
    UNTRACKED;

    public static SpotCheckMismatchTracked getFromBoolean(boolean tracked) {
        return tracked ? TRACKED : UNTRACKED;
    }
}
