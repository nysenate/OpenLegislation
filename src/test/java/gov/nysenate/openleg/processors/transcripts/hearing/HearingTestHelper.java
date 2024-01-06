package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import testing_utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static org.junit.Assert.fail;

public final class HearingTestHelper {
    private HearingTestHelper() {}

    public static Hearing getHearingFromFilename(String filename) {
        String fullText = "";
        try {
            File file = TestUtils.openTestResource("hearingTranscripts/" + filename);
            fullText = Files.readString(file.toPath());
        }
        catch (URISyntaxException | IOException e) {
            fail();
        }
        return HearingParser.getHearingFromText(filename, fullText);
    }
}
