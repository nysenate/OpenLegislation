package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.processors.transcripts.session.TranscriptLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TranscriptPdfParser {
    private static final Logger logger = LoggerFactory.getLogger(TranscriptPdfParser.class);
    private final boolean hasLineNumbers;
    private final List<List<String>> pages = new ArrayList<>();
    private List<String> currPage = new ArrayList<>();

    // Maps Strings to the number of blank lines to add.
    private static final String SENATE_LINE = "NEW YORK STATE SENATE";
    private static final Map<String, Integer> BLANK_LINES = Map.of(SENATE_LINE, 2,
            "THE STENOGRAPHIC RECORD", 4, "STENOGRAPHIC RECORD",  3, ":", 2,
            "SESSION", 3, "Secretary", 6);
    private static final Pattern BLANK_LINE_PATTERN = Pattern.compile(".*?(" +
            String.join("|", BLANK_LINES.keySet()) + ").*"),
        PRES_ERROR_PATTERN = Pattern.compile(" *((?:Acting ?|President ?){1,2}) *(\\d+.*)");

    public TranscriptPdfParser(LocalDateTime id, String transcriptText) {
        var lineArrayList = transcriptText.lines().map(TranscriptLine::new)
                .filter(tl -> !(tl.isBlank() && !tl.getText().matches(" {10,}")) && !tl.isStenographer())
                .collect(Collectors.toCollection(ArrayList::new));
        // Second line may be a page number after a line of whitespace, so the 3rd line is used.
        this.hasLineNumbers = lineArrayList.size() >= 3 && lineArrayList.get(2).hasLineNumber();
        processLines(lineArrayList);
        int firstPageLength = pages.get(0).size();
        for (int i = 1; i < pages.size() - 1; i++) {
            if (pages.get(i).size() != firstPageLength) {
                logger.warn("In Transcript " + id + ", there is a page length mismatch");
                break;
            }
        }
    }

    public List<List<String>> getPages() {
        return pages;
    }

    public boolean hasLineNumbers() {
        return hasLineNumbers;
    }

    /**
     * Various problems with Transcripts require corrections to lines.
     * @param lines to process and save.
     */
    private void processLines(ArrayList<TranscriptLine> lines) {
        // The last line won't need correction.
        for (int i = 0; i < lines.size() - 1; i++) {
            TranscriptLine currLine = lines.get(i), nextLine = lines.get(i + 1);
            if (pages.isEmpty() && hasLineNumbers && !nextLine.hasLineNumber()) {
                Matcher m = PRES_ERROR_PATTERN.matcher(nextLine.getText());
                if (m.find()) {
                    currLine = new TranscriptLine(currLine.getText() + " " + m.group(1));
                    nextLine = new TranscriptLine(m.group(2));
                    lines.set(i + 1, nextLine);
                }
            }
            if (needsCorrecting(nextLine)) {
                currLine = new TranscriptLine(currLine.getText() + " " + nextLine.getText());
                i++;
            }
            if (currLine.isPageNumber() && !currPage.isEmpty()) {
                pages.add(currPage);
                currPage = new ArrayList<>();
            }
            addLine(currLine);
        }
        addLine(lines.get(lines.size() - 1));
        addCurrPage();
    }

    /**
     * Tests for certain common formatting issues.
     * @param nextLine to check.
     * @return if this line needs to be combined with the last one.
     */
    private boolean needsCorrecting(TranscriptLine nextLine) {
        if (!hasLineNumbers)
            return !nextLine.getText().startsWith(" ");
        if (nextLine.isBlank() || nextLine.isPageNumber())
            return false;
        if (!nextLine.hasLineNumber())
            return true;
        return nextLine.getText().matches("\\d+") && Integer.parseInt(nextLine.getText()) != currPage.size() + 1;
    }

    /**
     * Adds a line to the current page.
     */
    private void addLine(TranscriptLine currLine) {
        // The first line added is always a page number.
        if (currPage.isEmpty())
            currPage.add(currLine.getText().trim());
        else
            currPage.add(currLine.getText());
        // Sometimes, manual spacing needs to be added.
        if (pages.isEmpty() && !hasLineNumbers) {
            Matcher m = BLANK_LINE_PATTERN.matcher(currLine.getText());
            if (m.find())
                currPage.addAll(Collections.nCopies(BLANK_LINES.get(m.group(1)), ""));
        }
    }

    /**
     * 26 transcripts without line numbers lack the proper heading, making actual error detection harder.
     * This corrects that by adding blank lines, so the page length is what it should be.
     */
    private void addCurrPage() {
        if (!hasLineNumbers) {
            boolean hasSenateLine = currPage.stream().anyMatch(str -> str.contains(SENATE_LINE));
            if (!hasSenateLine)
                currPage.addAll(Collections.nCopies(3, ""));
        }
        pages.add(currPage);
    }
}
