package gov.nysenate.openleg.processor.entry;

import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.dao.entity.committee.data.CommitteeDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.base.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by Chenguang He on 5/8/2017.
 */
@Category(IntegrationTest.class)
public class XmlSenCommProcessorIT extends BaseXmlProcessorTest {

    @Autowired private CommitteeDao committeeDao;

    @Test
    public void processCommittee() throws ParseError {
        CommitteeVersionId committeeId = new CommitteeVersionId(
                Chamber.SENATE,
                "Agriculture",
                SessionYear.of(2017),
                LocalDateTime.parse("2017-01-31T04:51:43")
        );

        String path = "processor/bill/senCommittee/2017-01-31-04.51.42.526466_SENCOMM_BSCXB_SENATE.XML";
        processXmlFile(path);

        Committee actual = committeeDao.getCommittee(committeeId);
        Committee expected = new Committee("Agriculture", Chamber.SENATE);
        expected.setPublishedDateTime(LocalDateTime.parse("2017-01-31T04:51:42"));
        expected.setMeetDay(DayOfWeek.TUESDAY);
        expected.setMeetTime(LocalTime.of(9, 0));
        expected.setLocation("Room 412 LOB");
        expected.setSession(SessionYear.of(2017));
        CommitteeMember committeeMember = new CommitteeMember();
        SessionMember sessionMember = new SessionMember();
        sessionMember.setAlternate(false);
        sessionMember.setIncumbent(true);
        sessionMember.setDistrictCode(0);
        sessionMember.setLbdcShortName("RITCHIE");
        sessionMember.setSessionMemberId(1413);
        sessionMember.setSessionYear(SessionYear.of(2017));
        sessionMember.setPersonId(1237);
        sessionMember.setFullName("RITCHIE");
        sessionMember.setMemberId(1415);
        sessionMember.setVerified(false);
        committeeMember.setMember(sessionMember);
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

}
