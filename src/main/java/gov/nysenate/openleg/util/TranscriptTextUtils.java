package gov.nysenate.openleg.util;

import gov.nysenate.openleg.processor.transcript.TranscriptLine;

import java.util.ArrayList;
import java.util.List;

public class TranscriptTextUtils
{

    /**
     * Generates pages from transcript text in a common format.
     *
     * <p>Transcript text of all formats are converted into a common single spaced
     * format, and minor errors on the first page are corrected.</p>
     *
     * @param fullText
     * @return
     */
    public static List<List<String>> getPdfFormattedPages(String fullText) {
        List<List<String>> rawPages = getPages(fullText);
        fixErrorsOnFirstPage(rawPages);

        List<List<String>> formattedPages = new ArrayList<List<String>>();
        for (List<String> pageLines : rawPages) {
            if (isFirstPage(pageLines, rawPages) && !pageHasLineNumbers(pageLines)) {
                formattedPages.add(parseWithManualSpacing(pageLines));
            }
            else {
                formattedPages.add(parseWithOriginalSpacing(pageLines));
            }
        }

        return formattedPages;
    }

    /**
     * Parse individual transcript text pages by their page numbers.
     * This solution works for all transcript text formats.
     * @param fullText
     * @return
     */
    private static List<List<String>> getPages(String fullText) {
        List<List<String>> pages = new ArrayList<List<String>>();
        List<String> page = new ArrayList<String>();

        String[] pageLines = fullText.split("\n");
        for (int lineNum = 0; lineNum < pageLines.length; lineNum++) {
            page.add(pageLines[lineNum]);

            if(endOfPage(pageLines, lineNum)) {
                pages.add(page);
                page = new ArrayList<String>();
            }
        }
        return pages;
    }

    private static boolean endOfPage(String[] pageLines, int lineNum) {
        // Ignore the first page number.
        if (lineNum > 10) {
            if (lineNum + 1 < pageLines.length) {
                TranscriptLine nextLine = new TranscriptLine(pageLines[lineNum + 1]);
                if (nextLine.isPageNumber()) {
                    return true;
                }
            }
        }

        if (lineNum + 1 == pageLines.length) {
            return true;
        }

        return false;
    }

    /**
     * Transcripts without line numbers must have their spacing done manually.
     */
    private static List<String> parseWithManualSpacing(List<String> pageLines) {
        List<String> page = new ArrayList<>();
        for (int i = 0; i < pageLines.size(); i++) {
            TranscriptLine line = new TranscriptLine(pageLines.get(i));

            if (line.isPageNumber()) {
                page.add(line.stripInvalidCharacters());
            }
            else if (!line.isEmpty() && !line.isStenographer()) {
                page.add(line.fullText());

                if (line.fullText().trim().equals("NEW YORK STATE SENATE")) {
                    addBlankLines(page, 2);
                }

                else if (line.fullText().trim().contains("STENOGRAPHIC RECORD")) {
                    addBlankLines(page, 2);
                }

                else if (line.isTime()) {
                    addBlankLines(page, 2);
                }

                else if (line.isSession()) {
                    addBlankLines(page, 3);
                }
            }
        }
        return page;
    }

    private static List<String> parseWithOriginalSpacing(List<String> pageLines) {
        List<String> page = new ArrayList<>();

        for (String pageLine : pageLines) {
            TranscriptLine line = new TranscriptLine(pageLine);

            if (line.isPageNumber()) {
                page.add(line.stripInvalidCharacters());
            }
            else if (!line.isEmpty() && !line.isStenographer()) {
                page.add(line.fullText());
            }
        }

        return page;
    }

    /**
     * Fixes a variety of formatting errors that occur on the first page of the original documents.
     */
    private static void fixErrorsOnFirstPage(List<List<String>> pages) {
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

    private static void addBlankLines(List<String> page, int numLines) {
        for (int i = 0; i < numLines; i++) {
            page.add("");
        }
    }

    private static boolean pageHasLineNumbers(List<String> pageLines) {
        for (String pageLine : pageLines) {
            TranscriptLine line = new TranscriptLine(pageLine);
            if (!line.isEmpty() && !line.isPageNumber()) {
                return line.hasLineNumber();
            }
        }
        return false;
    }

    private static boolean isFirstPage(List<String> pageLines, List<List<String>> rawPages) {
        return rawPages.indexOf(pageLines) == 0;
    }

    private static TranscriptLine getNextLine(List<String> pageLines, int i) {
        if (i + 1 < pageLines.size()) {
            return new TranscriptLine(pageLines.get(i + 1));
        }
        return null;
    }
}
