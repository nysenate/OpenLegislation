package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptFileDao;
import gov.nysenate.openleg.processors.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@Category(IntegrationTest.class)
public class TranscriptParserTest extends BaseTests {
    private final static String[] FILENAMES = {"simple.txt", "noDate.txt", "noTime.txt", "badStartLines.txt"};
    private final static String TEST_DIR = "src/test/resources/transcriptFiles/forParser/",
    FILE_TEXT = "                                                              10\n" +
            "\n         1\n" +
            "\n         2                       ALBANY, NEW YORK\n" +
            "\n         3                       January 1, 1992\n" +
            "\n         4                         10:00 a.m.\n" +
            "\n         5                       REGULAR SESSION\n" +
            "\n         6                       Some more text here.\n";

    @Autowired
    private TranscriptFileDao transcriptFileDao;

    @Autowired
    private TranscriptDataService transcriptDataService;

    @Autowired
    private TranscriptParser parser;

    @Test
    public void testProcess() throws IOException {
        TranscriptId testId = new TranscriptId(LocalDate.of(1992, 1, 1).atTime(10, 0));
        Transcript expectedTranscript = new Transcript(testId, FILENAMES[0],
                "REGULAR SESSION", "ALBANY, NEW YORK", FILE_TEXT);
        TranscriptFile transcriptFile = new TranscriptFile(new File(TEST_DIR + FILENAMES[0]));

        transcriptFileDao.updateTranscriptFile(transcriptFile);
        parser.process(transcriptFile);
        Transcript actualTranscript = transcriptDataService.getTranscript(testId);
        assertEquals(expectedTranscript, actualTranscript);
    }

    @Test
    public void testParseError() throws IOException {
        final TranscriptFile transcriptFile = new TranscriptFile(new File(TEST_DIR + FILENAMES[1]));
        assertThrows(ParseError.class, () -> parser.process(transcriptFile));

        final TranscriptFile transcriptFile2 = new TranscriptFile(new File(TEST_DIR + FILENAMES[2]));
        assertThrows(ParseError.class, () -> parser.process(transcriptFile2));
    }

    @Test
    public void testBadLines() throws IOException {
        TranscriptId testId = new TranscriptId(LocalDate.of(1992, 1, 1).atTime(10, 0));
        Transcript expectedTranscript = new Transcript(testId, FILENAMES[3],
                "REGULAR SESSION", "ALBANY, NEW YORK", FILE_TEXT);
        TranscriptFile transcriptFile = new TranscriptFile(new File(TEST_DIR + FILENAMES[3]));

        transcriptFileDao.updateTranscriptFile(transcriptFile);
        parser.process(transcriptFile);
        Transcript actualTranscript = transcriptDataService.getTranscript(testId);
        assertEquals(expectedTranscript, actualTranscript);
    }
}
