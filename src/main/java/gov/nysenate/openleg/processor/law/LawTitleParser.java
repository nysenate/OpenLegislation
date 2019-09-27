package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawChapterCode;
import gov.nysenate.openleg.model.law.LawDocInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LawTitleParser
{
    private final static Logger logger = LoggerFactory.getLogger(LawTitleParser.class);
    private final static String TYPES = "(?i)(SUB)?(ARTICLE|TITLE|PART|RULE)";
    private final static String SEPERATORS = "(\\\\n|-|\\.|\\s)+";
    private final static String nonSectionPrefixPattern = "(?i)(\\s|\\*)*%s\\s+%s" + SEPERATORS;
    private final static String SECTION_SIGNIFIER = "(Section |\\d+(-|\\w)*\\.)";
    private final static String beforeTitlePattern = "(?i).*?%s(\\*|\\.|\\s|\\\\n)*";
    // The title is just everything before the period.
    private final static String titlePattern = "([^.]+)";
    /** Pattern to match a full docTypeId, and and parse out the starting number. */
    private static Pattern idNumPattern = Pattern.compile("(\\d+)([-*]?.*)");
    /** String to match a docType and its id, saving the latter.*/
    private static final String docTypeString = ".*%s *(%s).*";
    private final static int MAX_WIDTH = 140;

    // Some laws do not have names for any of their sections.
    private final static List<String> NO_TITLES = Arrays.asList(
            LawChapterCode.LSA.name(), LawChapterCode.POA.name(),
            LawChapterCode.PNY.name(), LawChapterCode.PCM.name(),
            LawChapterCode.BAT.name(), LawChapterCode.CCT.name());
    private final static String NO_TITLE = "No title";

    /** For use in Roman numeral conversion. */
    private static final TreeMap<Integer, String> NUMERALS = new TreeMap<>();
    static {
        NUMERALS.put(50, "L");
        NUMERALS.put(40, "XL");
        NUMERALS.put(10, "X");
        NUMERALS.put(9, "IX");
        NUMERALS.put(5, "V");
        NUMERALS.put(4, "IV");
        NUMERALS.put(1, "I");
    }

    /** For use in number to word conversion. */
    private static final HashMap<Integer, String> NUMBER_WORDS = new HashMap<>();
    static {
        NUMBER_WORDS.put(1, "ONE");
        NUMBER_WORDS.put(2, "TWO");
        NUMBER_WORDS.put(3, "THREE");
        NUMBER_WORDS.put(4, "FOUR");
        NUMBER_WORDS.put(5, "FIVE");
        NUMBER_WORDS.put(6, "SIX");
        NUMBER_WORDS.put(7, "SEVEN");
        NUMBER_WORDS.put(8, "EIGHT");
        NUMBER_WORDS.put(9, "NINE");
        NUMBER_WORDS.put(10, "TEN");
        NUMBER_WORDS.put(11, "ELEVEN");
        NUMBER_WORDS.put(12, "TWELVE");
    }

    /** --- Methods --- */
    public static String extractTitle(LawDocInfo lawDocInfo, String bodyText) {
        if (lawDocInfo == null || lawDocInfo.getDocType() == null)
            return "";
        switch (lawDocInfo.getDocType()) {
            case CHAPTER:
                return extractTitleFromChapter(lawDocInfo);
            case TITLE: case SUBTITLE: case PART: case SUB_PART: case RULE: case ARTICLE: case SUBARTICLE:
                return extractTitleFromNonSection(lawDocInfo, bodyText);
            case SECTION:
                return extractTitleFromSection(lawDocInfo, bodyText);
            case INDEX:
                return "Index range: " + lawDocInfo.getDocTypeId();
            case PREAMBLE:
                return "Preamble";
            case JOINT_RULE:
                return NO_TITLE;
            case MISC:
                // Special city tax code.
                if (lawDocInfo.getDocumentId().equals(AbstractLawBuilder.CUBIT))
                    return "City Unincorporated Business Income Tax";
                // Special list of notwithstanding clauses.
                if (lawDocInfo.getDocumentId().equals(AbstractLawBuilder.ATTN))
                    return "ATTENTION";
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
    private static String extractTitleFromNonSection(LawDocInfo lawDocInfo, String bodyText) {
        String docTypeInText = getTextLabel(lawDocInfo, bodyText);
        String realID = docTypeInText.replaceAll("\\*.+", "").replaceAll("\\*", "\\\\*?");
        // A couple documents separate the number and letter of a Part like 2A.
        if (lawDocInfo.getDocumentId().startsWith(LawChapterCode.FCT.name() + "A5-BP") && realID.length() > 1)
            realID = realID.charAt(0) + "(\\.|\\\\n| )*" + realID.charAt(1);
        String typeLabel = lawDocInfo.getDocType().name();
        String label = String.format(nonSectionPrefixPattern, typeLabel, realID);
        String title = bodyText.replaceFirst(".*" + label, "");

        // Removes division names that might come after.
        title = title.replaceFirst(TYPES + "\\s+(1|I|A|ONE)?\\W.*", "");
        title = title.replaceFirst(SECTION_SIGNIFIER + ".*", "");
        title = title.replaceAll("\\\\n", " ").replaceAll("\\s{2,}", " ").replaceAll(" \\.", "");
        if (!lawDocInfo.getDocTypeId().contains("*"))
            title = title.replaceAll("^\\s*\\*+", "").replaceAll("\\*.*", "");
        if (title.trim().isEmpty())
            return NO_TITLE;
        title = removeNonCapitalized(title.trim());
        return capitalizeTitle(title);
    }

    /**
     * Extract the title from the section document using a common pattern if applicable or just getting the
     * first line or so.
     */
    private static String extractTitleFromSection(LawDocInfo docInfo, String text) {
        if (NO_TITLES.contains(docInfo.getLawId()) || docInfo.getLocationId().startsWith("R"))
            return NO_TITLE;
        if (text == null || text.isEmpty())
            return "";
        int asteriskLoc = docInfo.getDocTypeId().indexOf("*");
        String id = ((asteriskLoc == -1) ? docInfo.getDocTypeId() :
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
        if (!docInfo.getDocTypeId().contains("*"))
            title = title.replaceAll("^\\s*\\*", "").replaceAll("\\*.*", "");
        return StringUtils.abbreviate(title.trim(), MAX_WIDTH);
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
            return capStr.substring(0, 1) + Stream.of(capStr.substring(1).split(" "))
                    .map(s -> (s.matches("(Of|Or|The|A|And|An|To)")) ? s.toLowerCase() : s)
                    .collect(Collectors.joining(" "));
        }
        return title;
    }

    /**
     * Quickly converts a number to a Roman numeral. Used to display Articles
     * as Roman numerals, as they are in the Constitution text.
     *
     * @param number to convert.
     * @return a Roman numeral.
     */
    private static String toNumeral(int number) {
        if (number == 0)
            return "";
        int next = NUMERALS.floorKey(number);
        return NUMERALS.get(next) + toNumeral(number-next);
    }

    /**
     * Quickly converts a number 1-12 or 101-112 to a word.
     * @param number to convert.
     * @return a word/phrase.
     */
    private static String toWord(int number) {
        return (number > 100 ? "ONE HUNDRED " : "") + NUMBER_WORDS.getOrDefault(number%100, "no word");
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
        // Manual handling of strange GCT parts.
        if (lawDocInfo.getDocTypeId().equals("1-6"))
            return lawDocInfo.getDocTypeId();
        if (lawDocInfo.getDocumentId().equals(AbstractLawBuilder.CITY_TAX_STR + "P1"))
            return "I";

        Matcher idMatch = idNumPattern.matcher(lawDocInfo.getDocTypeId());
        if (!bodyText.isEmpty() && idMatch.matches()) {
            String textToMatch = bodyText.split("\\\\n", 2)[0].toUpperCase();
            int num = Integer.parseInt(idMatch.group(1));
            String options = idMatch.group(1) + "|" + toNumeral(num) + "|" + toWord(num);
            Pattern docTypePattern = Pattern.compile(String.format(docTypeString, lawDocInfo.getDocType().name(), options));
            Matcher docTypeMatcher = docTypePattern.matcher(textToMatch);
            if (docTypeMatcher.matches()) {
                return docTypeMatcher.group(1) + idMatch.group(2);
            }
            else {
                logger.warn("Could not find matching signifier for doc {}.", lawDocInfo.getDocumentId());
            }
        }
        return lawDocInfo.getDocTypeId();
    }
}