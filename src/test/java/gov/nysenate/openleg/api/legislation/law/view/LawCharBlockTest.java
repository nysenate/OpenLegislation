package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.processors.law.LawProcessorUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.*;
import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.CHAPTER;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.SECTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class LawCharBlockTest {
    @Test
    public void constructorTest() {
        String[] matches = {"AA", "BB", " \t", "\n"};
        LawCharBlockType[] types = {ALPHANUM, ALPHANUM, SPACE, NEWLINE};
        for (int i = 0; i < types.length; i++) {
            LawCharBlock curr = new LawCharBlock(matches[i]);
            assertEquals(matches[i], curr.text());
            assertEquals(types[i], curr.type());
        }
    }

    @Test
    public void dummyGetBlocksTest() {
        LawDocInfo info = LawProcessorUtils.getLawDocInfo(MHA, "-ROOT", CHAPTER);
        LawDocument doc = new LawDocument(info, "");
        doc.setDummy(true);
        List<LawCharBlock> blocks = LawCharBlock.getBlocks(doc);
        assertTrue(blocks.isEmpty());
    }

    @Test
    public void sectionGetBlocksTest() {
        String[] parts = {"", "1. The\ntitle.", "", " The text."};
        List<LawCharBlockType> expectedTypes = getExpectedTypes(parts);

        StringBuilder sb = new StringBuilder();
        for (String part : parts)
            sb.append(part);

        LawDocInfo info = LawProcessorUtils.getLawDocInfo(CCO, "1", SECTION);
        LawDocument doc = new LawDocument(info, sb.toString());
        doc.setTitle("The title");

        List<LawCharBlock> blocks = LawCharBlock.getBlocks(doc);
        assertEquals(expectedTypes.size(), blocks.size());
        for (int i = 0; i < blocks.size(); i++)
            assertEquals(expectedTypes.get(i), blocks.get(i).type());
    }

    @Test
    public void paragraphGetBlocksTest() {
        String[] textArray = {"Before paragraph.\n\n  1. Paragraph."};
        List<LawCharBlockType> expectedTypes = getExpectedTypes(textArray);
        // An extra newline should be added before the paragraph.
        String text = textArray[0].replaceFirst("\n", "");
        LawDocInfo info = LawProcessorUtils.getLawDocInfo(CCO, "2", SECTION);
        LawDocument doc = new LawDocument(info, text);

        List<LawCharBlock> blocks = LawCharBlock.getBlocks(doc);
        assertEquals(expectedTypes.size(), blocks.size());
        for (int i = 0; i < blocks.size(); i++)
            assertEquals(expectedTypes.get(i), blocks.get(i).type());
    }

    @Test
    public void unconsolidatedChapterGetBlocksTest() {
        String[] parts = {"", "First line!\n", "", "And more. ", "",
                BAT.getName() + " Law", "", " Here's text."};
        boldChapterTestHelper(parts, BAT, "2");
    }

    @Test
    public void consolidatedBoldTest() {
        String[] parts = {"Before. ", "", "Chapter III-B of the consolidated laws.", "", " After."};
        boldChapterTestHelper(parts, ABC, "3-B");
    }

    /**
     * Puts some common code together that tests chapters.
     * @param parts of test text.
     * @param code of the chapter.
     * @param docTypeId for the chapter.
     */
    private static void boldChapterTestHelper(String[] parts, LawChapterCode code, String docTypeId) {
        List<LawCharBlockType> expectedTypes = getExpectedTypes(parts);

        StringBuilder sb = new StringBuilder();
        for (String part : parts)
            sb.append(part);

        LawDocInfo info = LawProcessorUtils.getLawDocInfo(code, "-CH" + docTypeId, CHAPTER);
        LawDocument doc = new LawDocument(info, sb.toString());
        doc.setDocTypeId(docTypeId);

        List<LawCharBlock> actualBlocks = LawCharBlock.getBlocks(doc);
        assertEquals(expectedTypes.size(), actualBlocks.size());
        for (int i = 0; i < actualBlocks.size(); i++)
            assertEquals(expectedTypes.get(i), actualBlocks.get(i).type());
    }

    /**
     * Processes Strings one at a time to get the expected types.
     * Bold markers are represented in the array with empty strings.
     * @param parts of test text.
     * @return the types that the output should match.
     */
    private static List<LawCharBlockType> getExpectedTypes(String[] parts) {
        List<LawCharBlockType> expectedTypes = new ArrayList<>();
        for (String part : parts) {
            if (part.isEmpty())
                expectedTypes.add(BOLD_MARKER);
            else {
                Matcher m = getMatcher(part);
                while (m.find())
                    expectedTypes.add(parseType(m.group()));
            }
        }
        // Newlines are always added at the end.
        Collections.addAll(expectedTypes, NEWLINE, NEWLINE);
        return expectedTypes;
    }
}
