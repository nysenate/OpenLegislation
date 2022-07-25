package gov.nysenate.openleg.spotchecks.daybreak.process;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakBill;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakBillId;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class DaybreakFragmentSponsorParserTest {
    private final DaybreakBill senateBill = new DaybreakBill(new DaybreakBillId(new BaseBillId("S100", 2017), LocalDate.now()));
    private final DaybreakBill assemblyBill = new DaybreakBill(new DaybreakBillId(new BaseBillId("A100", 2017), LocalDate.now()));

    @Test
    public void parseSenateSponsor() {
        String sponsorsLine = "MURPHY";
        DaybreakFragmentSponsorParser.parseSponsors(senateBill, sponsorsLine);
        assertEquals(sponsorsLine, senateBill.getSponsor());
    }

    @Test
    public void parseSenateSponsorAndCoSponsors() {
        String sponsorsLine = "MURPHY, AKSHAR, STEWART-COUSINS, O'DONNELL";
        String expectedSponsor = "MURPHY";
        List<String> expectedCoSponsors = Arrays.asList("AKSHAR", "STEWART-COUSINS", "O'DONNELL");

        DaybreakFragmentSponsorParser.parseSponsors(senateBill, sponsorsLine);

        assertEquals(expectedSponsor, senateBill.getSponsor());
        assertEquals(expectedCoSponsors, senateBill.getCosponsors());
    }

    @Test
    public void parseAssemblySponsor() {
        String sponsorsLine = "CAHILL";
        DaybreakFragmentSponsorParser.parseSponsors(assemblyBill, sponsorsLine);
        assertEquals(sponsorsLine, assemblyBill.getSponsor());
    }

    @Test
    public void parseAssemblySponsorAndCoSponsors() {
        String sponsorsLine = "CAHILL, PAULIN, O'DONNELL";
        String expectedSponsor = "CAHILL";
        List<String> expectedCoSponsors = Arrays.asList("PAULIN", "O'DONNELL");

        DaybreakFragmentSponsorParser.parseSponsors(assemblyBill, sponsorsLine);

        assertEquals(expectedSponsor, assemblyBill.getSponsor());
        assertEquals(expectedCoSponsors, assemblyBill.getCosponsors());
    }

    @Test
    public void parseAssemblySponsorCoSponsorsAndMultiSponsors() {
        String sponsorsLine = "CAHILL, PAULIN, O'DONNELL; M-S: Aubry, Peoples-Stokes";
        String expectedSponsor = "CAHILL";
        List<String> expectedCoSponsors = Arrays.asList("PAULIN", "O'DONNELL");
        List<String> expectedMultiSponsors = Arrays.asList("Aubry", "Peoples-Stokes");

        DaybreakFragmentSponsorParser.parseSponsors(assemblyBill, sponsorsLine);

        assertEquals(expectedSponsor, assemblyBill.getSponsor());
        assertEquals(expectedCoSponsors, assemblyBill.getCosponsors());
        assertEquals(expectedMultiSponsors, assemblyBill.getMultiSponsors());
    }

    @Test
    public void parsesMembersWithFirstInitial() {
        String sponsorsLine = "P. LOPEZ";
        String expectedSponsor = "LOPEZ P";
        DaybreakFragmentSponsorParser.parseSponsors(assemblyBill, sponsorsLine);
        DaybreakFragmentSponsorParser.parseSponsors(senateBill, sponsorsLine);
        assertEquals(expectedSponsor, assemblyBill.getSponsor());
        assertEquals(expectedSponsor, senateBill.getSponsor());
    }

    @Test
    public void parsesMillers(){
        String sponsorsLine = "M. G. MILLER, M. L. MILLER; M-S: M. G. Miller";
        String expectedSponsor = "MILLER MG";
        List<String> expectedCoSponsor = List.of("MILLER ML");
        List<String> expectedMultiSponsor = List.of("Miller MG");
        DaybreakFragmentSponsorParser.parseSponsors(assemblyBill, sponsorsLine);
        assertEquals(expectedSponsor, assemblyBill.getSponsor());
        assertEquals(expectedCoSponsor, assemblyBill.getCosponsors());
        assertEquals(expectedMultiSponsor, assemblyBill.getMultiSponsors());
    }

    @Test
    public void parsesRules() {
        String sponsorsLine = "RULES COM";
        String expectedSponsor = "RULES";
        DaybreakFragmentSponsorParser.parseSponsors(senateBill, sponsorsLine);
        assertEquals(expectedSponsor, senateBill.getSponsor());
    }

    @Test
    public void parsesRulesWithSponsors() {
        String sponsorsLine = "RULES COM (Request of Heastie, Morelle)";
        String expectedSponsor = "RULES (Heastie)";
        List<String> expectedCoSponsors = List.of("Morelle");
        DaybreakFragmentSponsorParser.parseSponsors(assemblyBill, sponsorsLine);
        assertEquals(expectedSponsor, assemblyBill.getSponsor());
        assertEquals(expectedCoSponsors, assemblyBill.getCosponsors());
    }
}
