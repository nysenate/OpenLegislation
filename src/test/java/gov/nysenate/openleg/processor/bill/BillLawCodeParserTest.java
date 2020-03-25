package gov.nysenate.openleg.processor.bill;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.law.LawActionType;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.*;

import static org.junit.Assert.*;
import static gov.nysenate.openleg.model.law.LawActionType.*;


@Category(UnitTest.class)
public class BillLawCodeParserTest {
    private Map<LawActionType, TreeSet<String>> mapping = new EnumMap<>(LawActionType.class);

    // HELPERS
    private void compareToLawCode(String lawCode) {
        String actual = BillLawCodeParser.parse(lawCode, true);
        String expected = new Gson().toJson(mapping);
        assertEquals(expected, actual);
        mapping.clear();
    }

    private void put(LawActionType action, String... elements) {
        mapping.put(action, Sets.newTreeSet(Arrays.asList(elements)));
    }

    // TESTS
    @Test
    // Simple test to make sure the scope of amd extends to §§110-b, 64, 64-a and 64-c
    public void multiSectionTest() {
        // S344, 2017
        put(ADD, "ABC110-C");
        put(AMEND, "ABC64", "ABC110-B", "ABC64-C", "ABC64-A");
        compareToLawCode("Add §110-c, amd §§110-b, 64, 64-a & 64-c, ABC L");
    }


    @Test
    // Link to the higher level of law when a range of subsections are affected
    public void rangeTest() {
        // A2484, 2019
        put(LawActionType.ADD, "CORA24-A");
        put(LawActionType.AMEND, "PEN70.30");
        compareToLawCode("Add Art 24-A §§810 - 814, Cor L; amd §70.30, Pen L");

        // A6078, 2019
        put(LawActionType.ADD, "TAXA29-D");
        put(LawActionType.AMEND, "TAX1825");
        compareToLawCode("Add Art 29-D §§1299-M - 1299-V, amd §1825, Tax L");
    }

    @Test
    // Link to the more specific subsection (ELN8-600) when only that subsection is affected
    public void articleSingle() {
        // S7212, 2017
        put(AMEND, "ELN8-600", "ELN8-602", "ELN8-604", "ELN8-606");
        compareToLawCode("Amd Art 8 Title VI §§8-600, 8-602, 8-604 & 8-606, El L");
    }

    @Test
    public void subsectionTest() {
        // S2301, 2019
        put(ADD, "ELN8-503");
        put(AMEND, "ELN4-117", "ELN8-504", "ELN17-108", "ELN8-502",
            "ELN8-510", "ELN8-508");
        compareToLawCode("Amd §§4-117, 8-502, 8-504, 8-508, 8-510 & 17-108, add §8-503, El L");

        // A7121, 2019
        put(REPEAL, "EXC353", "EDN4402");
        compareToLawCode("Rpld §353 sub 15, Exec L; rpld §4402 sub 1 ¶b sub¶ 3 clause (h), Ed L");
    }

    @Test
    // Test articles with mixed numeral-letter names
    public void mixedTest() {
        // S1974, 2019
        put(ADD, "PMLA7-A");
        compareToLawCode("Add Art 7-A §§710 - 712, RWB L");
    }

    @Test
    // Test to make sure both Article 27 and Title 28 are put in the lawDocId
    public void multiLevelTest() {
        // A4398, 2019
        put(ADD, "ENVA27T30");
        compareToLawCode("Add Art 27 Title 30 §§27-3001 - 27-3005, En Con L");
    }

    @Test
    public void diverseTest() {
        // A04521, 2019
        put(ADD, "ISC4806", "ISC3224-D", "PBH4406-H", "ISC4303-A");
        put(AMEND, "ISC3221", "ISC3231", "SOS367-A", "ISC4303", "ISC4325", "ISC4910", "ISC3216",
                "EDN6810", "SOS364-J", "ISC4903", "ISC4914", "EDN6826-A", "PBH4910", "PBH4903", "PBH4914");
        compareToLawCode("Add §§4806, 3224-d & 4303-a, amd §§4903, 4910, 4914, 3216, 3221, 4303, 4325" +
                " & 3231, Ins L; add §4406-h, amd §§4903, 4910 & 4914, Pub Health L; amd §§367-a & " +
                "364-j, Soc Serv L; amd §§6810 & 6826-a, Ed L");
    }

