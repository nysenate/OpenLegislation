package gov.nysenate.openleg.processors.transcripts.session;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Set of methods that function on individual transcript lines to help with parsing logic.
 */
public class TranscriptLine {
    /** Regex to match any non-alphanumeric or whitespace characters. */
    private static final String INVALID_CHARACTERS_REGEX = "[^\\w .,?-]+";
    private static final Pattern LINE_SEP = Pattern.compile("\\R");


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

    /**
     * Converts raw transcript text into a filtered, ordered list of TranscriptLines ready for parsing.
     */
    public static List<TranscriptLine> prepareLines(String transcriptText) {
        return transcriptText.lines()
                .map(TranscriptLine::new)
                .filter(tl -> !tl.getCleanText().isBlank() && !tl.isStenographer())
                .dropWhile(tl -> tl.getStartingInt() == null)
                .toList();
    }

    /**
     * Converts raw transcript text into a filtered, ordered list of TranscriptLines ready for parsing, along with
     * the character offset (index) of the start of the line.
     */
    public static List<Map.Entry<Integer, TranscriptLine>> prepareLinesWithOffsets(String text) {
        List<Map.Entry<Integer, TranscriptLine>> result = new ArrayList<>();
        int lineStart = 0;
        for (var sep : LINE_SEP.matcher(text).results().toList()) {
            result.add(Map.entry(lineStart, new TranscriptLine(text.substring(lineStart, sep.start()))));
            lineStart = sep.end();
        }
        result.add(Map.entry(lineStart, new TranscriptLine(text.substring(lineStart))));
        return result.stream()
                .filter(e -> !e.getValue().getCleanText().isBlank() && !e.getValue().isStenographer())
                .dropWhile(e -> e.getValue().getStartingInt() == null)
                .toList();
    }

    public static boolean isNextPageNumber(TranscriptLine currLine, TranscriptLine nextLine, int currPageNum, boolean hasLineNumbers) {
        return currLine.getCleanText().matches(String.valueOf(currPageNum + 1)) && nextLine != null
                && (!hasLineNumbers || Objects.equals(nextLine.getStartingInt(), 1));
    }
}
