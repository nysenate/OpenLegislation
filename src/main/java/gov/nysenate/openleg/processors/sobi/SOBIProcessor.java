package gov.nysenate.openleg.processors.sobi;

import gov.nysenate.openleg.model.sobi.SOBIFragment;

public abstract class SOBIProcessor
{
    public abstract void process(SOBIFragment sobiFragment);

    @SuppressWarnings("serial")
    public static class ParseError extends Exception
    {
        public ParseError(String message) { super(message); }
    }
}
