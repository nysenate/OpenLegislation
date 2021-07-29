package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import testing_utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.fail;

public class PublicHearingTestHelper {
    public static List<List<String>> getPagesFromFileName(String filename) throws URISyntaxException, IOException {
        File file = TestUtils.openTestResource("hearing/" + filename);
        return PublicHearing.getPages(Files.readString(file.toPath()));
    }

    public static PublicHearing getHearingFromFilename(String filename) {
        String fullText = "";
        try {
            File file = TestUtils.openTestResource("hearing/" + filename);
            fullText = Files.readString(file.toPath());
        }
        catch (URISyntaxException | IOException e) {
            fail();
        }
        var id = new PublicHearingId(filename);
        // TODO: fix
        return PublicHearingTextUtils.getHearingFromText(id, fullText);
    }
}
