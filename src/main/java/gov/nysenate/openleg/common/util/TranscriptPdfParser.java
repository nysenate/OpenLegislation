package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.processors.transcripts.session.TranscriptLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TranscriptPdfParser {
    private static final String LABEL = "(\\s*NEW YORK STATE SENATE\\s*)|(.*STENOGRAPHIC RECORD.*)";
    private final LinkedList<TranscriptLine> allLines;
    private final boolean hasLineNumbers;
    private final List<List<String>> pages = new ArrayList<>();
    private List<String> currPage = new ArrayList<>();

    public TranscriptPdfParser(String transcriptText) {
        var lineArrayList = transcriptText.lines().map(TranscriptLine::new)
                .filter(tl -> !tl.getText().isEmpty()).collect(Collectors.toCollection(ArrayList::new));
        this.hasLineNumbers = transcriptHasLineNumbers(lineArrayList);
        this.allLines = correctLines(lineArrayList);
    }

    /**
     * Split text into pages, along with some processing.
     * This solution works for all transcript text formats.
     * @return a list of pages, which are themselves lists of lines.
     */
    public List<List<String>> getPages() {
        // If pages have already been processed, just return them.
        // TODO: trim() if there's line numbers?
        if (!pages.isEmpty())
            return pages;
        while (!allLines.isEmpty()) {
            this.currPage = new ArrayList<>();
            do
                addLine(allLines.pop());
            while (!allLines.isEmpty() && !allLines.peek().isPageNumber(-1));
            pages.add(currPage);
        }
        return pages;
    }

    // TODO: could be better, I think.
    // TODO: Add line numbers if they don't have them?
    private static boolean transcriptHasLineNumbers(ArrayList<TranscriptLine> lines) {
        if (lines.size() < 3)
            return false;
        return lines.get(1).hasLineNumber(-1) || lines.get(2).hasLineNumber(-1);
    }

    private LinkedList<TranscriptLine> correctLines(ArrayList<TranscriptLine> lines) {
        LinkedList<TranscriptLine> toReturn = new LinkedList<>();
        // The last line won't need correction.
        for (int i = 0; i < lines.size() - 1; i++) {
            TranscriptLine currLine = lines.get(i), nextLine = lines.get(i + 1);
            if (needsCorrecting(currLine, nextLine)) {
                // Spaces should be preserved for lines before incorrect page numbers.
                currLine = new TranscriptLine(currLine.getText() + " " + nextLine.getText());
                lines.remove(i + 1);
            }
            toReturn.add(currLine);
        }
        toReturn.add(lines.get(lines.size()-1));
        return toReturn;
    }

    private boolean needsCorrecting(TranscriptLine currLine, TranscriptLine nextLine) {
        boolean notProperNum = !nextLine.hasLineNumber(-1) && !nextLine.isPageNumber(-1);
        boolean lineNumError = hasLineNumbers && !nextLine.isBlank() && notProperNum;
        String trimmed = nextLine.getText().replaceAll("[.,;]+", "").trim();
        boolean badNumError = !hasLineNumbers && trimmed.matches("\\w+");
        return lineNumError || badNumError;
    }

    /**
     * Some lines need extra lines added after, and some need to be ignored.
     */
    private void addLine(TranscriptLine currLine) {
        // The first line added is always a page number.
        if (currPage.isEmpty())
            currPage.add(currLine.stripInvalidCharacters().trim());
        else if (!currLine.isBlank() && !currLine.isStenographer())
            currPage.add(currLine.getText());
        // Sometimes, manual spacing needs to be added.
        // TODO: Why? lol
        if (pages.isEmpty() && !hasLineNumbers) {
            int blankLines = 0;
            if (currLine.getText().matches(LABEL) || currLine.getTime().isPresent())
                blankLines = 2;
            else if (currLine.getSession().isPresent())
                blankLines = 3;
            currPage.addAll(Collections.nCopies(blankLines, ""));
        }
    }
}
