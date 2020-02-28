package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import gov.nysenate.openleg.model.law.LawActionType;
import gov.nysenate.openleg.model.law.LawChapterCode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillLawCodeParser {
    private static final Set<String> divisionIndicators = Sets.newHashSet("title", "part", "art");
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
            // We can't parse this action, so we'll go to the next chapter.
            else
            {
                // TODO: remove after testing
//                if (!actionString.equals("Amc") && !actionString.contains("Acc"))
//                    System.out.println("Bad action: " + actionString);
                continue;
            }

            if (chapter.matches(actionString + " " + altGenPattern))
                chapter += ", generally";
            // Can't match a list of unconsolidated chapters.
            if (chapter.contains(" Chaps "))
                continue;
            chapter = chapter.replaceAll(",? generally$", ", generally").replaceAll(", L$", " L");

            String[] tokens = chapter.split("(,| of the) ");
            if (tokens.length == 1)
                tokens = chapter.split("&");
            // The chapter title is usually the last item in the list delimited by commas.
            String chapterName = tokens[tokens.length - 1].trim();
            if (chapterName.equalsIgnoreCase("generally")) {
                if (tokens.length == 1)
                    continue;
                chapterName = tokens[tokens.length - 2];
                if (chapterName.contains(actionString)) {
                    general = true;
                    chapterName = chapterName.replaceFirst(actionString, "").trim();
                }
                // Sometimes, the chapter name comes with a separate action. For example, Rpld §101, amd UJCA, generally;
                else {
                    chapterList.add(chapterName + ", generally".trim());
                    chapter = chapter.replaceAll(", " + chapterName + ".*", "");
                    chapterName = chapterName.replaceFirst(".*? ", "");
                }
            }
            if (chapterName.toLowerCase().matches(".*various (law|chapter)s?.*"))
                continue;

            String firstWord = chapterName.split(" ", 2)[0];
            Optional<LawActionType> misplacedAction = LawActionType.lookupAction(firstWord);
            if (misplacedAction.isPresent()) {
                Matcher beforeChapterName = Pattern.compile(firstWord + " §+[-.\\w]+", Pattern.CASE_INSENSITIVE).matcher(chapter);
                if (beforeChapterName.find()) {
                    int commaIndex = beforeChapterName.end();
                    // If there is nothing after the section label, then there is no law chapter
                    // here, and it's probably in the next String in the list.
                    if (commaIndex == chapter.length() && i != chapterList.size()-1)
                        chapterList.set(i+1, chapter + ", " + chapterList.get(i+1));
                    // If a comma is already present, then a space was missing after it.
                    else if (chapter.codePointAt(commaIndex) == ',')
                        chapterList.add(chapter.substring(0, commaIndex+1) + " " + chapter.substring(commaIndex+1));
                    else
                        chapterList.add(chapter.substring(0, commaIndex) + "," + chapter.substring(commaIndex));
                    continue;
                }
                if (chapter.endsWith(" L")) {
                    chapterName = chapter.replaceFirst(actionString, "").trim();
                    general = true;
                }
            }

            if (chapterName.contains("§")) {
                // If what should be the chapter name has section labels, then the chapter name was
                // not properly found, and is probably in the next String.
                if (i != chapterList.size() - 1) {
                    chapterList.set(i + 1, chapter + ", " + chapterList.get(i + 1));
                    continue;
                }
            }
            Optional<LawChapterCode> currChapter = LawChapterCode.lookupCitation(chapterName);
            if (!currChapter.isPresent()) {
                continue;
            }
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
        // This function processes all the effects on a single volume of the law code
        // Sections/Articles of the specified chapter are separated by "," and "&"
        LinkedList<String> articleList = new LinkedList<>(
            Splitter.on(Pattern.compile("[&,]+")).trimResults().omitEmptyStrings().splitToList(chapter));

        // The list "context" will specify the full path to a law document, eg Art 27 Title 27 §§27-2701 will have
        //  context=[A27, T27, 27-2701] when it is ready to be added to the map
        List<String> context = new LinkedList<>();
        for (String article : articleList) {
            // Parse each section word-by-word
            LinkedList<String> tokenList = new LinkedList<>(
                Splitter.on(Pattern.compile(" +")).trimResults().omitEmptyStrings().splitToList(article));
            // Indicates whether we just parsed a new division title (Art, Part, or Title)
            boolean newDivision = false;
            for (int i = 0; i < tokenList.size(); i++) {
                String token = tokenList.get(i);
                // If token indicates a new level of division, then we add the first letter of the division to the context
                // also adjust the context list if we enter a new part/article/title
                if (isNewDivisionIndicator(context, tokenList, i)) {
                    context.add(token.toUpperCase().substring(0,1));
                    newDivision = true;
                }
                else if (isSectionNumber(token)) {
                    // Parse the possible Roman Numerals in the current token
                    token = processQualifier(token, currChapter, context);
                    if (newDivision)
                        context.add(context.remove(context.size()-1) + token);
                    else
                        context.add(token);
                    newDivision = false;
                }
                else if (token.equalsIgnoreCase("various")) {
                    putLawEffect(currAction, currChapter.toString() + " (generally)", mapping);
                    break;
                }
                else if (LawActionType.lookupAction(token).isPresent())
                    currAction = LawActionType.lookupAction(token).get();
                // If the next token is not a "Title/Part/Art" qualifier or 2 tokens ahead is a -, then we have
                // finished with that code
                // ( a "-" character after the next section name means we'll link to the parent law doc, not the sections)
                if (finished(tokenList, i)) {
                    if (context.size() > 0)
                        addLawEffect(currAction, currChapter, context, mapping);
                    // anything beyond this level of detail is extraneous
                    break;
                }
            }
        }
    }

    private static String processQualifier(String token, LawChapterCode chapter, List<String> context) {
        // Rules start with R and sections with §, but we don't need these characters
        if (token.charAt(0) == 'R' && (token.length() > 1 && Character.isDigit(token.charAt(1)))) {
            // If the token starts with an R followed by a number, then it is a rule
            token = token.substring(1);
        }
        token = token.toUpperCase().replaceAll("§", "");
        // Sometimes the article/title names have Roman Numerals in only the first half
        String[] splitToken = token.split("-");
        if (splitToken.length > 0) {
            boolean nonNumerical = false;
            if (context.size() > 0) {
                nonNumerical = !chapter.hasNumericalTitles() && context.get(context.size()-1).equals("T");
            }
            // Only convert Roman Numerals to numbers when the names of the levels are numerical (eg Title 5 not Title E)
            if (isRomanNumeral(splitToken[0]) && !nonNumerical && splitToken[0].length() > 0){
                splitToken[0] = romanNumeralValue(splitToken[0]);
                token = String.join("-", splitToken);
            }
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
        for (int i=0; i<context.size(); i++){
            // If the division indicator has been seen before, the first letter will already be in context
            if (context.get(i).charAt(0) == s.toUpperCase().charAt(0)){
                context.remove(i);
                break;
            }
        }
        return true;
    }

    private static boolean isSectionNumber(String s) {
        return s != null && (s.matches("R?§*\\d*\\.?\\d+-?.*") || isRomanNumeral(s) || s.matches("[A-Z]"));
    }

    private static boolean isRomanNumeral(String s) {
        return s != null && (s.matches("(L?X{0,3}|IX)|(IV|V?I{0,3})") ||
                (s.split("-").length > 1 && isRomanNumeral(s.split("-")[0])));
    }

    private static boolean finished(List<String> tokenList, int i) {
        // Indicates whether tokenList[i] is the last token necessary to fully qualify a LawDocId within a citation
        if (i == tokenList.size() - 1) {
            // there are no more tokens left
            return true;
        }
        // the section ends with a range of subsections
        boolean range = (i < tokenList.size() - 2 && tokenList.get(i+2).equals("-"));
        // the section has no more relevant information
        boolean unnecessary = !divisionIndicators.contains(tokenList.get(i+1).toLowerCase()) &&
                !isSectionNumber(tokenList.get(i+1));
        return range || unnecessary && !tokenList.get(i + 1).equalsIgnoreCase("various");
    }

    private static void addLawEffect(LawActionType action, LawChapterCode chapter, List<String> context, Map<LawActionType, TreeSet<String>> mapping) {
        // Adds the proposed change described by "action" onto the law described by "context" and "chapter"
        // If the latest item in context doesn't begin with a letter, then we are at the lowest level of the law tree (section)
        boolean leaf = Character.isDigit(context.get(context.size()-1).charAt(0));
        String section = chapter.toString() + (leaf ? context.get(context.size()-1) : String.join("", context.subList(0, context.size())));
        putLawEffect(action, section, mapping);
        // The last added level of context will be replaced by a new one for the next law section
        if (context.size() > 1 || leaf) {
            context.remove(context.size()-1);
        }
        // The context created by the new names of laws needs to be reset because their locations are irrelevant
        if (action == LawActionType.REN_TO) {
            context.clear();
        }
    }

    private static void putLawEffect(LawActionType action, String section, Map<LawActionType, TreeSet<String>> mapping) {
        // Add a new value to one of the actions in this.mapping
        // Ignore the new names of renamed laws
        if (!unlinkable.contains(section.substring(0, 3)) && action != LawActionType.REN_TO) {
            mapping.putIfAbsent(action, new TreeSet<>());
            mapping.get(action).add(section);
        }
    }

    private static int romanCharValue(char letter) {
        switch (letter) {
            case 'L':
                return 50;
            case 'X':
                return 10;
            case 'V':
                return 5;
            case 'I':
                return 1;
            default:
                return 0;
        }
    }

    private static String romanNumeralValue(String s) {
        // Converts a Roman Numeral string to the equivalent Arabic value string (eg romanNumeralValue("XIV") = "14")
        // Only works for uppercase strings with characters up to 'C'
        int val = 0;
        for (int i = 0; i < s.length() - 1; i++) {
            if (romanCharValue(s.charAt(i)) < romanCharValue(s.charAt(i + 1)))
                val -= romanCharValue(s.charAt(i));
            else
                val += romanCharValue(s.charAt(i));
        }
        val += romanCharValue(s.charAt(s.length() - 1));
        return Integer.toString(val);
    }
}
