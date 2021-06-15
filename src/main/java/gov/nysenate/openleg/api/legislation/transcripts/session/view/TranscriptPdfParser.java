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
    private static final Map<String, Integer> BLANK_LINES = Map.of("NEW YORK STATE SENATE", 2,
            "STENOGRAPHIC RECORD",  4, ":", 2, "SESSION", 3);
    private static final Pattern BLANK_LINE_PATTERN = Pattern.compile(".*?(" +
            String.join("|", BLANK_LINES.keySet()) + ").*");

    public TranscriptPdfParser(LocalDateTime id, String transcriptText) {
        var lineArrayList = transcriptText.lines().map(TranscriptLine::new)
                .filter(tl -> (!tl.isBlank() || tl.getText().matches(" {10,}")) && !tl.isStenographer())
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
    private void processLines(List<TranscriptLine> lines) {
        // The last line won't need correction.
        for (int i = 0; i < lines.size() - 1; i++) {
            TranscriptLine currLine = lines.get(i), nextLine = lines.get(i + 1);
            if (needsCorrecting(nextLine)) {
                currLine = new TranscriptLine(currLine.getText() + " " + nextLine.getText());
                i++;
            }
            if (currLine.isPageNumber() && !currPage.isEmpty())
                addCurrPage();
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
     * All transcripts without line numbers should have a final length of 26.
     */
    private void addCurrPage() {
        if (pages.isEmpty() && !hasLineNumbers)
            currPage.addAll(Collections.nCopies(26 - currPage.size(), ""));
        pages.add(currPage);
        currPage = new ArrayList<>();
    }
}
