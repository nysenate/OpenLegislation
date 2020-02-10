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
            "^([\\w ,-]+ +(New York(, )?|NY){1,2}[\\d -]*)$|(Cattaraugus County Reservation)$");
    private static final int INVALID_INDEX = -1;

    /**
     * Extract the Address information from the first page of a PublicHearing.
     * @param firstPage of the hearing.
     * @return the address where the hearing took place.
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

        StringBuilder address = new StringBuilder();
        for (int i = firstLineOfAddress; i <= lastLineOfAddress; i++) {
            address.append(PublicHearingTextUtils.stripLineNumber(firstPage.get(i)));
            // Keep address multiline format.
            if (i != lastLineOfAddress) {
                address.append("\n");
            }
        }
        return address.toString();
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
