package gov.nysenate.openleg.util;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;

public class PublicHearingTextUtils
{

    /**
     * Groups public hearing text into pages.
     * @param fullText
     */
    public static List<List<String>> getPages(String fullText) {
        List<List<String>> pages = new ArrayList<>();
        List<String> page = new ArrayList<>();
        List<String> lines = Splitter.on("\n").splitToList(fullText);
        for (String line : lines) {
            page.add(line);
            if (endOfPage(line)) {
                pages.add(page);
                page = new ArrayList<>();
            }
        }
        return pages;
    }

    private static boolean endOfPage(String line) {
        // Check for form feed character.
        if (line.contains("\f")) {
            return true;
        }
        return false;
    }
}
