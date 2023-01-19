package gov.nysenate.openleg.processors.committee;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.*;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import gov.nysenate.openleg.processors.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static gov.nysenate.openleg.legislation.committee.Chamber.SENATE;
import static org.junit.Assert.assertEquals;

/**
 * Created by Chenguang He on 5/8/2017.
 */
@Category(IntegrationTest.class)
public class XmlSenCommProcessorIT extends BaseXmlProcessorTest {

    @Autowired private CommitteeDataService committeeDataService;

    @Test
    public void processCommittee() throws ParseError {
        CommitteeVersionId committeeId = new CommitteeVersionId(
                SENATE,
                "AgricultureTEST",
                SessionYear.of(2017),
                LocalDateTime.parse("2017-01-31T04:51:43")
        );

        String path = "processor/committee/processtest/2017-01-31-04.51.42.526466_SENCOMM_BSCXB_SENATE.XML";
        processXmlFile(path);

        Committee actual = committeeDataService.getCommittee(committeeId);
        Committee expected = new Committee("AgricultureTEST", SENATE);
        expected.setPublishedDateTime(LocalDateTime.parse("2017-01-31T04:51:42"));
        expected.setMeetDay(DayOfWeek.TUESDAY);
        expected.setMeetTime(LocalTime.of(9, 0));
        expected.setLocation("Room 412 LOB");
        expected.setSession(SessionYear.of(2017));

        Member member = new Member();
        member.setIncumbent(true);
        member.setPersonId(1237);
        member.setNameFields("STEVE RITCHIE", "RITCHIE");
        member.setMemberId(1415);

        SessionMember sessionMember = new SessionMember();
        sessionMember.setMember(member);
        sessionMember.setAlternate(false);
        sessionMember.setDistrictCode(0);
        sessionMember.setLbdcShortName("RITCHIE");
        sessionMember.setSessionMemberId(1413);
        sessionMember.setSessionYear(SessionYear.of(2017));

        CommitteeMember committeeMember = new CommitteeMember();
        committeeMember.setSessionMember(sessionMember);
        committeeMember.setMajority(true);
        committeeMember.setTitle(CommitteeMemberTitle.CHAIR_PERSON);
        expected.addMember(committeeMember);

        /*
         * Comparision
         */
        assertEquals(expected.getChamber(), actual.getChamber());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getMeetDay(), actual.getMeetDay());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getReformed(), actual.getReformed());
        assertEquals(expected.getSessionId().getChamber(), actual.getSessionId().getChamber());
        assertEquals(expected.getSessionId().getSession(), actual.getSessionId().getSession());
        assertEquals(expected.getLocation(), actual.getLocation());
        assertEquals(expected.getVersionId().getChamber(), actual.getVersionId().getChamber());
        assertEquals(expected.getVersionId().getName(), actual.getVersionId().getName());
        assertEquals(expected.getVersionId().getSession(), actual.getVersionId().getSession());

    }

    /**
     * This tests the scenario where a committee version is overwritten such that it has the same
     * membership as the previous version.  In this scenario, the overwritten version should be removed so that the
     * previous version covers its former duration.  The previous version should have the updated meeting info of the
     * overwritten version.
     */
    @Test
    public void fixCommitteeTest() {
        final CommitteeSessionId testCommSeshId = new CommitteeSessionId(SENATE, "Nonsense", SessionYear.of(2017));
        final CommitteeVersionId testCommVerId =
                new CommitteeVersionId(testCommSeshId, LocalDate.parse("2017-01-01").atStartOfDay());
        final String file1 = "processor/committee/fixtest/2017-01-01-00.00.00.000000_SENCOMM_BSCXB_SENATE.XML";
        final String file2 = "processor/committee/fixtest/2017-01-02-00.00.00.000000_SENCOMM_BSCXB_SENATE.XML";
        final String file3 = "processor/committee/fixtest/2017-01-03-00.00.00.000000_SENCOMM_BSCXB_SENATE.XML";
        final String file2Fixed = "processor/committee/fixtest/fixed/2017-01-02-00.00.00.000000_SENCOMM_BSCXB_SENATE.XML";

        processXmlFile(file1);
        processXmlFile(file2);
        processXmlFile(file3);

        List<Committee> committeeHistory = committeeDataService.getCommitteeHistory(testCommSeshId);
        assertEquals("Three unique versions created initially", 3, committeeHistory.size());

        processXmlFile(file2Fixed);

        committeeHistory = committeeDataService.getCommitteeHistory(testCommSeshId);
        assertEquals("2 unique versions after fix", 2, committeeHistory.size());

        Committee version1 = committeeDataService.getCommittee(testCommVerId);
        assertEquals("Room 101", version1.getLocation());
        assertEquals(DayOfWeek.WEDNESDAY, version1.getMeetDay());
        assertEquals(LocalTime.of(11, 35), version1.getMeetTime());
        assertEquals("It's happening", version1.getMeetAltWeekText());
        assertEquals(LocalDate.parse("2017-01-03").atStartOfDay(), version1.getReformed());
    }

}
