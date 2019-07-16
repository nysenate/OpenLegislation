package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawChapterCode;
import gov.nysenate.openleg.model.law.LawDocInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LawTitleParser
{
    private final static Logger logger = LoggerFactory.getLogger(LawTitleParser.class);
    private final static String sectionTitlePattern = "(?i)((?:Section|Rule|§)\\s*%s).?\\s(.+?)\\.(.*)";
    private final static String TYPES = "(?i)(SUB)?(ARTICLE|TITLE|PART|RULE|SECTION)";
    private final static Pattern nonSectionPrefixPattern = Pattern.compile("((\\*\\s*)?" + TYPES + "(.+?)(\\\\n|--|\\.))");
    private final static int MAX_WIDTH = 140;
    private final static String WIDTH_WARN = "Document ID {} may have had title \"{}\" parsed incorrectly.";

    /** --- Methods --- */

    public static String extractTitle(LawDocInfo lawDocInfo, String bodyText) {
        if (lawDocInfo != null) {
            switch (lawDocInfo.getDocType()) {
                case CHAPTER:
                    return extractTitleFromChapter(lawDocInfo);
                case SUBTITLE:
                case PART:
                case SUBPART:
                case ARTICLE:
                case SUBARTICLE:
                case RULE:
                case TITLE:
                    return extractTitleFromNonSection(lawDocInfo, bodyText);
                case SECTION:
                    return extractTitleFromSection(lawDocInfo, bodyText);
            }
        }
        return "";
    }

    /**
     * Extract the chapter title using the mapping of law id to LawChapterType if possible.
     */
    private static String extractTitleFromChapter(LawDocInfo docInfo) {
        try {
            return LawChapterCode.valueOf(docInfo.getLawId()).getName();
        }
        catch (IllegalArgumentException ex) {
            return docInfo.getLawId() + " Law";
        }
    }

    /**
     * Parses the title for an article by assuming that most article titles are presented in all caps.
     */
    private static String extractTitleFromNonSection(LawDocInfo docInfo, String bodyText) {
        String title = bodyText;
        // Remove the location designator
        Matcher prefixMatcher = nonSectionPrefixPattern.matcher(bodyText);
        if (prefixMatcher.find())
            title = title.substring(prefixMatcher.end());
        // Removes division names that might come after.
        title = Pattern.compile(TYPES + "\\s+\\w+").split(title)[0];
        title = title.split("\\d+(-|\\w)*\\.")[0];
        title = title.replaceAll("(\\\\n|\\s{2,})", " ");
        if (title.length() > MAX_WIDTH)
            logger.warn(WIDTH_WARN, docInfo.getDocumentId(), title);
        return capitalizeTitle(title.trim());
    }

    /**
     * Extract the title from the section document using a common pattern if applicable or just getting the
     * first line or so.
     */
    private static String extractTitleFromSection(LawDocInfo docInfo, String text) {
        String title = "";
        if (text != null && !text.isEmpty()) {
            int asteriskLoc = docInfo.getDocTypeId().indexOf("*");
            String id = ((asteriskLoc == -1) ?  docInfo.getDocTypeId() :
                    docInfo.getDocTypeId().substring(0, asteriskLoc))
                    .toLowerCase();
            // UCC docs have 2 dashes in the text while the section name only has one.
            if (docInfo.getLawId().equals("UCC"))
                text = text.replaceFirst("--", "-");
            Pattern titlePattern = Pattern.compile(String.format(sectionTitlePattern, id));
            int sectionIdx = text.indexOf("§");
            String trimText = (sectionIdx == -1) ? text.trim() : text.substring(sectionIdx).trim();
            Matcher titleMatcher = titlePattern.matcher(trimText);
            if (titleMatcher.matches())
                title = titleMatcher.group(2).replaceAll("-\\\\n\\s*", "").replaceAll("\\\\n?\\s*", " ");
            else {
                logger.warn("Section title pattern mismatch for document id {}", docInfo.getDocumentId());
                title = "";
            }
        }
        return StringUtils.abbreviate(title, MAX_WIDTH);
    }

    private static String capitalizeTitle(String title) {
        if (title != null && !title.isEmpty()) {
            String capStr = WordUtils.capitalizeFully(title);
            return capStr.substring(0, 1) + Stream.of(capStr.substring(1).split(" "))
                    .map(s -> (s.matches("(Of|Or|The|A|And|An|To)")) ? s.toLowerCase() : s)
                    .collect(Collectors.joining(" "));
        }
        return title;
    }
}