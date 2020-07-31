package gov.nysenate.openleg.processor.law;

import com.google.common.base.Splitter;
import gov.nysenate.openleg.model.law.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RulesLawBuilder extends IdBasedLawBuilder implements LawBuilder {
    private static final String RULE_PATTERN = "((?:JOINT )?RULE [IVX]+\\\\n)";
    private static final String SPLIT_STR = "(PERMANENT JOINT RULES OF THE SENATE AND " +
            "ASSEMBLY\\\\n )|(INDEX TO ASSEMBLY RULES\\\\n)|(INDEX TO RULES OF THE SENATE\\\\n)";
    private static final Pattern INDEX_PATTERN = Pattern.compile("( {4,}[A-Z]\\\\n)");
    // A unique deliminator for use with String manipulation.
    private static final String DELIM = "~#~";

    public RulesLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }

    @Override
    public void addInitialBlock(LawBlock block, boolean isNewDoc, LawTreeNode priorRoot) {
        if (rootNode == null)
            processRules(block, isNewDoc, priorRoot);
        else
            super.addInitialBlock(block, isNewDoc, priorRoot);
    }

    /**
     * Basically creates divisions in the Rules document to process properly.
     * @param block of rules.
     * @param isNewDoc used in calls to superclass.
     * @param priorRoot to pass to addInitialBlock.
     */
    private void processRules(LawBlock block, boolean isNewDoc, LawTreeNode priorRoot) {
        // Process the Chapter alone.
        super.addInitialBlock(block, isNewDoc, priorRoot);
        String fullText = block.getText().toString().replaceAll(SPLIT_STR, DELIM);
        String[] ruleSplit = fullText.split(DELIM);
        // Process rules.
        block = new LawBlock(block, true);
        block.getText().append(ruleSplit[0]);
        processRule(block, isNewDoc, priorRoot, false);
        // Process joint rules.
        block = new LawBlock(block, true);
        block.getText().append(ruleSplit[1]);
        processRule(block, isNewDoc, priorRoot, true);
        // TODO: uncomment after discussing indices.
        // Process indices
//        block = new LawBlock(block, true);
//        block.getText().append(ruleSplit[2]);
//        processIndices(block, isNewDoc, priorRoot);
    }

    /**
     * Processes either the rules or joint rules.
     * @param block of all the rules or all the joint rules.
     * @param isJointRule if the documents to be processed are joint rules.
     */
    private void processRule(LawBlock block, boolean isNewDoc, LawTreeNode priorRoot, boolean isJointRule) {
        ArrayList<String> ruleFirstLine = new ArrayList<>();
        ruleFirstLine.add("No zeroth rule.");
        Matcher ruleMatch = Pattern.compile(RULE_PATTERN).matcher(block.getText());
        while (ruleMatch.find())
            ruleFirstLine.add(ruleMatch.group(1));
        String[] rules = block.getText().toString().split(RULE_PATTERN);
        // Create dummy Rule documents to parse properly.
        for (int i = 1; i < rules.length; i++) {
            String currRuleText = ruleFirstLine.get(i) + rules[i];
            String[] sections = currRuleText.split("├Á|§|õ|Section *1");
            // Process a Rule.
            LawBlock currRule = new LawBlock(block, true);
            currRule.setDocumentId(block.getLawId() + (isJointRule ? "JR" : "R") + i);
            currRule.setLocationId(currRule.getDocumentId().substring(3));
            currRule.getText().append(sections[0]);
            addInitialBlock(currRule, isNewDoc, priorRoot);
            // Create dummy Section documents for everything under this Rule.
            for (int j = 1; j < sections.length; j++) {
                LawBlock currSection = new LawBlock(currRule, true);
                currSection.setDocumentId(currRule.getDocumentId() + "S" + j);
                currSection.setLocationId(currRule.getLocationId() + "S" + j);
                currSection.getText().append(j == 1 ? "Section 1" : "§").append(sections[j]);
                addInitialBlock(currSection, isNewDoc, priorRoot);
            }
        }
    }

    /**
     * Processes indices at the end of the full rule document.
     * @param block of all the indices.
     */
    private void processIndices(LawBlock block, boolean isNewDoc, LawTreeNode priorRoot) {
        Matcher matcher = INDEX_PATTERN.matcher(block.getText());
        List<String> indexLabels = new ArrayList<>();
        while (matcher.find())
            indexLabels.add(matcher.group(1));
        List<String> indexTexts = Splitter.on(INDEX_PATTERN).omitEmptyStrings().splitToList(block.getText());
        for (int i = 0; i < indexTexts.size(); i++) {
            LawBlock index = new LawBlock(block, true);
            index.setDocumentId(index.getLawId() + "INDEX" + indexLabels.get(i).replaceAll("[^A-Z]", ""));
            index.setLocationId(index.getDocumentId().substring(3));
            index.getText().append(indexLabels.get(i)).append(indexTexts.get(i));
            addInitialBlock(index, isNewDoc, priorRoot);
        }
    }
}
