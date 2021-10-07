package gov.nysenate.openleg.processors.law;

import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import gov.nysenate.openleg.legislation.law.LawTreeNode;
import testing_utils.LawTestUtils;

import static org.junit.Assert.*;

public class LawBuilderTestHelper {
    /**
     * Tests that a node was correctly added.
     * @param locId of node to be added.
     * @param type of node to be added.
     */
    public static void testChildNode(String locId, LawDocumentType type, LawChapterCode code, IdBasedLawBuilder builder) {
        LawDocInfo info = LawTestUtils.getLawDocInfo(code, locId, type);
        builder.addChildNode(new LawTreeNode(info, ++builder.sequenceNo));
        if (builder.rootNode == null)
            builder.rootNode = builder.currParent();
        if (!type.isSection())
            assertEquals(info, builder.currParent().getLawDocInfo());
        else
            assertNotEquals(info, builder.currParent().getLawDocInfo());
    }

    /**
     * Tests that the hierarchy of a node is correctly determined.
     * @param locId of node to be created and added.
     * @param type of node.
     * @param expected value in hierarchy.
     */
    public static void testHierarchy(String locId, LawDocumentType type, String expected, LawChapterCode code, IdBasedLawBuilder builder) {
        // determineHierarchy is never called on a section.
        if (type.isSection())
            fail();
        LawDocInfo info = LawTestUtils.getLawDocInfo(code, locId, type);
        LawBlock block = LawTestUtils.getLawBlock(code, locId);
        assertEquals(expected, builder.determineHierarchy(block.getDocumentId()));
        builder.addChildNode(new LawTreeNode(info, ++builder.sequenceNo));
    }
}
