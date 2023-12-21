package gov.nysenate.openleg.processors.law;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawVersionId;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing_utils.LawTestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class RulesLawBuilderTest {
    private static final String[] ASSEMBLY_RULE_LOC_IDS = {"ASSEMBLYRULES", "R1", "R1S1", "R1S2", "R1S3", "R1S4", "R1S5", "R1S6", "R1S7", "R1S8", "R2", "R2S1", "R2S2", "R2S3", "R2S4"};
    private static final String[] SENATE_RULE_LOC_IDS = {"SENATERULES", "R1", "R1S1", "R2", "R2S1"};
    private static final String[] JOINT_RULE_LOC_IDS = {"JR1", "JR1S1", "JR1S2", "JR1S3", "JR1S4", "JR1S5", "JR1S6", "JR2", "JR2S1"};
    //private static final String[] INDEX_LOC_IDS = {"INDEXA", "INDEXB"};

    @Test
    public void rulesProcessedTest() {
        RulesLawBuilder assemblyBuilder = initRulesBuilder(Chamber.ASSEMBLY);
        RulesLawBuilder senateBuilder = initRulesBuilder(Chamber.SENATE);
        testRulesLawBuilder(assemblyBuilder);
        testRulesLawBuilder(senateBuilder);
        List<LawDocument> assemblyJointDocs = assemblyBuilder.lawDocMap.values().stream().filter(n -> n.getDocumentId().startsWith("JR")).toList();
        List<LawDocument> senateJointDocs = senateBuilder.lawDocMap.values().stream().filter(n -> n.getDocumentId().startsWith("JR")).toList();
        assertEquals(assemblyJointDocs.size(), senateJointDocs.size());
        for (String locId : JOINT_RULE_LOC_IDS) {
            LawDocument assemblyDoc = assemblyBuilder.lawDocMap.get(LawChapterCode.CMA.name() + locId);
            LawDocument senateDoc = senateBuilder.lawDocMap.get(LawChapterCode.CMS.name() + locId);
            assertEquals(assemblyDoc.getText().replaceAll(" {2,}", " "), senateDoc.getText().replaceAll(" {2,}", " "));
            assertEquals(assemblyDoc.getTitle(), senateDoc.getTitle());
        }
    }

    /**
     * Initializes one of two possible RulesLawBuilders.
     * @param chamber of the RulesBuilder to make.
     * @return the initialized RulesLawBuilder.
     */
    private static RulesLawBuilder initRulesBuilder(Chamber chamber) {
        String lawId = (chamber == Chamber.SENATE ? LawChapterCode.CMS : LawChapterCode.CMA).name();
        RulesLawBuilder builder = (RulesLawBuilder) AbstractLawBuilder.makeLawBuilder(new LawVersionId(lawId, LocalDate.now()), null);
        String fileName = LawTestUtils.TEST_DATA_DIRECTORY + chamber.name().substring(0, 1).toUpperCase() +
                chamber.name().substring(1).toLowerCase() + "RulesSample";
        Scanner scanner;
        try {
            scanner = new Scanner(new File(fileName));
        }
        catch (FileNotFoundException e) {
            fail("Error! Sample data not found.");
            return builder;
        }
        StringBuilder text = new StringBuilder();
        while (scanner.hasNextLine())
            text.append(scanner.nextLine()).append("\\n");
        String rootLocId = chamber.name() + "RULES";
        LawBlock fullTextBlock = new LawBlock();
        fullTextBlock.setLawId(lawId);
        fullTextBlock.setLocationId(rootLocId);
        fullTextBlock.setDocumentId(lawId + rootLocId);
        fullTextBlock.getText().append(text);
        builder.addInitialBlock(fullTextBlock, true, null);
        return builder;
    }

    /**
     * Tests a single RulesLawBuilder.
     * @param builder to test.
     */
    private void testRulesLawBuilder(RulesLawBuilder builder) {
        String lawId = builder.rootNode.getLawId();
        String[] ruleLocIds = (lawId.equals(LawChapterCode.CMA.name()) ? ASSEMBLY_RULE_LOC_IDS : SENATE_RULE_LOC_IDS);
        for (String locId : ruleLocIds)
            assertTrue(builder.rootNode.find(lawId + locId).isPresent());
        for (String locId : JOINT_RULE_LOC_IDS)
            assertTrue(builder.rootNode.find(lawId + locId).isPresent());
//        List<LawTreeNode> nodes = builder.rootNode.getAllNodes();
//        for (String locId : INDEX_LOC_IDS)
//            assertTrue(builder.rootNode.find(lawId + locId).isPresent());
//        assertEquals(ruleLocIds.length + JOINT_RULE_LOC_IDS.length + INDEX_LOC_IDS.length, nodes.size());
    }
}
