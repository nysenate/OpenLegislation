package gov.nysenate.openleg.processors.transcripts.session;

import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Set of methods that function on individual transcript lines to help with parsing logic.
 */
public class TranscriptLine {
    /** Regex to match any non-alphanumeric or whitespace characters. */
    private static final String INVALID_CHARACTERS_REGEX = "[^\\w .,?-]+";

    private final String text, cleanText;
    private final Integer startingInt;

    public TranscriptLine(@NonNull String text) {
        this.text = text.stripTrailing().replaceAll("\f", "");
        this.cleanText = text.replaceAll(INVALID_CHARACTERS_REGEX, "").trim();
        Integer temp = null;
        try {
            temp = Integer.parseInt(cleanText.split(" {2}")[0].trim());
        } catch (NumberFormatException ignored) {}
        this.startingInt = temp;
    }

    public String getText() {
        return text;
    }

    public String getCleanText() {
        return cleanText;
    }

    public Integer getStartingInt() {
        return startingInt;
    }

    /**
     * Lines with Stenographers need to be treated differently.
     * @return true if this line contains the stenographer information.
     */
    public boolean isStenographer() {
        return text.matches(".*(" + Stenographer.CANDYCO1.getName() + "|\\(518\\) 371-8910).*");
    }
}
