package gov.nysenate.openleg.legislation.transcripts.hearing;

import com.google.common.base.Splitter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PublicHearingTextUtils {
    public static final List<String> STENOGRAPHER_LINES = List.of("Geneva Worldwide, Inc.", "256 West 38 t h Street, 10 t h Floor, New York, NY 10018");
    private static final String WRONG_FORMAT_REGEX = STENOGRAPHER_LINES.get(0) + "|" + STENOGRAPHER_LINES.get(1);
    // This is a utility class.
    private PublicHearingTextUtils() {}

    public static boolean isWrongFormat(List<List<String>> pages) {
        return pages.get(pages.size() - 2).stream().anyMatch(str -> str.trim().matches(WRONG_FORMAT_REGEX));
    }

    /**
     * Groups public hearing text into pages, which are lists of lines.
     * @param fullText of the hearing.
     */
    public static List<List<String>> getPages(String fullText) {
        fullText = fullText.replaceAll("\r\n", "\n");
        return Splitter.on("\f").splitToList(fullText).stream().map(PublicHearingTextUtils::getLines)
                .filter(page -> !page.isEmpty()).toList();
    }

    private static List<String> getLines(String page) {
        LinkedList<String> lineList = Splitter.on("\n").splitToList(page).stream()
                .dropWhile(String::isEmpty).collect(Collectors.toCollection(LinkedList::new));
        if (lineList.isEmpty())
            return lineList;
        // Drops empty Strings from the end of the list as well.
        while (lineList.getLast().isEmpty())
            lineList.removeLast();
        // Adds a dummy line if there's no page number.
        if (!lineList.peekFirst().trim().matches("(?i)(Page )?\\d+"))
            lineList.addFirst("");
        return lineList;
    }
}
