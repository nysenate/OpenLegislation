package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawChapterCode;
import gov.nysenate.openleg.model.law.LawDocInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
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

    // Quite a few documents have incorrect data. Their titles are manually
    // listed here. If the title can be parsed fine, null is listed instead,
    // and this is merely used to suppress errors.
    private final static HashMap<String, String> BAD_DATA = new HashMap<>();
    static {
        BAD_DATA.put(LawChapterCode.EXC.name() + "A17-B", null);
        BAD_DATA.put(LawChapterCode.ENV.name() + "A21T5", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-BT4", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-CT1", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-CT2", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-DT1", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-DT2", null);
        BAD_DATA.put(LawChapterCode.TAX.name() + "171-L", "Certain overpayments credited against outstanding tax debt owed to the city of New York");
        BAD_DATA.put(LawChapterCode.TRA.name() + "14-L", "Airport improvement and revitalization");
        BAD_DATA.put(LawChapterCode.PBG.name() + "A13T111", "Village of East Rochester Housing Authority");
        BAD_DATA.put(LawChapterCode.PAR.name() + "27.09", "Convictions; bail forfeitures; failure to appear");
        BAD_DATA.put(LawChapterCode.GMU.name() + "119-OOO", "Inclusion of Cornell University as a member of the governing body of an entity created by intermunicipal agreement to construct and operate water treatment plants and water distribution systems in or adjoining the county of Tompkins");
        BAD_DATA.put(LawChapterCode.PAB.name() + "T1", "Private Activity Bond Allocation Act of 1990");
        BAD_DATA.put(LawChapterCode.YTS.name() + "A9", "Income Tax Surcharge");
        BAD_DATA.put(LawChapterCode.CPL.name() + "340.40", "Modes of trial");
        BAD_DATA.put(LawChapterCode.CPL.name() + "A530", "Orders of Recognizance or Bail With Respect to Defendants In Criminal Actions and Proceedings--When and By What Courts Authorized");
        BAD_DATA.put(LawChapterCode.ENV.name() + "71-1721", "Commissioner's enforcement power and duty");
        BAD_DATA.put(LawChapterCode.PBG.name() + "445", "Plattsburgh Housing Authority");
        BAD_DATA.put(LawChapterCode.PBH.name() + "265-F", "Severability");
        BAD_DATA.put(LawChapterCode.DEA.name() + "A1-A", NO_TITLE);
        BAD_DATA.put(LawChapterCode.ENV.name() + "A27T29", "Mercury Thermostat Collection Act");
        BAD_DATA.put(LawChapterCode.GCM.name() + "4-D", "Credit relating to the annual increase in certain payments to a landlord by a taxpayer relocating industrial and commercial employment opportunities");
        BAD_DATA.put(LawChapterCode.PBA.name() + "1621-B", "Definitions");
        // Problems in updates that are fixed later.
        BAD_DATA.put(LawChapterCode.GBS.name() + "380-V", "Severability");
        BAD_DATA.put(LawChapterCode.VAT.name() + "385", "Dimensions and weights of vehicles");
        BAD_DATA.put(LawChapterCode.GBS.name() + "1600", "Laws repealed");
        BAD_DATA.put(LawChapterCode.GBS.name() + "1601", "When to take effect");
    }

    /** --- Methods --- */
    public static String extractTitle(LawDocInfo lawDocInfo, String bodyText) {
        if (lawDocInfo == null || lawDocInfo.getDocType() == null)
            return "";
        String checkData = BAD_DATA.getOrDefault(lawDocInfo.getDocumentId(), null);
        if (checkData != null)
            return checkData;
        if (lawDocInfo.getDocumentId().equals(AbstractLawBuilder.CITY_TAX_STR + "P1-6"))
            return "City Personal Income Tax on Residents";
        switch (lawDocInfo.getDocType()) {
            case CHAPTER:
                return extractTitleFromChapter(lawDocInfo);
            case TITLE: case SUBTITLE: case PART: case SUBPART: case RULE: case ARTICLE: case SUBARTICLE:
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
        if (!docInfo.getDocTypeId().contains("*"))
            title = title.replaceAll("^\\s*\\*", "").replaceAll("\\*.*", "");
        return StringUtils.abbreviate(title.trim(), MAX_WIDTH);
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

    /**
     * Quickly converts a number to a Roman numeral. Used to display Articles
     * as Roman numerals, as they are in the Constitution text.
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
            if (docTypeMatcher.matches())
                return docTypeMatcher.group(1) + idMatch.group(2);
            if (!LawTitleParser.BAD_DATA.containsKey(lawDocInfo.getDocumentId()))
                logger.warn("Could not find matching signifier for doc {}.", lawDocInfo.getDocumentId());
        }
        return lawDocInfo.getDocTypeId();
    }
}