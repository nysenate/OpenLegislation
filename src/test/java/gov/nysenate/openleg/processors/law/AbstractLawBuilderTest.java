package gov.nysenate.openleg.processors.law;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.law.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing_utils.LawTestUtils;

import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.openleg.legislation.law.LawDocumentType.*;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class AbstractLawBuilderTest {

    private AbstractLawBuilder builder;
    private LawChapterCode code;

    @Test
    public void initTests() {
        initTest("CCO", "-CH77");
    }

    @Test
    public void createRootDocumentTest() {
        testDummy("MHA", null);
        init("ABC", "");
        testInitialBlock("1", SECTION, "1");
        assertTrue(builder.rootNode.getLawDocInfo().isDummy());
        init("MHA", "");
        testInitialBlock("A5", ARTICLE, "5");
        assertTrue(builder.rootNode.getLawDocInfo().isDummy());
    }

    @Test
    public void normalAddInitialBlockTest() {
        init("MHY", "-CH27");
        testInitialBlock("A1", ARTICLE, "1");
        testInitialBlock("1", SECTION, "1");
        testInitialBlock("A2", ARTICLE, "2");
        testInitialBlock("2", SECTION, "2");
    }

    @Test
    public void GCTAddInitialBlockTest() {
        String cityLawId = AbstractLawBuilder.CITY_TAX_STR.substring(0, 3);
        String cityLocId = AbstractLawBuilder.CITY_TAX_STR.substring(3);
        init(cityLawId, "-CH21");
        testInitialBlock("A2-D", ARTICLE, "2-D");
        testInitialBlock(cityLocId, SECTION, cityLocId);
        testInitialBlock(cityLocId + "P1-6", PART, "1-6");
        testInitialBlock(cityLocId + "P1", PART, "1");
        testInitialBlock(cityLocId + "-1", SECTION, "1");
        testInitialBlock(cityLocId + "-2", SECTION, "2");
        testInitialBlock("25-B", SECTION, "25-B");
    }

    @Test
    public void constitutionAddInitialBlockTest() {
        init("CNS", "AS");
        testInitialBlock("AA1", PREAMBLE, "");
        testInitialBlock("A1", ARTICLE, "1");
        testInitialBlock("A1S2", SECTION, "2");
    }

    @Test
    public void addInitialBlockTestGCM() {
        init("GCM", "-CH772");
        testInitialBlock("P3", PART, "3");
        testInitialBlock("P3S1", SUBPART, "1");
        testInitialBlock("11", SECTION, "11");
        testInitialBlock("CUBIT", MISC, "CUBIT");
    }

    @Test
    public void addInitialBlockTestACA() {
        init("ACA", "-CH11-C");
        testInitialBlock("TA", TITLE, "A");
        testInitialBlock("TAA1", ARTICLE, "1");
        testInitialBlock("1.01", SECTION, "1.01");
        testInitialBlock("ATTN", MISC, "ATTN");
    }

    @Test(expected = IllegalStateException.class)
    public void miscAddInitialBlockTest() {
        code = LawChapterCode.CCO;
        builder = (AbstractLawBuilder) AbstractLawBuilder.makeLawBuilder(new LawVersionId(code.name(), LocalDate.now()), null);
        LawBlock root = LawTestUtils.getLawBlock(code, "-CH77");
        builder.addInitialBlock(root, false, null);
        assertTrue(builder.lawDocMap.isEmpty());
        testInitialBlock("XXX", MISC, "XXX");

        init("VIL", "-CH64");
        builder.addInitialBlock(LawTestUtils.getLawBlock(code, "A1"), false, null);
        assertFalse(builder.lawDocMap.containsKey(code.name() + "A1"));
        testInitialBlock("1", SECTION, "1");
        assertTrue(builder.lawDocMap.containsKey(code.name() + "1"));
        builder.addInitialBlock(LawTestUtils.getLawBlock(code, "A2"), true, null);
        assertTrue(builder.lawDocMap.containsKey(code.name() + "A2"));
        testInitialBlock("2", SECTION, "2");
        assertTrue(builder.lawDocMap.containsKey(code.name() + "2"));

        // Tries to add an initial block without first making a root node.
        init("TAX", "");
        builder.rootNode = new LawTreeNode(new LawDocInfo(), 0);
        testInitialBlock("A1", ARTICLE, "1");
    }

    @Test
    public void masterUpdateTest() {
        initTreeToBeUpdated();
        // Note that 9 and 24 are new.
        String[] locIds = {"-CH772", "P1", "1", "P2", "2", "3", "4", "P3", "P3S1", "9", "10", "P3S2", "23", "24", "P5", "61"};
        createAndAddUpdateBlock(locIds);
        for (String s : locIds) {
            Optional<LawTreeNode> currNode = builder.rootNode.findNode(code.name() + s, false);
            if (currNode.isEmpty())
                fail("Rebuilt tree is missing node with location ID" + s);
            boolean isNew = currNode.get().getDocumentId().matches(code.name() + "(9|24)");
            assertEquals(LocalDate.now().minusDays(isNew ? 0 : 1), currNode.get().getPublishDate());
        }
    }

    @Test
    public void skipLocIdsMasterUpdateTest() {
        String[] locIds = {"-CH55", "A1", "1", "2", "A2", "5", "11", "A2-A*", "41*", "42*", "43*"};
        initTreeToBeUpdated(false, "SOS", locIds);
        assertTrue(builder.lawDocMap.isEmpty());
        // Replaces section 11 with a repeat section.
        locIds[6] = "2";
        createAndAddUpdateBlock(locIds);

        Optional<LawTreeNode> articleOne = builder.rootNode.findNode("SOSA1", false);
        Optional<LawTreeNode> articleTwo = builder.rootNode.findNode("SOSA2", false);
        if (articleOne.isEmpty() || articleTwo.isEmpty())
            fail("Articles should be present.");
        assertTrue(articleOne.get().find("SOS2").isPresent());
        assertFalse(articleTwo.get().find("SOS2").isPresent());
        // DocIds with asterisks should've been ignored.
        Collection<LawTreeNode> children = builder.rootNode.getChildren().values();
        assertFalse(children.stream().anyMatch(v -> v.getDocumentId().contains("*")));
        assertFalse(children.stream().anyMatch(v -> v.getPublishDate().equals(LocalDate.now())));
    }

    @Test
    public void noPriorRootMasterUpdateTest() {
        init("CCO", "");
        String[] locIds = {"-CH77", "A1", "1"};
        createAndAddUpdateBlock(locIds);
        assertEquals(locIds.length, builder.lawDocMap.values().size());
        assertTrue(builder.rootNode.getChildren().values().stream().allMatch(v -> v.getPublishDate().equals(LocalDate.now())));
    }

    @Test
    public void repealUpdateTest() {
        initTreeToBeUpdated();
        LawBlock repealBlock = LawTestUtils.getLawBlock(code, "P1", "*REPEAL*");
        builder.addUpdateBlock(repealBlock);
        Optional<LawTreeNode> foundNode = builder.rootNode.findNode(repealBlock.getDocumentId(), false);
        if (foundNode.isEmpty())
            fail("Repealed node was not present.");
        assertEquals(foundNode.get().getRepealedDate(), LocalDate.now());

        repealBlock = LawTestUtils.getLawBlock(code, "2018", "*REPEAL*");
        builder.addUpdateBlock(repealBlock);
        // This should print out a debugger line saying the node could not be found.
    }

    @Test
    public void deleteUpdateTest() {
        initTreeToBeUpdated();
        LawBlock deleteBlock = LawTestUtils.getLawBlock(code, "P2", "*DELETE*");
        Optional<LawTreeNode> originalNode = builder.rootNode.findNode(deleteBlock.getDocumentId(), false);
        if (originalNode.isEmpty())
            fail("Node to test deletion was not present.");
        LinkedHashMap<String, LawTreeNode> children = originalNode.get().getChildren();
        builder.addUpdateBlock(deleteBlock);
        assertFalse(builder.rootNode.findNode(deleteBlock.getDocumentId(), false).isPresent());
        for (LawTreeNode child : children.values())
            assertFalse(builder.rootNode.find(child.getDocumentId()).isPresent());
        // Deleting documents that don't exist should process with no problem.
        deleteBlock = LawTestUtils.getLawBlock(code, "2018", "*DELETE*");
        builder.addUpdateBlock(deleteBlock);
    }

    @Test
    public void updateTest() {
        initTreeToBeUpdated();
        LawBlock updateBlock = LawTestUtils.getLawBlock(code, "P3S1", "");
        String newTitle = "New Title";
        updateBlock.getText().replace(0, 1000, "SUBPART 1\\\n" + newTitle);
        builder.addUpdateBlock(updateBlock);
        Optional<LawTreeNode> foundNode = builder.rootNode.findNode(updateBlock.getDocumentId(), false);
        if (foundNode.isEmpty())
            fail("Node to update was not present.");
        assertEquals(LocalDate.now(), foundNode.get().getPublishDate());
        assertEquals(newTitle, builder.lawDocMap.get(updateBlock.getDocumentId()).getTitle());
    }

    @Test(expected = LawParseException.class)
    public void nullRootUpdateTest() {
        initTreeToBeUpdated();
        builder.rootNode = null;
        LawBlock test = LawTestUtils.getLawBlock(code, "1", "");
        builder.addUpdateBlock(test);
    }

    @Test(expected = LawParseException.class)
    public void noDocumentToUpdateTest() {
        initTreeToBeUpdated();
        LawBlock test = LawTestUtils.getLawBlock(code, "2018", "");
        builder.addUpdateBlock(test);
    }

    @Test(expected = LawParseException.class)
    public void badMethodTest() {
        initTreeToBeUpdated();
        LawBlock block = LawTestUtils.getLawBlock(code, "1", "XXX");
        builder.addUpdateBlock(block);
    }

    @Test
    public void miscTests() {
        String lawId = "XXX";
        builder = (AbstractLawBuilder) AbstractLawBuilder.makeLawBuilder(new LawVersionId(lawId, LocalDate.now()), null);
        assertNull(builder.rootNode);
        assertNull(builder.lawInfo);
        LawBlock block = new LawBlock();
        block.setLawId(lawId);
        block.setLocationId("-CH42424242");
        block.setDocumentId(lawId + block.getLocationId());
        builder.addInitialBlock(block, true, null);
        assertTrue(builder.lawInfo.getName().isEmpty());
        assertEquals(LawType.MISC, builder.lawInfo.getType());

        lawId = "CCO";
        init(lawId, "-CH77");
        LawTree tree = builder.getProcessedLawTree();
        assertEquals(lawId, tree.getLawVersionId().lawId());
        assertEquals(LocalDate.now(), tree.getLawVersionId().publishedDate());
        assertEquals(builder.lawInfo.toString(), tree.getLawInfo().toString());
        assertEquals(1, tree.getPublishedDates().size());
        assertEquals(LocalDate.now(), tree.getPublishedDates().get(0));
        assertEquals(builder.rootNode.toString(), tree.getRootNode().toString());

        builder.addInitialBlock(LawTestUtils.getLawBlock(code, "A1"), true, null);
        builder.addInitialBlock(LawTestUtils.getLawBlock(code, "A2"), false, null);
        builder.addInitialBlock(LawTestUtils.getLawBlock(code, "1"), false, null);
        builder.addInitialBlock(LawTestUtils.getLawBlock(code, "2"), true, null);
        List<LawDocument> processedDocs = builder.getProcessedLawDocuments();
        processedDocs.sort(Comparator.comparing(LawDocId::getDocumentId));
        assertEquals(3, processedDocs.size());
        assertEquals(code.name() + "-CH77", processedDocs.get(0).getDocumentId());
        assertEquals(code.name() + "2", processedDocs.get(1).getDocumentId());
        assertEquals(code.name() + "A1", processedDocs.get(2).getDocumentId());

        AbstractLawBuilder newBuilder = (AbstractLawBuilder) AbstractLawBuilder.makeLawBuilder(new LawVersionId(lawId, LocalDate.now()), tree);
        assertEquals(builder.rootNode.toString(), newBuilder.rootNode.toString());
        assertEquals(builder.lawInfo.toString(), newBuilder.lawInfo.toString());
    }

    /**
     * Tests the initialization method.
     * @param lawId of builder to make.
     * @param locId of chapter document, or an empty string if there's no chapter document.
     */
    private void initTest(String lawId, String locId) {
        init(lawId, locId);
        String chapterId = locId.matches(".*\\d") ? locId.replace("-CH", "") : "";
        assertNotNull(builder.rootNode);
        assertEquals(chapterId, builder.lawInfo.getChapterId());
        LawDocument rootDoc = builder.lawDocMap.get(lawId + locId);
        assertNotNull(rootDoc);
        assertEquals(CHAPTER, rootDoc.getDocType());
        assertEquals(chapterId, rootDoc.getDocTypeId());
        assertEquals(1, builder.sequenceNo);
    }

    /**
     * Tests that a dummy root document was properly created.
     * @param lawId of dummy to create.
     */
    private void testDummy(String lawId, LawTreeNode priorRoot) {
        init(lawId, "");
        LawBlock rootBlock = LawTestUtils.getLawBlock(code, "1");
        builder.addInitialBlock(rootBlock, true, priorRoot);
        LawDocument createdRoot = builder.lawDocMap.get(code + "-ROOT");
        assertTrue(createdRoot.isDummy());
        assertEquals(lawId, createdRoot.getLawId());
        assertEquals(lawId + "-ROOT", createdRoot.getDocumentId());
        assertEquals("-ROOT", createdRoot.getLocationId());
        assertEquals(CHAPTER, createdRoot.getDocType());
        assertEquals("ROOT", createdRoot.getDocTypeId());
        assertEquals(rootBlock.getPublishedDate(), createdRoot.getPublishedDate());
        assertTrue(createdRoot.getText().isEmpty());

        final String dummyTitle = "Dummy title";
        // Tests if a dummy is correctly made when there is a prior root node.
        if (priorRoot == null) {
            assertTrue(builder.lawInfo.getChapterId().isEmpty());
            createdRoot.setTitle(dummyTitle);
            testDummy(lawId, new LawTreeNode(new LawDocInfo(createdRoot), 1));
        }
        // Tests if a dummy is correctly made when there is a prior, non-dummy root node.
        else if (priorRoot.getLawDocInfo().isDummy()) {
            assertEquals(dummyTitle, createdRoot.getTitle());
            createdRoot.setDummy(false);
            testDummy(lawId, new LawTreeNode(new LawDocInfo(createdRoot), 1));
        }
    }

    /**
     * Tests adding a created block.
     * @param locId the location ID of the block to be created.
     * @param expectedType of the processed LawDocument.
     * @param expectedDocTypeId of the processed LawDocument.
     */
    private void testInitialBlock(String locId, LawDocumentType expectedType, String expectedDocTypeId) {
        LawBlock toTest = LawTestUtils.getLawBlock(code, locId);
        builder.addInitialBlock(toTest, true, null);
        LawDocument lawDoc = builder.lawDocMap.get(code.name() + locId);
        assertEquals(expectedType, lawDoc.getDocType());
        assertEquals(expectedDocTypeId, lawDoc.getDocTypeId());
    }

    /**
     * Helper method to initialize builder.
     * Does not initialize a root node if locId is an empty String.
     * @param lawId of the builder.
     */
    private void init(String lawId, String locId) {
        code = LawChapterCode.valueOf(lawId);
        builder = (AbstractLawBuilder) AbstractLawBuilder.makeLawBuilder(new LawVersionId(lawId, LocalDate.now()), null);
        if (!locId.isEmpty()) {
            LawBlock root = LawTestUtils.getLawBlock(code, locId);
            builder.addInitialBlock(root, true, null);
        }
    }

    private void initTreeToBeUpdated() {
        initTreeToBeUpdated(true, "GCM", new String[]{"-CH772", "P1", "1", "P2", "2", "3", "4",
                "P3", "P3S1", "10", "P3S2", "23", "P5", "61"});
    }

    private void initTreeToBeUpdated(boolean areNewDocs, String lawId, String[] locIds) {
        code = LawChapterCode.valueOf(lawId);
        builder = (AbstractLawBuilder) AbstractLawBuilder.makeLawBuilder(new LawVersionId(lawId, LocalDate.now()), null);
        for (String locId : locIds) {
            LawBlock toAdd = LawTestUtils.getLawBlock(code, locId);
            // Subtracts a day so that the publish time can be updated.
            toAdd.setPublishedDate(LocalDate.now().minusDays(1));
            builder.addInitialBlock(toAdd, areNewDocs, null);
        }
    }

    private void createAndAddUpdateBlock(String[] locIds) {
        StringBuilder rebuildBlockText = new StringBuilder();
        for (String s : locIds)
            rebuildBlockText.append(code.name()).append(s).append("\\\\n");
        LawBlock rebuildBlock = LawTestUtils.getLawBlock(code, "", "*MASTER*");
        rebuildBlock.getText().delete(0, rebuildBlock.getText().length()).append(rebuildBlockText);
        builder.addUpdateBlock(rebuildBlock);
    }
}
