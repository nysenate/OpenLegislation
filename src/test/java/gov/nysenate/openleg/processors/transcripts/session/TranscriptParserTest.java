package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.transcripts.session.*;
import gov.nysenate.openleg.processors.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@Category(UnitTest.class)
public class TranscriptParserTest {
    private static final String TEST_DIR = "src/test/resources/transcriptFiles/forParser/";

    @Test
    public void testProcess() throws IOException {
        TranscriptId testId = TranscriptId.from(LocalDate.of(2021, 12, 31).atTime(11, 0),
                "REGULAR SESSION");
        String filename = "SenateLD123121.txt";
        Transcript expectedTranscript = new Transcript(testId, DayType.LEGISLATIVE, filename, "ALBANY, NEW YORK", "");
        Transcript actualTranscript = processFilename(filename);
        assertEquals(expectedTranscript.getId(), actualTranscript.getId());
        assertEquals(expectedTranscript.getDayType(), actualTranscript.getDayType());
        assertEquals(expectedTranscript.getLocation(), actualTranscript.getLocation());
    }

    @Test
    public void testNoBillInformation() throws IOException {
        TranscriptId testId = TranscriptId.from(LocalDate.of(2007, 7, 16).atTime(12, 56),
                "REGULAR SESSION");
        String filename = "billsBefore2009.txt";

        Transcript expectedTranscript = new Transcript(testId, DayType.SESSION, filename, "ALBANY, NEW YORK", "");

        Transcript actualTranscript = processFilename(filename);

        assertEquals(expectedTranscript.getLinkedBills(), actualTranscript.getLinkedBills());
    }

    @Test
    public void testBillVariations() throws IOException {
        TranscriptId testId = TranscriptId.from(LocalDate.of(2009, 7, 16).atTime(12, 56),
                "REGULAR SESSION");
        String filename = "billVariations.txt";

        final int startingLineNum = 9;
        var expectedBillIds = new ArrayList<BillMention>();
        String[] ids = {"S2", "S3", "S4", "S5", "S6A", "S6B", "A6", "A7", "A8", "A9", "A10", "J1", "J2", "R3", "R4", "B10", "B20", "C30", "C40"};
        for (int lineNum = startingLineNum; lineNum <= 27; lineNum++) {
            var currId = new BillId(ids[lineNum - startingLineNum], 2009);
            expectedBillIds.add(new BillMention(currId, new Position(1, lineNum)));
        }
        Transcript expectedTranscript = new Transcript(testId, DayType.SESSION, filename, "ALBANY, NEW YORK", "", expectedBillIds);

        Transcript actualTranscript = processFilename(filename);

        assertEquals(expectedTranscript.getLinkedBills(), actualTranscript.getLinkedBills());
    }

    @Test
    public void testBillsAcrossLines() throws IOException {
        TranscriptId testId = TranscriptId.from(LocalDate.of(2009, 7, 16).atTime(12, 56),
                "REGULAR SESSION");
        String filename = "billsAcrossLines.txt";

        LinkedHashSet<BillId> expectedBillIds = new LinkedHashSet<>();
        String[] ids = {"S1", "S2", "S3", "S4", "R8"};
        for (String id : ids) {
            expectedBillIds.add(new BillId(id, 2009));
        }
        Transcript expectedTranscript = new Transcript(testId, DayType.SESSION, filename, "ALBANY, NEW YORK", "", List.of());

        Transcript actualTranscript = processFilename(filename);

        assertEquals(expectedTranscript.getLinkedBills(), actualTranscript.getLinkedBills());
    }

    @Test
    public void testParseError() {
        assertThrows(ParseError.class, () -> processFilename("noDate.txt"));
        assertThrows(ParseError.class, () -> processFilename("noTime.txt"));
    }

    private static Transcript processFilename(String filename) throws IOException {
        return TranscriptParser.parse(new TranscriptFile(new File(TEST_DIR + filename)));
    }
}
