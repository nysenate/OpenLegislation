package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.entity.CommitteeDao;
import gov.nysenate.openleg.model.entity.*;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.util.*;


public class CommitteeDaoTests extends BaseTests{
    private static Logger logger = LoggerFactory.getLogger(CommitteeDaoTests.class);

    private static final Object[][] testMembers = {
        {"SAMPSON", 369, 2009},
        {"MORAHAN", 441, 2009},
        {"SEWARD", 371, 2009},
        {"BRESLIN", 372, 2009}
    };

    private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

    private Map<String, Committee> createdCommittees = new HashMap<String,Committee>();

    @Autowired
    private CommitteeDao committeeDao;

    private CommitteeMember committeeMemberFromTriple(Object[] triple){
        CommitteeMember cm = new CommitteeMember();
        Member m = new Member();
        m.setChamber(Chamber.SENATE);
        m.setMemberId((int)triple[1]);
        m.setSessionYear((int)triple[2]);
        m.setLbdcShortName((String)triple[0]);
        cm.setMember(m);
        cm.setMajority(m.getMemberId()%2==0);
        cm.setTitle(CommitteeMemberTitle.MEMBER);
        return cm;
    }

    @Before
    public void initialize(){
        Committee test1 = new Committee();
        test1.setName("test committee 1");
        test1.setChamber(Chamber.SENATE);
        test1.setMeetTime(new Time(LocalTime.parse("09:00").toDateTimeToday().toDate().getTime()));
        test1.setLocation("my house");
        test1.setMeetDay("every day");
        test1.setMeetAltWeek(false);
        test1.setMeetAltWeekText("dont do it");
        test1.setSession(2009);
        test1.setPublishDate(dateFormat.parseDateTime("2009-01-01").toDate());
        test1.setMembers(new ArrayList<CommitteeMember>());
        for(int n=0; n<2; n++){
            CommitteeMember cm = committeeMemberFromTriple(testMembers[n]);
            cm.setSequenceNo(n+1);
            test1.getMembers().add(cm);
        }
        createdCommittees.put("test1", test1);

        Committee test1nomod = new Committee(test1);
        test1nomod.setPublishDate(dateFormat.parseDateTime("2009-02-01").toDate());
        createdCommittees.put("test1nomod", test1nomod);

        Committee test1v2 = new Committee(test1);
        CommitteeMember seward = committeeMemberFromTriple(testMembers[2]);
        seward.setSequenceNo(3);
        test1v2.getMembers().add(seward);
        test1v2.setPublishDate(dateFormat.parseDateTime("2009-03-01").toDate());
        createdCommittees.put("test1v2", test1v2);

        Committee test1MeetChange = new Committee(test1);
        test1MeetChange.setLocation("broom closet");
        test1MeetChange.setPublishDate(dateFormat.parseDateTime("2009-04-01").toDate());
        createdCommittees.put("test1MeetChange", test1MeetChange);

        Committee test1replace = new Committee(test1);
        CommitteeMember breslin = committeeMemberFromTriple(testMembers[3]);
        test1replace.getMembers().add(breslin);
        breslin.setSequenceNo(4);
        createdCommittees.put("test1replace", test1replace);

        Committee test1v2merge = new Committee(test1v2);
        test1v2merge.setPublishDate(dateFormat.parseDateTime("2009-02-10").toDate());
        createdCommittees.put("test1v2merge", test1v2merge);

    }

    @Test
    public void deleteCommittees(){
        for(Map.Entry<String, Committee> committeeEntry : createdCommittees.entrySet()){
            committeeDao.deleteCommittee(committeeEntry.getValue());
        }
        logger.info("All test committees deleted.");
    }

    @Test
    public void insertCommitteeTest(){
        committeeDao.updateCommittee(createdCommittees.get("test1"));
    }

    @Test
    public void getCommitteeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(createdCommittees.get("test1"));
        Committee committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE);
        assert(committee.equals(createdCommittees.get("test1")));
    }

    @Test
    public void getCommitteeByTimeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(createdCommittees.get("test1"));
        committeeDao.updateCommittee(createdCommittees.get("test1v2"));
        Committee committee1 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-01-10").toDate());
        Committee committee2 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-03-10").toDate());
        assert(!committee1.equals(committee2));
        assert(committee1.equals(createdCommittees.get("test1")));
        assert(committee2.equals(createdCommittees.get("test1v2")));
    }

    @Test
    public void insertSameCommitteeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(createdCommittees.get("test1"));
        committeeDao.updateCommittee(createdCommittees.get("test1nomod"));
        Committee committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE);
        assert(committee.equals(createdCommittees.get("test1")));
        assert(!committee.equals(createdCommittees.get("test1nomod")));
    }

    @Test
    public void insertReplacementCommitteeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(createdCommittees.get("test1"));
        committeeDao.updateCommittee(createdCommittees.get("test1replace"));
        Committee committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-01-01").toDate());
        assert(!committee.equals(createdCommittees.get("test1")));
        assert(committee.equals(createdCommittees.get("test1replace")));
    }

    @Test
    public void insertMergeCommitteeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(createdCommittees.get("test1"));
        committeeDao.updateCommittee(createdCommittees.get("test1v2"));
        Committee committee1 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-01-10").toDate());
        Committee committee2 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-02-20").toDate());
        Committee committee3 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-03-10").toDate());
        assert(!committee1.equals(committee3));
        assert(committee1.equals(createdCommittees.get("test1")));
        assert(committee3.equals(createdCommittees.get("test1v2")));
        assert(committee2.equals(committee1));
        committeeDao.updateCommittee(createdCommittees.get("test1v2merge"));
        committee1 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-01-10").toDate());
        committee2 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-02-20").toDate());
        committee3 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, dateFormat.parseDateTime("2009-03-10").toDate());
        assert(!committee1.equals(committee3));
        assert(committee1.equals(createdCommittees.get("test1")));
        assert(committee3.equals(createdCommittees.get("test1v2merge")));
        assert(committee2.equals(committee3));
    }

    @Test
    public void updateCommitteeMeetingTest(){
        deleteCommittees();
        committeeDao.updateCommittee(createdCommittees.get("test1"));
        Committee committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE);
        assert(committee.equals(createdCommittees.get("test1")));
        committeeDao.updateCommittee(createdCommittees.get("test1MeetChange"));
        committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE);
        assert(!committee.equals(createdCommittees.get("test1")));
        assert(!committee.meetingEquals(createdCommittees.get("test1")));
        assert(committee.memberEquals(createdCommittees.get("test1")));
        assert(committee.meetingEquals(createdCommittees.get("test1MeetChange")));
        assert(committee.memberEquals(createdCommittees.get("test1MeetChange")));
    }

    @Test
    public void committeeHistoryTest(){
        List<Committee> allCommittees = committeeDao.getCommitteeList(Chamber.SENATE);
        List<List<Committee>> allCommitteeHistories = new ArrayList<List<Committee>>();
        for(Committee committee : allCommittees){
            allCommitteeHistories.add(committeeDao.getCommitteeHistory(committee.getName(), committee.getChamber()));
        }
        int n=0;
        n=n+n;
    }
}
