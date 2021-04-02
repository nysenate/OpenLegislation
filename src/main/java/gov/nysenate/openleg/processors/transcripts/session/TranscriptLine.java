package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.api.legislation.transcripts.session.view.Stenographer;
import org.apache.commons.text.WordUtils;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Set of methods that function on individual transcript lines to help with parsing logic.
 */
public class TranscriptLine {
    /** Regex to match any non alphanumeric or whitespace characters. */
    private static final String INVALID_CHARACTERS_REGEX = "[^\\w .,]+";

    /** All page numbers occur in the first 10 characters of a line. */
    private static final int MAX_PAGE_NUM_INDEX = 10, MAX_PAGE_LINES = 25;

    /** The actual text of the line. */
    private final String text;

    public TranscriptLine(@NonNull String text) {
        if (!text.isBlank())
            text = text.stripTrailing();
        this.text = text.replaceAll("[\r\f\t]", "");
        //int[] codePoints = text.chars().filter(Character::isBmpCodePoint).toArray();
        //this.text = new String(codePoints, 0, codePoints.length);
    }

    public String getText() {
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
        Optional<Integer> num = getNumber(text);
        if (num.isEmpty())
            return false;
        // Page numbers are right aligned.
        return text.indexOf(num.get().toString()) > MAX_PAGE_NUM_INDEX;
    }

    /**
     * Determines if this TranscriptLine's text contains a line number.
     * @return <code>true</code> if this TranscriptLine contains a line number;
     *         <code>false</code> otherwise.
     */
    public boolean hasLineNumber() {
        // Split on two spaces so time typos don't get treated as line numbers.
        String[] split = text.trim().split(" {2}");
        Optional<Integer> num = getNumber(split[0].replaceAll(INVALID_CHARACTERS_REGEX, ""));
        return num.isPresent() && num.get() <= MAX_PAGE_LINES && !isPageNumber();
    }

    /**
     * Extracts and returns the location data from a TranscriptLine.
     * @return the location.
     */
    public Optional<String> getLocation() {
        String temp = removeLineNumber().toUpperCase().replaceAll("\\s+", " ").trim();
        if (temp.matches((".*ALBANY.*NEW.*YORK.*")))
            return Optional.of(temp);
        return Optional.empty();
    }

    /**
     * Extracts the date information from lines which contains the date, or an
     * empty Optional if a date can't be extracted.
     * @return the Optional, which may have a date.
     */
    public Optional<LocalDate> getDate() {
        String temp = WordUtils.capitalizeFully(removeLineNumber().replaceAll("[ ,]+", " ")
                .replace(".", "").trim());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d yyyy");
        try {
            return Optional.of(LocalDate.parse(temp, dtf));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    /**
     * Returns a string with time information parsed from this TranscriptLine,
     * or an empty Optional if no time can be parsed.
     * @return the Optional, which may have a time.
     */
    public Optional<LocalTime> getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hmma");
        String date = removeLineNumber().replaceAll("[:. ]", "").replace("Noon", "pm").trim().toUpperCase();
        try {
            return Optional.of(LocalTime.parse(date, dtf));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    public Optional<String> getSession() {
        if (text.contains("SESSION"))
            return Optional.of(removeLineNumber().trim());
        return Optional.empty();
    }

    public boolean isBlank() {
        return stripInvalidCharacters().isBlank();
    }

    /**
     * Lines with Stenographers need to be treated differently.
     * @return true if this line contains the stenographer information.
     */
    public boolean isStenographer() {
        return text.matches(".*(" + Stenographer.CANDYCO1.getName() + "|\\(518\\) 371-8910).*");
    }

    /**
     * Removes invalid characters from a line of text, such as broken pipe or binary.
     * @return The line with invalid characters removed.
     */
    public String stripInvalidCharacters() {
        return text.replaceAll(INVALID_CHARACTERS_REGEX,"");
    }

    /**
     * Attempts to remove the line number from this line.
     * @return Returns line text with the line number removed
     * or the text unaltered if it doesn't have a line number.
     */
    protected String removeLineNumber() {
        if (hasLineNumber())
            return text.trim().substring(text.trim().length() < 2 ? 1 : 2);
        return text;
    }

    /** --- Internal Methods --- */

    private static Optional<Integer> getNumber(String text) {
        try {
            return Optional.of(Integer.parseInt(text.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
