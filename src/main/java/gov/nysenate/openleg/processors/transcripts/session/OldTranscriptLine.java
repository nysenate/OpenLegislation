package gov.nysenate.openleg.processors.transcripts.session;

import org.apache.commons.text.WordUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Set of methods that function on individual transcript lines to help with parsing logic.
 */
public class OldTranscriptLine
{
    /** Regex to match any non alphanumeric or whitespace characters. */
    private static final String invalidCharactersRegex = "[^a-zA-Z0-9 ]+";

    /** All page numbers occur in the first 10 characters of a line. */
    private static final int MAXIMUM_PAGE_LINE_INDEX = 10;

    /** The maximum number of lines on a page. A number greater than this
     * cannot be a line number. */
    private static final int MAXIMUM_PAGE_LINE_NUMBER = 27;

    /** The actual text of the line. */
    private final String text;

    public OldTranscriptLine(String text) {
        this.text = text;
    }

    public String fullText() {
        return text;
    }

    /**
     * Page number is usually right aligned at the top of each page.
     * However, sometimes it's left aligned on the next line instead.
     * e.g. 082895.v1, 011299.v1
     * @return <code>true</code> if line contains a page number;
     *         <code>false</code> otherwise.
     */
    public boolean isPageNumber() {
        String validText = stripInvalidCharacters().trim();
        if (isNumber(validText)) {
            return isRightAligned(validText) || greaterThanMaxPageLineNum(validText);
        }
        return false;
    }

    /**
     * Determines if this TranscriptLine's text contains a line number.
     * @return <code>true</code> if this TranscriptLine contains a line number;
     *         <code>false</code> otherwise.
     */
    public boolean hasLineNumber() {
        // split on two spaces so time typo's don't get treated as line numbers.
        return isNumber(text.trim().split("  ")[0]) && !isPageNumber();
    }

    /**
     * Attempts to remove the line number from this line.
     * @return Returns line text with the line number removed
     * or the text unaltered if it doesn't have a line number.
     */
    public String removeLineNumber() {
        if (hasLineNumber()) {
            if (text.trim().length() < 2) {
                return text.trim().substring(1);
            }
            return text.trim().substring(2);
        }
        return text;
    }

    /**
     * Determine if this TranscriptLine's text contains the transcripts location.
     */
    public boolean isLocation() {
        return text.toUpperCase().contains("ALBANY")
                && text.toUpperCase().contains("NEW")
                && text.toUpperCase().contains("YORK");
    }

    /**
     * Extracts and returns the location data from a TranscriptLine.
     * Only use this if you know via {@link #isLocation()} that this line contains the location.
     */
    public String getLocation() {
        return removeLineNumber().toUpperCase().trim().replaceAll("\\s+", " ");
    }

    /**
     * Determines if this TranscriptLine's text contains date information.
     */
    public boolean isDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d yyyy");
        try {
            LocalDate.parse(getDateString(), dtf);
        }
        catch (DateTimeParseException ex) {
            return false;
        }

        return true;
    }

    /**
     * Extracts the date information from lines which containd the date.
     * Only use if the line contains date information via {@link #isDate()}.
     */
    public String getDateString() {
        return WordUtils.capitalizeFully(removeLineNumber().replace(" , ", " ").replace(", ", " ")
                .replace(",", " ").replace(".", "").replace("  ", " ").trim());
    }

    /**
     * Determines if this TranscriptLine contains the time of the transcript.
     */
    public boolean isTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hmma");
        try {
            LocalTime.parse(getTimeString(), dtf);
        } catch (DateTimeParseException ex) {
            return false;
        }

        return true;
    }

    /**
     * Returns a string with time information parsed from this TranscriptLine.
     * Only use if {@link #isTime()} determines this line has time information.
     */
    public String getTimeString() {
        // remove all erroneous characters including spaces.
        String date = removeLineNumber().replace(":", "").replace(".", "").replace(" ", "").trim();

        if (date.contains("Noon"))
            date = date.replace("Noon", "pm");

        return date.toUpperCase();
    }

    /**
     * Determines if this TranscriptLine contains the Transcript's session type info.
     */
    public boolean isSession() {
        return text.contains("SESSION");
    }

    public boolean isEmpty() {
        return text.replaceAll(invalidCharactersRegex,"").trim().isEmpty();
    }

    /**
     * Determines if this TranscriptLine contains the stenographer information.
     */
    public boolean isStenographer() {
        return text.contains("Candyco Transcription Service, Inc.") || text.contains("(518) 371-8910");
    }

    /**
     * Removes invalid characters from a line of text, such as broken pipe or binary.
     * @return The line with invalid characters removed.
     */
    public String stripInvalidCharacters() {
        return text.replaceAll(invalidCharactersRegex,"");
    }

    /** --- Internal Methods --- */

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    private boolean greaterThanMaxPageLineNum(String validText) {
        return Integer.parseInt(validText) > MAXIMUM_PAGE_LINE_NUMBER;
    }

    private boolean isRightAligned(String validText) {
        int startIndex = text.indexOf(validText);
        return startIndex > MAXIMUM_PAGE_LINE_INDEX;
    }
}
