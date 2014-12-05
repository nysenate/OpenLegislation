package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.util.PublicHearingTextUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PublicHearingAddressParser
{

    private static final Pattern LAST_ADDRESS_LINE = Pattern.compile(
            "^(\\w+( \\w+)?, (New York|NY)( +)?(\\d+)?)|(Cattaraugus County Reservation)$");

    private static final int INVALID_INDEX = -1;

    /**
     * Extract the Address information from the first page of a PublicHearing.
     * @param firstPage
     * @return
     */
    public String parse(List<String> firstPage) {
        String address = null;
        for (int i = 0; i < firstPage.size(); i++) {
            String line = PublicHearingTextUtils.stripLineNumber(firstPage.get(i));
            if (matchesLastAddressLine(line)) {
                address = extractAddress(firstPage, i);
            }
        }
        return address;
    }

    private String extractAddress(List<String> firstPage, int lastLineOfAddress) {
        int firstLineOfAddress = getFirstLineOfAddress(firstPage, lastLineOfAddress);
        if (firstLineOfAddress == INVALID_INDEX) {
            return null;
        }

        String address = "";
        for (int i = firstLineOfAddress; i <= lastLineOfAddress; i++) {
            address += PublicHearingTextUtils.stripLineNumber(firstPage.get(i));
            // Keep address multiline format.
            if (i != lastLineOfAddress) {
                address += "\n";
            }
        }
        return address;
    }

    /** Always a blank line before address info */
    private int getFirstLineOfAddress(List<String> firstPage, int lastLineOfAddress) {
        for (int i = lastLineOfAddress; i >= 0; i--) {
            String line = firstPage.get(i);
            if (!PublicHearingTextUtils.hasContent(line)) {
                return ++i;
            }
        }
        return INVALID_INDEX;
    }

    private boolean matchesLastAddressLine(String line) {
        Matcher addressMatcher = LAST_ADDRESS_LINE.matcher(line);
        return addressMatcher.find();
    }
}
