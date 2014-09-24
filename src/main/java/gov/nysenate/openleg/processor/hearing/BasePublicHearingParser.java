package gov.nysenate.openleg.processor.hearing;

import java.util.regex.Pattern;

public abstract class BasePublicHearingParser
{
    protected static final Pattern SEPARATOR = Pattern.compile("^\\s*(\\d+)?\\s*-+$");

    /**
     * Determines if this line contains PublicHearing content.
     * @param line PublicHearing line of text to check for content.
     * @return <code>true</code> if this line contains text excluding the line number.
     * <code>false</code> otherwise.
     */
    protected boolean hasContent(String line) {
        String blankLine = "^\\s*(\\d+)?\\s*$";
        return !line.matches(blankLine);
    }

    /**
     * Returns this String's text with the leading line number and whitespace removed.
     * @param line
     * @return
     */
    protected String stripLineNumber(String line) {
        return line.replaceAll("^\\s*(\\d+)?\\s{2,}(\\w*)", "$2");
    }
}
