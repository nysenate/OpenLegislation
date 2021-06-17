package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.processors.ParseError;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PublicHearingTextUtils {
    private static final String LINE_NUM = "^\\s*\\d{0,2}(\\s+|$)";
    // This is a utility class.
    private PublicHearingTextUtils() {}

    public static PublicHearing getHearingFromText(PublicHearingId id, String fullText) {
        List<List<String>> pages = PublicHearing.getPages(fullText);
        List<String> firstPage = pages.get(0);
        if (pages.size() < 2)
            throw new ParseError("Public hearing in file " + id.getFileName() + " is too short.");
        boolean isWrongFormat = pages.get(1).stream().anyMatch(str -> str.contains("Geneva Worldwide, Inc."));
        // Splits up the data.
        String pageText = firstPage.stream().map(str -> str.replaceAll(LINE_NUM, ""))
                .collect(Collectors.joining("\n")).split("PRESIDING|PRESENT|SPONSORS")[0];
        String splitPattern = isWrongFormat ? "\n{5,}" : "-{10,}";
        List<String> dashSplit = Arrays.stream(pageText.split(splitPattern, 3)).map(String::trim)
                .filter(str -> !str.isEmpty()).collect(Collectors.toList());
        // Retrieves information from text.
        String[] addrDateTime = getAddrDateTime(dashSplit.get(dashSplit.size() - 1), isWrongFormat);
        boolean hasAddress = addrDateTime.length > 1;
        var dateTimeParser = new PublicHearingDateTimeParser(addrDateTime[hasAddress ? 1 : 0],
                pages.get(pages.size() - 1));
        var hearing = new PublicHearing(id, dateTimeParser.getDate(), fullText);
        hearing.setStartTime(dateTimeParser.getStartTime());
        hearing.setEndTime(dateTimeParser.getEndTime());
        hearing.setCommittees(PublicHearingCommitteeParser.parse(dashSplit.get(0)));
        String title = dashSplit.size() < 2 ? "No title" : dashSplit.get(dashSplit.size() - 2);
        hearing.setTitle(title.replaceAll("\\s+", " ").trim());
        setAddress(hasAddress, addrDateTime[0], hearing);
        return hearing;
    }

    /**
     * Splits out hearing information from text.
     * @param toSplit initial data.
     * @param isWrongFormat if corrections need to be made.
     * @return an array with the address, date, and time.
     */
    private static String[] getAddrDateTime(String toSplit, boolean isWrongFormat) {
        if (isWrongFormat) {
            String reversed = new StringBuilder(toSplit).reverse().toString();
            reversed = reversed.replaceFirst("\n", "");
            toSplit = new StringBuilder(reversed).reverse().toString();
        }
        return toSplit.split("\n{2,}");
    }

    private static void setAddress(boolean hasAddress, String possibleAddress, PublicHearing hearing) {
        String address;
        if (hasAddress)
            address = possibleAddress;
        else if (hearing.getTitle().contains("(VIRTUAL|ONLINE).+HEARING"))
            address =  "Virtual Hearing";
        else
            address = "No address";
        hearing.setAddress(address);
    }
}
