package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.hearing.PublicHearingCommittee;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PublicHearingCommitteeParserTest extends BaseTests
{
    private PublicHearingCommitteeParser committeeParser;

    @Before
    public void setup() {
        committeeParser = new PublicHearingCommitteeParser();
    }

    @Test
    public void basicCommitteeParse() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "06-02-14 NYsenate_Labor_Savino_FINAL.txt");

        List<PublicHearingCommittee> expected = new ArrayList<>();
        PublicHearingCommittee expectedCommittee = new PublicHearingCommittee();
        expectedCommittee.setName("LABOR");
        expectedCommittee.setChamber(Chamber.SENATE);
        expected.add(expectedCommittee);

        List<PublicHearingCommittee> actual = committeeParser.parse(pages.get(0));
        assertEquals(expected, actual);
    }

    @Test
    public void basicTaskForceParse() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt");

        List<PublicHearingCommittee> expected = new ArrayList<>();
        PublicHearingCommittee expectedCommittee = new PublicHearingCommittee();
        expectedCommittee.setName("HEROIN AND OPIOID ADDICTION");
        expectedCommittee.setChamber(Chamber.SENATE);
        expected.add(expectedCommittee);

        List<PublicHearingCommittee> actual = committeeParser.parse(pages.get(0));
        assertEquals(expected, actual);
    }

    @Test
    public void multipleCommitteesParse() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "09-17-13 Carlucci_Mental Health_Ogdensburg_FINAL.txt");

        PublicHearingCommittee expectedCommittee1 = new PublicHearingCommittee();
        expectedCommittee1.setName("MENTAL HEALTH AND ENVIRONMENTAL DISABILITIES");
        expectedCommittee1.setChamber(Chamber.ASSEMBLY);

        PublicHearingCommittee expectedCommittee2 = new PublicHearingCommittee();
        expectedCommittee2.setName("MENTAL HEALTH AND ENVIRONMENTAL DISABILITIES");
        expectedCommittee2.setChamber(Chamber.SENATE);

        PublicHearingCommittee expectedCommittee3 = new PublicHearingCommittee();
        expectedCommittee3.setName("HEALTH");
        expectedCommittee3.setChamber(Chamber.SENATE);

        List<PublicHearingCommittee> expected = new ArrayList<>();
        expected.add(expectedCommittee1);
        expected.add(expectedCommittee2);
        expected.add(expectedCommittee3);

        List<PublicHearingCommittee> actual = committeeParser.parse(pages.get(0));
        assertEquals(expected, actual);
    }

    @Test
    public void noWhiteSpaceInCommitteeName() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "02-09-12 ChildCareHearing_Final.txt");

        PublicHearingCommittee expectedCommittee1 = new PublicHearingCommittee();
        expectedCommittee1.setName("SOCIAL SERVICES");
        expectedCommittee1.setChamber(Chamber.SENATE);

        PublicHearingCommittee expectedCommittee2 = new PublicHearingCommittee();
        expectedCommittee2.setName("CHILDREN AND FAMILIES");
        expectedCommittee2.setChamber(Chamber.SENATE);

        List<PublicHearingCommittee> expected = new ArrayList<>();
        expected.add(expectedCommittee1);
        expected.add(expectedCommittee2);

        List<PublicHearingCommittee> actual = committeeParser.parse(pages.get(0));
        assertEquals(expected, actual);
    }

    // TODO: This test fails, file: 03-01-12 NYS Joint 3-Committee Hearing_Final.txt
    // This files committees are not correct!
    @Ignore
    @Test
    public void anotherMultipleCommitteesParse() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "03-01-12 NYS Joint 3-Committee Hearing_Final.txt");

        PublicHearingCommittee expectedCommittee1 = new PublicHearingCommittee();
        expectedCommittee1.setName("INVESTIGATIONS AND GOVERNMENT OPERATIONS");
        expectedCommittee1.setChamber(Chamber.SENATE);

        PublicHearingCommittee expectedCommittee2 = new PublicHearingCommittee();
        expectedCommittee2.setName("CIVIL SERVICE AND PENSIONS");
        expectedCommittee2.setChamber(Chamber.SENATE);

        PublicHearingCommittee expectedCommittee3 = new PublicHearingCommittee();
        expectedCommittee3.setName("RACING, GAMING, AND WAGERING");
        expectedCommittee3.setChamber(Chamber.SENATE);

        List<PublicHearingCommittee> expected = new ArrayList<>();
        expected.add(expectedCommittee1);
        expected.add(expectedCommittee2);
        expected.add(expectedCommittee3);

        List<PublicHearingCommittee> actual = committeeParser.parse(pages.get(0));
        assertEquals(expected, actual);
    }

    @Test
    public void emptyCommitteeParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "03-12-14 Roundtable on the Compassionate Care Act_Savino_FINAL.txt");

        List<PublicHearingCommittee> expected = new ArrayList<>();
        PublicHearingCommittee expectedCommittee = new PublicHearingCommittee();
        expectedCommittee.setName("");
        expectedCommittee.setChamber(Chamber.SENATE);
        expected.add(expectedCommittee);

        List<PublicHearingCommittee> actual = committeeParser.parse(pages.get(0));
        assertEquals(expected, actual);
    }

    @Test
    public void missingCommitteeParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "01-03-13 HurricaneSandy_NYS TaskForce Roundtable_Final.txt");

        List<PublicHearingCommittee> actual = committeeParser.parse(pages.get(0));
        assertEquals(null, actual);
    }

    @Test
    public void majorityCoalitionParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "09-11-13 NYS Majority Coalition Forum_Buffalo_ FINAL.txt");

        List<PublicHearingCommittee> expected = new ArrayList<>();
        PublicHearingCommittee expectedCommittee = new PublicHearingCommittee();
        expectedCommittee.setName("MAJORITY COALITION");
        expectedCommittee.setChamber(Chamber.SENATE);
        expected.add(expectedCommittee);

        List<PublicHearingCommittee> actual = committeeParser.parse(pages.get(0));
        assertEquals(expected, actual);
    }
}