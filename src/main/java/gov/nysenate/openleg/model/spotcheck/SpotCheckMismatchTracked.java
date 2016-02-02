package gov.nysenate.openleg.model.spotcheck;

public enum SpotCheckMismatchTracked {
    TRACKED,
    UNTRACKED;

    public static SpotCheckMismatchTracked getFromBoolean(boolean tracked) {
        return tracked ? TRACKED : UNTRACKED;
    }
}
