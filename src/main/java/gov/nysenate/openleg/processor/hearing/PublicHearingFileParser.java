package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.model.hearing.PublicHearingFile;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PublicHearingFileParser
{
    /**
     * Groups the PublicHearingFile's raw text into Lists of Strings representing pages.
     * @param publicHearingFile
     * @return A List of a List of Strings representing a page.
     * @throws java.io.IOException
     */
    public List<List<String>> getPublicHearingPages(PublicHearingFile publicHearingFile) throws IOException {
        List<List<String>> pages = new ArrayList<List<String>>();
        List<String> lines = FileUtils.readLines(publicHearingFile.getFile(), "latin1");

        List<String> page = new ArrayList<String>();
        for (String line : lines) {
            page.add(line);
            if (endOfPage(line)) {
                pages.add(page);
                page = new ArrayList<String>();
            }
        }
        return pages;
    }

    /**
     * Determines if the given line contains an end of page character.
     * @param line
     * @return
     */
    private boolean endOfPage(String line) {
        char newPageChar = 12;
        return line.contains(String.valueOf(newPageChar)) ? true : false;
    }
}
