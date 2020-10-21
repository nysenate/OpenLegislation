package gov.nysenate.openleg.processors.law;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawTreeNode;
import gov.nysenate.openleg.legislation.law.LawVersionId;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static gov.nysenate.openleg.legislation.law.LawDocumentType.ARTICLE;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class ConstitutionLawBuilderTest {
    private ConstitutionLawBuilder builder;
    private static final LawChapterCode CODE = LawChapterCode.CNS;
    private static final int NUM_ARTICLES = 2, SECTIONS_PER_ARTICLE = 2;
    private static final Map<String, String> LOC_ID_TO_TITLE = new HashMap<>();
    static {
        LOC_ID_TO_TITLE.put("AA1", "Preamble");
        LOC_ID_TO_TITLE.put("A1", "Bill of Rights");
        LOC_ID_TO_TITLE.put("A1S1", "Rights, privileges and franchise secured; uncontested primary elections");
        LOC_ID_TO_TITLE.put("A1S2", "Trial by jury; how waived");
        LOC_ID_TO_TITLE.put("A2", "Suffrage");
        LOC_ID_TO_TITLE.put("A2S1", "Qualifications of voters");
        LOC_ID_TO_TITLE.put("A2S2", "Absentee voting");
    }

    @Test
    public void testConBuilder() {
        initConstitutionBuilder();
        assertEquals(NUM_ARTICLES, builder.rootNode.getChildNodeList().size());
        String preambleLocId = "AA1";
        addConDocument(preambleLocId, "", false);
        Optional<LawTreeNode> preamble = builder.rootNode.findNode(CODE.name() + preambleLocId, false);
        assertTrue(preamble.isPresent());
        assertEquals(2, preamble.get().getSequenceNo());
        for (int articleNum = 1; articleNum <= NUM_ARTICLES; articleNum++) {
            String articleLocId = "A" + articleNum;
            Optional<LawTreeNode> article = builder.rootNode.findNode(CODE.name() + articleLocId, false);
            assertTrue(article.isPresent());
            assertSame(ARTICLE, article.get().getDocType());
            assertEquals(LOC_ID_TO_TITLE.get(articleLocId), builder.lawDocMap.get(CODE.name() + articleLocId).getTitle());
            for (int sectionNum = 1; sectionNum <= SECTIONS_PER_ARTICLE; sectionNum++) {
                String sectionLocId = articleLocId + "S" + sectionNum;
                addConDocument(sectionLocId, "", false);
                Optional<LawTreeNode> section = builder.rootNode.findNode(CODE.name() + sectionLocId, false);
                assertTrue(section.isPresent());
                assertEquals(articleLocId, section.get().getParent().getLocationId());
            }
        }
    }

    private void addConDocument(String locId, String method, boolean isNewDoc) {
        LawBlock block = LawProcessorUtils.getLawBlock(CODE, locId, method);
        builder.addInitialBlock(block, isNewDoc, null);
        assertTrue(builder.rootNode.findNode(CODE.name() + locId, false).isPresent());
    }

    /**
     * Helper method to initialize builder.
     * Does not initialize a root node if locId is an empty String.
     */
    private void initConstitutionBuilder() {
        builder = (ConstitutionLawBuilder) AbstractLawBuilder.makeLawBuilder(new LawVersionId(CODE.name(), LocalDate.now()), null);
        Scanner scanner;
        try {
            scanner = new Scanner(new File(LawProcessorUtils.TEST_DATA_DIRECTORY + "ConstitutionRootSample"));
        }
        catch (FileNotFoundException e) {
            fail("Error! Sample data not found.");
            return;
        }
        StringBuilder text = new StringBuilder();
        while (scanner.hasNextLine())
            text.append(scanner.nextLine()).append("\\n");
        String rootLocId = "AS";
        LawBlock fullTextBlock = new LawBlock();
        fullTextBlock.setLawId(CODE.name());
        fullTextBlock.setLocationId(rootLocId);
        fullTextBlock.setDocumentId(CODE.name() + rootLocId);
        fullTextBlock.getText().append(text);
        builder.addInitialBlock(fullTextBlock, true, null);
    }
}
