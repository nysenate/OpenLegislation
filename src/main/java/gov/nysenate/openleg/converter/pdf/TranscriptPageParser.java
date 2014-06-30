package gov.nysenate.openleg.converter.pdf;

import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.util.TranscriptLine;

import java.util.ArrayList;
import java.util.List;

public class TranscriptPageParser
{

    public List<TranscriptPage> parsePages(Transcript transcript) {
        List<TranscriptPage> pages = new ArrayList<TranscriptPage>();
        List<List<String>> rawPages = splitPages(transcript.getTranscriptText());

        fixErrorsOnFirstPage(rawPages);

        for (List<String> pageLines : rawPages) {
            if (isFirstPage(pageLines, rawPages) && !pageHasLineNumbers(pageLines)) {
                pages.add(parseWithManualSpacing(pageLines));
            }
            else {
                pages.add(parseWithOriginalSpacing(pageLines));
            }
        }

        return pages;
    }

    /**
     * Transcripts without line numbers must have their spacing done manually.
     */
    private TranscriptPage parseWithManualSpacing(List<String> pageLines) {
        TranscriptPage page = new TranscriptPage();
        int lineCount = 0;
        for (int i = 0; i < pageLines.size(); i++) {
            TranscriptLine line = new TranscriptLine(pageLines.get(i));

            if (line.isTranscriptNumber()) {
                page.setTranscriptNumber(line.removeInvalidCharacters());
                lineCount++;
            }
            else if (!line.isEmpty() && !line.isStenographer()) {
                page.addLine(line);
                lineCount++;

                if (line.fullText().trim().equals("NEW YORK STATE SENATE")) {
                    addBlankLines(page, 2);
                    lineCount += 2;
                }

                else if (line.fullText().trim().contains("STENOGRAPHIC RECORD")) {
                    addBlankLines(page, 2);
                    lineCount += 2;
                }

                else if (line.isTime()) {
                    addBlankLines(page, 2);
                    lineCount += 2;
                }

                else if (line.isSession()) {
                    addBlankLines(page, 3);
                    lineCount += 3;
                }
            }
        }

        page.setLineCount(lineCount);
        return page;
    }

    private TranscriptPage parseWithOriginalSpacing(List<String> pageLines) {
        TranscriptPage page = new TranscriptPage();
        int lineCount = 0;

        for (String pageLine : pageLines) {
            TranscriptLine line = new TranscriptLine(pageLine);

            if (line.isTranscriptNumber()) {
                page.setTranscriptNumber(line.removeInvalidCharacters());
                lineCount++;
            }
            else if (!line.isEmpty() && !line.isStenographer()) {
                page.addLine(line);
                lineCount++;
            }
        }

        page.setLineCount(lineCount);
        return page;
    }

    /**
     * Fixes a variety of formatting errors that occur on the first page of original documents.
     */
    private void fixErrorsOnFirstPage(List<List<String>> pages) {
        List<String> correctedFirstPage = new ArrayList<String>();
        List<String> firstPage = pages.get(0);

        for (int i = 0; i < firstPage.size(); i++) {
            TranscriptLine line = new TranscriptLine(firstPage.get(i));

            if (!line.isEmpty()) {
                if (line.fullText().endsWith(",") || line.fullText().endsWith(", Acting")) {
                    // Combine two lines into one; corrects formatting. i.e. 123096.v1
                    TranscriptLine nextLine = getNextLine(firstPage, i);
                    if (nextLine.fullText().trim().equals("President") || nextLine.fullText().trim().equals("Acting President")) {
                        line = new TranscriptLine(line.fullText() + " " + nextLine.fullText().trim());
                        // Skip next line since we combined it with the previous line.
                        i++;
                    }
                }

                correctedFirstPage.add(line.fullText());
            }
        }

        pages.set(pages.indexOf(firstPage), correctedFirstPage);
    }

    private void addBlankLines(TranscriptPage page, int numLines) {
        for (int i = 0; i < numLines; i++) {
            TranscriptLine blankLine = new TranscriptLine(" ");
            page.addLine(blankLine);
        }
    }

    private boolean pageHasLineNumbers(List<String> pageLines) {
        for (String pageLine : pageLines) {
            TranscriptLine line = new TranscriptLine(pageLine);
            if (!line.isEmpty() && !line.isTranscriptNumber()) {
                return line.hasLineNumber();
            }
        }
        return false;
    }


    private boolean isFirstPage(List<String> pageLines, List<List<String>> rawPages) {
        return rawPages.indexOf(pageLines) == 0;
    }

    private TranscriptLine getNextLine(List<String> pageLines, int i) {
        if (i + 1 < pageLines.size()) {
            return new TranscriptLine(pageLines.get(i + 1));
        }
        return null;
    }

    /**
     * Split text into 'pages' based on the transcript number which is the first line on each page.
     */
    private List<List<String>> splitPages(String transcriptText) {
        List<String> page = new ArrayList<String>();
        List<List<String>> pages = new ArrayList<List<String>>();

        String[] line = transcriptText.split("\n");
        TranscriptLine nextLine;
        for (int i = 0; i < line.length; i++) {
            page.add(line[i]);

            // Ignore the first transcript number.
            if (i > 10) {
                if (i + 1 < line.length) {
                    nextLine = new TranscriptLine(line[i + 1]);

                    if (nextLine.isTranscriptNumber()) {
                        pages.add(page);
                        page = new ArrayList<String>();
                    }
                }
            }

            // add the last page.
            if (i == line.length - 1) {
                pages.add(page);
            }
        }
        return pages;
    }
}
