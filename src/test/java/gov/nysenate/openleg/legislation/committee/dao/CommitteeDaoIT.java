package gov.nysenate.openleg.legislation.committee.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import testing_utils.TestData;
import testing_utils.TimeUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class CommitteeDaoIT extends BaseTests {

    @Autowired
    protected CommitteeDao committeeDao;

    private static final Map<String, Committee> createdCommittees = new HashMap<>();
    private static final int NUM_INITIAL_COMMITTEES = 35;
    private static final int NUM_INITIAL_COMMITTEE_VERSIONS = 68;

    private static CommitteeMember committeeMemberFromId(int sessionMemberId) {
        CommitteeMember cm = new CommitteeMember();
        cm.setSessionMember(TestData.SESSION_MEMBER_DATA.get(sessionMemberId));
        cm.setMajority(false);
        cm.setTitle(CommitteeMemberTitle.MEMBER);
        return cm;
    }

    private static void assertEqualsBesidesReformed(Committee one, Committee two) {
        if (Objects.equals(one.getReformed(), two.getReformed()))
            fail();
        one.setReformed(two.getReformed());
        assertEquals(one, two);
    }

    @BeforeClass
    public static void init() {
        Committee test1 = new Committee("test committee 1", Chamber.SENATE);
        test1.setMeetTime(LocalTime.parse("09:00"));
        test1.setLocation("my house");
        test1.setMeetDay(DayOfWeek.FRIDAY);
        test1.setMeetAltWeek(false);
        test1.setMeetAltWeekText("don't do it");
        test1.setSession(SessionYear.of(2009));
        test1.setPublishedDateTime(LocalDate.parse("2009-01-01").atStartOfDay());
        for(int n = 1; n < 3; n++) {
            CommitteeMember cm = committeeMemberFromId(n);
            cm.setSequenceNo(n);
            test1.addMember(cm);
        }
        createdCommittees.put("test1", test1);

        Committee test1nomod = new Committee(test1);
        test1nomod.setPublishedDateTime(LocalDate.parse("2009-02-01").atStartOfDay());
        createdCommittees.put("test1nomod", test1nomod);

        Committee test1v2 = new Committee(test1);
        CommitteeMember seward = committeeMemberFromId(3);
        seward.setSequenceNo(3);
        test1v2.addMember(seward);
        test1v2.setPublishedDateTime(LocalDate.parse("2009-03-01").atStartOfDay());
        createdCommittees.put("test1v2", test1v2);

        Committee test1MeetChange = new Committee(test1);
        test1MeetChange.setLocation("broom closet");
        test1MeetChange.setPublishedDateTime(LocalDate.parse("2009-04-01").atStartOfDay());
        createdCommittees.put("test1MeetChange", test1MeetChange);

        Committee test1replace = new Committee(test1);
        CommitteeMember breslin = committeeMemberFromId(4);
        breslin.setSequenceNo(4);
        test1replace.addMember(breslin);
        createdCommittees.put("test1replace", test1replace);

        Committee test1v2merge = new Committee(test1v2);
        test1v2merge.setPublishedDateTime(LocalDate.parse("2009-02-10").atStartOfDay());
        createdCommittees.put("test1v2merge", test1v2merge);

        Committee test1v2mergeReplace = new Committee(test1);
        test1v2mergeReplace.setPublishedDateTime(test1v2merge.getPublishedDateTime());
        createdCommittees.put("test1v2mergeReplace", test1v2mergeReplace);

        Committee test1v3 = new Committee(test1);
        test1v3.setPublishedDateTime(LocalDate.parse("2009-05-01").atStartOfDay());
        createdCommittees.put("test1v3", test1v3);

        Committee test2 = new Committee(test1);
        test2.setName("test committee 2");
        createdCommittees.put("test2", test2);
    }

    @Test
    public void getCommitteeByVersionIdTest() {
        Committee rules = committeeDao.getCommittee(new CommitteeVersionId(new CommitteeId(Chamber.SENATE, "Rules"),
                new SessionYear(2011), LocalDateTime.now()));
        assertEquals(LocalDate.ofYearDay(2011, 1).atStartOfDay(), rules.getCreated());
    }

    @Test
    public void getCommitteeByIdTest() {
        committeeDao.getCommittee(new CommitteeId(Chamber.SENATE, "Finance"));
        try {
            committeeDao.getCommittee(new CommitteeId(Chamber.ASSEMBLY, "blah"));
        }
        catch (EmptyResultDataAccessException e) {
            return;
        }
        fail();
    }

    @Test
    public void getCommitteeListTest() {
        List<CommitteeId> ids = committeeDao.getCommitteeList();
        assertTrue(ids.size() >= NUM_INITIAL_COMMITTEES);
    }

    @Test
    public void getAllSessionIdsTest() {
        List<CommitteeSessionId> sessionIds = committeeDao.getAllSessionIds().stream().filter(
                s -> s.getSession().year() <= 2013).toList();
        assertEquals(NUM_INITIAL_COMMITTEE_VERSIONS, sessionIds.size());
        for (CommitteeSessionId id : sessionIds) {
            for (Committee c : committeeDao.getCommitteeHistory(id))
                assertTrue(c.getReformed() == null || c.getReformed().getYear() > 2013);
        }
    }

    /**
     * Tests that a committee with a new, more recent date is properly stored and retrieved.
     */
    @Test
    public void updateCommitteeTest() {
        Committee oldEthics = committeeDao.getCommittee(new CommitteeId(Chamber.SENATE, "Ethics"));
        oldEthics.setYear(LocalDate.now().getYear());
        oldEthics.setSession(SessionYear.current());
        LocalDateTime now = TimeUtils.roundToMicroseconds(LocalDateTime.now());
        oldEthics.setPublishedDateTime(now);
        committeeDao.updateCommittee(oldEthics, null);
        Committee newEthics = committeeDao.getCommittee(oldEthics.getId());
        assertEquals(now, newEthics.getPublishedDateTime());
        for (CommitteeMember cm : newEthics.getMembers())
            assertEquals(SessionYear.current(), cm.getSessionMember().getSessionYear());
        assertTrue(oldEthics.meetingEquals(newEthics));
    }

    @Test
    public void getCommitteeByTimeTest() {
        Committee test1 = createdCommittees.get("test1");
        committeeDao.updateCommittee(test1, null);
        Committee committee1 = committeeDao.getCommittee(test1.getVersionId());
        assertEquals(committee1, test1);

        Committee test1v2 = createdCommittees.get("test1v2");
        committeeDao.updateCommittee(test1v2, null);
        Committee committee2 = committeeDao.getCommittee(test1v2.getVersionId());
        assertEquals(committee2, test1v2);
        // Committee should've been updated with a new publish date.
        assertNotEquals(committee1, committee2);
    }

    @Test
    public void getCommitteeHistoryTest() {
        // Committees from 2011 are initial data wih no history.
        List<CommitteeSessionId> ids = committeeDao.getAllSessionIds().stream()
                .filter(id -> id.getSession().year() == 2011).toList();
        for (CommitteeSessionId id : ids)
            assertEquals(1, committeeDao.getCommitteeHistory(id).size());
    }

    @Test
    public void insertSameCommitteeTest() {
        Committee test1 = createdCommittees.get("test1");
        Committee test1nomod = createdCommittees.get("test1nomod");
        committeeDao.updateCommittee(test1, null);
        committeeDao.updateCommittee(test1nomod, null);
        Committee committee = committeeDao.getCommittee(test1.getId());
        assertEquals(committee, test1);
        assertEquals(committee, test1nomod);
        assertNotEquals(committee.getPublishedDateTime(), test1nomod.getPublishedDateTime());
    }

    @Test
    public void insertReplacementCommitteeTest() {
        Committee test1 = createdCommittees.get("test1");
        Committee test1replace = createdCommittees.get("test1replace");
        committeeDao.updateCommittee(test1, null);
        committeeDao.updateCommittee(test1replace, null);
        Committee committee = committeeDao.getCommittee(test1.getVersionId());
        assertNotEquals(committee, test1);
        assertEquals(committee, test1replace);
    }

    @Test
    public void testEquals() {
        // initial test
        Committee test1 = createdCommittees.get("test1");
        // same as 1v2 but earlier pub date
        Committee test1v2merge = createdCommittees.get("test1v2merge");
        // reverts 1v2 merge to initial membership
        Committee test1v2mergeReplace = createdCommittees.get("test1v2mergeReplace");
        // adds new member to test1
        Committee test1v2 = createdCommittees.get("test1v2");
        // same as test1 but pub date is after 1v2
        Committee test1v3 = createdCommittees.get("test1v3");
        assertNotEquals(test1, test1v2merge);
        assertNotEquals(test1, test1v2);
        assertEquals(test1, test1v3);
        assertNotEquals(test1.getPublishedDateTime(), test1v3.getPublishedDateTime());

        assertEquals(test1v2merge, test1v2);
        assertNotEquals(test1v2merge.getPublishedDateTime(), test1v2.getPublishedDateTime());
        assertNotEquals(test1v2merge, test1v3);

        assertNotEquals(test1v2, test1v3);
        assertTrue(test1.membersEquals(test1v3));
        assertFalse(test1.membersEquals(test1v2));
        assertTrue(test1v2merge.membersEquals(test1v2));
        assertTrue(test1.membersEquals(test1v2mergeReplace));
    }

    @Test
    public void insertMergeCommitteeTest() {
        Committee test1 = createdCommittees.get("test1");
        Committee test1v2merge = createdCommittees.get("test1v2merge");
        Committee test1v2mergeReplace = createdCommittees.get("test1v2mergeReplace");
        Committee test1v2 = createdCommittees.get("test1v2");
        Committee test1v3 = createdCommittees.get("test1v3");

        committeeDao.updateCommittee(test1, null);
        committeeDao.updateCommittee(test1v2, null);
        committeeDao.updateCommittee(test1v3, null);

        CommitteeVersionId test1id = test1.getVersionId();
        List<CommitteeVersionId> ids = new ArrayList<>();
        String[] dates = {"2009-01-10", "2009-02-20", "2009-03-10", "2009-05-10"};
        for (String date : dates)
            ids.add(new CommitteeVersionId(test1id.getChamber(), test1id.getName(), test1id.getSession(), LocalDate.parse(date).atStartOfDay()));

        assertEqualsBesidesReformed(committeeDao.getCommittee(ids.get(0)), test1);
        assertEqualsBesidesReformed(committeeDao.getCommittee(ids.get(1)), test1);
        assertEqualsBesidesReformed(committeeDao.getCommittee(ids.get(2)), test1v2);
        assertEquals(committeeDao.getCommittee(ids.get(3)), test1v3);

        committeeDao.updateCommittee(test1v2merge, null);
        assertEqualsBesidesReformed(committeeDao.getCommittee(ids.get(0)), test1);
        assertEqualsBesidesReformed(committeeDao.getCommittee(ids.get(1)), test1v2merge);
        assertEqualsBesidesReformed(committeeDao.getCommittee(ids.get(2)), test1v2merge);
        assertEquals(committeeDao.getCommittee(ids.get(3)), test1v3);

        committeeDao.updateCommittee(test1v2mergeReplace, null);
        assertEqualsBesidesReformed(committeeDao.getCommittee(ids.get(0)), test1);
        assertEqualsBesidesReformed(committeeDao.getCommittee(ids.get(1)), test1);
        // The member list should be different.
        assertNotEquals(committeeDao.getCommittee(ids.get(2)), test1);
        assertEquals(committeeDao.getCommittee(ids.get(3)), test1);
    }

    @Test
    public void updateCommitteeMeetingTest() {
        Committee test1 = createdCommittees.get("test1");
        Committee test1MeetChange = createdCommittees.get("test1MeetChange");
        committeeDao.updateCommittee(test1, null);
        Committee committee = committeeDao.getCommittee(test1.getId());
        assertEquals(committee, test1);
        committeeDao.updateCommittee(test1MeetChange, null);
        committee = committeeDao.getCommittee(test1.getId());
        assertNotEquals(committee, test1);
        assertFalse(committee.meetingEquals(test1));
        assertTrue(committee.membersEquals(test1));
        assertTrue(committee.meetingEquals(test1MeetChange));
        assertTrue(committee.membersEquals(test1MeetChange));
    }
}
