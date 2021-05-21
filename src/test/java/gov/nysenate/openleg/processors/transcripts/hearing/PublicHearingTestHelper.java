package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import org.apache.commons.io.FileUtils;
import testing_utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.fail;

public class PublicHearingTestHelper {
    public static List<List<String>> getPagesFromFileName(String filename) throws URISyntaxException, IOException {
        File file = TestUtils.openTestResource("hearing/" + filename);
        return PublicHearingTextUtils.getPages(FileUtils.readFileToString(file, Charset.defaultCharset()));
    }

    public static PublicHearing getHearingFromFilename(String filename) {
        String fullText = "";
        try {
            File file = TestUtils.openTestResource("hearing/" + filename);
            fullText = Files.readString(file.toPath());
        }
        catch (Exception e) {
            fail();
        }
        var id = new PublicHearingId(filename);
        return PublicHearingTextUtils.getHearingFromText(id, fullText);
    }
}
