package gov.nysenate.openleg.dao.entity.committee;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.entity.committee.data.CommitteeDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CommitteeDaoTests extends BaseTests{
    private static Logger logger = LoggerFactory.getLogger(CommitteeDaoTests.class);

    @Autowired
    protected CommitteeDao committeeDao;

//    @Autowired
////    protected TestCommittees testCommittees;
//
//    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//    @Test
//    public void deleteCommittees(){
//        for(Committee committee : testCommittees.getCommittees()){
//            committeeDao.deleteCommittee(committee.getId());
//        }
//        logger.info("All test committees deleted.");
//    }
//
//    @Test
//    public void insertCommitteeTest(){
//        committeeDao.updateCommittee(testCommittees.getCommittee("test1"), null);
//    }
//
//    @Test
//    public void getCommitteeTest(){
//        deleteCommittees();
//        Committee test1 = testCommittees.getCommittee("test1");
//        committeeDao.updateCommittee(test1, null);
//        Committee committee = committeeDao.getCommittee(test1.getId());
//        assert(committee.equals(testCommittees.getCommittee("test1")));
//    }
//
//    @Test
//    public void getCommitteeByTimeTest(){
//        deleteCommittees();
//        Committee test1 = testCommittees.getCommittee("test1");
//        Committee test1v2 = testCommittees.getCommittee("test1v2");
//        committeeDao.updateCommittee(test1, null);
//        committeeDao.updateCommittee(test1v2, null);
//        Committee committee1 = committeeDao.getCommittee(test1.getVersionId());
//        Committee committee2 = committeeDao.getCommittee(test1v2.getVersionId());
//        assert(!committee1.equals(committee2));
//        assert(committee1.equals(test1));
//        assert(committee2.equals(test1v2));
//    }
//
//    @Test
//    public void insertSameCommitteeTest(){
//        deleteCommittees();
//        Committee test1 = testCommittees.getCommittee("test1");
//        Committee test1nomod = testCommittees.getCommittee("test1nomod");
//        committeeDao.updateCommittee(test1, null);
//        committeeDao.updateCommittee(test1nomod, null);
//        Committee committee = committeeDao.getCommittee(test1.getId());
//        assert(committee.equals(test1));
//        assert(!committee.equals(test1nomod));
//    }
//
//    @Test
//    public void insertReplacementCommitteeTest(){
//        deleteCommittees();
//        Committee test1 = testCommittees.getCommittee("test1");
//        Committee test1replace = testCommittees.getCommittee("test1replace");
//        committeeDao.updateCommittee(test1, null);
//        committeeDao.updateCommittee(test1replace, null);
//        Committee committee = committeeDao.getCommittee(test1.getVersionId());
//        assert(!committee.equals(test1));
//        assert(committee.equals(test1replace));
//    }
//
//    @Test
//    public void insertMergeCommitteeTest(){
//        deleteCommittees();
//        Committee test1 = testCommittees.getCommittee("test1");     // initial test
//        Committee test1v2merge = testCommittees.getCommittee("test1v2merge");   // same as 1v2 but earlier pub date
//        Committee test1v2mergeReplace = testCommittees.getCommittee("test1v2mergeReplace"); // reverts 1v2 merge to initial membership
//        Committee test1v2 = testCommittees.getCommittee("test1v2"); // adds new member to test1
//        Committee test1v3 = testCommittees.getCommittee("test1v3"); // same as test1 but pub date is after 1v2
//        assert(!test1.equals(test1v2merge));
//        assert(!test1.equals(test1v2));
//        assert(!test1.equals(test1v3));
//        assert(!test1v2merge.equals(test1v2));
//        assert(!test1v2merge.equals(test1v3));
//        assert(!test1v2.equals(test1v3));
//        assert(test1.membersEquals(test1v3));
//        assert(!test1.membersEquals(test1v2));
//        assert(test1v2merge.membersEquals(test1v2));
//        assert(test1.membersEquals(test1v2mergeReplace));
//
////        CommitteeVersionId test1id = test1.getVersionId();
////        CommitteeVersionId int1 = new CommitteeVersionId(test1id.getChamber(), test1id.getName(), test1id.getSession(),
////                dateFormat.parseDateTime("2009-01-10").toDate());
////        CommitteeVersionId int2 = new CommitteeVersionId(test1id.getChamber(), test1id.getName(), test1id.getSession(),
////                dateFormat.parseDateTime("2009-02-20").toDate());
////        CommitteeVersionId int3 = new CommitteeVersionId(test1id.getChamber(), test1id.getName(), test1id.getSession(),
////                dateFormat.parseDateTime("2009-03-10").toDate());
////        CommitteeVersionId int4 = new CommitteeVersionId(test1id.getChamber(), test1id.getName(), test1id.getSession(),
////                dateFormat.parseDateTime("2009-05-10").toDate());
//
////        committeeDao.updateCommittee(test1);
////        committeeDao.updateCommittee(test1v2);
////        committeeDao.updateCommittee(test1v3);
////        Committee committee1 = committeeDao.getCommittee(int1);
////        Committee committee2 = committeeDao.getCommittee(int2);
////        Committee committee3 = committeeDao.getCommittee(int3);
////        Committee committee4 = committeeDao.getCommittee(int4);
////        assert(committee1.equals(test1));
////        assert(committee2.equals(test1));
////        assert(committee3.equals(test1v2));
////        assert(committee4.equals(test1v3));
//
////        committeeDao.updateCommittee(test1v2merge);
////        committee1 = committeeDao.getCommittee(int1);
////        committee2 = committeeDao.getCommittee(int2);
////        committee3 = committeeDao.getCommittee(int3);
////        committee4 = committeeDao.getCommittee(int4);
////        assert(committee1.equals(test1));
////        assert(committee2.equals(test1v2merge));
////        assert(committee3.equals(test1v2merge));
////        assert(committee4.equals(test1v3));
//
////        committeeDao.updateCommittee(test1v2mergeReplace);
////        committee1 = committeeDao.getCommittee(int1);
////        committee2 = committeeDao.getCommittee(int2);
////        committee3 = committeeDao.getCommittee(int3);
////        committee4 = committeeDao.getCommittee(int4);
////        assert(committee1.equals(test1));
////        assert(committee2.equals(test1));
////        assert(committee3.equals(test1));
////        assert(committee4.equals(test1));
//    }
//
//    @Test
//    public void updateCommitteeMeetingTest(){
//        deleteCommittees();
//        Committee test1 = testCommittees.getCommittee("test1");
//        Committee test1MeetChange = testCommittees.getCommittee("test1MeetChange");
//        committeeDao.updateCommittee(test1, null);
//        Committee committee = committeeDao.getCommittee(test1.getId());
//        assert(committee.equals(test1));
//        committeeDao.updateCommittee(test1MeetChange, null);
//        committee = committeeDao.getCommittee(test1.getId());
//        assert(!committee.equals(test1));
//        assert(!committee.meetingEquals(test1));
//        assert(committee.membersEquals(test1));
//        assert(committee.meetingEquals(test1MeetChange));
//        assert(committee.membersEquals(test1MeetChange));
//    }
//
//    @Test
//    public void anotherCommitteeTest() {
//        committeeDao.getCommittee(new CommitteeVersionId(Chamber.SENATE, "Ethics", SessionYear.of(2011), LocalDateTime.now()));
//    }
}
