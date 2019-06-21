package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import gov.nysenate.openleg.model.law.LawActionType;
import gov.nysenate.openleg.model.law.LawChapterCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class BillLawCodeParser
{
    private static final Logger logger = LoggerFactory.getLogger(BillLawCodeParser.class);

    private final String sectionChar = "§";
    private final Set<String> sectionIndicators = new HashSet<>(Arrays.asList("title", "rule", "part", "art"));

    /** --- Output --- */

    private Map<LawActionType, HashSet<String>> mapping = new HashMap<>();
    private String json = "";

    /** --- Constructor --- */

    public BillLawCodeParser() {
    }

    /* --- Methods --- */
    /**
     * This method splits the law code string into a mapping of action->{law_doc_id} (see master.law_document table),
     * it also sets this.json to a JSON encoging of the map
     *
     * @param lawCode the law code citation of a Bill Amendment, eg (Amd §3635, Ed L)
     */
    public void parse(String lawCode) {
        // Eliminate extraneous remarks like "(as proposed in S. 6513-B and A. 8508-A)". This will also remove some (sub)
        // qualifiers, but these are more detail than we need
        lawCode = lawCode.replaceAll("\\(.*\\)", "");

        // Law codes are usually delimited by semi-colons for each affected chapter
        List<String> chapterList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(lawCode);

        // Each iteration handles all the effects on one chapter of the law code
        for (String chapter : chapterList) {

            // Handle chapters that are changed "generally" separately
            boolean general = false;

            // The action (eg Amd, Add) appears as the first word in the string
            LawActionType currAction;
            String actionString = chapter.split(" ")[0];
            if (LawActionType.lookupAction(actionString).isPresent()) {
                currAction = LawActionType.lookupAction(actionString).get();
            }
            // Doesn't handle renaming or moving laws
            else {
                break;
            }
            String[] tokens = chapter.split(", ");
            // The chapter title is the last item in the list delimited by a comma (when notes at the end are removed)
            String chapterName = tokens[tokens.length - 1];
            if (chapterName.equalsIgnoreCase("generally")) {
                general = true;
                chapterName = tokens[tokens.length - 2].replaceFirst(actionString, "");
                if (chapterName.toLowerCase().contains("various laws")) {
                    addLawCode(currAction, "VARIOUS-GENERALLY");
                    break;
                }
            }
            LawChapterCode currChapter;
            if (LawChapterCode.lookupCitation(chapterName).isPresent()) {
                currChapter = LawChapterCode.lookupCitation(chapterName).get();
            }
            // If the law chapter is not recognized, or is part of the consolidated laws of a certain year
            // (eg Chap 408 of 1999), we cannot link to it
            else {
                continue;
            }
            if (general) {
                addLawCode(currAction, currChapter.toString() + "-GENERALLY");
            }
            chapter = chapter.replaceAll(chapterName, "");
            parseChapterAffects(chapter, currChapter, currAction);
        }
        Gson gson = new Gson();
        json = gson.toJson(mapping);
    }

    public Map<LawActionType,HashSet<String>> getMapping() {
        return mapping;
    }
    public String getJson() { return json; }
    public void clearMapping() { mapping.clear(); }


    /** --- Internal Methods --- */
    private boolean isSectionNumber(String s) {
        return s != null && s.matches("R?§*\\d*\\.?\\d+-?.*");
    }

    private void addLawCode(LawActionType action, String section) {
        mapping.putIfAbsent(action, new HashSet<>());
        mapping.get(action).add(section);
    }

    private void parseChapterAffects(String chapter, LawChapterCode currChapter, LawActionType currAction){
        // Sections/Articles of the specified chapter are separated by "," and "&"
        LinkedList<String> articleList = new LinkedList<>(
                Splitter.on(Pattern.compile("[&,]+")).trimResults().omitEmptyStrings().splitToList(chapter));

        for (String article: articleList) {
            // Parse each section word-by-word
            LinkedList<String> tokenList = new LinkedList<>(
                    Splitter.on(Pattern.compile("[ ]+")).trimResults().omitEmptyStrings().splitToList(article));

            // The goal is to get currId to match a valid documentId for LawDocId
            StringBuilder currId = new StringBuilder(currChapter.toString());
            boolean subsection = false;
            for (int i = 0; i < tokenList.size(); i++) {
                String token = tokenList.get(i);
                if (sectionIndicators.contains(token.toLowerCase())) {
                    currId.append(token.toUpperCase().charAt(0));
                    subsection = true;
                }
                else if (isSectionNumber(token)) {
                    if (subsection) {
                        token = token.split("-")[0];
                    }
                    token = token.toUpperCase().replaceAll(sectionChar, "");
                    currId.append(token);
                    // If the next token is not a "Title" qualifier or -, then we have finished with that code
                    // ( a "-" character before or after the section name means it's a subsection and thus not relevant)
                    if (i + 1 == tokenList.size() || !sectionIndicators.contains(tokenList.get(i+1).toLowerCase()) ||
                        tokenList.get(i+1).equals("-")) {
                        addLawCode(currAction, currId.toString());
                        // anything beyond this level of detail is extraneous
                        break;
                    }
                }
                else if (LawActionType.lookupAction(token).isPresent()) {
                    currAction = LawActionType.lookupAction(token).get();
                }
            }
        }
    }
}
