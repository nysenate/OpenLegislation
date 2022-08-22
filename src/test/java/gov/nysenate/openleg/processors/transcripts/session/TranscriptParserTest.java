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

import static gov.nysenate.openleg.processors.transcripts.session.TranscriptParser.process;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@Category(UnitTest.class)
public class TranscriptParserTest {
    private static final String[] FILENAMES = {"simple.txt", "noDate.txt", "noTime.txt"};
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
        TranscriptId testId = new TranscriptId(LocalDate.of(1992, 1, 1).atTime(10, 0),
                "Regular Session");
        Transcript expectedTranscript = new Transcript(testId, FILENAMES[0], "ALBANY, NEW YORK", FILE_TEXT);
        TranscriptFile transcriptFile = new TranscriptFile(new File(TEST_DIR + FILENAMES[0]));
        Transcript actualTranscript = process(transcriptFile);
        assertEquals(expectedTranscript, actualTranscript);
    }

    @Test
    public void testParseError() throws IOException {
        final TranscriptFile transcriptFile = new TranscriptFile(new File(TEST_DIR + FILENAMES[1]));
        assertThrows(ParseError.class, () -> process(transcriptFile));

        final TranscriptFile transcriptFile2 = new TranscriptFile(new File(TEST_DIR + FILENAMES[2]));
        assertThrows(ParseError.class, () -> process(transcriptFile2));
    }

    // TODO: remove
    @Test
    public void TESTTEST() throws IOException {
        process(new TranscriptFile(new File(TEST_DIR + "031793.v1")));
    }
}
