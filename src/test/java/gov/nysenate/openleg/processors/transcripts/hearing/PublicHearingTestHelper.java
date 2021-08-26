package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import testing_utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static org.junit.Assert.fail;

public class PublicHearingTestHelper {
    private PublicHearingTestHelper() {};

    public static PublicHearing getHearingFromFilename(String filename) {
        String fullText = "";
        try {
            File file = TestUtils.openTestResource("hearing_transcripts/" + filename);
            fullText = Files.readString(file.toPath());
        }
        catch (URISyntaxException | IOException e) {
            fail();
        }
        return PublicHearingTextUtils.getHearingFromText(filename, fullText);
    }
}
