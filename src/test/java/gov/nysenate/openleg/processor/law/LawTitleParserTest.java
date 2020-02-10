package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.law.LawDocInfo;
import gov.nysenate.openleg.model.law.LawDocumentType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class LawTitleParserTest {

    // NOTE: Make sure to use '\\n' instead of '\n' for line endings to match the encoding of law files.
    // TODO: Currently only tests parsing of Article titles.

    private LawDocInfo lawInfo = new LawDocInfo();

    @Before
    public void before() {
        lawInfo.setDocType(LawDocumentType.ARTICLE);
    }

    private void assertTitle(String expectedTitle, String text, String docTypeID, String docID) {
        lawInfo.setDocTypeId(docTypeID);
        lawInfo.setDocumentId(docID);
        assertEquals(expectedTitle, LawTitleParser.extractTitle(lawInfo, text));
    }

    @Test
    public void parsesArticleTitleEndingWithSection() {
        // BNKA14
        String text = "                               ARTICLE XIV\\n" +
                "            Laws Repealed; Construction; When to Take Effect.\\n" +
                "Section 700. Laws repealed.\\n" +
                "        701. Construction.\\n" +
                "        702. When to take effect.";

        String expectedTitle = "Laws Repealed; Construction; When to Take Effect.";
        assertTitle(expectedTitle, text, "XIV", "BNKA14");
    }
    @Test
    public void parsesEmptyTitle() {
        // EDNA6
        String text = "                                  ARTICLE 6\\n" +
                " \\n" +
                "Section 293. Establishment of interstate library district.\\n" +
                "          294. Appointment  of  members  of  governing board of interstate\\n" +
                "                 library district.\\n" +
                "          295. Support of interstate library district.\\n" +
                "          296. Compact administrator.\\n" +
                "          297. Withdrawal.";
        String expectedTitle = "No title";
        assertTitle(expectedTitle, text, "6", "EDNA6");
    }
    @Test
    public void emptyTitleWithoutEmptyLine() {
        // AGMA13
        String text = "                              ARTICLE XIII\\n" +
                "Section 157. Apples; closed packages; definition.\\n" +
                "        158. Apples; adoption of grades; branding.\\n" +
                "        159. Sale of apples; presumption; rules and regulations.\\n" +
                "        160. Standard evaporated apples; definition; sale of regulated.\\n";
        String expectedTitle = "No title";
        assertTitle(expectedTitle, text, "XIII", "AGMA13");
    }
    @Test
    public void parsesTitleWithoutCapsOrSection() {
        // BNKA15
        String text = "                               ARTICLE XV\\n" +
                "      General Provisions Applicable to Banking Stock Corporations,\\n" +
                "               Limited Liability Investment Companies, and\\n" +
                "                    Limited Liability Trust Companies\\n" +
                "Title  1.   Definitions; Application; Certificates; Miscellaneous.\\n" +
                "       2.   Corporate powers.";
        String expectedTitle = "General Provisions Applicable to Banking Stock Corporations, " +
                "Limited Liability Investment Companies, and Limited Liability Trust Companies";
        assertTitle(expectedTitle, text, "XV", "BNKA15");
    }
    @Test
    public void parsesTitleWithNoBody() {
        // YTSA9
        String text = "                                  TAXES\\n" +
                "                               ARTICLE IX\\n" +
                "                          INCOME TAX SURCHARGE";
        String expectedTitle = "Income Tax Surcharge";
        assertTitle(expectedTitle, text, "IX", "YTSA9");
    }
    @Test
    public void spacesBeforeSection() {
        // ACATAA1
        String text = "                                ARTICLE 1\\n" +
                "                               SHORT TITLE\\n" +
                "  Section  1.01.  Short title.\\n";
        String expectedTitle = "Short Title";
        assertTitle(expectedTitle, text, "1", "ACATAA1");
    }
    @Test
    public void dashesInTitle() {
        // CPLA100
        String text = "               ARTICLE 100--COMMENCEMENT OF ACTION IN LOCAL\\n" +
                "                  CRIMINAL COURT--LOCAL CRIMINAL COURT\\n" +
                "                         ACCUSATORY INSTRUMENTS\\n" +
                "Section 100.05 Commencement of action; in general.\\n" +
                "        100.07 Commencement of action; effect of family court\\n" +
                "                 proceeding.\\n";
        String expectedTitle = "Commencement of Action In Local Criminal Court--local Criminal Court Accusatory Instruments";
        assertTitle(expectedTitle, text, "100", "CPLA100");
    }
    @Test
    public void centeredNonTitleInfo() {
        // CVPA10
        String text = "                               ARTICLE 10\\n" +
                "                            PARTIES GENERALLY\\n" +
                "        1001. Necessary joinder of parties.\\n" +
                "                (a) Parties who should be joined.\\n" +
                "                (b) When joinder excused.\\n" +
                "        1002. Permissive joinder of parties.\\n" +
                "                (a) Plaintiffs.\\n" +
                "                (b) Defendants.\\n" +
                "                (c) Separate relief; separate trials.\\n" +
                "        1003. Nonjoinder and misjoinder of parties.";
        String expectedTitle = "Parties Generally";
        assertTitle(expectedTitle, text, "10", "CVPA10");
    }

    @Test
    public void capsTitleEndingWithTITLE() {
        // PBHA27-B
        String text = "                              ARTICLE 27-B\\n" +
                "                        TREATMENT OF HYPERTENSION\\n" +
                "TITLE  I.  Mary Lasker Heart and Hypertension Institute (õõ 2720-2722).\\n" +
                "       II. Centers for the treatment of hypertension (õõ 2723-2724).\\n";
        String expectedTitle = "Treatment of Hypertension";
        assertTitle(expectedTitle, text, "27-B", "PBHA27-B");
    }

    @Test
    public void onlyCapTitle() {
        // TWNA3-AT2
        String text = "                                 Title 2\\n" +
                "                               TOWN BOARD\\n";
        String expectedTitle = "Town Board";
        lawInfo.setDocType(LawDocumentType.TITLE);
        assertTitle(expectedTitle, text, "2", "TWNA3-AT2");
    }

    @Test
    public void partAndTitleSameLineNoSeparator() {
        // EPTA7P8
        String text = "                     PART 8. HONORARY TRUSTS FOR PETS\\n" +
                "  7-8.1 Trusts for pets";
        String expectedTitle = "Honorary Trusts For Pets";
        lawInfo.setDocType(LawDocumentType.PART);
        assertTitle(expectedTitle, text, "8", "EPTA7P8");
    }

    @Ignore
    @Test
    public void noTitleStarStarting() {
        // ACAATTN
        String text = "  * Notwithstanding that Chapter 73 of the Laws of 1983 amends\\n" +
                "subdivision (b) of Section 219 of the General Business Law, it is the\\n" +
                "determination of the Legislative Bill Drafting Commission, pursuant to\\n" +
                "bill sections 13 and 14 of Chapter 876 of the Laws of 1983, to juxtapose\\n" +
                "and transfer such subdivision to be subdivision 2 of Section 11.01, to\\n" +
                "Chapter 11-C of the Consolidated Laws entitled \"The Arts and Cultural\\n" +
                "Affairs Law\", as enacted by such Chapter 876 of the Laws of 1983.\\n" +
                "  * Notwithstanding that Chapter 851 of the Laws of 1983 adds\\n" +
                "subdivision 10 to Section 672 to the Executive Law, it is the";
        // TODO Cant find this on the LRS website to confirm the title.
        String expectedTitle = "ATTENTION";
        assertTitle(expectedTitle, text, "", "ACAATTN");
    }

    @Ignore
    @Test
    public void BNKA13E() {
        // BNKA13-E
        String text = "                             ARTICLE XIII-E\\n" +
                "          JOINT DEPOSITS AND SHARES; Unauthorized Withdrawals;\\n" +
                "                  Withdrawals From Decedents' Accounts\\n" +
                "Section 675. Joint deposits and shares; ownership and payment.\\n" +
                "        676. Unauthorized withdrawals from savings or time deposit\\n" +
                "               accounts.";
        String expectedTitle = "JOINT DEPOSITS AND SHARES; Unauthorized Withdrawals; Withdrawals From Decedents' Accounts";
        assertTitle(expectedTitle, text, "XIII-E", "BNKA13-E");
    }
}
