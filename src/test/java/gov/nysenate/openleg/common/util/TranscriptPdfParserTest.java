package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// TODO: to UnitTest
@Category(IntegrationTest.class)
public class TranscriptPdfParserTest extends BaseTests {
    private static final String TEST_FILE_DIR = "src/test/resources/transcriptFiles/";
    private static final String NORMAL_TRANSCRIPT = "2020-01-01T1100.v1";

    @Autowired
    private TranscriptDataService transcriptDataService;

    @Test
    public void spacingTest() {
        var ids = transcriptDataService.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        for (var id : ids) {
            Transcript t = transcriptDataService.getTranscript(id);
            var parser = new TranscriptPdfParser(t.getDateTime(), t.getText());
            if (parser.getPages().get(1).size() != 26)
                System.out.println(t.getDateTime());
        }
    }

    @Test
    public void normalTranscriptText() throws IOException {
        var dateTime = LocalDate.of(2020, 1, 1).atStartOfDay();
        List<List<String>> pages = new TranscriptPdfParser(dateTime, getText(NORMAL_TRANSCRIPT)).getPages();
        assertEquals(3, pages.size());
        for (var page : pages) {
            assertTrue(page.get(0).matches("\\d+"));
            assertEquals(26, page.size());
        }
    }

    private static String getText(String filename) throws IOException {
        return Files.readString(Paths.get(TEST_FILE_DIR + filename));
    }
}
