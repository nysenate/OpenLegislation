package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.processors.transcripts.session.TranscriptLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TranscriptPdfParser {
    private static final String LABEL = "(\\s*NEW YORK STATE SENATE\\s*)|(.*STENOGRAPHIC RECORD.*)";
    public final boolean hasLineNumbers;
    private final List<List<String>> pages = new ArrayList<>();
    private List<String> currPage = new ArrayList<>();

    public TranscriptPdfParser(String transcriptText) {
        var lineArrayList = transcriptText.lines().map(TranscriptLine::new)
                .filter(tl -> !tl.getText().isEmpty() && !tl.isStenographer())
                .collect(Collectors.toCollection(ArrayList::new));
        this.hasLineNumbers = transcriptHasLineNumbers(lineArrayList);
        processLines(lineArrayList);
    }

    public List<List<String>> getPages() {
        return pages;
    }

    // TODO: could be better, I think.
    private static boolean transcriptHasLineNumbers(ArrayList<TranscriptLine> lines) {
        if (lines.size() < 3)
            return false;
        return lines.get(1).hasLineNumber() || lines.get(2).hasLineNumber();
    }

    private void processLines(ArrayList<TranscriptLine> lines) {
        // The last line won't need correction.
        for (int i = 0; i < lines.size() - 1; i++) {
            TranscriptLine currLine = lines.get(i), nextLine = lines.get(i + 1);
            Matcher m = Pattern.compile(" *((?:Acting ?|President ?){1,2})(.*)").matcher(nextLine.getText());
            if (hasLineNumbers && !nextLine.hasLineNumber() && m.find()) {
                currLine = new TranscriptLine(currLine.getText() + " " + m.group(1));
                nextLine = new TranscriptLine(m.group(2));
                lines.set(i + 1, nextLine);
            }
            if (needsCorrecting(nextLine)) {
                currLine = new TranscriptLine(currLine.getText() + " " + nextLine.getText());
                lines.remove(i + 1);
            }
            if (currLine.isPageNumber() && !currPage.isEmpty()) {
                pages.add(currPage);
                currPage = new ArrayList<>();
            }
            if (!currLine.isBlank())
                addLine(currLine);
        }
        addLine(lines.get(lines.size() - 1));
        pages.add(currPage);
    }

    private boolean needsCorrecting(TranscriptLine nextLine) {
        if (hasLineNumbers) {
            if (nextLine.isBlank() || nextLine.isPageNumber())
                return false;
            if (!nextLine.hasLineNumber())
                return true;
            return nextLine.getText().matches("\\d+") && Integer.parseInt(nextLine.getText()) != currPage.size() + 1;
        }
        // Second part used to fix Transcript 1999-01-12T11:05.
        return !nextLine.getText().startsWith(" ") || nextLine.getText().matches(" 5[5-9]");
    }

    /**
     * Some lines need extra lines added after, and some need to be ignored.
     */
    private void addLine(TranscriptLine currLine) {
        // The first line added is always a page number.
        if (currPage.isEmpty())
            currPage.add(currLine.stripInvalidCharacters().trim());
        else
            currPage.add(currLine.getText());
        // Sometimes, manual spacing needs to be added.
        // TODO: Better normalize first pages.
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
