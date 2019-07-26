package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import gov.nysenate.openleg.model.law.LawActionType;
import gov.nysenate.openleg.model.law.LawChapterCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;


@Component
public class BillLawCodeParser {
    private static final Logger logger = LoggerFactory.getLogger(BillLawCodeParser.class);

    private final Set<String> divisionIndicators = Sets.newHashSet("title", "part", "art");

    /**
     * --- Output ---
     */

    private Map<LawActionType, HashSet<String>> mapping = new HashMap<>();
    private String json = "";

    /**
     * --- Constructor ---
     */

    public BillLawCodeParser() {
    }

    /* --- Methods --- */

    /**
     * This method splits the law code string into a mapping of action->{LawDocId String} (see LawDocId) and stores it
     * in this.mapping, it also sets this.json to a JSON encoding of the map
     *
     * @param lawCode the law code citation of a Bill Amendment, eg (Amd §3635, Ed L)
     */
    public void parse(String lawCode) {
        // Eliminate extraneous remarks like "(as proposed in S. 6513-B and A. 8508-A)". This will also remove some (sub)
        //  qualifiers, but these are more detail than we need anyway
        lawCode = lawCode.replaceAll("\\s*\\([^\\)]*\\)\\s*", "");
        // Rpldadd refers to the REPEAL_ADD action, and will not cause the same parsing problems as Rpld & add
        lawCode = lawCode.replaceAll("(?i)Rpld & add", "Rpldadd");
        // For every rename/renumerate clause, we'll parse the new names of each article separately under the REN_TO law
        //  action. This makes it easy to link to the new names if we want to, but currently we ignore them.
        lawCode = lawCode.replaceAll("(?i)to be", ", rento");

        // Law codes are usually delimited by semi-colons for each affected volume
        List<String> chapterList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(lawCode);

        // Each iteration handles all the effects on one volume of the law
        for (String chapter : chapterList) {

            // Handle volumes that are changed "generally" separately
            boolean general = false;

            // The action (eg Amd, Add) appears as the first word in the string
            LawActionType currAction;
            String actionString = chapter.split(" ")[0];
            if (LawActionType.lookupAction(actionString).isPresent()) {
                currAction = LawActionType.lookupAction(actionString).get();
            }
            else { // we can't parse this action, so we'll go to the next chapter
                continue;
            }

            String[] tokens = chapter.split(", ");
            // The chapter title is the last item in the list delimited by a comma (when notes at the end are removed)
            String chapterName = tokens[tokens.length - 1];
            if (chapterName.equalsIgnoreCase("generally")) {
                general = true;
                chapterName = tokens[tokens.length - 2].replaceFirst(actionString, "");
                if (chapterName.toLowerCase().contains("various laws")) {
                    break; // no need to list "various laws"
                }
            }
            LawChapterCode currChapter;
            if (LawChapterCode.lookupCitation(chapterName).isPresent()) {
                currChapter = LawChapterCode.lookupCitation(chapterName).get();
            }
            // If the law chapter is not recognized, or is part of the unconsolidated laws of a recent year
            // (eg Chap 408 of 1999), we cannot link to it. (Some older unconsolidated laws like Chap 912 of 1920 can be linked)
            else {
                continue;
            }
            if (general) {
                putLawEffect(currAction, currChapter.toString() + " (generally)");
                continue;
            }
            chapter = chapter.replaceAll(chapterName, "");
            parseChapterAffects(chapter, currChapter, currAction);
        }
        Gson gson = new Gson();
        json = gson.toJson(mapping);
    }

