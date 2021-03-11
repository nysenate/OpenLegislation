package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing_utils.LawTestUtils;

import java.util.List;

import static gov.nysenate.openleg.api.legislation.law.view.LawPdfUtil.BOLD_MARKER;
import static gov.nysenate.openleg.api.legislation.law.view.LawPdfUtil.getLines;
import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class LawPdfUtilTest {
    @Test
    public void dummyGetBlocksTest() {
        LawDocInfo info = LawTestUtils.getLawDocInfo(MHA, "-ROOT", CHAPTER);
        LawDocument doc = new LawDocument(info, "");
        doc.setDummy(true);
        assertTrue(getLines(doc).isEmpty());
    }

    @Test
    public void articleTest() {
        var expected = "ARTICLE I\n\n" + BOLD_MARKER + "    SHORT TITLE" + BOLD_MARKER + " Section 1. Short title.";
        boldTestHelper(expected, STF, ARTICLE, "SHORT TITLE");
    }

    @Test
    public void sectionGetBlocksTest() {
        var expected = BOLD_MARKER + "1. The\ntitle." + BOLD_MARKER + " The text.";
        boldTestHelper(expected, CCO, SECTION, "The title.");
    }

    @Test
    public void paragraphGetBlocksTest() {
        var text = "Before paragraph.\n  1. Paragraph.";
        LawDocInfo info = LawTestUtils.getLawDocInfo(LAB, "2", SECTION);
        LawDocument doc = new LawDocument(info, text);

        List<String> lines = LawPdfUtil.getLines(doc);
        StringBuilder withNewlines = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            withNewlines.append(lines.get(i));
            if (i != lines.size() - 1)
                withNewlines.append("\n");
        }
        // An extra line should've been added before the paragraph.
        assertEquals(text.replaceFirst("\n", "\n\n"), withNewlines.toString().trim());
    }

    @Test
    public void consolidatedBoldTest() {
        var expected = "Before.  " + BOLD_MARKER + "CHAPTER III-B OF THE CONSOLIDATED LAWS" + BOLD_MARKER + " After.";
        boldTestHelper(expected, ABC, CHAPTER, "");
    }

    @Test
    public void unconsolidatedBoldTest() {
        var expected = BOLD_MARKER + "Chapter 1016 of the laws of 1969" + BOLD_MARKER + "\n" +
                BOLD_MARKER + "NEW YORK CITY HEALTH AND HOSPITALS CORPORATION ACT" + BOLD_MARKER;
        boldTestHelper(expected, HHC, CHAPTER, "");
    }

    @Test
    public void rulesBoldTest() {
        var expected = BOLD_MARKER + "RULES OF THE SENATE\n\n    OF THE STATE OF NEW YORK" + BOLD_MARKER + "\n" +
                "\n    2021-2022 Rules  of  the  Senate  for  the  year  2021-2022,  as adopted by Senate Resolution number 2 of 2021.";
        boldTestHelper(expected, CMS, CHAPTER, "");
    }

    @Test
    public void constitutionBoldTest() {
        var expected = BOLD_MARKER + "THE CONSTITUTION OF THE STATE OF NEW YORK" + BOLD_MARKER +
                "\n\n    ARTICLE I\n\n    Bill of Rights";
        boldTestHelper(expected, CNS, CHAPTER, "");
    }

    /**
     * Puts some common code together that tests chapters.
     * @param code of the chapter.
     */
    private static void boldTestHelper(String expected, LawChapterCode code, LawDocumentType type, String title) {
        String text = expected.replaceAll(BOLD_MARKER, "");
        LawDocInfo info = LawTestUtils.getLawDocInfo(code, "LOC", type);
        LawDocument doc = new LawDocument(info, text);
        doc.setTitle(title);

        List<String> lines = getLines(doc);
        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            actual.append(lines.get(i));
            if (i != lines.size() - 1)
                actual.append("\n");
        }
        assertEquals(expected, actual.toString().trim());
    }
}
