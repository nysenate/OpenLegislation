package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
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
    public void testParseError() {
        assertThrows(ParseError.class, () -> processFilename("noDate.txt"));
        assertThrows(ParseError.class, () -> processFilename("noTime.txt"));
    }

    private static Transcript processFilename(String filename) throws IOException {
        return TranscriptParser.parse(new TranscriptFile(new File(TEST_DIR + filename)));
    }
}
