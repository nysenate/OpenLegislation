package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingTextUtils;
import gov.nysenate.openleg.processors.ParseError;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PublicHearingParser {
    private static final Charset CP_1252 = Charsets.toCharset("CP1252");
    private static final String LINE_NUM = "^\\s*\\d{0,2}(\\s+|$)";
    private static final Pattern VIRTUAL_HEARING = Pattern.compile("(VIRTUAL|ONLINE).+HEARING");
    private PublicHearingParser() {}

    /**
     * Parses a {@link PublicHearingFile}, extracting a
     * {@link PublicHearing PublicHearing}.
     * @param publicHearingFile
     * @throws IOException
     */
    public static PublicHearing process(PublicHearingFile publicHearingFile) throws IOException {
        String fullText = Files.readString(publicHearingFile.getFile().toPath(), CP_1252);
        return getHearingFromText(publicHearingFile.getFileName(), fullText);
    }

    protected static PublicHearing getHearingFromText(String filename, String fullText) {
        List<List<String>> pages = PublicHearingTextUtils.getPages(fullText);
        if (pages.size() < 2)
            throw new ParseError("Public hearing in file " + filename + " is too short.");
        boolean isWrongFormat = PublicHearingTextUtils.isWrongFormat(pages);
        List<String> dataList = getDataList(pages.get(0), isWrongFormat);
        // Retrieves address, date, and time data from text.
        String[] placeTimeData = getAddressAndDateTime(dataList.get(dataList.size() - 1), isWrongFormat);
        boolean hasAddress = placeTimeData.length > 1;
        var dateTimeParser = new PublicHearingDateTimeParser(placeTimeData[hasAddress ? 1 : 0],
                pages.get(pages.size() - 1));

        // Set the data.
        String title = dataList.size() < 2 ? "No title" : dataList.get(dataList.size() - 2)
                .replaceAll("\\s+", " ").trim();
        String address = "No address";
        if (hasAddress)
            address = placeTimeData[0];
        else if (VIRTUAL_HEARING.matcher(title).find())
            address = "Virtual Hearing";
        var hearing = new PublicHearing(filename, fullText, title, address,
                dateTimeParser.getDate(), dateTimeParser.getStartTime(), dateTimeParser.getEndTime());
        hearing.setHosts(HearingHostParser.parse(dataList.get(0)));
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
                .filter(str -> !str.isEmpty()).toList();
    }

    /**
     * Splits out hearing information from text.
     * @param toSplit initial data.
     * @param isWrongFormat if corrections need to be made.
     * @return an array with the address, date, and time.
     */
    private static String[] getAddressAndDateTime(String toSplit, boolean isWrongFormat) {
        if (isWrongFormat) {
            String reversed = new StringBuilder(toSplit).reverse().toString();
            reversed = reversed.replaceFirst("\n", "");
            toSplit = new StringBuilder(reversed).reverse().toString();
        }
        return toSplit.split("\n{2,}");
    }
}
