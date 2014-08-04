package gov.nysenate.openleg.model.sobi;

public class SobiFragmentNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = 6975622202596491910L;

    public SobiFragmentNotFoundEx() {}

    public SobiFragmentNotFoundEx(String message) {
        super(message);
    }
}
