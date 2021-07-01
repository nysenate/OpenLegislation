package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class TranscriptPdfParserTest {
    private static final String TEST_FILE_DIR = "src/test/resources/transcriptFiles/forPdfParser/",
            NORMAL_LINE_NUM = "2020-01-01T11:00", BLANK_LINE_BEFORE_PAGE_NUM = "1993-03-10T12:10",
            NORMAL_NO_LINE_NUM = "2000-01-05T12:10", ACTING_PRES_ERROR = "1995-03-16T10:00",
            MISPLACED_NUM = "1998-03-10T15:10", ACTING_PRES_ERROR_MISPLACED_LINE_NUM = "1996-06-26T10:00",
            NORMAL_NO_LINE_NUM_1998 = "1998-01-07T12:15";

    private boolean expectedNumberedLines;

    @Test
    public void numberedTranscriptTest() {
        expectedNumberedLines = true;
        testTranscript(NORMAL_LINE_NUM, 3, 26);
        testTranscript(BLANK_LINE_BEFORE_PAGE_NUM, 12, 24);
        testTranscript(ACTING_PRES_ERROR, 3, 24);
        testTranscript(MISPLACED_NUM, 62, 26);
        testTranscript(ACTING_PRES_ERROR_MISPLACED_LINE_NUM, 3, 24);
    }

    @Test
    public void nonNumberedTranscriptText() {
        expectedNumberedLines = false;
        testTranscript(NORMAL_NO_LINE_NUM, 21, 26);
        testTranscript(NORMAL_NO_LINE_NUM_1998, 14, 26, Map.of(1, 18, 2, 20));
    }

    private void testTranscript(String dateTime, int expectedPageCount, int defaultPageLength) {
        testTranscript(dateTime, expectedPageCount, defaultPageLength, Map.of());
    }

    private void testTranscript(String dateTime, int expectedPageCount, int defaultPageLength,
                                Map<Integer, Integer> badPageLengths) {
        String text = "";
        try {
            text = Files.readString(Paths.get(TEST_FILE_DIR + dateTime.replaceAll(":", "")));
        }
        catch (IOException e) {
            fail();
        }
        var pdfParser = new TranscriptPdfParser(LocalDateTime.parse(dateTime), text);
        assertEquals(expectedNumberedLines, pdfParser.hasLineNumbers());
        List<List<String>> pages = pdfParser.getPages();
        assertEquals(expectedPageCount, pages.size());

        // The last page may end short, so it's ignored.
        for (int i = 0; i < pages.size() - 1; i++) {
            var page = pages.get(i);
            assertTrue(page.get(0).matches("\\d+"));
            int expectedPageLength = badPageLengths.getOrDefault(i + 1, defaultPageLength);
            assertEquals(expectedPageLength, page.size());
        }
    }
}
