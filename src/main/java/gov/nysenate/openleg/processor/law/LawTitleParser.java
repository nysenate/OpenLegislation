package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawChapterCode;
import gov.nysenate.openleg.model.law.LawDocInfo;
import gov.nysenate.openleg.model.law.LawDocumentType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LawTitleParser
{
    private final static Logger logger = LoggerFactory.getLogger(LawTitleParser.class);
    private final static String TYPES = "(?i)(SUB)?(ARTICLE|TITLE|PART|RULE|SECTION)";
    private final static String SEPERATORS = "(\\\\n|-|\\.|\\s)+";
    private final static String nonSectionPrefixPattern = "(?i)(\\s|\\*)*%s\\s+%s" + SEPERATORS;
    private final static String SECTION_SIGNIFIER = "\\d+(-|\\w)*\\..*";
    //private final static String sectionTitlePattern = "(?i)(Section|Sec\\.|Rule|§)\\s*%s\\.?(\\s|\\\\n)*([^.]+)\\..*";
    private final static String beforeTitlePattern = "(?i).*?%s\\.?(\\s|\\\\n)*";
    // The title is just everything before the period.
    private final static String titlePattern = "([^.]+)";
    private final static String DELIM = "\\\\~\\\\";
    private final static int MAX_WIDTH = 140;

    // Some laws do not have names for any of their sections.
    private final static List<String> LAWS_NO_TITLES = Arrays.asList(
            LawChapterCode.CMA.name(), LawChapterCode.CMS.name(),
            LawChapterCode.LSA.name(), LawChapterCode.POA.name(),
            LawChapterCode.PNY.name(), LawChapterCode.PCM.name(),
            LawChapterCode.BAT.name(), LawChapterCode.CCT.name());
    private final static String NO_TITLE = "No title";

    /** --- Methods --- */
    public static String extractTitle(LawDocInfo lawDocInfo, String bodyText) {
        if (lawDocInfo != null) {
            switch (lawDocInfo.getDocType()) {
                case CHAPTER:
                    return extractTitleFromChapter(lawDocInfo);
                case TITLE:
                case SUBTITLE:
                case PART:
                case SUBPART:
                case ARTICLE:
                case SUBARTICLE:
                case RULE:
                    return extractTitleFromNonSection(lawDocInfo, bodyText);
                case SECTION:
                    return extractTitleFromSection(lawDocInfo, bodyText);
                case INDEX:
                    return "Index range: " + lawDocInfo.getDocTypeId();
                case PREAMBLE:
                    return "Preamble";
                case MISC:
                    // Special city tax code.
                    if (lawDocInfo.getDocTypeId().equals("CUBIT"))
                        return "CITY UNINCORPORATED BUSINESS INCOME TAX";
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
        // Remove the location designator
        String fixedID = docInfo.getDocTypeId().replaceAll("\\*.+", "");
        // A couple documents separate the number and letter of a Part like 2A.
        if (docInfo.getLawId().equals(LawChapterCode.FCT.name()) && docInfo.getDocType() == LawDocumentType.PART && fixedID.length() > 1)
            fixedID = fixedID.substring(0, fixedID.length()-1) + "(\\.|\\\\n| )*" + fixedID.codePointAt(fixedID.length()-1);
        String label = String.format(nonSectionPrefixPattern, docInfo.getDocType().name(), fixedID);
        String title = bodyText.replaceFirst(label, DELIM);
        title = title.replaceFirst(".*" + DELIM, "");
        if (title.startsWith("Section "))
            return NO_TITLE;
        // Removes division names that might come after.
        title = title.replaceAll(TYPES + "\\s+\\w+.*", "");
        title = title.replaceAll(SECTION_SIGNIFIER, "");
        title = title.replaceAll("\\\\n", " ").replaceAll("\\s{2,}", " ");
        title = removeNonCapitalized(title.trim());
        return capitalizeTitle(title.trim());
    }

    /**
     * Extract the title from the section document using a common pattern if applicable or just getting the
     * first line or so.
     */
    private static String extractTitleFromSection(LawDocInfo docInfo, String text) {
        if (LAWS_NO_TITLES.contains(docInfo.getLawId()))
            return NO_TITLE;
        if (text == null || text.isEmpty())
            return "";
        int asteriskLoc = docInfo.getDocTypeId().indexOf("*");
        String id = ((asteriskLoc == -1) ?  docInfo.getDocTypeId() :
                docInfo.getDocTypeId().substring(0, asteriskLoc))
                .toLowerCase();
        // PEP sections like 302-A sometimes don't have the - in the text.
        if (docInfo.getLawId().equals(LawChapterCode.PEP.name()))
            id = id.replace("-", "-?");

        // UCC docs have 2 dashes in the text while the section name only has one.
        if (docInfo.getLawId().equals(LawChapterCode.UCC.name()))
            text = text.replaceFirst("--", "-").replaceFirst("\\\\n {2}", " ");
        // Removes everything before the title.
        String fullPattern = String.format(beforeTitlePattern, id) + titlePattern + ".*";
        Matcher sectionMatcher = Pattern.compile(fullPattern).matcher(text);
        String title = "";
        if (sectionMatcher.matches())
            title = sectionMatcher.group(2).replaceAll("-\\\\n\\s*", "").replaceAll("\\\\n?\\s*", " ");
        else
            logger.warn("Section title pattern mismatch for document id {}", docInfo.getDocumentId());

        // If the section starts with labelling a subsection, there's no title.
        if (title.trim().startsWith("(a)"))
            return NO_TITLE;
        return StringUtils.abbreviate(title, MAX_WIDTH);
    }

    /**
     * Takes in a String and if the first word is in all caps, the title should
     * only be words in all caps.
     * @param title to process.
     * @return Only the words relevant to the title.
     */
    private static String removeNonCapitalized(String title) {
        String[] words = title.split(" ");
        // Title is not just capital letters.
        if (words.length == 0 || !words[0].equals(words[0].toUpperCase()))
            return title;
        StringBuilder ret = new StringBuilder();
        for (String word : words) {
            if (!word.equals(word.toUpperCase()))
                break;
            ret.append(word).append(" ");
        }
        return ret.toString();
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