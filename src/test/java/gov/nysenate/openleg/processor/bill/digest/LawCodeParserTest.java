package gov.nysenate.openleg.processor.bill.digest;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.client.view.bill.BillAmendmentView;
import gov.nysenate.openleg.dao.bill.data.SqlBillDao;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.law.LawActionType;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.BillLawCodeParser;
import gov.nysenate.openleg.processor.bill.XmlLDSummProcessor;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;


@Category(SillyTest.class)
public class LawCodeParserTest extends BaseXmlProcessorTest {

    @Autowired
    BillDataService billDataService;
    @Autowired
    XmlLDSummProcessor xmlLDSummProcessor;


    // HELPERS
    private BillAmendment getFirstAmendment(BaseBillId id) {
        Bill b = getBill(id);
        List<BillAmendment> amendments = b.getAmendmentList();
        assert (amendments.size() > 0);
        return b.getActiveAmendment();
    }

    private void compareMaps(BillAmendment amd, Map<LawActionType, HashSet<String>> expected) {
        BillLawCodeParser parser = new BillLawCodeParser();
        parser.parse(amd.getLaw());

        Map<LawActionType, HashSet<String>> actual = parser.getMapping();
        assertEquals(expected, actual);
    }


    // TESTS
    @Test
    public void multiSectionTest() {
        // Simple test to make sure the scope of amd extends to §§110-b, 64, 64-a and 64-c

        // Add §110-c, amd §§110-b, 64, 64-a & 64-c, ABC L
        final BaseBillId baseBillId = new BaseBillId("S00344", 2017);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("ABC110-C"));
        exp.put(LawActionType.AMEND, Sets.newHashSet("ABC64", "ABC110-B", "ABC64-C", "ABC64-A"));
        compareMaps(amd, exp);
    }


    @Test
    public void rangeTest() {
        // Link to the higher level of law (CORA24-A) when a range of subsections are affected

        // Add Art 24-A §§810 - 814, Cor L; amd §70.30, Pen L
        BaseBillId baseBillId = new BaseBillId("A02484", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("CORA24-A"));
        exp.put(LawActionType.AMEND, Sets.newHashSet("PEN70.30"));
        compareMaps(amd, exp);
        baseBillId = new BaseBillId("A6078", 2019);
        amd = getFirstAmendment(baseBillId);

        // Amd Art 29-D §§1299-M - 1299-V, amd §1825, Tax L
        exp.clear();
        exp.put(LawActionType.ADD, Sets.newHashSet("TAXA29-D"));
        exp.put(LawActionType.AMEND, Sets.newHashSet("TAX1825"));
        compareMaps(amd, exp);
    }

    @Test
    public void articleSingle() {
        // Link to the more specific subsection (ELN8-600) when only that subsection is affected

        // Amd Art 8 Title VI §§8-600, 8-602, 8-604 & 8-606, El L
        final BaseBillId baseBillId = new BaseBillId("S7212", 2017);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.AMEND, Sets.newHashSet("ELN8-600", "ELN8-602", "ELN8-604", "ELN8-606"));
        compareMaps(amd, exp);
    }


    @Test
    public void subsectionTest() {
        // Amd §§4-117, 8-502, 8-504, 8-508, 8-510 & 17-108, add §8-503, El L
        final BaseBillId baseBillId = new BaseBillId("S02301", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("ELN8-503"));
        exp.put(LawActionType.AMEND, Sets.newHashSet("ELN4-117", "ELN8-504", "ELN17-108", "ELN8-502",
            "ELN8-510", "ELN8-508"));
        compareMaps(amd, exp);
    }

    @Test
    public void mixedTest() {
        // Test articles with mixed numer-letter names

        // Add Art 7-A §§710 - 712, RWB L
        final BaseBillId baseBillId = new BaseBillId("S01974", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("PMLA7-A"));
        compareMaps(amd, exp);
    }

    @Test
    public void multiLevelTest() {
        // Test to make sure both Article 27 and Title 28 are put in the lawDocId

        // Add Art 27 Title 28 §§27-2801 - 27-2805, En Con L
        final BaseBillId baseBillId = new BaseBillId("A04398", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("ENVA27T28"));
        compareMaps(amd, exp);
    }

    @Test
    public void diverseTest() {
        // Add §§4806, 3224-d & 4303-a, amd §§4903, 4910, 4914, 3216, 3221, 4303, 4325 &
        // 3231,
        // Ins L; add §4406-h, amd §§4903, 4910 & 4914, Pub Health L; amd §§367-a
        // & 364-j, Soc Serv L; amd §§6810 & 6826-a, Ed L
        final BaseBillId baseBillId = new BaseBillId("A04521", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("ISC4806", "ISC3224-D", "PBH4406-H", "ISC4303-A"));
        exp.put(LawActionType.AMEND, Sets.newHashSet("ISC3221", "ISC3231", "SOS367-A", "ISC4303", "ISC4325",
            "ISC4910", "ISC3216", "EDN6810", "SOS364-J", "ISC4903", "ISC4914", "EDN6826-A", "PBH4910", "PBH4903", "PBH4914"));
        compareMaps(amd, exp);
    }

    @Test
    public void generallyTest() {
        // Tests to make sure law volumes that are amended generally are handled properly

        // Amd Lien L, generally; amd §§199-a & 663, Lab L; amd §§6201, 6210, 6211, 6223 & R6212, CPLR; amd §§624 & 630, ...
        // BC L; amd §§609 & 1102, Lim Lil L
        final BaseBillId baseBillId = new BaseBillId("A00486", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.AMEND, Sets.newHashSet("BSC630", "LLC609", "LAB199-A", "LLC1102", "LAB663",
                "CVP6212", "BSC624", "CVP6201", "CVP6223", "CVP6210", "CVP6211", "LIE (generally)"));
        compareMaps(amd, exp);
    }

    @Test
    public void variousGenerallyTest() {
        // Amd Pub Health L, generally
        final BaseBillId baseBillId = new BaseBillId("S01527", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        compareMaps(amd, exp);
    }

    @Test
    public void variedTest() {
        // Add §208-b, Pub Health L; add §99-ff, St Fin L
        BaseBillId baseBillId = new BaseBillId("A00736", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("PBH208-B", "STF99-FF"));
        compareMaps(amd, exp);

        // Add Art 14 Part 4 §685, Ed L; add §337-a, Soc Serv L
        baseBillId = new BaseBillId("A00700", 2019);
        amd = getFirstAmendment(baseBillId);

        exp.clear();
        exp.put(LawActionType.ADD, Sets.newHashSet("SOS337-A", "EDN685"));
        compareMaps(amd, exp);

        // Amd Pub Health L, generally; amd ��6540, 6542, 6731, 6741, 6807, 6909, 6957 &
        // 7901, add �6545-a, Ed L; amd �461-c, Soc Serv L;
        // amd
        // ��13-b & 13-c, Work Comp L; amd �33.04, Ment Hyg L; amd �406, Gen Bus L
        baseBillId = new BaseBillId("S00907", 2019);
        amd = getFirstAmendment(baseBillId);

        exp.clear();
        exp.put(LawActionType.AMEND, Sets.newHashSet("EDN6807", "WKC13-B", "EDN6909", "WKC13-C", "EDN6957",
                "SOS461-C", "EDN7901", "EDN6542", "EDN6741", "PBH (generally)", "EDN6731", "GBS406", "MHY33.04", "EDN6540"));
        exp.put(LawActionType.ADD, Sets.newHashSet("EDN6545-A"));
        compareMaps(amd, exp);

        // Rpld �590 sub 11, Lab L
        baseBillId = new BaseBillId("A00616", 2019);
        amd = getFirstAmendment(baseBillId);

        exp.clear();
        exp.put(LawActionType.REPEAL, Sets.newHashSet("LAB590"));
        compareMaps(amd, exp);
    }

    @Test
    public void contextTest() {
        // Make sure that the second reference of "Art" in "Art 39-F Art Head" is ignored

        // Amd Art 39-F Art Head, §899-aa, add §899-bb, Gen Bus L; amd §208, St Tech L
        BaseBillId baseBillId = new BaseBillId("A8884", 2017);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.AMEND, Sets.newHashSet("GBS899-AA", "STT208", "GBSA39-F"));
        exp.put(LawActionType.ADD, Sets.newHashSet("GBS899-BB"));
        compareMaps(amd, exp);

        // Amd Art 4-D Head, §§71-y & 71-z, add §§71-aa - 71-dd, Ag & Mkts L; add §97-yyyy, St Fin L
        baseBillId = new BaseBillId("S7254", 2017);
        amd = getFirstAmendment(baseBillId);

        exp.clear();
        exp.put(LawActionType.AMEND, Sets.newHashSet("AGM71-Z", "AGM71-Y", "AGMA4-D"));
        exp.put(LawActionType.ADD, Sets.newHashSet("STF97-YYYY", "AGMA4-D"));
        compareMaps(amd, exp);
    }


    @Test
    public void subTest() {
        // Tests to make sure extraneous qualifiers like "sub" are ignored

        // Add §13.32, Pks & Rec L; add §1105-D, Tax L; add §92-ii, St Fin L; amd Art 27 Title 27 Head, §§27-2701, ...
        //  27-2703, 27-2707 & 27-2709, rpld §§27-2701 sub 4, 27-2711 & 27-2713, add §27-2706, En Con L
        final BaseBillId baseBillId = new BaseBillId("A270", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.AMEND, Sets.newHashSet("ENV27-2709", "ENV27-2707", "ENVA27T27", "ENV27-2703", "ENV27-2701"));
        exp.put(LawActionType.ADD, Sets.newHashSet("PAR13.32", "STF92-II", "ENV27-2706", "TAX1105-D"));
        exp.put(LawActionType.REPEAL, Sets.newHashSet("ENV27-2713", "ENV27-2711", "ENV27-2701"));
        compareMaps(amd, exp);
    }

    @Test
    public void renameSimpleTest() {
        // Tests that only the old name of renamed articles are included

        // Ren §465 to be §466, add §465, Lab L
        final BaseBillId baseBillId = new BaseBillId("A270", 2015);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("LAB465"));
        exp.put(LawActionType.RENAME, Sets.newHashSet("LAB465"));
        compareMaps(amd, exp);
    }

    @Test
    public void renameTest() {
        // Tests that the new context created by "Art 10" is ignored

        // Ren Art 9 §§90 & 91 to be Art 10 §§100 & 101, add Art 9 §§90 - 98, Civ Rts L
        final BaseBillId baseBillId = new BaseBillId("S405", 2017);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("CVRA9"));
        exp.put(LawActionType.RENAME, Sets.newHashSet("CVR90", "CVR91"));
        compareMaps(amd, exp);
    }


    @Test
    public void renameMultiTest() {
        // Amd §§37-0203 & 37-0211, ren §§37-0209, 37-0211 & 37-0213 to be §§37-0211, 37-0213 & 37-0215, add §37-0209, En Con L
        final BaseBillId baseBillId = new BaseBillId("A4739", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("ENV37-0209"));
        exp.put(LawActionType.AMEND, Sets.newHashSet("ENV37-0203", "ENV37-0211"));
        exp.put(LawActionType.RENAME, Sets.newHashSet("ENV37-0213", "ENV37-0209", "ENV37-0211"));
        compareMaps(amd, exp);
    }

    @Test
    public void parenthesisTest() {
        // Make sure parenthesis are reluctantly, not greedily

        // Amd §§1006, 209, 802, 1203, 1306 & 1101, rpld §206, §1101 sub (s), §802 sub (b), §1203 sub (c) ¶2, ...
        // §1306 sub (d), §102 subs (a-1) & (e-1), Lim Lil L; rpld §121-201 sub (c), §121-902 sub (d), ...
        // §121-1500 sub (a) ¶(II), §121-1502 sub (f) ¶(II), §121-1300 sub (f), §121-101 subs (a-1) & (a-2), ...
        // amd §§121-201, 121-902, 121-1500, 121-1502 & 121-1300, Partn L; rpld §23.03 sub 4, Arts & Cul L; add §89-j, St Fin L
        final BaseBillId baseBillId = new BaseBillId("S3361", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.AMEND, Sets.newHashSet("LLC1101", "LLC209", "LLC1203", "LLC1006", "LLC802", "LLC1306"));
        exp.put(LawActionType.ADD, Sets.newHashSet("STF89-J"));
        exp.put(LawActionType.REPEAL, Sets.newHashSet("ACA23.03", "LLC1101", "LLC1203", "LLC802",
                "LLC1306", "LLC206", "LLC102"));
        compareMaps(amd, exp);
    }

    @Test
    public void romanNumeralTest() {
        // Test basic Roman Numeral parsing

        // Add Art 2 Title VI §§266 - 266-c, Pub Health L
        final BaseBillId baseBillId = new BaseBillId("S6742", 2009);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, Sets.newHashSet("PBHA2T6"));
        compareMaps(amd, exp);
    }

    @Test
    public void romanNumeralTest2() {
        // Test Roman Numeral Parsing and make sure that Title 2, 3, 4, and 5 are all under Article 10

        // Add §§29-h & 29-i, amd §21, Exec L; add Art 10 Title I §§1000 - 1003, Title II §§1010 - 1016, ...
        // Title III §§1020 - 1027, Title IV §§1030 - 1031, Title V §§1040 - 1047, Pub Health L; amd Pen L, generally; ...
        // amd CP L, generally; amd §§1310, 1311-a & 1313, CPLR; add §30, amd §1825, Tax L
        final BaseBillId baseBillId = new BaseBillId("A8217", 2009);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.AMEND, Sets.newHashSet("CVP1311-A", "TAX1825", "CVP1313", "EXC21",
                "CPL (generally)", "CVP1310", "PEN (generally)"));
        exp.put(LawActionType.ADD, Sets.newHashSet("PBHA10T4", "PBHA10T5", "EXC29-H", "PBHA10T2", "EXC29-I",
                "PBHA10T3", "PBHA10T1", "TAX30"));
        compareMaps(amd, exp);
    }

    @Test
    public void nonNumericalTest() {
        // Test that the part name, but not the title name is parsed

        // Add Part III Title V Art 740 §§740.10 - 740.25, rpld §440.50, CP L; amd §§259-i & 646-a, Exec L
        final BaseBillId baseBillId = new BaseBillId("A5010", 2009);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.REPEAL, Sets.newHashSet("CPL440.50"));
        exp.put(LawActionType.ADD, Sets.newHashSet("CPLP3TVA740"));
        exp.put(LawActionType.AMEND, Sets.newHashSet("EXC259-I", "EXC646-A"));
        compareMaps(amd, exp);
    }
}



