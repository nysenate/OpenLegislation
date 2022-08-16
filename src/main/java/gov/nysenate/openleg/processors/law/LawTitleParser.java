package gov.nysenate.openleg.processors.law;

import gov.nysenate.openleg.common.util.NumberConversionUtils;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;

public final class LawTitleParser {
    private static final Logger logger = LoggerFactory.getLogger(LawTitleParser.class);
    private static final String TYPES = "(?i)(SUB)?(ARTICLE|TITLE|PART|RULE)", SEPARATORS = "(-|\\.|\\s|\\\\n)+",
    // The first %s will be filled with the type (ARTICLE, TITLE, and so on).
    NON_SECTION_PREFIX_PATTERN = "(?i)(\\s|\\*)*(?<type>%s)\\s+(?<docId>%s)" + SEPARATORS,
    SECTION_SIGNIFIER = "(Section |\\d+(-|\\w)*\\.)",
    // Characters to discard between the docId and the title.
    IRRELEVANT_CHARS = "(\\*|\\.|\\s|\\\\n)*",
    // The %s here will be filled with the docId.
    BEFORE_TITLE_PATTERN = "(?i).*?(?<docId>%s)" + IRRELEVANT_CHARS,
    // The title is just everything before the first period.
    TITLE_PATTERN = "(?<title>[^.]+)", SUBSECTION = "(1\\.|[(]a[)]|a\\.)",
    // Matches all docId's.
    DUMMY_ID = "[\\w.-]+",
    // String to match a docType and its id, saving the latter.
    DOC_TYPE_STRING = ".*?%s *%s.*";
    // Pattern to match a full docTypeId, and parse out the starting number.
    private static final Pattern ID_NUM_PATTERN = Pattern.compile("(\\d+)([-*]?.*)");
    private static final int MAX_WIDTH = 140;

    // Some laws do not have names for any of their sections.
    private static final List<String> NO_TITLES = List.of(LSA.name(), POA.name(),
            PNY.name(), PCM.name(), BAT.name(), CCT.name());
    static final String NO_TITLE = "No title";

    private LawTitleParser() {}

    /** --- Methods --- */
    public static String extractTitle(LawDocInfo lawDocInfo, String bodyText) {
        if (lawDocInfo == null  || bodyText == null)
            return "";
        return switch (lawDocInfo.getDocType()) {
            case CHAPTER -> extractTitleFromChapter(lawDocInfo);
            case TITLE, SUBTITLE, PART, SUBPART, RULE, ARTICLE, SUBARTICLE, MISC ->
                    extractTitleFromNonSection(lawDocInfo, bodyText);
            case SECTION -> extractTitleFromSection(lawDocInfo, bodyText);
            case INDEX -> "Index of: " + lawDocInfo.getDocTypeId();
            case PREAMBLE -> "Preamble";
            case JOINT_RULE -> NO_TITLE;
            default -> "";
        };
    }

    /**
     * Extract the chapter title using the mapping of law id to LawChapterType if possible.
     */
    private static String extractTitleFromChapter(LawDocInfo docInfo) {
        try {
            return LawChapterCode.valueOf(docInfo.getLawId()).getChapterName();
        }
        catch (IllegalArgumentException ex) {
            return docInfo.getLawId() + " Law";
        }
    }

    /**
     * Parses the title for an article by assuming that most article titles are presented in all caps.
     */
    private static String extractTitleFromNonSection(LawDocInfo lawDocInfo, String bodyText) {
        String realID = getRealID(lawDocInfo, bodyText);
        String typeLabel = lawDocInfo.getDocType().name();
        String label = NON_SECTION_PREFIX_PATTERN.formatted(typeLabel, realID);
        String title = bodyText.replaceAll("\\* NB.*?\\\\n ", "").replaceFirst(".*?" + label, "")
                // Removes division names that might come after, and converts
                // whitespace into single spaces.
                .replaceFirst(TYPES + "\\s+(1|I|A|ONE)?\\W.*", "")
                .replaceFirst(SECTION_SIGNIFIER + ".*", "").replaceAll("\\\\n", " ")
                .replaceAll("\\s{2,}", " ").replaceAll(" *\\.", "");
        if (!lawDocInfo.getDocTypeId().contains("*"))
            title = title.replaceAll("^\\s*\\*+", "").replaceAll("\\*.*", "");
        if (title.trim().isEmpty())
            return NO_TITLE;
        title = removeNonCapitalized(title.trim());
        return capitalizeTitle(title);
    }

    /**
     * Extract the title from the section document.
     */
    private static String extractTitleFromSection(LawDocInfo docInfo, String text) {
        if (text.isEmpty())
            return "";
        if (NO_TITLES.contains(docInfo.getLawId()))
            return NO_TITLE;
        String id = idAdjustment(docInfo);
        text = textAdjustment(docInfo, text);
        Matcher sectionMatcher = sectionPattern(docInfo.getLawId(), id).matcher(text);
        String title = "";
        if (sectionMatcher.matches())
            title = sectionMatcher.group("title");
        else {
            logger.warn("Section title pattern mismatch for document id {}", docInfo.getDocumentId());
            sectionMatcher = sectionPattern(docInfo.getLawId(), DUMMY_ID).matcher(text);
            if (sectionMatcher.matches()) {
                title = sectionMatcher.group("title");
                logger.warn("Title was able to be guessed.");
            }
            else
                logger.warn("Unable to guess section title.");
        }
        title = title.replaceAll("-\\\\n\\s*", "").replaceAll("\\\\n?\\s*", " ")
                .replaceAll("\\s{2,}", " ");

        // If the section starts with labelling a section or subsection, there's no title.
        if (Pattern.compile("^" + SUBSECTION).matcher(title + ".").find())
            return NO_TITLE;
        if (!docInfo.getDocTypeId().contains("*"))
            title = title.replaceAll("^\\s*\\*", "").replaceAll("\\*.*", "");
        String ret = StringUtils.abbreviate(title.trim(), MAX_WIDTH);
        // Some rules have no titles.
        if (ret.length() == MAX_WIDTH && docInfo.getLocationId().startsWith("R"))
            return NO_TITLE;
        return ret;
    }