    @Test
    // Tests to make sure law volumes that are amended generally are handled properly
    public void generallyTests() {
        // A486, 2019
        put(AMEND, "BSC630", "LLC609", "LAB199-A", "LLC1102", "LAB663", "CVP6212", "BSC624",
                "CVP6201", "CVP6223", "CVP6210", "CVP6211", "LIE (generally)");
        compareToLawCode("Amd Lien L, generally; amd §§199-a & 663, Lab L; amd §§6201, 6210, 6211, " +
                "6223 & R6212, CPLR; amd §§624 & 630, BC L; amd §§609 & 1102, Lim Lil L");

        // S1527, 2019
        put(AMEND, "PBH (generally)");
        compareToLawCode("Amd Pub Health L, generally");

    }

    @Test
    public void variedTest() {
        // A736, 2019
        put(LawActionType.ADD, "PBH208-B", "STF99-FF");
        compareToLawCode("Add §208-b, Pub Health L; add §99-ff, St Fi");

        // A700, 2019
        put(LawActionType.ADD, "SOS337-A", "EDN685");
        compareToLawCode("Add Art 14 Part 4 §685, Ed L; add §337-a, Soc Serv L");

        // S907, 2019
        put(AMEND, "EDN6807", "WKC13-B", "EDN6909", "WKC13-C", "EDN6957",
                "SOS461-C", "EDN7901", "EDN6542", "EDN6741", "PBH (generally)", "EDN6731",
                "GBS406", "MHY33.04", "EDN6540");
        put(ADD, "EDN6545-A");
        compareToLawCode("Amd Pub Health L, generally; amd §§6540, 6542, 6731, 6741, 6807, 6909, 6957" +
                " & 7901, add §6545-a, Ed L; amd §461-c, Soc Serv L; amd §§13-b & 13-c, Work " +
                "Comp L; amd §33.04, Ment Hyg L; amd §406, Gen Bus L");

        // A616, 2019
        put(REPEAL, "LAB590");
        compareToLawCode("Rpld §590 sub 11, Lab L");

        // S4163, 2009
        put(AMEND, "RCO12");
        compareToLawCode("Amd §12, Rel Corp L");

        // S6156, 2009
        put(AMEND, "PBO1-104", "PBO14-100", "PBO14-114", "PBO30");
        put(ADD, "PBO2-128", "PBO6-125", "PBO18-100");
        compareToLawCode("Amd §§1-104, 14-100 & 14-114, add §§2-128 & 6-125 & Art 18 §18-100; amd §30, Pub Off L");
    }

    @Test
    // Make sure that the second reference of "Art" in "Art 39-F Art Head" is ignored
    public void contextTest() {
        // A8884, 2017
        put(AMEND, "GBS899-AA", "STT208", "GBSA39-F");
        put(ADD, "GBS899-BB");
        compareToLawCode("Amd Art 39-F Art Head, §899-aa, add §899-bb, Gen Bus L; amd §208, St Te");

        // S7254, 2017
        put(AMEND, "AGM71-Z", "AGM71-Y", "AGMA4-D");
        put(ADD, "STF97-YYYY", "AGMA4-D");
        compareToLawCode("Amd Art 4-D Head, §§71-y & 71-z, add §§71-aa - 71-dd, Ag & Mkts L; add §97-yyyy, St Fin");

        // A1168, 2009
        put(ADD, "LABA20-D");
        compareToLawCode("Add Art 20-D §§760 -765, Lab L");
    }


    @Test
    // Tests to make sure extraneous qualifiers are ignored
    public void subTest() {
        // A270, 2019
        put(AMEND, "ENV27-2709", "ENV27-2707", "ENVA27T27", "ENV27-2703", "ENV27-2701");
        put(ADD, "PAR13.32", "STF92-II", "ENV27-2706", "TAX1105-D");
        put(REPEAL, "ENV27-2713", "ENV27-2711", "ENV27-2701");
        compareToLawCode("Add §13.32, Pks & Rec L; add §1105-D, Tax L; add §92-ii, St Fin L; amd Art " +
                "27 Title 27 Head, §§27-2701, 27-2703, 27-2707 & 27-2709, rpld §§27-2701 sub 4, " +
                "27-2711 & 27-2713, add §27-2706, En Con L");

        // S3501, 2011
        put(AMEND, "EDN2588");
        compareToLawCode("Amd §2588, rpld subs 3, 4 & 7, Ed L");

        // S3812, 2013
        put(AMEND, "TAX (generally)");
        put(REPEAL, "TAX1210", "TAX1210-E", "TAX1224");
        compareToLawCode("Rpld §1210 op¶ sub¶¶ (i), (ii) & (iii), §1210-E, §1224 subs (d) - (r), (t) - (gg), amd Tax L, generally");
    }

