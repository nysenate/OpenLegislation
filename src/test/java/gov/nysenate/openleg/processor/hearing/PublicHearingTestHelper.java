package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.TestUtils;
import gov.nysenate.openleg.util.FileIOUtils;
import gov.nysenate.openleg.util.PublicHearingTextUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class PublicHearingTestHelper
{
    public static List<List<String>> getPagesFromFileName(String fileName) throws URISyntaxException, IOException {
        File file = TestUtils.openTestResource("hearing/" + fileName);
        return PublicHearingTextUtils.getPages(FileUtils.readFileToString(file));
    }
}
