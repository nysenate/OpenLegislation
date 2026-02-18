package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.transcripts.session.DayType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.processors.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashSet;

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
    public void testBillVariations() throws IOException {
        TranscriptId testId = TranscriptId.from(LocalDate.of(2009, 7, 16).atTime(12, 56),
                "REGULAR SESSION");
        String filename = "billVariations.txt";

        LinkedHashSet<BaseBillId> expectedBillIds = new LinkedHashSet<>();
        String[] ids = {"S2", "S3", "S4", "S5", "A7", "A8", "A9", "A10", "J1", "J2", "B10", "B20", "C30", "C40"};
        for (String id : ids) {
            expectedBillIds.add(new BaseBillId(id, 2009));
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

        LinkedHashSet<BaseBillId> expectedBillIds = new LinkedHashSet<>();
        String[] ids = {"S1", "S2", "S3", "S4"};
        for (String id : ids) {
            expectedBillIds.add(new BaseBillId(id, 2009));
        }
        Transcript expectedTranscript = new Transcript(testId, DayType.SESSION, filename, "ALBANY, NEW YORK", "", expectedBillIds);

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
