package gov.nysenate.openleg.legislation;

public class BadSessionYearException extends IllegalArgumentException {
    public BadSessionYearException(int badYear, int startYear) {
        super("Session year was %d, but must be >= %d".formatted(badYear, startYear));
    }
}
