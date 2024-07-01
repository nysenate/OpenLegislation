package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.processors.transcripts.session.TranscriptLine;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranscriptPdfParser {
    private final boolean hasLineNumbers;
    private final List<List<String>> pages = new ArrayList<>();
    private List<String> currPage = new ArrayList<>();
    private int currPageNum;

    // Maps Strings to the number of blank lines to add.
    private static final Map<String, Integer> BLANK_LINES = Map.of("NEW YORK STATE SENATE", 2,
            "STENOGRAPHIC RECORD",  4, ":", 2, "SESSION", 3);
    private static final Pattern BLANK_LINE_PATTERN = Pattern.compile(".*?(" +
            String.join("|", BLANK_LINES.keySet()) + ").*");

    public TranscriptPdfParser(String transcriptText) {
        var lineArrayList = transcriptText.lines().map(TranscriptLine::new)
                .filter(tl -> !tl.getCleanText().isBlank() && !tl.isStenographer())
                // Some starting lines may need to be skipped.
                .dropWhile(tLine -> tLine.getStartingInt() == null)
                .toList();
        this.currPageNum = lineArrayList.get(0).getStartingInt();
        this.hasLineNumbers = lineArrayList.get(1).getStartingInt() != null;
        processLines(lineArrayList);
    }

    public List<List<String>> getPages() {
        return pages;
    }

    protected boolean hasLineNumbers() {
        return hasLineNumbers;
    }

    private static TranscriptLine getLine(List<TranscriptLine> lines, int index) {
        return index < lines.size() ? lines.get(index) : new TranscriptLine("");
    }

    private boolean isNextPageNumber(TranscriptLine currLine, TranscriptLine nextLine) {
        return Objects.equals(currLine.getStartingInt(), currPageNum + 1) &&
                currLine.getCleanText().matches("\\d+") &&
                (!hasLineNumbers || Objects.equals(nextLine.getStartingInt(), 1));
    }

    /**
     * Various problems with Transcripts require corrections to lines.
     * @param lines to process and save.
     */
    private void processLines(List<TranscriptLine> lines) {
        for (int i = 0; i < lines.size(); i++) {
            TranscriptLine currLine = lines.get(i);
            TranscriptLine nextLine = getLine(lines, i + 1);
            if (isNextPageNumber(currLine, nextLine)) {
                addCurrPage();
                currPageNum = currLine.getStartingInt();
            }
            else if (needsCorrecting(nextLine, getLine(lines, i + 2))) {
                currLine = new TranscriptLine(currLine.getText() + " " + nextLine.getText());
                i++;
            }
            addLine(currLine);
        }
        addCurrPage();
    }

    /**
     * Tests for certain common formatting issues.
     * @param nextLine to check.
     * @return if this line needs to be combined with the last one.
     */
    private boolean needsCorrecting(TranscriptLine nextLine, TranscriptLine nextNextLine) {
        if (currPage.isEmpty() || isNextPageNumber(nextLine, nextNextLine)) {
            return false;
        }
        if (!hasLineNumbers) {
            return !nextLine.getText().startsWith(" ");
        }
        return !Objects.equals(nextLine.getStartingInt(), currPage.size() + 1);
    }

    /**
     * Adds a line to the current page.
     */
    private void addLine(TranscriptLine currLine) {
        // The first line added is always a page number, which may need some cleaning.
        currPage.add(currPage.isEmpty() ? currLine.getCleanText() : currLine.getText());
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
