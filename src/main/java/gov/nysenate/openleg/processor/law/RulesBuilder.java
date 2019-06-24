package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.law.LawVersionId;

public class RulesBuilder extends IdBasedLawBuilder {
    public static final String TODO = "JOINT RULE ";
    protected static final String SENATE_RULES_STR = "CMS";
    protected static final String ASSEMBLY_RULES_STR = "CMA";

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

    private void processRules(LawBlock block, boolean isNewDoc) {
        String[] rules = block.getText().toString().split(TODO)[0].split("RULE [IVX]+\\\\n");
        String lawID = block.getLawId();
        // Process the chapter alone.
        block.getText().setLength(0);
        block.getText().append(rules[0]);
        super.addInitialBlock(block, isNewDoc);

        for (int i = 1; i < rules.length; i++) {
            String currRuleText = "RULE " + toNumeral(i) + "\\n" + rules[i];
            String[] sections = currRuleText.replace("Section", "õ").split(" {2}õ");
            // Process a Rule.
            LawBlock currRule = new LawBlock(block, true);
            currRule.setDocumentId(lawID + "R" + i);
            currRule.setLocationId("R" + i);
            currRule.getText().append(sections[0]);
            super.addInitialBlock(currRule, isNewDoc);

            for (int j = 1; j < sections.length; j++) {
                LawBlock currSection = new LawBlock(currRule, true);
                currSection.getText().append((j == 1 ? "  Section" : "  õ")).append(sections[i]);
                currSection.setLocationId(currRule.getLocationId() + "S" + j);
                currSection.setDocumentId(lawID + currSection.getLocationId());
                super.addInitialBlock(currSection, isNewDoc);
            }
        }
    }
}
