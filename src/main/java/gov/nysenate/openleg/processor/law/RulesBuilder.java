package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;

public class RulesBuilder extends IdBasedLawBuilder {
    private static final String JOINT_SPLIT = "PERMANENT JOINT RULES OF THE SENATE AND ASSEMBLY";

    public RulesBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }

    @Override
    public void addInitialBlock(LawBlock block, boolean isNewDoc) {
        if (rootNode == null)
            processRules(block, isNewDoc);
        else
            super.addInitialBlock(block, isNewDoc);
    }

    @Override
    protected boolean isLikelySectionDoc(LawDocument lawDoc) {
        return lawDoc.getLocationId().replaceAll("R\\d+", "").matches("S.+");
    }

    /**
     * Basically creates divisions in the Rules document to process properly.
     * @param block of rules.
     * @param isNewDoc used in calls to superclass.
     */
    private void processRules(LawBlock block, boolean isNewDoc) {
        String[] ruleSplit = block.getText().toString().split(JOINT_SPLIT);
        String lawID = block.getLawId();
        // Process the chapter alone.
        super.addInitialBlock(block, isNewDoc);
        for (int r = 0; r < 2; r++) {
            String ruleType = (r == 1 ? "JOINT " : "");
            String ruleTypeAbbr = (r == 1 ? "JR" : "R");
            String[] rules = ruleSplit[r].split(ruleType + "RULE [IVX]+\\\\n");
            // Create dummy Rule documents to parse properly.
            for (int i = 1; i < rules.length; i++) {
                String currRuleText = ruleType + "RULE " + toNumeral(i) + "\\n" + rules[i];
                String[] sections = currRuleText.split("├Á|§|õ|Section *1");
                // Process a Rule.
                LawBlock currRule = new LawBlock(block, true);
                currRule.setDocumentId(lawID + ruleTypeAbbr + i);
                currRule.setLocationId(ruleTypeAbbr + i);
                currRule.getText().append(sections[0]);
                super.addInitialBlock(currRule, isNewDoc);
                // Create dummy section documents for everything under this Rule.
                for (int j = 1; j < sections.length; j++) {
                    LawBlock currSection = new LawBlock(currRule, true);
                    currSection.getText().append(j == 1 ? "Section 1" : "§").append(sections[j]);
                    currSection.setLocationId(currRule.getLocationId() + "S" + j);
                    currSection.setDocumentId(lawID + currSection.getLocationId());
                    super.addInitialBlock(currSection, isNewDoc);
                }
            }
        }
    }
}
