package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawDocInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LawTitleParser
{
    private static final Logger logger = LoggerFactory.getLogger(LawTitleParser.class);

    /** The law files are most likely sent in CP850 encoding. */
    protected static Charset LAWFILE_CHARSET = Charset.forName("CP850");

    protected static String sectionTitlePattern = "(?i)((?:Section|§)\\s*%s).?\\s(.+?)\\.(.*)";

    /** --- Instance Variables --- */

    /** --- Constructors --- */

    /** --- Methods --- */

    /**
     *
     *
     * @param text
     * @return
     */
    public static String extractTitleFromSection(LawDocInfo docInfo, String text) {
        String title = "";
        if (text != null && !text.isEmpty()) {
            int asteriskLoc = docInfo.getLocationId().indexOf("*");
            String locationId = (asteriskLoc != -1)
                                ? docInfo.getLocationId().substring(0, asteriskLoc) : docInfo.getLocationId();
            Pattern titlePattern = Pattern.compile(String.format(sectionTitlePattern, locationId.toLowerCase()));
            int sectionIdx = text.indexOf("§");
            String trimText = (sectionIdx != -1) ? text.substring(sectionIdx).trim() : text.trim();
            Matcher titleMatcher = titlePattern.matcher(trimText);
            if (titleMatcher.matches()) {
                title = titleMatcher.group(2).replaceAll("-\\\\n\\s*", "").replaceAll("\\\\n?\\s*", " ");
            }
            else {
                logger.warn("Section title pattern mismatch for {} with locId {} against {}",docInfo.getDocumentId(), locationId, trimText);
            }
        }
        return StringUtils.abbreviate(title, 140);
    }

    /** --- Internal Methods --- */

}