    private void parseChapterAffects(String chapter, LawChapterCode currChapter, LawActionType currAction) {
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
                Splitter.on(Pattern.compile("[ ]+")).trimResults().omitEmptyStrings().splitToList(article));
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
                    if (newDivision) {
                        context.add(context.remove(context.size()-1) + token);
                    }
                    else {
                        context.add(token);
                    }
                    newDivision = false;
                }
                else if (token.equalsIgnoreCase("various")) {
                    putLawEffect(currAction, currChapter.toString() + " (generally)");
                    break;
                }
                else if (LawActionType.lookupAction(token).isPresent()) {
                    currAction = LawActionType.lookupAction(token).get();
                }
                // If the next token is not a "Title/Part/Art" qualifier or 2 tokens ahead is a -, then we have
                // finished with that code
                // ( a "-" character after the next section name means we'll link to the parent law doc, not the sections)
                if (finished(tokenList, i)) {
                    if (context.size() > 0){
                        addLawEffect(currAction, currChapter, context);
                    }
                    // anything beyond this level of detail is extraneous
                    break;
                }
            }
        }
    }

    private String processQualifier(String token, LawChapterCode chapter, List<String> context) {
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

    private boolean isNewDivisionIndicator(List<String> context, List<String> tokens, int idx) {
        // Returns true if a string indicates a level of division (eg Art, Title) and that level of division has not yet
        //  been encountered in this section
        // For example, in the citation Amd Art 39-F Art Head, the first instance of "Art" is relevant, but not the second
        String s = tokens.get(idx);
        if (!divisionIndicators.contains(s.toLowerCase()) || tokens.subList(0, idx).contains(s)){
            return false;
        }
        for (int i=0; i<context.size(); i++){
            // If the division indicator has been seen before, the first letter will already be in context
            if (context.get(i).charAt(0) == s.toUpperCase().charAt(0)){
                context.remove(i);
                break;
            }
        }
        return true;
    }

    private boolean isSectionNumber(String s) {
        // Returns true if the string is a number, a number with a decimal, a Roman Numeral, a number with 'R'/'§' in
        // front of it, or an uppercase letter. This covers all possible quantifiers in the law code.
        return s != null && (s.matches("R?§*\\d*\\.?\\d+-?.*") || isRomanNumeral(s) || s.matches("[A-Z]"));
    }

    private boolean isRomanNumeral(String s) {
        // Returns true if the string is a well-formed Roman Numeral with values<50, or a Roman Numeral followed by a dash
        return s != null && (s.matches("(L?X{0,3}|IX)|(IV|V?I{0,3})") ||
                (s.split("-").length > 1 && isRomanNumeral(s.split("-")[0])));
    }

    private boolean finished(List<String> tokenList, int i) {
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
        if (range || unnecessary && !tokenList.get(i+1).equalsIgnoreCase("various")) {
            return true;
        }
        return false;
    }

    private void addLawEffect(LawActionType action, LawChapterCode chapter, List<String> context) {
        // Adds the proposed change described by "action" onto the law described by "context" and "chapter"
        // If the latest item in context doesn't begin with a letter, then we are at the lowest level of the law tree (section)
        boolean leaf = Character.isDigit(context.get(context.size()-1).charAt(0));
        if (leaf) {
            // link to only the section number
            putLawEffect(action, chapter.toString() + context.get(context.size()-1));
        }
        else {
            // link to the entire path through the law tree
            String hierarchy = String.join("", context.subList(0, context.size()));
            putLawEffect(action, chapter.toString() + hierarchy);
        }
        // The last added level of context will be replaced by a new one for the next law section
        if (context.size() > 1 || leaf) {
            context.remove(context.size()-1);
        }
        // The context created by the new names of laws needs to be reset because their locations are irrelevant
        if (action == LawActionType.REN_TO) {
            context.clear();
        }
    }

    private void putLawEffect(LawActionType action, String section) {
        // Add a new value to one of the actions in this.mapping
        // The next line can be removed once we can parse the constitution/NYC charter/NYC charter
        Set<String> unlinkable = Sets.newHashSet("CNS", "ADC", "NYC");
        // Ignore the new names of renamed laws
        if (!unlinkable.contains(section.substring(0, 3)) && action != LawActionType.REN_TO) {
            mapping.putIfAbsent(action, new HashSet<>());
            mapping.get(action).add(section);
        }
    }

    public Map<LawActionType, HashSet<String>> getMapping() {
        return mapping;
    }

    public String getJson() {
        return json;
    }

    public void clearMapping() {
        mapping.clear();
    }

    private int romanCharValue(char letter) {
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

    private String romanNumeralValue(String s) {
        // Converts a Roman Numeral string to the equivalent Arabic value string (eg romanNumeralValue("XIV") = "14")
        //  Only works for uppercase strings with characters up to 'C'
        int val = 0;
        for (int i = 0; i < s.length() - 1; i++) {
            if (romanCharValue(s.charAt(i)) < romanCharValue(s.charAt(i + 1))) {
                val -= romanCharValue(s.charAt(i));
            } else {
                val += romanCharValue(s.charAt(i));
            }
        }
        val += romanCharValue(s.charAt(s.length() - 1));
        return Integer.toString(val);
    }
}