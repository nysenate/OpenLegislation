package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.TestUtils;
import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

public class PublicHearingTestHelper
{
    public static List<List<String>> getPagesFromFileName(String filename) throws URISyntaxException, IOException {
        File file = TestUtils.openTestResource("hearing/" + filename);
        return PublicHearingTextUtils.getPages(FileUtils.readFileToString(file, Charset.defaultCharset()));
    }
}
