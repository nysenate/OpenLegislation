package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.entity.CommitteeDao;
import gov.nysenate.openleg.entity.TestCommittees;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.service.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.MemberService;
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

    @Autowired
    protected CommitteeDao committeeDao;

    @Autowired
    protected TestCommittees testCommittees;

    private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Test
    public void deleteCommittees(){
        for(Committee committee : testCommittees.getCommittees()){
            committeeDao.deleteCommittee(committee);
        }
        logger.info("All test committees deleted.");
    }

    @Test
    public void insertCommitteeTest(){
        committeeDao.updateCommittee(testCommittees.getCommittee("test1"));
    }

    @Test
    public void getCommitteeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(testCommittees.getCommittee("test1"));
        Committee committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE);
        assert(committee.equals(testCommittees.getCommittee("test1")));
    }

    @Test
    public void getCommitteeByTimeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(testCommittees.getCommittee("test1"));
        committeeDao.updateCommittee(testCommittees.getCommittee("test1v2"));
        Committee committee1 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-01-10").toDate());
        Committee committee2 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-03-10").toDate());
        assert(!committee1.equals(committee2));
        assert(committee1.equals(testCommittees.getCommittee("test1")));
        assert(committee2.equals(testCommittees.getCommittee("test1v2")));
    }

    @Test
    public void insertSameCommitteeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(testCommittees.getCommittee("test1"));
        committeeDao.updateCommittee(testCommittees.getCommittee("test1nomod"));
        Committee committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE);
        assert(committee.equals(testCommittees.getCommittee("test1")));
        assert(!committee.equals(testCommittees.getCommittee("test1nomod")));
    }

    @Test
    public void insertReplacementCommitteeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(testCommittees.getCommittee("test1"));
        committeeDao.updateCommittee(testCommittees.getCommittee("test1replace"));
        Committee committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-01-01").toDate());
        assert(!committee.equals(testCommittees.getCommittee("test1")));
        assert(committee.equals(testCommittees.getCommittee("test1replace")));
    }

    @Test
    public void insertMergeCommitteeTest(){
        deleteCommittees();
        committeeDao.updateCommittee(testCommittees.getCommittee("test1"));
        committeeDao.updateCommittee(testCommittees.getCommittee("test1v2"));
        Committee committee1 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-01-10").toDate());
        Committee committee2 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-02-20").toDate());
        Committee committee3 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-03-10").toDate());
        assert(!committee1.equals(committee3));
        assert(committee1.equals(testCommittees.getCommittee("test1")));
        assert(committee3.equals(testCommittees.getCommittee("test1v2")));
        assert(committee2.equals(committee1));
        committeeDao.updateCommittee(testCommittees.getCommittee("test1v2merge"));
        committee1 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-01-10").toDate());
        committee2 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-02-20").toDate());
        committee3 = committeeDao.getCommittee("test committee 1", Chamber.SENATE, 2009, dateFormat.parseDateTime("2009-03-10").toDate());
        assert(!committee1.equals(committee3));
        assert(committee1.equals(testCommittees.getCommittee("test1")));
        assert(committee3.equals(testCommittees.getCommittee("test1v2merge")));
        assert(committee2.equals(committee3));
    }

    @Test
    public void updateCommitteeMeetingTest(){
        deleteCommittees();
        committeeDao.updateCommittee(testCommittees.getCommittee("test1"));
        Committee committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE);
        assert(committee.equals(testCommittees.getCommittee("test1")));
        committeeDao.updateCommittee(testCommittees.getCommittee("test1MeetChange"));
        committee = committeeDao.getCommittee("test committee 1", Chamber.SENATE);
        assert(!committee.equals(testCommittees.getCommittee("test1")));
        assert(!committee.meetingEquals(testCommittees.getCommittee("test1")));
        assert(committee.memberEquals(testCommittees.getCommittee("test1")));
        assert(committee.meetingEquals(testCommittees.getCommittee("test1MeetChange")));
        assert(committee.memberEquals(testCommittees.getCommittee("test1MeetChange")));
    }

    @Test
    public void committeeHistoryTest(){
        List<Committee> allCommittees = committeeDao.getCommitteeList(Chamber.SENATE);
        List<List<Committee>> allCommitteeHistories = new ArrayList<List<Committee>>();
        for(Committee committee : allCommittees){
            allCommitteeHistories.add(committeeDao.getCommitteeHistory(committee.getName(), committee.getChamber()));
        }
        for(List<Committee> committeeHistory : allCommitteeHistories){
            for(int i=0; i<committeeHistory.size(); i++){
                if(i+1<committeeHistory.size()){
                    Committee leftCommittee = committeeHistory.get(i);
                    Committee rightCommittee = committeeHistory.get(i+1);
                    assert(leftCommittee.getYear()<=rightCommittee.getYear());
                    assert(leftCommittee.getPublishDate().before(rightCommittee.getPublishDate()));
                    if(leftCommittee.getYear()==rightCommittee.getYear()){
                        assert(leftCommittee.getReformed().equals(rightCommittee.getPublishDate()));
                    }
                }
            }
        }
    }
}
