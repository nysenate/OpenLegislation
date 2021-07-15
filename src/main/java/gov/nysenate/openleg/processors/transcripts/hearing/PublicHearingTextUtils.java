package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
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

    public static PublicHearing getHearingFromText(PublicHearingId hearingId, String fullText, List<CommitteeSessionId> comSessionIds) {
        List<List<String>> pages = PublicHearing.getPages(fullText);
        if (pages.size() < 2)
            throw new ParseError("Public hearing in file " + hearingId.getFileName() + " is too short.");
        boolean isWrongFormat = pages.get(1).stream().anyMatch(str -> str.contains("Geneva Worldwide, Inc."));
        List<String> dataList = getDataList(pages.get(0), isWrongFormat);
        // Retrieves information from text.
        String[] addrDateTime = getAddrDateTime(dataList.get(dataList.size() - 1), isWrongFormat);
        boolean hasAddress = addrDateTime.length > 1;
        var dateTimeParser = new PublicHearingDateTimeParser(addrDateTime[hasAddress ? 1 : 0],
                pages.get(pages.size() - 1));

        // Set the data.
        var hearing = new PublicHearing(hearingId, dateTimeParser.getDate(), fullText);
        hearing.setStartTime(dateTimeParser.getStartTime());
        hearing.setEndTime(dateTimeParser.getEndTime());
        String title = dataList.size() < 2 ? "No title" : dataList.get(dataList.size() - 2);
        hearing.setTitle(title.replaceAll("\\s+", " ").trim());
        hearing.setHosts(HearingHostParser.parse(dataList.get(0)));
        setAddress(hasAddress, addrDateTime[0], hearing);

        // TODO: remove
        System.out.println("**********");
        System.out.println(dataList.get(0));
        System.out.println(hearing.getHosts());
        System.out.println("**********");
        System.out.println();

        return hearing;
    }

    /**
     * The first page should be split into parts, for easier data processing.
     * @param firstPage to pull data sections from.
     * @param isWrongFormat to indicate a different stenographer that uses a different format.
     * @return the list of data sections.
     */
    private static List<String> getDataList(List<String> firstPage, boolean isWrongFormat) {
        String pageText = firstPage.stream().map(str -> str.replaceAll(LINE_NUM, ""))
                .collect(Collectors.joining("\n")).split("PRESIDING|PRESENT|SPONSORS")[0];
        String splitPattern = isWrongFormat ? "\n{5,}" : "-{10,}";
        return Arrays.stream(pageText.split(splitPattern, 3)).map(String::trim)
                .filter(str -> !str.isEmpty()).collect(Collectors.toList());
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
