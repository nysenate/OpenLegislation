package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.processors.transcripts.session.TranscriptLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TranscriptPdfParser {
    private static final int MAX_PAGE_LINES = 27;
    private static final String LABEL = "(\\s*NEW YORK STATE SENATE\\s*)|(.*STENOGRAPHIC RECORD.*)";
    private LinkedList<TranscriptLine> allLines;
    private final boolean hasLineNumbers;
    private final List<List<String>> pages = new ArrayList<>();
    private List<String> currPage = new ArrayList<>();
    private TranscriptLine currLine;

    public TranscriptPdfParser(String transcriptText) {
        this.allLines = transcriptText.lines().map(TranscriptLine::new)
                .filter(tl -> !tl.getText().isEmpty()).collect(Collectors.toCollection(LinkedList::new));
        rightAlignPageNumbers();
        this.hasLineNumbers = transcriptHasLineNumbers();
    }

    public void rightAlignPageNumbers() {
        if (!allLines.get(0).getText().matches(" +") || !allLines.get(1).getText().matches("\\d+"))
            return;
        // Otherwise, page numbers are left aligned and need to be corrected.
        var temp = new LinkedList<TranscriptLine>();
        while (!allLines.isEmpty()) {
            this.currLine = allLines.pop();
            var nextLine = allLines.peek();
            if (currLine.isBlank() && nextLine != null && nextLine.getText().trim().matches("\\d+")) {
                this.currLine = new TranscriptLine(currLine.getText() + " " + nextLine.getText());
                allLines.pop();
            }
            temp.add(currLine);
        }
        this.allLines = temp;
    }

    /**
     * Split text into pages, along with some processing.
     * This solution works for all transcript text formats.
     * @return a list of pages, which are themselves lists of lines.
     */
    public List<List<String>> getPages() {
        while (!allLines.isEmpty())
            addNextPage();
        return pages;
    }

    /**
     * Used to process one page at a time.
     */
    private void addNextPage() {
        this.currPage = new ArrayList<>();
        do {
            this.currLine = allLines.pop();
            var nextLine = (allLines.isEmpty() ? new TranscriptLine("") : allLines.peek());
            if (needsCorrecting(nextLine)) {
                this.currLine = new TranscriptLine(currLine.getText() + " " + nextLine.getText());
                allLines.pop();
            }
            addLine();
        } while (!endOfPage());
        pages.add(currPage);
    }

    // TODO: if the first page has no line nums, is this true for all pages?
    private boolean transcriptHasLineNumbers() {
        boolean firstNumFound = false;
        for (int i = 0; i < MAX_PAGE_LINES; i++) {
            TranscriptLine line = allLines.get(i);
            if (TranscriptLine.getNumber(line.getText()).isPresent()) {
                if (!firstNumFound)
                    firstNumFound = true;
                else
                    return !line.isPageNumber(i);
            }
        }
        return false;
    }

    private boolean needsCorrecting(TranscriptLine nextLine) {
        boolean lineNumError = !nextLine.isBlank() && hasLineNumbers && !nextLine.hasLineNumber(currPage.size() + 1)
                && !nextLine.isPageNumber(currPage.size() + 1);
        // For pages where it starts with a line of spaces, then a page number on the next line.
        boolean pageNumError = currLine.getText().matches(" +") && currPage.isEmpty() && nextLine.isPageNumber(0);
        return lineNumError || pageNumError;
    }

    /**
     * Checks if we are at the end of a page.
     * @return if we should end this page.
     */
    private boolean endOfPage() {
        if (allLines.isEmpty())
            return true;
        return allLines.peek().isPageNumber(currPage.size());
    }

    /**
     * Some lines need extra lines added after, and some need to be ignored.
     */
    private void addLine() {
        // The first line added is always a page number.
        if (currPage.isEmpty())
            currPage.add(currLine.stripInvalidCharacters());
        else if (!currLine.isBlank() && !currLine.isStenographer())
            currPage.add(currLine.getText());
        if (pages.isEmpty() && !hasLineNumbers)
            addManualSpacing();
    }

    /**
     * Sometimes, manual spacing needs to be added.
     */
    private void addManualSpacing() {
        int blankLines = 0;
        if (currLine.getText().matches(LABEL) || currLine.getTime().isPresent())
            blankLines = 2;
        else if (currLine.getSession().isPresent())
            blankLines = 3;
        currPage.addAll(Collections.nCopies(blankLines, ""));
    }
}