    @Test
    // Tests that the new context created by "Art 10" is ignored
    public void renameTests() {
        // A270, 2015
        put(ADD, "LAB465");
        put(RENAME, "LAB465");
        compareToLawCode("Ren §465 to be §466, add §465, Lab L");

        // S405, 2017
        put(ADD, "CVRA9");
        put(RENAME, "CVR90", "CVR91");
        compareToLawCode("Ren Art 9 §§90 & 91 to be Art 10 §§100 & 101, add Art 9 §§90 - 98, Civ Rts");
    }


    @Test
    public void renameMultiTest() {
        // A4739, 2019
        put(ADD, "ENV37-0209");
        put(AMEND, "ENV37-0203", "ENV37-0211");
        put(RENAME, "ENV37-0213", "ENV37-0209", "ENV37-0211");
        compareToLawCode("Amd §§37-0203 & 37-0211, ren §§37-0209, 37-0211 & 37-0213 to be §§37-0211, " +
                "37-0213 & 37-0215, add §37-0209, En Con L");
    }

    @Test
    // Make sure parenthesis are matched reluctantly, not greedily
    public void parenthesisTest() {
        // S3361, 2019
        put(AMEND, "LLC1006", "LLC209", "LLC802", "LLC1203", "LLC1306", "LLC1101", "PTR121-201",
                "PTR121-902", "PTR121-1500", "PTR121-1502", "PTR121-1300");
        put(ADD, "STF89-J");
        put(REPEAL, "ACA23.03", "LLC1101", "LLC1203", "LLC802",
                "LLC1306", "LLC206", "LLC102", "PTR121-1500", "PTR121-201", "PTR121-902", "PTR121-1300", "PTR121-1502", "PTR121-101");
        compareToLawCode("Amd §§1006, 209, 802, 1203, 1306 & 1101, rpld §206, §1101 sub (s), §802 sub" +
                " (b), §1203 sub (c) ¶2, §1306 sub (d), §102 subs (a-1) & (e-1), Lim Lil L; rpld" +
                " §121-201 sub (c), §121-902 sub (d), §121-1500 sub (a) ¶(II), §121-1502 sub (f)" +
                " ¶(II), §121-1300 sub (f), §121-101 subs (a-1) & (a-2), amd §§121-201, 121-902," +
                " 121-1500, 121-1502 & 121-1300, Partn L; rpld §23.03 sub 4, Arts & Cul L; add " +
                "§89-j, St Fin L");
    }

    @Test
    // Test basic Roman Numeral parsing
    public void romanNumeralTest() {
        // S6742, 2009
        put(ADD, "PBHA2T6");
        compareToLawCode("Add Art 2 Title VI §§266 - 266-c, Pub Health L");

        // A3982, 2015
        put(ADD, "PBGA9");
        compareToLawCode("Add Art IX §§170 - 176, Pub Hous L");

        // A8217, 2009
        put(AMEND, "CVP1311-A", "TAX1825", "CVP1313", "EXC21",
                "CPL (generally)", "CVP1310", "PEN (generally)");
        put(ADD, "PBHA10T4", "PBHA10T5", "EXC29-H", "PBHA10T2", "EXC29-H", "EXC29-I",
                "PBHA10T3", "PBHA10T1", "TAX30");
        compareToLawCode("Add §§29-h & 29-i, amd §21, Exec L; add Art 10 Title I §§1000 - 1003, Title" +
                " II §§1010 - 1016, Title III §§1020 - 1027, Title IV §§1030 - 1031, Title V " +
                "§§1040 - 1047, Pub Health L; amd Pen L, generally; amd CP L, generally; amd " +
                "§§1310, 1311-a & 1313, CPLR; add §30, amd §1825, Tax L");
    }

    @Test
    // Test that the part name, but not the title name is parsed
    public void nonNumericalTest() {
        // A5010, 2009
        put(REPEAL, "CPL440.50");
        put(ADD, "CPLP3TVA740");
        put(AMEND, "EXC259-I", "EXC646-A");
        compareToLawCode("Add Part III Title V Art 740 §§740.10 - 740.25, rpld §440.50, CP L; " +
                "amd §§259-i & 646-a, Exec L");
    }

    @Test
    // Tests parsing of law codes with "various" that should be ignored.
    public void variousUnlinkableTest() {
        // A8867, 2009
        put(AMEND, "BSC1004", "BSC1007", "NPC1004", "NPC1007");
        compareToLawCode("Rpld various provisions, amd NYC Ad Cd, generally; amd §§1004 & 1007, " +
                "BC L; amd §§1004 & 1007, N-PC L");
    }

