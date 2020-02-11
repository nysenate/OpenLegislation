package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.law.LawDocInfo;
import gov.nysenate.openleg.model.law.LawDocumentType;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class LawTitleParserTest {

    // NOTE: Make sure to use '\\n' instead of '\n' for line endings to match the encoding of law files.
    private LawDocInfo lawInfo = new LawDocInfo();

    // Article tests
    @Test
    public void parsesArticleTitleEndingWithSection() {
        String text = "                               ARTICLE XIV\\n" +
                "            Laws Repealed; Construction; When to Take Effect.\\n" +
                "Section 700. Laws repealed.\\n" +
                "        701. Construction.\\n" +
                "        702. When to take effect.";

        String expectedTitle = "Laws Repealed; Construction; When to Take Effect";
        testArticleTitle(text, expectedTitle,"XIV", "BNKA14");
    }
    @Test
    public void parsesEmptyTitle() {
        String text = "                                  ARTICLE 6\\n" +
                " \\n" +
                "Section 293. Establishment of interstate library district.\\n" +
                "          294. Appointment  of  members  of  governing board of interstate\\n" +
                "                 library district.\\n" +
                "          295. Support of interstate library district.\\n" +
                "          296. Compact administrator.\\n" +
                "          297. Withdrawal.";
        String expectedTitle = "No title";
        testArticleTitle(text, expectedTitle,"6", "EDNA6");
    }
    @Test
    public void emptyTitleWithoutEmptyLine() {
        String text = "                              ARTICLE XIII\\n" +
                "Section 157. Apples; closed packages; definition.\\n" +
                "        158. Apples; adoption of grades; branding.\\n" +
                "        159. Sale of apples; presumption; rules and regulations.\\n" +
                "        160. Standard evaporated apples; definition; sale of regulated.\\n";
        String expectedTitle = "No title";
        testArticleTitle(text, expectedTitle,"XIII", "AGMA13");
    }
    @Test
    public void parsesTitleWithoutCapsOrSection() {
        String text = "                               ARTICLE XV\\n" +
                "      General Provisions Applicable to Banking Stock Corporations,\\n" +
                "               Limited Liability Investment Companies, and\\n" +
                "                    Limited Liability Trust Companies\\n" +
                "Title  1.   Definitions; Application; Certificates; Miscellaneous.\\n" +
                "       2.   Corporate powers.";
        String expectedTitle = "General Provisions Applicable to Banking Stock Corporations, " +
                "Limited Liability Investment Companies, and Limited Liability Trust Companies";
        testArticleTitle(text, expectedTitle,"XV", "BNKA15");
    }
    @Test
    public void parsesTitleWithNoBody() {
        String text = "                                  TAXES\\n" +
                "                               ARTICLE IX\\n" +
                "                          INCOME TAX SURCHARGE";
        String expectedTitle = "Income Tax Surcharge";
        testArticleTitle(text, expectedTitle,"IX", "YTSA9");
    }
    @Test
    public void spacesBeforeSection() {
        String text = "                                ARTICLE 1\\n" +
                "                               SHORT TITLE\\n" +
                "  Section  1.01.  Short title.\\n";
        String expectedTitle = "Short Title";
        testArticleTitle(text, expectedTitle,"1", "ACATAA1");
    }
    @Test
    public void dashesInTitle() {
        String text = "               ARTICLE 100--COMMENCEMENT OF ACTION IN LOCAL\\n" +
                "                  CRIMINAL COURT--LOCAL CRIMINAL COURT\\n" +
                "                         ACCUSATORY INSTRUMENTS\\n" +
                "Section 100.05 Commencement of action; in general.\\n" +
                "        100.07 Commencement of action; effect of family court\\n" +
                "                 proceeding.\\n";
        String expectedTitle = "Commencement of Action In Local Criminal Court--local Criminal Court Accusatory Instruments";
        testArticleTitle(text, expectedTitle,"100", "CPLA100");
    }
    @Test
    public void centeredNonTitleInfo() {
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
        testArticleTitle(text, expectedTitle,"10", "CVPA10");
    }

    @Test
    public void capsTitleEndingWithTITLE() {
        String text = "                              ARTICLE 27-B\\n" +
                "                        TREATMENT OF HYPERTENSION\\n" +
                "TITLE  I.  Mary Lasker Heart and Hypertension Institute (õõ 2720-2722).\\n" +
                "       II. Centers for the treatment of hypertension (õõ 2723-2724).\\n";
        String expectedTitle = "Treatment of Hypertension";
        testArticleTitle(text, expectedTitle,"27-B", "PBHA27-B");
    }

    @Test
    public void extraNewlineBeforeTitle() {
        String text = "                               ARTICLE 15\\n                             " +
                "WATER RESOURCES\\nTitle 1.  Short title; statement of policy; definitions; " +
                "general\\n            provisions\\n      3.  Powers and duties";
        String expectedTitle = "Water Resources";
        testArticleTitle(text, expectedTitle,"15", "ENVA15");
    }

    @Test
    public void asteriskInText() {
        String text = "                              ARTICLE 130*\\n                           " +
                "GENERAL PROVISIONS\\n                   Subarticle 1. Introductory summary.\\n" +
                "Section 6500.   Introduction.\\n";
        String expectedTitle = "General Provisions";
        testArticleTitle(text, expectedTitle,"130*", "EDNA130*");
    }

    @Test
    public void asteriskFirstInText() {
        String text = "                            * ARTICLE 31-A-2\\n                      " +
                "TAX ON REAL ESTATE TRANSFERS\\n                         IN THE TOWN OF CHATHAM" +
                "\\nSection 1439-a. Definitions.\\n        1439-b. Imposition of tax.\\n        " +
                "* NB Repealed December 31, 2027\\n  * NB There are 3 Article 31-A-2's\\n";
        String expectedTitle = "Tax On Real Estate Transfers In the Town of Chatham";
        testArticleTitle(text, expectedTitle,"31-A-2*2", "TAXA31-A-2*2");
    }

    // Section tests
    @Test
    public void reluctantMatching() {
        String text = "  * Section 1. Legislative declaration. The legislature hereby declares\\nthat the " +
                "operation of responsible democratic government requires that\\nthe fullest " +
                "opportunity be afforded to the people to petition their\\ngovernment for the " +
                "redress of grievances and to express freely to\\nappropriate officials their " +
                "opinions on legislation and governmental\\noperations; and that, to preserve " +
                "and maintain the integrity of the\\ngovernmental decision-making process in " +
                "this state, it is necessary that\\nthe identity, expenditures and activities of" +
                " persons and organizations\\nretained, employed or designated to influence the " +
                "passage or defeat of\\nany legislation by either house of the legislature or " +
                "the approval, or\\nveto, of any legislation by the governor and attempts to " +
                "influence the\\nadoption or rejection of any rule or regulation having the " +
                "force and\\neffect of law or the outcome of any rate making proceeding by a " +
                "state\\nagency, be publicly and regularly disclosed.\\n  * NB Chapter 2 of the " +
                "laws of 1999 repealed, effective January 1,\\n2000, the lobbying act as enacted" +
                " by section 1 of chapter 1040 of the\\nlaws of 1981. A new lobbying act was " +
                "enacted as Article 1-A of the\\nlegislative law.\\n  Section 5 of such chapter " +
                "2 transfers all of the functions and powers\\nof the New York temporary state " +
                "commission on lobbying created by such\\nchapter 1040 to the New York temporary" +
                " state commission on lobbying\\ncreated by Article 1-A of the legislative law " +
                "with respect to receiving\\nthe periodic and annual reports required to be " +
                "filed pursuant to\\nsections 8, 9 and 10 of the repealed chapter 1040. Also, " +
                "pursuant to\\nsection 7 of such chapter 2, any action or proceeding commenced " +
                "prior to\\nJanuary 1, 2000 under the old lobbying act shall be continued,\\n" +
                "prosecuted and defended pursuant to the old lobbying act as in effect on\\n" +
                "December 31, 1999.\\n  Because of these provisions, the Commission will " +
                "continue to set out\\nthe full text of the repealed lobbying act through " +
                "December 31, 2000 in\\norder that lobbyists who were subject to the former " +
                "lobbying act may be\\nadequately apprised of the responsibilities and " +
                "obligations imposed upon\\nthem as continued by sections 5 and 7 of chapter 2 " +
                "of the laws of 1999.\\n";
        String expectedTitle = "Legislative declaration";
        testSectionTitle(text, expectedTitle,"RLA1");
    }

    @Test
    public void asteriskAfterDocTypeId() {
        String text = "  § 6514.* Revocation of certificates; amendment of registrations. (h)\\n" +
                "That a physician is or has been guilty  * NB This section 6514 came under old " +
                "title VIII. Although old 6514\\nwas repealed, there were amendments made, which" +
                " are still live. As noted\\nabove, no reference to 6514 added by 987/1971.\\n";
        String expectedTitle = "Revocation of certificates; amendment of registrations";
        testSectionTitle(text, expectedTitle,"EDN6514*");
    }

    @Test
    public void asteriskBeforeDocTypeId() {
        String text = "  * § 308-a. Establishment of county of Schuyler wireless surcharge. 1." +
                "\\nNotwithstanding the provisions of any law to the contrary, the county of\\n" +
                "Schuyler";
        String expectedTitle = "Establishment of county of Schuyler wireless surcharge";
        testSectionTitle(text, expectedTitle, "CNT308-A*10");
    }

    @Test
    public void noAsteriskInText() {
        String text = "  § 28. Biofuel production credit. (a) General. A taxpayer subject to\\n" +
                "tax under article nine, nine-A or twenty-two of this chapter shall be\\nallowed" +
                " a credit against such tax pursuant to the provisions referenced\\nin " +
                "subdivision (d) of this section.  * NB Effective until January 1, 2015\\n  * " +
                "(d) Cross-references. For application of the credit provided for in\\nthis " +
                "section, see the following provisions of this chapter:\\n  (1) Article 9: " +
                "Section 187-c.\\n  (2) Article 9-A: Section 210-B, subdivision 24.\\n  (3) " +
                "Article 22: Section 606, subsections (i) and (jj).\\n  * NB Effective January 1" +
                ", 2015\\n";
        String expectedTitle = "Biofuel production credit";
        testSectionTitle(text, expectedTitle, "TAX28*2");
    }

    @Test
    public void twoAsterisksInText() {
        String text = "  ** § 97-mm. State police motor vehicle law enforcement account. 1.\\n" +
                "There is hereby established in the joint custody of the state\\ncomptroller and" +
                " the commissioner of taxation and finance a fund to be\\nknown as the \"state " +
                "police motor vehicle law enforcement account\".\\n ** NB There are 2 § 97-mm's";
        String expectedTitle = "State police motor vehicle law enforcement account";
        testSectionTitle(text, expectedTitle, "STF97-MM*2");
    }

    @Test
    public void PEPsection() {
        String text = "  § 302A. Total loss notice and waiver of the gap amount. 1. If the\\nreta";
        String expectedTitle = "Total loss notice and waiver of the gap amount";
        testSectionTitle(text, expectedTitle,"PEP302-A");
    }

    @Test
    public void EPTsection() {
        String text = "§ 1-1.1 Short title; how cited\\n  This chapter shall be known as the " +
                "Estates, Powers and Trusts Law and\\nmay be cited as EPTL.  A section of this " +
                "law may be cited by article,\\npart and section number, to wit, EPTL 1-1.1, " +
                "which refers to article 1,\\npart 1, section 1, without being preceded by the " +
                "word article, part or\\nsection or the symbol §.\\n";
        String expectedTitle = "Short title; how cited";
        testSectionTitle(text, expectedTitle,"EPT1-1.1");
    }

    // No title tests
    @Test
    public void jointRule() {
        String text = "JOINT RULE II\\n  ";
        hasNoTitle(text, "2", "CMSJR2", LawDocumentType.JOINT_RULE);
    }

    @Test
    public void sectionRule(){
        String text = "§  2.  The  administration  and  operations  of  the  Senate  shall be\\n" +
                "conducted in a fair and nonpartisan manner, including access to services\\n" +
                "necessary to all members  and  their  offices,  without  regard  to  the\\n" +
                "members conference.\\n  ";
        hasNoTitle(text, "2", "CMSR5S2", LawDocumentType.SECTION);
    }

    @Test
    public void BATsection() {
        String text = "                Chapter 47 of the laws of 1931 relating to\\n" +
                "             bridges and tunnels in New York and New Jersey.\\n  Section 1. " +
                "The state of New Jersey by appropriate legislation\\nconcurring herein, the " +
                "states of New York and New Jersey hereby declare\\nand agree that the " +
                "vehicular traffic moving across the interstate waters\\nwithin the port of " +
                "New York district, created by the compact of April\\nthirty, nineteen hundred " +
                "twenty-one, between the said states, which said\\nphrase \"interstate waters\" " +
                "as used in this act shall include the portion\\nof the Hudson river within the " +
                "said port of New York district north of\\nthe New Jersey state line, " +
                "constitutes a general movement of traffic\\nwhich follows the most accessible " +
                "and practicable routes, and that the\\nusers of each bridge or tunnel over or " +
                "under the said waters benefit by\\nthe existence of every other bridge or " +
                "tunnel since all such bridges and\\ntunnels as a group facilitate the movement " +
                "of such traffic and relieve\\ncongestion at each of the several bridges and " +
                "tunnels.  Accordingly the\\n";
        hasNoTitle(text, "1", "BAT1", LawDocumentType.SECTION);
    }

    @Test
    public void LSAsection() {
        String text = "  Section 1. The provisions of this act shall apply in the city of New\\nYork.\\n";
        hasNoTitle(text, "1", "LSA1", LawDocumentType.SECTION);
    }

    @Test
    public void aSubsectionWithParentheses() {
        String text = "  * § 2-a. (a) 1. For purposes of this section, annual income shall mean" +
                "\\nthe federal adjusted gross income as reported on the New York state\\nincome" +
                " tax return. Total annual income means the sum of the annual\\nincomes ";
        hasNoTitle(text, "2-A", "ERL2-A", LawDocumentType.SECTION);
    }

    @Test
    public void oneSubsection() {
        String text = "  § 50. 1. Notwithstanding the provisions of any other law to the\\n" +
                "contrary, the dormitory authority and the urban development corporation\\nare " +
                "hereby authorized to issue bonds or notes in one or more series for\\nthe " +
                "purpose of funding project costs undertaken by or on behalf of\\nspecial act " +
                "school districts, state-supported schools for the blind and\\ndeaf, approved " +
                "private special education schools, non-public schools,\\ncommunity centers, day" +
                " care facilities, residential camps, day camps,\\nand other state costs " +
                "associated with such capital projects. The\\naggregate ";
        hasNoTitle(text, "50", "UDA50", LawDocumentType.SECTION);
    }

    @Test
    public void nonSectionNoText() {
        String text = "                                 TITLE S\\n";
        hasNoTitle(text, "S", "ACATS", LawDocumentType.TITLE);
    }

    @Test
    public void typeWordAfterDeclaration() {
        String text = "                                 TITLE 2\\nSection 302. Definitions.\\n";
        hasNoTitle(text, "2", "RSSA8T2", LawDocumentType.TITLE);
    }

    // Miscellaneous tests
    @Test
    public void onlyCapTitle() {
        String text = "                                 Title 2\\n" +
                "                               TOWN BOARD\\n";
        String expectedTitle = "Town Board";
        testTitle(text, expectedTitle,"2", "TWNA3-AT2", LawDocumentType.TITLE);
    }

    @Test
    public void partAndTitleSameLineNoSeparator() {
        String text = "                     PART 8. HONORARY TRUSTS FOR PETS\\n" +
                "  7-8.1 Trusts for pets";
        String expectedTitle = "Honorary Trusts For Pets";
        testTitle(text, expectedTitle,"8", "EPTA7P8", LawDocumentType.PART);
    }

    @Test
    public void subArticleTest() {
        String text = "                    Subarticle 2.  State management.\\n";
        String expectedTitle = "State Management";
        testTitle(text, expectedTitle,"2", "EDNA130*SA2", LawDocumentType.SUBARTICLE);
    }

    @Ignore
    @Test
    public void unconsolidatedWithSubsection() {
        String text = "  § 10. (a) Supervisory board; created. There shall be created for the\\n" +
                "city of Troy a supervisory board for the purpose of reviewing, directing\\nand " +
                "supervising the financial management of the city of Troy during any\\nemergency" +
                " period and following termination of any emergency period for\\nthe purpose of" +
                " overseeing and advising the chief executive officer and\\nthe chief fiscal " +
                "officer, as such terms are defined in paragraphs 5 and\\n5-a of section 2.00 of" +
                " the local finance law, of the city of Troy and\\nmaking recommendations " +
                "regarding the city's budget. ";
        String expectedTitle = "Supervisory board; created.";
        testTitle(text, expectedTitle,"10", "TRY10", LawDocumentType.SECTION);
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
        String expectedTitle = "";
        testTitle(text, expectedTitle,"", "ACAATTN", LawDocumentType.MISC);
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
        testArticleTitle(text, expectedTitle,"XIII-E", "BNKA13-E");
    }

    private void testArticleTitle(String text, String expectedTitle, String docTypeID, String docID) {
        testTitle(text, expectedTitle, docTypeID, docID, LawDocumentType.ARTICLE);
    }

    private void testSectionTitle(String text, String expectedTitle, String docID) {
        testTitle(text, expectedTitle, docID.substring(3), docID, LawDocumentType.SECTION);
    }

    private void hasNoTitle(String text, String docTypeID, String docID, LawDocumentType type) {
        testTitle(text, LawTitleParser.NO_TITLE, docTypeID, docID, type);
    }

    private void testTitle(String text, String expectedTitle, String docTypeID, String docID, LawDocumentType type) {
        lawInfo.setDocType(type);
        lawInfo.setDocTypeId(docTypeID);
        lawInfo.setLawId(docID.substring(0, 3));
        lawInfo.setLocationId(docID.substring(3));
        lawInfo.setDocumentId(docID);
        assertEquals(expectedTitle, LawTitleParser.extractTitle(lawInfo, text));
    }
}
