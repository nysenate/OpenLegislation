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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LawTitleParser
{
    private final static Logger logger = LoggerFactory.getLogger(LawTitleParser.class);
    private final static String TYPES = "(SUB)?(ARTICLE|TITLE|PART|RULE|SECTION)";
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
    private final static List<String> NO_TITLES = Arrays.asList(
            LawChapterCode.CMA.name(), LawChapterCode.CMS.name(),
            LawChapterCode.LSA.name(), LawChapterCode.POA.name(),
            LawChapterCode.PNY.name(), LawChapterCode.PCM.name(),
            LawChapterCode.BAT.name(), LawChapterCode.CCT.name());
    private final static String NO_TITLE = "No title";

    // Quite a few documents have incorrect data. Their titles are manually
    // listed here. If the title can be parsed fine, null is listen instead,
    // and this is merely used to suppress errors.
    protected final static HashMap<String, String> BAD_DATA = new HashMap<>();
    static {
        BAD_DATA.put(LawChapterCode.EXC.name() + "A17-B", null);
        BAD_DATA.put(LawChapterCode.TAX.name() + "171-L", "Certain overpayments credited against outstanding tax debt owed to the city of New York");
        BAD_DATA.put(LawChapterCode.TRA.name() + "14-L", "Airport improvement and revitalization");
        BAD_DATA.put(LawChapterCode.ENV.name() + "A21T5", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-BT4", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-CT1", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-CT2", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-DT1", null);
        BAD_DATA.put(LawChapterCode.PBA.name() + "A10-DT2", null);
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
    }

    /** --- Methods --- */
    public static String extractTitle(LawDocInfo lawDocInfo, String bodyText) {
        if (lawDocInfo == null)
            return "";
        String checkData = BAD_DATA.getOrDefault(lawDocInfo.getDocumentId(), null);
        if (checkData != null)
            return checkData;
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
            case MISC:
                // Special city tax code.
                if (lawDocInfo.getDocTypeId().equals("CUBIT"))
                    return "CITY UNINCORPORATED BUSINESS INCOME TAX";
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
        if (docInfo.getDocumentId().equals(LawChapterCode.PBA.name() + "A10-B"))
            return NO_TITLE;
        String realID = docInfo.getDocTypeId().replaceAll("\\*.+", "");
        // A couple documents separate the number and letter of a Part like 2A.
        if (docInfo.getDocumentId().startsWith(LawChapterCode.FCT.name() + "A5-BP") && realID.length() > 1)
            realID = realID.charAt(0) + "(\\.|\\\\n| )*" + realID.charAt(1);
        String label = String.format(nonSectionPrefixPattern, docInfo.getDocType().name(), realID);
        String title = bodyText.replaceFirst(label, DELIM);
        title = title.replaceFirst(".*" + DELIM, "");
        if (title.matches("^(Section |" + SECTION_SIGNIFIER + ")"))
            return NO_TITLE;

        // Removes division names that might come after.
        title = title.replaceAll("(?i) " + TYPES + "\\s+\\w+.*", "");
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
        if (NO_TITLES.contains(docInfo.getLawId()))
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