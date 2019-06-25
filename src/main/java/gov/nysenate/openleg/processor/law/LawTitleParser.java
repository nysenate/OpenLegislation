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

    private final static String sectionTitlePattern = "(?i)((?:Section|§)\\s*%s).?\\s(.+?)\\.(.*)";
    private final static Pattern nonSectionPrefixPattern = Pattern.compile("((\\*\\s*)?(SUB)?(ARTICLE|TITLE|PART|RULE)(.+?)(\\\\n|--))");
    private final static Pattern uppercasePattern = Pattern.compile("([A-Z]{2,})");
    private final static Pattern endOfUppercasePattern = Pattern.compile("((\\\\n\\s*(\\d+)?(.)?\\s*[A-Z]{1}[a-z]+)|(\\\\nTITLE))");

    /** --- Methods --- */

    public static String extractTitle(LawDocInfo lawDocInfo, String bodyText) {
        if (lawDocInfo != null) {
            switch (lawDocInfo.getDocType()) {
                case CHAPTER:
                    return extractTitleFromChapter(lawDocInfo);
                case SUBTITLE:
                case PART:
                case SUB_PART:
                case ARTICLE:
                case RULE:
                case TITLE:
                    return extractTitleFromNonSection(bodyText);
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
            LawChapterCode chapterType = LawChapterCode.valueOf(docInfo.getLawId());
            return chapterType.getName();
        }
        catch (IllegalArgumentException ex) {
            return docInfo.getLawId() + " Law";
        }
    }

    /**
     * Parses the title for an article by assuming that most article titles are presented in all caps.
     */
    private static String extractTitleFromNonSection(String bodyText) {
        String title = bodyText;
        // Remove the location designator
        Matcher prefixMatcher = nonSectionPrefixPattern.matcher(bodyText);
        if (prefixMatcher.find()) {
            title = title.substring(prefixMatcher.end());
        }

        // If uppercase words, title is all the uppercase words
        Matcher uppercaseMatcher = uppercasePattern.matcher(title);
        if (uppercaseMatcher.find()) {
            // Match the first line that starts with a non uppercase word.
            Matcher endOfUppercaseMatcher = endOfUppercasePattern.matcher(title);
            if (endOfUppercaseMatcher.find()) {
                title = title.substring(0, endOfUppercaseMatcher.start());
            }
        }
        // Otherwise, remove the 'body' and the title is what remains.
        else {
            Pattern bodyPattern = Pattern.compile("((\\\\n|^)( {2})?)(\\w.*)");
            Matcher bodyMatcher = bodyPattern.matcher(title);
            if (bodyMatcher.find()) {
                title = title.substring(0, bodyMatcher.start());
            }
        }

        title = title.replaceAll("\\\\n", " ");
        title = title.replaceAll("\\s{2,}", " ");
        return capitalizeTitle(title.trim());
    }

    /**
     * Extract the title from the section document using a common pattern if applicable or just getting the
     * first line or so.
     */
    private static String extractTitleFromSection(LawDocInfo docInfo, String text) {
        String title = "";
        if (text != null && !text.isEmpty()) {
            int asteriskLoc = docInfo.getLocationId().indexOf("*");
            String locationId = (asteriskLoc != -1)
                                ? docInfo.getLocationId().substring(0, asteriskLoc) : docInfo.getLocationId();
            String toFormat = locationId.replaceFirst("[^S]*S", "").toLowerCase();
            Pattern titlePattern = Pattern.compile(String.format(sectionTitlePattern, toFormat));
            int sectionIdx = text.indexOf("§");
            String trimText = (sectionIdx != -1) ? text.substring(sectionIdx).trim() : text.trim();
            Matcher titleMatcher = titlePattern.matcher(trimText);
            if (titleMatcher.matches()) {
                title = titleMatcher.group(2).replaceAll("-\\\\n\\s*", "").replaceAll("\\\\n?\\s*", " ");
            }
            else {
                logger.warn("Section title pattern mismatch for document id {}", docInfo.getDocumentId());
                title = trimText;
            }
        }
        return StringUtils.abbreviate(title, 140);
    }

    private static String capitalizeTitle(String title) {
        if (title != null && !title.isEmpty()) {
            String capStr = WordUtils.capitalizeFully(title);
            return capStr.substring(0, 1) + Stream.of(capStr.substring(1).split(" "))
                    .map(s -> (s.matches("(Of|Or|The|For|A|And|An)")) ? s.toLowerCase() : s)
                    .collect(Collectors.joining(" "));
        }
        return title;
    }
}