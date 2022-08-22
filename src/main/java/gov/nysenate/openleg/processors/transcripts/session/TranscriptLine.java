package gov.nysenate.openleg.processors.transcripts.session;

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
public record TranscriptLine(String text) {
    /** Regex to match any non-alphanumeric or whitespace characters. */
    private static final String INVALID_CHARACTERS_REGEX = "[^\\w .,?-]+";
    private static final String SESSION = "SESSION";

    /** All line numbers occur in the first 10 characters of a line. */
    private static final int MAX_PAGE_NUM_INDEX = 10, MAX_PAGE_LINES = 25;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hmma"),
            DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d yyyy");

    public TranscriptLine(@NonNull String text) {
        if (!text.isBlank())
            text = text.stripTrailing();
        this.text = text.replaceAll("\f", "");
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
     * Page numbers are right aligned at the top of each page.
     * @return <code>true</code> if line contains a page number;
     *         <code>false</code> otherwise.
     */
    public boolean isPageNumber() {
        Optional<Integer> num = getNumber(text);
        if (num.isEmpty())
            return false;
        return text.indexOf(num.get().toString()) > MAX_PAGE_NUM_INDEX;
    }

    /**
     * Extracts and returns the location data from a TranscriptLine.
     * @return the location.
     */
    public Optional<String> getLocation() {
        String temp = removeLineNumber().replaceAll("\\s+", " ").trim();
        if (temp.matches(("(?i)ALBANY[ ,]+NEW YORK")))
            return Optional.of(temp.toUpperCase());
        return Optional.empty();
    }

    /**
     * Extracts the date from the line if possible.
     * @return the Optional, which will have a date if one was found.
     */
    public Optional<LocalDate> getDate() {
        String temp = WordUtils.capitalizeFully(removeLineNumber().replaceAll("[ ,]+", " ")
                .replace(".", "").trim());
        try {
            return Optional.of(LocalDate.parse(temp, DATE_FORMATTER));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    /**
     * Extracts the time from the line if possible.
     * @return the Optional, which will have a time if one was found.
     */
    public Optional<LocalTime> getTime() {
        String time = removeLineNumber().replaceAll("[:. ]", "").replace("Noon", "pm").trim().toUpperCase();
        try {
            return Optional.of(LocalTime.parse(time, TIME_FORMATTER));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    public Optional<String> getSession() {
        if (!text.contains(SESSION))
            return Optional.empty();
        String[] temp = removeLineNumber().replaceAll(" {2,}", " ").trim().split(SESSION);
        if (temp.length == 0)
            return Optional.empty();
        String prefix = temp[0].replaceAll(" ", "") + " ";
        // If the suffix exists, it's a Roman numeral, and shouldn't be capitalized.
        String suffix = temp.length > 1 ? " " + temp[1] : "";
        return Optional.of(WordUtils.capitalizeFully(prefix + SESSION) + suffix);
    }

    public boolean isBlank() {
        return text.replaceAll(INVALID_CHARACTERS_REGEX,"").isBlank();
    }

    /**
     * Lines with Stenographers need to be treated differently.
     * @return true if this line contains the stenographer information.
     */
    public boolean isStenographer() {
        return text.matches(".*(" + Stenographer.CANDYCO1.getName() + "|\\(518\\) 371-8910).*");
    }

    /**
     * Attempts to remove the line number from this line.
     * @return Returns line text with the line number removed
     * or the text unaltered if it doesn't have a line number.
     */
    String removeLineNumber() {
        if (hasLineNumber())
            return text.replaceFirst("\\d+", "").trim();
        return text;
    }

    private static Optional<Integer> getNumber(String text) {
        try {
            return Optional.of(Integer.parseInt(text.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
