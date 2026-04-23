package gov.nysenate.openleg.processors.bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import gov.nysenate.openleg.common.util.NumberConversionUtils;
import gov.nysenate.openleg.legislation.law.LawActionType;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillLawCodeParser {

    private static final Logger logger = LoggerFactory.getLogger(BillLawCodeParser.class);

    // We don't have these law chapters.
    private static final Set<String> UNLINKABLE = Sets.newHashSet("ADC", "NYC");
    // LawDocumentType Strings
    private static final Set<String> DIVISION_INDICATORS = getDivisionIndicators();
    // Matches citations to unconsolidated chapters given as "Chap N of YEAR"
    private static final String ALT_GEN_PATTERN = "(?i)(Chap \\d+ of \\d+)";

    /* --- Methods --- */

    /**
     * @param lawCode the law code citation of a Bill Amendment, eg (Amd §3635, Ed L)
     * @return a List of lawCode fragments split by the law chapter to which they apply
     */
    private static List<String> getSegments(String lawCode) {
        // Eliminates extraneous remarks and some subsection descriptions.
        lawCode = lawCode.replaceAll("\\s*\\([^)]*\\)\\s*", "");
        // Combining "Rpld & add" into a single token recognized by LawActionType.
        lawCode = lawCode.replaceAll("(?i)Rpld & add", "Rpldadd");
        // Each new name of renamed laws will be parsed separately under the REN_TO law action.
        lawCode = lawCode.replaceAll("(?i) to be", ", rento");
        // Law codes are usually delimited by semicolons for each affected volume.
        return new ArrayList<>(Splitter.on(";").trimResults().omitEmptyStrings().splitToList(lawCode));
    }

    private static Set<String> getDivisionIndicators() {
        Set<String> divisionIndicators = Sets.newHashSet();
        for (LawDocumentType t : LawDocumentType.values()) {
            divisionIndicators.add(t.name().toLowerCase());
        }
        divisionIndicators.add("art");
        return divisionIndicators;
    }

    /**
     * Calls BillLawCodeParser.parse() and converts the JSON result to a map.
     * @return a map of LawActionTypes to list of associated laws
     */
    public static Map<String, List<String>> parseToMap(String lawCode, boolean hasValidLaws) {
        String json = parse(lawCode, hasValidLaws);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<>() {});
        }
        catch (JsonProcessingException ex) {
            logger.error("Failed to apply bill amendment's related laws: {}", json, ex);
            return new HashMap<>();
        }
    }

    /**
     * Processes a full law code, and returns the JSON of a map from LawActionType's to law
     * document ID's.
     *
     * @param lawCode the law code citation of a Bill Amendment, eg (Amd §3635, Ed L)
     */
    public static String parse(String lawCode, boolean hasValidLaws) {
        Map<LawActionType, TreeSet<String>> mapping = new EnumMap<>(LawActionType.class);
        if (!hasValidLaws) {
            return new Gson().toJson(mapping);
        }

        List<String> segments = getSegments(lawCode);
        for (int i = 0; i < segments.size(); i++) {
            String segment = segments.get(i).trim();

            // Handle volumes that are changed "generally" separately
            if (segment.equals("generally")) {
                continue;
            }
            boolean general = false;

            String actionString = segment.split(" ")[0].replaceAll(",", "");
            Optional<LawActionType> optionalAction = LawActionType.lookupAction(actionString);

            // We can't parse this action, so we'll skip it.
            if (optionalAction.isEmpty()) {
                if (segment.contains(",")) {
                    segments.add(segment.split(",", 2)[1]);
                }
                continue;
            }
            LawActionType action = optionalAction.get();

            if (segment.matches(actionString + " " + ALT_GEN_PATTERN)) {
                segment += ", generally";
            }
            // Can't match a list of unconsolidated chapters.
            if (segment.contains(" Chaps ")) {
                continue;
            }
            segment = segment.replaceAll(",? generally$", ", generally").replaceAll(" L,", ",").replaceAll(" L( |$)", " ").trim();

            String[] tokens = segment.split("(,| of the) ");
            if (tokens.length == 1) {
                tokens = segment.split("&");
            }
            // The chapter title is usually the last item in the list delimited by commas.
            String chapterName = tokens[tokens.length - 1].trim();
            if (chapterName.equalsIgnoreCase("generally") || (tokens.length == 1 && !chapterName.contains("§"))) {
                if (tokens.length != 1) {
                    chapterName = tokens[tokens.length - 2];
                }
                if (chapterName.contains(actionString)) {
                    general = true;
                    chapterName = chapterName.replaceFirst(actionString, "").trim();
                }
                // Sometimes, the chapter name comes with a separate action.
                // For example, Rpld §101, amd UJCA, generally;
                else {
                    segments.add((chapterName + ", generally").trim());
                    segment = segment.replaceAll(", " + chapterName + ".*", "");
                    chapterName = chapterName.replaceFirst(".*? ", "");
                }
            }
            if (chapterName.toLowerCase().matches(".*various (law|chapter)s?.*")) {
                continue;
            }

            if (chapterName.contains("§")) {
                // If what should be the chapter name has section labels, then the chapter name was
                // not properly found, and is probably in the next String.
                if (i != segments.size()-1) {
                    segments.set(i+1, segment + ", " + segments.get(i+1));
                    continue;
                }
            }

            String firstWord = chapterName.split(" ", 2)[0];
            Optional<LawActionType> misplacedAction = LawActionType.lookupAction(firstWord);
            if (misplacedAction.isPresent()) {
                Matcher beforeChapterName = Pattern.compile(firstWord + " §+[-.\\w]+", Pattern.CASE_INSENSITIVE).matcher(segment);
                if (beforeChapterName.find()) {
                    int commaIndex = beforeChapterName.end();
                    // If there is nothing after the section label, then there is no law chapter
                    // here, and it's probably in the next String in the list.
                    if (commaIndex == segment.length()) {
                        if (i != segments.size() - 1) {
                            segments.set(i + 1, segment + ", " + segments.get(i + 1));
                        }
                    }
                    // If a comma is already present, then a space was missing after it.
                    else if (segment.charAt(commaIndex) == ',') {
                        segments.add(segment.substring(0, commaIndex + 1) + " " + segment.substring(commaIndex + 1));
                    }
                    else {
                        segments.add(segment.substring(0, commaIndex) + "," + segment.substring(commaIndex));
                    }
                    continue;
                }
            }

            Optional<LawChapterCode> optionalChapterCode = LawChapterCode.lookupCitation(chapterName);
            if (optionalChapterCode.isEmpty()) {
                continue;
            }
            LawChapterCode chapterCode = optionalChapterCode.get();

            if (general) {
                putLawEffect(action, chapterCode + " (generally)", mapping);
            }
            else {
                parseChapterAffects(segment.replaceAll(chapterName, "").trim(), chapterCode, action, mapping);
            }
        }
        return new Gson().toJson(mapping);
    }

    /**
     * Parses through a LawCode fragment
     * @param chapter a LawCode fragment referencing a particular law chapter
     */
    private static void parseChapterAffects(String chapter, LawChapterCode chapterCode, LawActionType action, Map<LawActionType, TreeSet<String>> mapping) {
        // Listing subsections gives us some trouble, so we'll manually remove them.
        chapter = chapter.replaceAll(" subs .*? & .*?,", ",");
        // divide chapter into articles
        LinkedList<String> articles = new LinkedList<>(
            Splitter.on(Pattern.compile("[&,]+")).trimResults().omitEmptyStrings().splitToList(chapter));

        // The list "context" will specify the full path to a law document, eg Art 27 Title 27 §§27-2701 will have
        //  context=[A27, T27, 27-2701] when it is ready to be added to the map
        LinkedList<String> context = new LinkedList<>();
        for (String article : articles) {
            // Parse each section word-by-word
            LinkedList<String> tokens = new LinkedList<>(
                Splitter.on(Pattern.compile(" +")).trimResults().omitEmptyStrings().splitToList(article));
            // Indicates whether we just parsed a new division title (Art, Part, or Title)
            boolean newDivision = false;
            for (int i = 0; ; i++) {
                String token = tokens.get(i);
                if (token.equalsIgnoreCase("various")) {
                    putLawEffect(action, chapterCode.toString() + " (generally)", mapping);
                    break;
                }
                if (isNewDivisionIndicator(context, tokens, i)) {
                    context.add(token.toUpperCase().substring(0,1));
                    newDivision = true;
                }
                else if (isSectionNumber(token)) {
                    // Parse the possible Roman Numerals in the current token
                    token = processQualifier(token, chapterCode, context);
                    context.add((newDivision ? context.pollLast() : "") + token);
                    newDivision = false;
                }
                action = LawActionType.lookupAction(token).orElse(action);
                if (isFinished(tokens, i)) {
                    break;
                }
            }
            addLawEffect(action, chapterCode, context, mapping);
        }
    }

    /**
     * Removes unnecessary characters and simplifies Roman Numerals
     * @param token a LawCode fragment to be simplified
     * @param context the full path to a law document, e.g. Art 27 Title 27 §§27-2701 -> [A27, T27, 27-2701]
     * @return a simplified version of @param token
     */
    private static String processQualifier(String token, LawChapterCode chapter, LinkedList<String> context) {
        // Rules start with R and sections with §, but we don't need these characters
        token = token.toUpperCase().replaceAll("(^R)|§", "");
        // Sometimes the article/title names have Roman Numerals in only the first half
        String[] splitToken = token.split("-", 2);
        boolean nonNumerical = false;
        if (!context.isEmpty()) {
            nonNumerical = !chapter.hasNumericalTitles() && context.peekLast().equals("T");
        }
        // Only convert Roman Numerals to numbers when the names of the levels are numerical (eg Title 5 not Title E)
        if (isRomanNumeral(splitToken[0]) && !nonNumerical) {
            splitToken[0] = Integer.toString(NumberConversionUtils.numeralToInt(splitToken[0]));
            token = String.join("-", splitToken);
        }
        return token;
    }

    /**
     * @return true if a string indicates a level of division (eg Art, Title) and that level of division has not yet
     * been encountered in this section.
     * For example, in the citation Amd Art 39-F Art Head, the first instance of "Art" is relevant, but not the second
     */
    private static boolean isNewDivisionIndicator(List<String> context, List<String> tokens, int idx) {
        String s = tokens.get(idx);
        if (!DIVISION_INDICATORS.contains(s.toLowerCase()) || tokens.subList(0, idx).contains(s)) {
            return false;
        }
        for (int i = 0; i < context.size(); i++){
            // If the division indicator has been seen before, the first letter will already be in context
            if (context.get(i).charAt(0) == s.toUpperCase().charAt(0)) {
                context.remove(i);
                break;
            }
        }
        return true;
    }

    private static boolean isSectionNumber(String s) {
        return s.matches("R?§*\\d*[.-]?\\d+.*") || isRomanNumeral(s);
    }

    private static boolean isRomanNumeral(String s) {
        return s.matches("[IVXL]+(-[IVXL]+)?");
    }

    private static boolean isFinished(List<String> tokens, int i) {
        // Indicates whether tokenList[i] is the last token necessary to fully qualify a LawDocId within a citation
        if (i == tokens.size() - 1) {
            return true;
        }
        // the section ends with a range of subsections
        boolean range = (i < tokens.size() - 2 && tokens.get(i+2).startsWith("-"));
        String nextToken = tokens.get(i + 1);
        // the section has no more relevant information
        boolean unnecessary = !DIVISION_INDICATORS.contains(nextToken.toLowerCase()) &&
                !isSectionNumber(nextToken) &&
                !nextToken.equalsIgnoreCase("various");
        return range || unnecessary;
    }

    /**
     * Builds the section ID from the current context, adds it to the map, and pops the last context entry
     */
    private static void addLawEffect(LawActionType action, LawChapterCode chapter, LinkedList<String> context, Map<LawActionType, TreeSet<String>> mapping) {
        if (context.isEmpty()) {
            return;
        }

        // Adds the proposed change described by "action" onto the law described by "context" and "chapter"
        // If the latest item in context doesn't begin with a letter, then we are at the lowest level of the law tree (section)
        boolean leaf = Character.isDigit(context.peekLast().charAt(0));
        String section = chapter.toString() + (leaf ? context.peekLast() : String.join("", context.subList(0, context.size())));
        putLawEffect(action, section, mapping);
        // The last added level of context will be replaced by a new one for the next law section
        if (context.size() > 1 || leaf) {
            context.pollLast();
        }
        // The context created by the new names of laws needs to be reset because their locations are irrelevant
        if (action == LawActionType.REN_TO) {
            context.clear();
        }
    }

    /**
     * Adds a new section value under the given action, skipping unlinkable chapters and REN_TO destinations
     */
    private static void putLawEffect(LawActionType action, String section, Map<LawActionType, TreeSet<String>> mapping) {
        // Ignore the new names of renamed laws
        if (!UNLINKABLE.contains(section.substring(0, 3)) && action != LawActionType.REN_TO) {
            mapping.putIfAbsent(action, new TreeSet<>());
            mapping.get(action).add(section);
        }
    }
}
