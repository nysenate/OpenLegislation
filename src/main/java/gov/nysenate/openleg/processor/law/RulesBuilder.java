package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;

public class RulesBuilder extends IdBasedLawBuilder {
    // TODO: process joint rules
    public static final String TODO = "JOINT RULE ";

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
        String[] rules = block.getText().toString().split(TODO)[0].split("RULE [IVX]+\\\\n");
        String lawID = block.getLawId();
        // Process the chapter alone.
        block.getText().setLength(0);
        block.getText().append(rules[0]);
        super.addInitialBlock(block, isNewDoc);
        // Create dummy Rule documents to parse properly.
        for (int i = 1; i < rules.length; i++) {
            String currRuleText = "RULE " + toNumeral(i) + "\\n" + rules[i];
            String[] sections = currRuleText.split("├Á|§|õ|Section *1");
            // Process a Rule.
            LawBlock currRule = new LawBlock(block, true);
            currRule.setDocumentId(lawID + "R" + i);
            currRule.setLocationId("R" + i);
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
