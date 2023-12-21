package gov.nysenate.openleg.spotchecks.model;

public enum SpotCheckMismatchIgnore {

    NOT_IGNORED(-1),
    IGNORE_PERMANENTLY(0),
    IGNORE_UNTIL_RESOLVED(1),
    IGNORE_ONCE(2);

    private final int code;

    SpotCheckMismatchIgnore(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
