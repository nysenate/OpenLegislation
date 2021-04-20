package gov.nysenate.openleg.common.util;

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
            "THE STENOGRAPHIC RECORD", 4, "STENOGRAPHIC RECORD",  3, "Secretary", 6);
    private static final Pattern BLANK_LINE_PATTERN = Pattern.compile(".*?(" +
            // First pipe is trimmed off.
            BLANK_LINES.keySet().stream().reduce("", (one, two) -> one + "|" + two).substring(1) + ").*");

    public TranscriptPdfParser(LocalDateTime id, String transcriptText) {
        var lineArrayList = transcriptText.lines().map(TranscriptLine::new)
                .filter(tl -> !tl.getText().isEmpty() && !tl.isStenographer())
                .collect(Collectors.toCollection(ArrayList::new));
        // Second line may be a page number after whitespace, so 3rd line is used.
        this.hasLineNumbers = lineArrayList.size() >= 3 && lineArrayList.get(2).hasLineNumber();
        processLines(lineArrayList);
        if (!pages.get(0).get(0).matches("\\d+"))
            logger.warn("Transcript " + id + " doesn't have a page number on the first page.");
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
            addLine(currLine);
        }
        addLine(lines.get(lines.size() - 1));
        pages.add(currPage);
    }

    /**
     * Tests for certain common formatting issues.
     * @param nextLine to check.
     * @return if this line needs to be combined with the last one.
     */
    private boolean needsCorrecting(TranscriptLine nextLine) {
        if (hasLineNumbers) {
            if (nextLine.isBlank() || nextLine.isPageNumber())
                return false;
            if (!nextLine.hasLineNumber())
                return true;
            return nextLine.getText().matches("\\d+") && Integer.parseInt(nextLine.getText()) != currPage.size() + 1;
        }
        // TODO: second part is to address Transcript 1999-01-12T11:05. Just fix it in database?
        return !nextLine.getText().startsWith(" ") || nextLine.getText().matches(" 5[5-9]");
    }

    /**
     * Some lines need extra lines added after, and some need to be ignored.
     */
    private void addLine(TranscriptLine currLine) {
        if (currLine.isBlank())
            return;
        // The first line added is always a page number.
        if (currPage.isEmpty())
            currPage.add(currLine.stripInvalidCharacters().trim());
        else
            currPage.add(currLine.getText());
        // Sometimes, manual spacing needs to be added.
        if (pages.isEmpty() && !hasLineNumbers) {
            int blankLines = 0;
            Matcher m = BLANK_LINE_PATTERN.matcher(currLine.getText());
            if (m.matches())
                blankLines = BLANK_LINES.get(m.group(1));
            else if (currLine.getTime().isPresent())
                blankLines = 2;
            else if (currLine.getSession().isPresent())
                blankLines = 3;
            currPage.addAll(Collections.nCopies(blankLines, ""));
        }
    }
}
