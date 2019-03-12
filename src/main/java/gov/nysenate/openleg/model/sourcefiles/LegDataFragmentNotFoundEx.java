package gov.nysenate.openleg.model.sourcefiles;

public class LegDataFragmentNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = 6975622202596491910L;

    public LegDataFragmentNotFoundEx() {}

    public LegDataFragmentNotFoundEx(String message) {
        super(message);
    }
}
