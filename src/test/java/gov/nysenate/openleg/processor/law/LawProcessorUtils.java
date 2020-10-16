package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;

import java.time.LocalDate;

import static gov.nysenate.openleg.model.law.LawDocumentType.ARTICLE;
import static gov.nysenate.openleg.model.law.LawDocumentType.SECTION;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public abstract class LawProcessorUtils {

    /**
     * Creates and returns a LawDocInfo. See getLawBlock for param info.
     * @return a new LawDocInfo based on the LawBlock from getLawBlock.
     */
    public static LawDocInfo getLawDocInfo(LawChapterCode code, String locId, LawDocumentType type) {
        LawDocInfo ret = new LawDocInfo(getLawBlock(code, locId));
        ret.setDocType(type);
        return ret;
    }

    /**
     * Creates and returns a LawBlock with the default method (which is really an update).
     * See getLawBlock for param info.
     * @return the new LawBlock.
     */
    public static LawBlock getLawBlock(LawChapterCode code, String locId) {
        return getLawBlock(code, locId, "");
    }

    /**
     * Creates and returns a LawBlock with a published date of today.
     * @return the new LawBlock
     */
    public static LawBlock getLawBlock(LawChapterCode code, String locId, String method) {
        String lawId = code.name();
        LawBlock ret = new LawBlock();
        ret.setDocumentId(lawId + locId);
        ret.setLawId(lawId);
        ret.setPublishedDate(LocalDate.now());
        ret.setLocationId(locId);
        ret.setMethod(method);
        ret.setConsolidated(code.getType() == LawType.CONSOLIDATED);
        ret.getText().append(ret.getDocumentId()).append(" text, with method ").append(method.isEmpty() ? "UPDATE" : method);
        return ret;
    }

    /**
     * Tests that a node was correctly added.
     * @param locId of node to be added.
     * @param type of node to be added.
     */
    public static void testChildNode(String locId, LawDocumentType type, LawChapterCode code, IdBasedLawBuilder builder) {
        LawDocInfo info = LawProcessorUtils.getLawDocInfo(code, locId, type);
        builder.addChildNode(new LawTreeNode(info, ++builder.sequenceNo));
        if (builder.rootNode == null)
            builder.rootNode = builder.currParent();
        if (type != SECTION)
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
        if (type == SECTION)
            fail();
        LawDocInfo info = LawProcessorUtils.getLawDocInfo(code, locId, type);
        LawBlock block = LawProcessorUtils.getLawBlock(code, locId);
        assertEquals(expected, builder.determineHierarchy(block));
        builder.addChildNode(new LawTreeNode(info, ++builder.sequenceNo));
    }
}
