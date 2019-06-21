package gov.nysenate.openleg.processor.bill.digest;

import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.dao.bill.data.SqlBillDao;
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
        return amendments.get(0);
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
        // Add §110-c, amd §§110-b, 64, 64-a & 64-c, ABC L
        final BaseBillId baseBillId = new BaseBillId("S00344", 2017);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("ABC110-C")));
        exp.put(LawActionType.AMEND, new HashSet<>(Arrays.asList("ABC64", "ABC110-B", "ABC64-C", "ABC64-A")));
        compareMaps(amd, exp);
    }


    @Test
    public void rangeTest() {
        // Add Art 24-A §§810 - 814, Cor L; amd §70.30, Pen L
        final BaseBillId baseBillId = new BaseBillId("A02484", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("CORA24")));
        exp.put(LawActionType.AMEND, new HashSet<>(Arrays.asList("PEN70.30")));
        compareMaps(amd, exp);
    }

    @Test
    public void subsectionTest() {
        // Amd §§4-117, 8-502, 8-504, 8-508, 8-510 & 17-108, add §8-503, El L
        final BaseBillId baseBillId = new BaseBillId("S02301", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("ELN8-503")));
        exp.put(LawActionType.AMEND, new HashSet<>(Arrays.asList("ELN4-117", "ELN8-504", "ELN17-108", "ELN8-502", "ELN8-510", "ELN8-508")));
        compareMaps(amd, exp);
    }

    @Test
    public void articleTest() {
        // Amd §§4-117, 8-502, 8-504, 8-508, 8-510 & 17-108, add §8-503, El L
        final BaseBillId baseBillId = new BaseBillId("S01974", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("PMLA7")));
        compareMaps(amd, exp);
    }

    @Test
    public void multiQualifierTest() {
        // Add Art 27 Title 28 §§27-2801 - 27-2805, En Con L
        final BaseBillId baseBillId = new BaseBillId("A04398", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("ENVA27T28")));
        compareMaps(amd, exp);
    }

    @Test
    public void diverseTest() {
        // Add §§4806, 3224-d & 4303-a, amd §§4903, 4910, 4914, 3216, 3221, 4303, 4325 &
        // 3231,
        //Ins L; add §4406-h, amd §§4903, 4910 & 4914, Pub Health L; amd §§367-a
        //& 364-j, Soc Serv L; amd §§6810 & 6826-a, Ed L
        final BaseBillId baseBillId = new BaseBillId("A04521", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("ISC4806", "ISC3224-D", "PBH4406-H", "ISC4303-A")));
        exp.put(LawActionType.AMEND, new HashSet<>(Arrays.asList("ISC3221", "ISC3231", "SOS367-A", "ISC4303", "ISC4325",
                "ISC4910", "ISC3216", "EDN6810", "SOS364-J", "ISC4903", "ISC4914", "EDN6826-A", "PBH4910", "PBH4903", "PBH4914")));
        compareMaps(amd, exp);
    }

    @Test
    public void generallyTest() {
        // Amd Lien L, generally; amd §§199-a & 663, Lab L; amd §§6201, 6210, 6211, 6223 & R6212, CPLR; amd §§624 & 630, BC L; amd §§609 & 1102, Lim Lil L
        final BaseBillId baseBillId = new BaseBillId("A00486", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.AMEND, new HashSet<>(Arrays.asList("BSC630", "LLC609", "LAB199-A", "LLC1102", "LAB663",
                "CVPR6212", "BSC624", "CVP6201", "CVP6223", "CVP6210", "CVP6211", "LIE-GENERALLY")));
        compareMaps(amd, exp);
    }

    @Test
    public void variousGenerallyTest() {
        // Amd Pub Health L, generally
        final BaseBillId baseBillId = new BaseBillId("S01527", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.AMEND, new HashSet<>(Arrays.asList("VARIOUS-GENERALLY")));
        compareMaps(amd, exp);
    }

    @Test
    public void jsonTest() {
        // Amd Pub Health L, generally
        final BaseBillId baseBillId = new BaseBillId("A04521", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        BillLawCodeParser parser = new BillLawCodeParser();
        parser.parse(amd.getLaw());
        amd.setRelatedLawsJson(parser.getJson());
    }

    @Test
    public void variedTest() {
        // Add §208-b, Pub Health L; add §99-ff, St Fin L
        BaseBillId baseBillId = new BaseBillId("A00736", 2019);
        BillAmendment amd = getFirstAmendment(baseBillId);

        Map<LawActionType, HashSet<String>> exp = new HashMap<>();
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("PBH208-B", "STF99-FF")));
        compareMaps(amd, exp);

        // Add Art 14 Part 4 §685, Ed L; add §337-a, Soc Serv L
        baseBillId = new BaseBillId("A00700", 2019);
        amd = getFirstAmendment(baseBillId);

        exp.clear();
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("SOS337-A", "EDNA14P4")));
        compareMaps(amd, exp);

        // Amd Art 2 §2, Constn
        baseBillId = new BaseBillId("S00871", 2019);
        amd = getFirstAmendment(baseBillId);

        exp.clear();
        exp.put(LawActionType.AMEND, new HashSet<>(Arrays.asList("CNSA2")));
        compareMaps(amd, exp);

        // Amd Pub Health L, generally; amd ��6540, 6542, 6731, 6741, 6807, 6909, 6957 &
        // 7901, add �6545-a, Ed L; amd �461-c, Soc Serv L;
        // amd
        // ��13-b & 13-c, Work Comp L; amd �33.04, Ment Hyg L; amd �406, Gen Bus L
        baseBillId = new BaseBillId("S00907", 2019);
        amd = getFirstAmendment(baseBillId);

        exp.clear();
        exp.put(LawActionType.AMEND, new HashSet<>(Arrays.asList("EDN6807", "WKC13-B", "EDN6909", "WKC13-C", "EDN6957",
                "SOS461-C", "EDN7901", "EDN6542", "EDN6741", "PBH-GENERALLY", "EDN6731", "GBS406", "MHY33.04", "EDN6540")));
        exp.put(LawActionType.ADD, new HashSet<>(Arrays.asList("EDN6545-A")));
        compareMaps(amd, exp);

        // Rpld �590 sub 11, Lab L
        baseBillId = new BaseBillId("A00616", 2019);
        amd = getFirstAmendment(baseBillId);

        exp.clear();
        exp.put(LawActionType.REPEAL, new HashSet<>(Arrays.asList("LAB590")));
        compareMaps(amd, exp);
    }

}



