package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.model.hearing.PublicHearingFile;
import gov.nysenate.openleg.util.PublicHearingTextUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class PublicHearingTestHelper
{
    public static File openFile(String fileName) throws URISyntaxException {
        ClassLoader classloader = PublicHearingTestHelper.class.getClassLoader();
        return new File(classloader.getResource("hearing/" + fileName).toURI());
    }

    public static List<List<String>> getPagesFromFileName(String fileName) throws URISyntaxException, IOException {
        File file = openFile(fileName);
        return PublicHearingTextUtils.getPages(FileUtils.readFileToString(file));
    }
}
