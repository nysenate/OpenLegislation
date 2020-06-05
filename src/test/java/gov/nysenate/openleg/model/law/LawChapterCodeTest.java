package gov.nysenate.openleg.model.law;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;
import static gov.nysenate.openleg.model.law.LawChapterCode.*;


@Category(UnitTest.class)
public class LawChapterCodeTest {
    private void test(String citation, LawChapterCode expected) {
        assertEquals(expected, LawChapterCode.lookupCitation(citation).orElse(null));
    }

    /**
     * Tests all unique citations that could not otherwise be identified.
     */
    @Test
    public void uniqueCitationTest() {
        test("Rec & Pks", PAR);
        test("El", ELN);
        test("NYS Med Care Fac Fin Ag Act", MCF);
        test("Fin", STF);
    }

    /**
     * Tests Strings that don't give enough information to lookup a chapter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void edgeCases() {
        test("", null);
        test(" ", null);
        test("E", null);
        test(null, null);
    }

    /**
     * Tests some different citation suffixes.
     */
    @Test
    public void variousEndings() {
        test("Vol Amb Wkr Ben L", VAW);
        test("St Fin L.", STF);
        test("Tax Law", TAX);
    }

    /**
     * Tests unconsolidated law processing.
     */
    @Test
    public void unconsolidatedLaws() {
        test("Chap 115 of 1894", LSA);
        test("Chap 43 of 1922", DPN);
        test("Emerg Ten Prot Act", ETP);
    }

    /**
     * Tests citations with conjugations, or which correspond to chapters with conjugations.
     */
    @Test
    public void conjunctionTest() {
        test("Ct Claims Act", CTC);
        test("NYC Health & Hosp Corp Act", HHC);
        test("V T L", VAT);
        test("Ag & Mkts L", AGM);
        test("Art & Cul L", ACA);
        test("NYS Print L", PPD);
        test("EPT L", EPT);
        test("RWB L", PML);
    }

    /**
     * Tests some incorrect citations.
     */
    @Test
    public void malformedCiatations() {
        test("520 & 1306-a", null);
        test("606 Tax L", null);
        test("NYC LL46 of 1989", null);
        test("Ment Hgy", MHY);
    }

    /**
     * Tests citations where multiple matching chapters are found.
     */
    @Test
    public void multipleMatchesTest() {
        test("Constn", CNS);
        test("BC L", BSC);
        test("Cor", COR);
        test("CP L", CPL);
        test("State", STL);
    }

    /**
     * Tests everything else.
     */
    @Test
    public void coverageTests() {
        assertTrue(LawChapterCode.isUnconsolidated(LSA.name()));
        assertFalse(LawChapterCode.isUnconsolidated(CNS.name()));
        assertTrue(TAX.hasNumericalTitles());
        assertFalse(CVS.hasNumericalTitles());
        assertEquals(LawType.CONSOLIDATED, CCO.getType());
        assertEquals("Cooperative Corporations", CCO.getName());
    }
}