    /**
     * Takes in a String and if the first word is in all caps, the title should
     * only be words in all caps.
     *
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
            return capStr.charAt(0) + Stream.of(capStr.substring(1).split(" "))
                    .map(s -> (s.matches("(Of|Or|The|A|And|An|To)")) ? s.toLowerCase() : s)
                    .collect(Collectors.joining(" "));
        }
        return title;
    }

    /**
     * Various modifications may need to be done to get the ID that is in the text.
     * @param lawDocInfo to pull data from.
     * @param bodyText to check against.
     * @return the proper ID for title matching.
     */
    private static String getRealID(LawDocInfo lawDocInfo, String bodyText) {
        String docTypeInText = getTextLabel(lawDocInfo, bodyText);
        String realID = docTypeInText.replaceAll("\\*.+", "").replaceAll("\\*", "\\\\*?");
        // A couple documents separate the number and letter of a Part like 2A.
        if (lawDocInfo.getDocumentId().startsWith(FCT.name() + "A5-BP") && realID.length() > 1)
            realID = realID.charAt(0) + "(\\.|\\\\n| )*" + realID.charAt(1);
        return realID;
    }

    /**
     * Numbers may be displayed as a number (like 6), a Roman numeral
     * (like VI), or as a word (like SIX). This method finds and returns
     * whichever one is applicable.
     * @param lawDocInfo to be processed.
     * @param bodyText to check against.
     * @return the label ID.
     */
    private static String getTextLabel(LawDocInfo lawDocInfo, String bodyText) {
        String docTypeId = lawDocInfo.getDocTypeId();
        // Manual handling of strange GCT parts.
        if (docTypeId.equals("1-6"))
            return docTypeId;
        if (lawDocInfo.getDocumentId().equals(AbstractLawBuilder.CITY_TAX_STR + "P1"))
            return "I";

        Matcher idMatch = ID_NUM_PATTERN.matcher(docTypeId);
        if (!bodyText.isEmpty() && idMatch.matches()) {
            String options = NumberConversionUtils.allOptions(idMatch.group(1));
            Pattern docTypePattern = Pattern.compile(DOC_TYPE_STRING.formatted(lawDocInfo.getDocType().name(), options));
            Matcher docTypeMatcher = docTypePattern.matcher(bodyText.toUpperCase());
            if (docTypeMatcher.matches())
                return docTypeMatcher.group(1) + idMatch.group(2);
            logger.warn("Could not find matching signifier for doc {}", lawDocInfo.getDocumentId());
        }
        return docTypeId;
    }

    /**
     * Some adjustments may need to be made to the text to parse titles correctly.
     * @param docInfo to pull relevant info from.
     * @param text to modify.
     * @return the usable text.
     */
    private static String textAdjustment(LawDocInfo docInfo, String text) {
        text = text.replaceFirst("^[ *]*", "");
        // UCC docs have 2 dashes in the text while the section name only has one.
        if (docInfo.getLawId().equals(UCC.name()))
            text = text.replaceFirst("--", "-").replaceFirst("\\\\n {2}", " ");
        // The first section of unconsolidated laws may have an introduction to the chapter.
        if (docInfo.getDocTypeId().equals("1") && isUnconsolidated(docInfo.getLawId()))
            text = text.replaceFirst(".*?(Section|§) +1", "Section 1");
        return text;
    }

    /**
     * Some changes may need to be made to the docTypeId before it can be used.
     * @param docInfo to pull relevant info from.
     * @return the usable ID.
     */
    private static String idAdjustment(LawDocInfo docInfo) {
        int asteriskLoc = docInfo.getDocTypeId().indexOf("*");
        String id = ((asteriskLoc == -1) ? docInfo.getDocTypeId() :
                docInfo.getDocTypeId().substring(0, asteriskLoc))
                .toLowerCase();
        // PEP sections like 302-A sometimes don't have the - in the text.
        if (docInfo.getLawId().equals(PEP.name()))
            id = id.replace("-", "-?");
        return id;
    }

    /**
     * Makes some adjustments to the titlePatterns based on some inconsistent formatting.
     * @param lawId some have different parsing rules.
     * @param id the fixed docId.
     * @return a Pattern for matching use.
     */
    private static Pattern sectionPattern(String lawId, String id) {
        // A non-unconsolidated law may have "#." or "a." or "(a)" before the title.
        String trueBeforeTitlePattern = BEFORE_TITLE_PATTERN + (isUnconsolidated(lawId) ? "" : SUBSECTION + "?");
        // EPT laws end their titles with a newline (\n).
        String trueTitlePattern = TITLE_PATTERN.replace(".", lawId.equals(EPT.name()) ? "\\\\" : ".");
        String fullPattern = trueBeforeTitlePattern.formatted(id) + trueTitlePattern + ".*";
        return Pattern.compile(fullPattern);
    }
}