    @Test
    // Tests law code with "various" that refers to a linkable chapter
    public void variousLinkableTest() {
        // A7583, 2011
        put(REPEAL, "GMU (generally)", "PBA (generally)");
        compareToLawCode("Rpld various Titles, Gen Muni L; rpld various Titles, Pub Auth L");
    }

    @Test
    public void variousSectionTest() {
        // S5598, 2009
        put(AMEND, "GMU959");
        put(REPEAL, "TAX (generally)");
        compareToLawCode("Amd §959, Gen Muni L; rpld various §§ of the Tax L");
    }

    @Test
    public void variousLawsTest() {
        //S4610, 2015
        compareToLawCode("Amd Various Laws, generally");
    }

    @Test
    public void complexTest() {
        // S5758, 2011
        put(REPEAL, "PEN165.74", "PEN420.00", "SOS341", "LAB27-A", "MHR27", "GMU207-M", "CNT702",
                "SOS423", "EDN1950", "TAX1210", "TAX1210-D", "TAX1210-E", "TAX1224", "TAX1262-O", "ERL1");
        compareToLawCode("Amd Various Laws, generally; rpld §§165.74 & 420.00, Pen L; rpld §341 " +
                "sub 5, Soc Serv L; rpld §27-a sub 1 ¶e, Lab L; rpld §27 sub 5, Munic Home Rule " +
                "L; rpld §207-m, Gen Muni L; rpld §702 sub 6, County L; rpld §423 sub 5, Soc " +
                "Serv L; rpld §1950 sub 17, Ed L; rpld §1210 op¶, sub (a) ¶3 sub¶ (iii), sub (b)" +
                " ¶3 sub¶ (iii), §§1210-D & 1210-E, §1224 subs (d) - (r), (t) - (z), (z-1), (aa)" +
                " - (gg), §1262-o, Tax L; rpld §1 sub 2, Emerg Hous Rent Cont L; rpld §46 sub 6," +
                " Chap 116 of 1997");
    }

    @Test
    public void UJCAtest() {
        // A5338, 2013, partial
        put(REPEAL, "UJCA22", "UJC106", "UJC1306", "UJC2012");
        put(AMEND, "UJC (generally)");
        compareToLawCode("Rpld Art 22, §106 subs 6, 8 & 9, §§1306 & 2012, amd UJCA, generally");
    }

    @Test
    public void missingCommaTests() {
        // A8219, 2009
        put(ADD, "EDN6507-A");
        compareToLawCode("Add §6507-a Ed L");

        // A7329, 2009
        put(AMEND, "PBO87");
        compareToLawCode("amd §87 Pub Off L");

        // Can't find in database
        put(AMEND, "ISC (generally)");
        compareToLawCode("Amd Isc L");
    }

    @Test
    public void incorrectSemicolonPlacement() {
        // A5265, 2009
        put(ADD, "ELN5-212-A");
        put(AMEND, "ELN5-212-A");
        compareToLawCode("Add §5-212-a; amd §5-212-a, El L;");

        // S2333, 2011
        put(ADD, "RSSA23");
        compareToLawCode("add Art 23 §§1300 - 1304; R & SS L");

        // A6351, 2013
        put(AMEND, "ISC (generally)", "PBH4406-D", "PBH4406-C", "PBH4901", "PBH4902", "PBH4905");
        compareToLawCode("Amd Ins L; generally; amd §§4406-d, 4406-c, 4901, 4902 & 4905, Pub Health L");

        // S685, 2013
        compareToLawCode("Amd §§666, 200, 645 & 648, rpld §668, add §201-a; NYC Chart;");
    }

    @Test
    public void missingSpaceTest() {
        // S4483, 2011
        put(ADD, "UDA45");
        compareToLawCode("Add §45,UDC Act");
    }

    @Test
    public void emptyMapping() {
        // S922, 2015
        compareToLawCode("Amd Chap 899 of 1984");

        // S250, 2009
        compareToLawCode("Amd Chaps 50, 53, 54 & 55 of 2008");

        // If there are no valid laws, the mapping should be empty.
        assertEquals("{}", BillLawCodeParser.parse("N/A", false));
    }

    @Test
    public void badActionTest() {
        // A6668, 2009
        put(REPEAL, "SOS384-B");
        compareToLawCode("Amc §384-b, rpld §384-b sub 6, Soc Serv L");

        // A9406, 2011
        compareToLawCode("amd§454, Bank L");
    }

    @Test
    public void testNoOutOfBoundsException() {
        // Can't find in database
        put(ADD, "PEN145.75") ;
        compareToLawCode("Add §145.75, Pen L; amd §510.10");
    }
}
