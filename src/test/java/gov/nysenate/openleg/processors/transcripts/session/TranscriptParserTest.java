package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
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
    private static final String TEST_DIR = "src/test/resources/transcriptFiles/forParser/",
    FILE_TEXT = """
                                                                          10

                     1

                     2                       ALBANY, NEW YORK

                     3                       January 1, 1992

                     4                         10:00 a.m.

                     5                       REGULAR SESSION

                     6                       Some more text here.
            """;

    @Test
    public void testProcess() throws IOException {
        TranscriptId testId = TranscriptId.from(LocalDate.of(1992, 1, 1).atTime(10, 0),
                "REGULAR SESSION");
        String filename = "simple.txt";
        Transcript expectedTranscript = new Transcript(testId, null, filename, "ALBANY, NEW YORK", FILE_TEXT);
        Transcript actualTranscript = processFilename(filename);
        assertEquals(expectedTranscript, actualTranscript);
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
