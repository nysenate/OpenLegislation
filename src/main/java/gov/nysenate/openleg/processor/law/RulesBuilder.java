package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RulesBuilder extends IdBasedLawBuilder {
    private static final String RULE_PATTERN = "((?:JOINT )?RULE [IVX]+\\\\n)";

    public RulesBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }

    @Override
    public void addInitialBlock(LawBlock block, boolean isNewDoc, LawTreeNode priorRoot) {
        if (rootNode == null)
            processRules(block, isNewDoc, priorRoot);
        else
            super.addInitialBlock(block, isNewDoc, priorRoot);
    }

    @Override
    protected boolean isLikelySectionDoc(LawDocument lawDoc) {
        return lawDoc.getLocationId().replaceAll("R\\d+", "").matches("S.+");
    }

    /**
     * Basically creates divisions in the Rules document to process properly.
     * @param block of rules.
     * @param isNewDoc used in calls to superclass.
     * @param priorRoot
     */
    private void processRules(LawBlock block, boolean isNewDoc, LawTreeNode priorRoot) {
        // Process the Chapter alone.
        super.addInitialBlock(block, isNewDoc, null);
        // Keep the first line of each Rule for later use.
        ArrayList<String> ruleStart = new ArrayList<>();
        ruleStart.add("No zeroth rule.");
        Matcher ruleMatch = Pattern.compile(RULE_PATTERN).matcher(block.getText());
        while (ruleMatch.find())
            ruleStart.add(ruleMatch.group(1));
        String[] rules = block.getText().toString().split(RULE_PATTERN);
        // Create dummy Rule documents to parse properly.
        int lastRule = 0;
        String ruleTypeAbbr = "R";
        for (int i = 1; i < rules.length; i++) {
            String currRuleText = ruleStart.get(i) + rules[i];
            // Once the joint rules are found, the docTypeId resets.
            if (lastRule == 0 && currRuleText.startsWith("J")) {
                lastRule = i-1;
                ruleTypeAbbr = "JR";
            }
            String[] sections = currRuleText.split("├Á|§|õ|Section *1");
            // Process a Rule.
            LawBlock currRule = new LawBlock(block, true);
            currRule.setDocumentId(block.getLawId() + ruleTypeAbbr + (i - lastRule));
            currRule.setLocationId(currRule.getDocumentId().substring(3));
            currRule.getText().append(sections[0]);
            super.addInitialBlock(currRule, isNewDoc, priorRoot);
            // Create dummy Section documents for everything under this Rule.
            for (int j = 1; j < sections.length; j++) {
                LawBlock currSection = new LawBlock(currRule, true);
                currSection.getText().append(j == 1 ? "Section 1" : "§").append(sections[j]);
                currSection.setLocationId(currRule.getLocationId() + "S" + j);
                currSection.setDocumentId(currRule.getDocumentId() + "S" + j);
                super.addInitialBlock(currSection, isNewDoc, priorRoot);
            }
        }
    }
}
