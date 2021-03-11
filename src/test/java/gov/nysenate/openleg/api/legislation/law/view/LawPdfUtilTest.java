package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocument;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing_utils.LawTestUtils;

import java.util.List;

import static gov.nysenate.openleg.api.legislation.law.view.LawPdfUtil.BOLD_MARKER;
import static gov.nysenate.openleg.api.legislation.law.view.LawPdfUtil.getLines;
import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.CHAPTER;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.SECTION;
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
    public void sectionGetBlocksTest() {
        LawDocInfo info = LawTestUtils.getLawDocInfo(CCO, "1", SECTION);
        LawDocument doc = new LawDocument(info, "1. The\ntitle. The text.");
        doc.setTitle("The title.");

        List<String> lines = getLines(doc);
        assertEquals(4, lines.size());
        String newlineToSpace = lines.get(0) + " " + lines.get(1);
        String toMatch = BOLD_MARKER + ".*" + doc.getTitle() + BOLD_MARKER + ".*";
        assertTrue(newlineToSpace.matches(toMatch));
        assertEquals("", lines.get(2));
        assertEquals("", lines.get(3));
    }

    @Test
    public void paragraphGetBlocksTest() {
        String text = "Before paragraph.\n  1. Paragraph.";
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
        String expected = "Before." + BOLD_MARKER + "  CHAPTER III-B OF THE CONSOLIDATED LAWS" + BOLD_MARKER + " After.";
        boldChapterTestHelper(expected, ABC, "3-B");
    }

    // TODO: remove this
    @Test
    public void exactTest() {
        String text = """
                                           PUBLIC SERVICE LAW
                                         LAWS 1910, CHAPTER 480
                An act relating to the public service of utility companies, constituting
                     chapter forty-eight of the consolidated laws.
                Became  a law June 14, 1910, with the approval of the Governor.  Passed,
                     three-fifths being present.
                  The People of the  State  of  New  York,  represented  in  Senate  and
                Assembly, do enact as follows:
                                   CHAPTER 48 OF THE CONSOLIDATED LAWS
                                         THE PUBLIC SERVICE LAW.
                Article  1.   The department of public service. (Secs. 1-26.)
                         2.   Residential  gas,  electric  and  steam  utility  service.
                                (Secs. 30-53.)
                         3-C. Provisions   relating   to   liquid   petroleum   pipeline
                                corporations. (Secs. 63-ee--63-ff.)
                         4.   Provisions  relating  to  gas  and  electric corporations;
                                regulation of price of gas and electricity.
                                (Secs. 64-77.)
                         4-A. Provisions  relating  to  steam  corporations;  regulating
                                price of steam. (Secs. 78-89.)
                         4-B. Provisions relating to water (Secs. 89-a--89-p.)
                         5.   Provisions  relating  to telegraph and telephone lines and
                                to telephone and telegraph corporations.
                                (Secs. 90--102.)
                         6.   Provisions affecting two  or  more  kinds  of  the  public
                                service and the persons and corporations furnishing such
                                service.  (Secs. 105--119-c.)
                         7.   Siting of major utility transmission facilities.
                                (Secs. 120--130.)
                         7-A. Home insulation and conservation. (Secs. 135-a--135-o.)
                         8.   Siting  of  major  steam  electric  generating facilities.
                                (Secs. 140--149-a.)
                         9.   Commissions and offices abolished; saving clause;  repeal.
                                (Secs. 150 - 154.)
                         10.  Siting of Major Electric Generating Facilities.
                                (Secs. 160-173.)
                         11.    Provisions relating to cable television companies.
                                (Secs. 211-230.)
                """;
        LawDocInfo info = LawTestUtils.getLawDocInfo(PBS, "-CH48", CHAPTER);
        LawDocument doc = new LawDocument(info, text);
        for (String line : getLines(doc))
            System.out.println(line);
    }

    /**
     * Puts some common code together that tests chapters.
     * @param code of the chapter.
     * @param docTypeId for the chapter.
     */
    private static void boldChapterTestHelper(String expected, LawChapterCode code, String docTypeId) {
        String text = expected.replaceAll(BOLD_MARKER, "");

        LawDocInfo info = LawTestUtils.getLawDocInfo(code, "-CH" + docTypeId, CHAPTER);
        LawDocument doc = new LawDocument(info, text);
        doc.setDocTypeId(docTypeId);

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
