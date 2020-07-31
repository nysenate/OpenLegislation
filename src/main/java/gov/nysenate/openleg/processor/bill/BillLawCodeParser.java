package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import gov.nysenate.openleg.model.law.LawActionType;
import gov.nysenate.openleg.model.law.LawChapterCode;
import gov.nysenate.openleg.model.law.LawDocumentType;
import gov.nysenate.openleg.util.RomanNumerals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillLawCodeParser {

    private static final Logger logger = LoggerFactory.getLogger(BillLawCodeParser.class);
    private static final Set<String> divisionIndicators = Sets.newHashSet();

    static {
        for (LawDocumentType t : LawDocumentType.values())
            divisionIndicators.add(t.name().toLowerCase());
        divisionIndicators.add("art");
    }

    // We don't have these law chapters.
    private static final Set<String> unlinkable = Sets.newHashSet("ADC", "NYC");
    private static final String altGenPattern = "(?i)(Chap \\d+ of \\d+)";

    /* --- Methods --- */

    private static List<String> getChapterList(String lawCode) {
        // Eliminates extraneous remarks.
        lawCode = lawCode.replaceAll("\\s*\\([^)]*\\)\\s*", "");
        // Compressing this action into one word simplifies parsing.
        lawCode = lawCode.replaceAll("(?i)Rpld & add", "Rpldadd");
        // Each new name of renamed laws will be parsed separately under the REN_TO law action.
        lawCode = lawCode.replaceAll("(?i) to be", ", rento");
        // Law codes are usually delimited by semi-colons for each affected volume.
        return new ArrayList<>(Splitter.on(";").trimResults().omitEmptyStrings().splitToList(lawCode));
    }

    /**
     * Processes a full law code, and returns the JSON of a map from LawActionType's to law
     * document ID's.
     *
     * @param lawCode the law code citation of a Bill Amendment, eg (Amd §3635, Ed L)
     */
    public static String parse(String lawCode, boolean hasValidLaws) {
        Map<LawActionType, TreeSet<String>> mapping = new EnumMap<>(LawActionType.class);
        if (!hasValidLaws)
            return new Gson().toJson(mapping);
        List<String> chapterList = getChapterList(lawCode);

        for (int i = 0; i < chapterList.size() && i < lawCode.length()/2; i++) {
            String chapter = chapterList.get(i).trim();
            if (chapter.equals("generally"))
                continue;
            // Handle volumes that are changed "generally" separately
            boolean general = false;
            LawActionType currAction;
            String actionString = chapter.split(" ")[0].replaceAll(",", "");
            if (LawActionType.lookupAction(actionString).isPresent())
                currAction = LawActionType.lookupAction(actionString).get();
            // We can't parse this action, so we'll skip it.
            else {
                if (chapter.contains(","))
                    chapterList.add(chapter.split(",", 2)[1]);
                continue;
            }

            if (chapter.matches(actionString + " " + altGenPattern))
                chapter += ", generally";
            // Can't match a list of unconsolidated chapters.
            if (chapter.contains(" Chaps "))
                continue;
            chapter = chapter.replaceAll(",? generally$", ", generally").replaceAll(" L,", ",").replaceAll(" L( |$)", " ").trim();

            String[] tokens = chapter.split("(,| of the) ");
            if (tokens.length == 1)
                tokens = chapter.split("&");
            // The chapter title is usually the last item in the list delimited by commas.
            String chapterName = tokens[tokens.length - 1].trim();
            if (chapterName.equalsIgnoreCase("generally") || (tokens.length == 1 && !chapterName.contains("§"))) {
                if (tokens.length != 1)
                    chapterName = tokens[tokens.length - 2];
                if (chapterName.contains(actionString)) {
                    general = true;
                    chapterName = chapterName.replaceFirst(actionString, "").trim();
                }
                // Sometimes, the chapter name comes with a separate action. For example, Rpld §101, amd UJCA, generally;
                else {
                    chapterList.add((chapterName + ", generally").trim());
                    chapter = chapter.replaceAll(", " + chapterName + ".*", "");
                    chapterName = chapterName.replaceFirst(".*? ", "");
                }
            }
            if (chapterName.toLowerCase().matches(".*various (law|chapter)s?.*"))
                continue;

            if (chapterName.contains("§")) {
                // If what should be the chapter name has section labels, then the chapter name was
                // not properly found, and is probably in the next String.
                if (i != chapterList.size()-1) {
                    chapterList.set(i+1, chapter + ", " + chapterList.get(i+1));
                    continue;
                }
            }

            String firstWord = chapterName.split(" ", 2)[0];
            Optional<LawActionType> misplacedAction = LawActionType.lookupAction(firstWord);
            if (misplacedAction.isPresent()) {
                Matcher beforeChapterName = Pattern.compile(firstWord + " §+[-.\\w]+", Pattern.CASE_INSENSITIVE).matcher(chapter);
                if (beforeChapterName.find()) {
                    int commaIndex = beforeChapterName.end();
                    // If there is nothing after the section label, then there is no law chapter
                    // here, and it's probably in the next String in the list.
                    if (commaIndex == chapter.length()) {
                        if (i != chapterList.size() - 1)
                            chapterList.set(i + 1, chapter + ", " + chapterList.get(i + 1));
                    }
                    // If a comma is already present, then a space was missing after it.
                    else if (chapter.charAt(commaIndex) == ',')
                        chapterList.add(chapter.substring(0, commaIndex+1) + " " + chapter.substring(commaIndex+1));
                    else
                        chapterList.add(chapter.substring(0, commaIndex) + "," + chapter.substring(commaIndex));
                    continue;
                }
            }

            Optional<LawChapterCode> currChapter = Optional.empty();
            try {
                currChapter = LawChapterCode.lookupCitation(chapterName);
            } catch (Exception ex) {
                logger.error("Error parsing Law Chapter Code from chapter name: " + chapterName, ex);
            }
            if (!currChapter.isPresent())
                continue;
            if (general)
                putLawEffect(currAction, currChapter.get().toString() + " (generally)", mapping);
            else
                parseChapterAffects(chapter.replaceAll(chapterName, "").trim(), currChapter.get(), currAction, mapping);
        }
        return new Gson().toJson(mapping);
    }

    private static void parseChapterAffects(String chapter, LawChapterCode currChapter, LawActionType currAction, Map<LawActionType, TreeSet<String>> mapping) {
        // Listing subsections gives us some trouble, so we'll manually remove them.
        chapter = chapter.replaceAll(" subs .*? & .*?,", ",");
        LinkedList<String> articleList = new LinkedList<>(
            Splitter.on(Pattern.compile("[&,]+")).trimResults().omitEmptyStrings().splitToList(chapter));

        // The list "context" will specify the full path to a law document, eg Art 27 Title 27 §§27-2701 will have
        //  context=[A27, T27, 27-2701] when it is ready to be added to the map
        LinkedList<String> context = new LinkedList<>();
        for (String article : articleList) {
            // Parse each section word-by-word
            LinkedList<String> tokenList = new LinkedList<>(
                Splitter.on(Pattern.compile(" +")).trimResults().omitEmptyStrings().splitToList(article));
            // Indicates whether we just parsed a new division title (Art, Part, or Title)
            boolean newDivision = false;
            for (int i = 0; ; i++) {
                String token = tokenList.get(i);
                if (token.equalsIgnoreCase("various")) {
                    putLawEffect(currAction, currChapter.toString() + " (generally)", mapping);
                    break;
                }
                if (isNewDivisionIndicator(context, tokenList, i)) {
                    context.add(token.toUpperCase().substring(0,1));
                    newDivision = true;
                }
                else if (isSectionNumber(token)) {
                    // Parse the possible Roman Numerals in the current token
                    token = processQualifier(token, currChapter, context);
                    context.add((newDivision ? context.pollLast() : "") + token);
                    newDivision = false;
                }
                currAction = LawActionType.lookupAction(token).orElse(currAction);
                if (finished(tokenList, i))
                    break;
            }
            addLawEffect(currAction, currChapter, context, mapping);
        }
    }

    private static String processQualifier(String token, LawChapterCode chapter, LinkedList<String> context) {
        // Rules start with R and sections with §, but we don't need these characters
        token = token.toUpperCase().replaceAll("(^R)|§", "");
        // Sometimes the article/title names have Roman Numerals in only the first half
        String[] splitToken = token.split("-", 2);
        boolean nonNumerical = false;
        if (context.size() > 0)
            nonNumerical = !chapter.hasNumericalTitles() && context.peekLast().equals("T");
        // Only convert Roman Numerals to numbers when the names of the levels are numerical (eg Title 5 not Title E)
        if (isRomanNumeral(splitToken[0]) && !nonNumerical){
            splitToken[0] = Integer.toString(RomanNumerals.numeralToInt(splitToken[0]));
            token = String.join("-", splitToken);
        }
        return token;
    }

    private static boolean isNewDivisionIndicator(List<String> context, List<String> tokens, int idx) {
        // Returns true if a string indicates a level of division (eg Art, Title) and that level of division has not yet
        // been encountered in this section
        // For example, in the citation Amd Art 39-F Art Head, the first instance of "Art" is relevant, but not the second
        String s = tokens.get(idx);
        if (!divisionIndicators.contains(s.toLowerCase()) || tokens.subList(0, idx).contains(s))
            return false;
        for (int i = 0; i < context.size(); i++){
            // If the division indicator has been seen before, the first letter will already be in context
            if (context.get(i).charAt(0) == s.toUpperCase().charAt(0)){
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

    private static boolean finished(List<String> tokenList, int i) {
        // Indicates whether tokenList[i] is the last token necessary to fully qualify a LawDocId within a citation
        if (i == tokenList.size() - 1)
            return true;
        // the section ends with a range of subsections
        boolean range = (i < tokenList.size() - 2 && tokenList.get(i+2).startsWith("-"));
        // the section has no more relevant information
        boolean unnecessary = !divisionIndicators.contains(tokenList.get(i+1).toLowerCase()) &&
                !isSectionNumber(tokenList.get(i+1));
        return range || unnecessary && !tokenList.get(i+1).equalsIgnoreCase("various");
    }

    private static void addLawEffect(LawActionType action, LawChapterCode chapter, LinkedList<String> context, Map<LawActionType, TreeSet<String>> mapping) {
        if (context.isEmpty())
            return;
        // Adds the proposed change described by "action" onto the law described by "context" and "chapter"
        // If the latest item in context doesn't begin with a letter, then we are at the lowest level of the law tree (section)
        boolean leaf = Character.isDigit(context.peekLast().charAt(0));
        String section = chapter.toString() + (leaf ? context.peekLast() : String.join("", context.subList(0, context.size())));
        putLawEffect(action, section, mapping);
        // The last added level of context will be replaced by a new one for the next law section
        if (context.size() > 1 || leaf)
            context.pollLast();
        // The context created by the new names of laws needs to be reset because their locations are irrelevant
        if (action == LawActionType.REN_TO)
            context.clear();
    }

    private static void putLawEffect(LawActionType action, String section, Map<LawActionType, TreeSet<String>> mapping) {
        // Add a new value to one of the actions in this.mapping
        // Ignore the new names of renamed laws
        if (!unlinkable.contains(section.substring(0, 3)) && action != LawActionType.REN_TO) {
            mapping.putIfAbsent(action, new TreeSet<>());
            mapping.get(action).add(section);
        }
    }
}
