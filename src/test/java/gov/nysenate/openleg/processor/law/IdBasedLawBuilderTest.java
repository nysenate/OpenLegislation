package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.law.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static gov.nysenate.openleg.model.law.LawDocumentType.*;

@Category(UnitTest.class)
public class IdBasedLawBuilderTest {
    private IdBasedLawBuilder builder;
    private LawChapterCode code;

    @Test
    public void altConstructorTest() {
        final String lawId = "VIL";
        init(lawId, "-CH64");

        IdBasedLawBuilder secondBuilder = new IdBasedLawBuilder(new LawVersionId(lawId, LocalDate.now()), null);
        secondBuilder.addInitialBlock(LawProcessorUtils.getLawBlock(code, "-CH64"), true, null);
        assertEquals(builder.lawInfo.toString(), secondBuilder.lawInfo.toString());
        assertEquals(builder.sequenceNo, secondBuilder.sequenceNo);
        assertEquals(builder.rootNode.getDocumentId(), secondBuilder.rootNode.getDocumentId());
    }

    @Test
    public void stackTest(){
        init("GMU", "-CH24");
        assertEquals(1, builder.parentNodes.size());
        assertTrue(builder.currParent().isRootNode());
        testChildNode("A1", ARTICLE);
        builder.clearParents();
        assertTrue(builder.isNodeListEmpty());
        assertNull(builder.currParent());
    }

    @Test
    public void addChildNodeTest() {
        init("CCO", "-CH77");
        testChildNode("A1", ARTICLE);
        testChildNode("1", SECTION);
    }

    @Test
    public void addChildNodeGCTTest() {
        init("GCT", "-CH21");
        testChildNode("A2-D", ARTICLE);
        testChildNode("25-A", SECTION);
        testChildNode("25-AP1-6", PART);
        testChildNode("25-AP1", PART);
        testChildNode("25-AP2", PART);
        testChildNode("25-B", SECTION);
    }

    @Test
    public void determineHierarchyTest() {
        init("TAX", "-CH60");
        testHierarchy("A29", ARTICLE, "A29");
        testHierarchy("A29P1", PART, "P1");
        testHierarchy("A29P1SPA", SUBPART, "SPA");
        testHierarchy("A29P1SPB", SUBPART, "SPB");
        testHierarchy("A29P2", PART, "P2");
        testHierarchy("A29P2SPA", SUBPART, "SPA");
        testHierarchy("A29P2SPB", SUBPART, "SPB");
        testHierarchy("A30", ARTICLE, "A30");
    }

    @Test
    public void specialGCTDetermineHierarchyTest() {
        init("GCT", "-CH21");
        testHierarchy("A2-D", ARTICLE, "A2-D");
        testHierarchy("25-AP1-6", PART, "P1-6");
        testHierarchy("25-AP1", PART, "P1");
        testChildNode("25-A-1", SECTION);
        testChildNode("25-A-2", SECTION);
        testHierarchy("25-AP2", PART, "P2");
        testChildNode("25-A-11", SECTION);
        testChildNode("25-A-12", SECTION);
        testChildNode("25-B", SECTION);
    }

    /**
     * Helper method to initialize builder.
     * Does not initialize a root node if locId is an empty String.
     * @param lawId of the builder.
     */
    private void init(String lawId, String locId) {
        code = LawChapterCode.valueOf(lawId);
        builder = (IdBasedLawBuilder) AbstractLawBuilder.makeLawBuilder(new LawVersionId(lawId, LocalDate.now()), null);
        if (!locId.isEmpty()) {
            LawBlock root = LawProcessorUtils.getLawBlock(code, locId);
            builder.addInitialBlock(root, true, null);
        }
    }

    private void testChildNode(String locId, LawDocumentType type) {
        LawProcessorUtils.testChildNode(locId, type, code, builder);
    }

    private void testHierarchy(String locId, LawDocumentType type, String expected) {
        LawProcessorUtils.testHierarchy(locId, type, expected, code, builder);
    }
}