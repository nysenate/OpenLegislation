package gov.nysenate.openleg.service;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.entity.TestCommittees;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.service.entity.CommitteeService;
import gov.nysenate.openleg.util.DateUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

public class CommitteeServiceTests extends BaseTests{
    private static Logger logger = LoggerFactory.getLogger(CommitteeServiceTests.class);

    @Autowired
    CommitteeService committeeService;
    @Autowired
    TestCommittees testCommittees;

    @Test
    public void deleteCommittees(){
        logger.info("Deleting all committees");
        for(Committee committee : testCommittees.getCommittees()){
            committeeService.deleteCommittee(committee.getId());
        }
    }

    @Test
    public void insertCommittee(){
        logger.info("Inserting new committee");
        committeeService.updateCommittee(testCommittees.getCommittee("test1"));
    }

    @Test
    public void insertAllCommittees(){
        for(Committee committee : testCommittees.getCommittees()){
            committeeService.updateCommittee(committee);
        }
    }

    private static int cacheTimerRunCount = 0;
    @Test
    public void cacheTimer(){
        logger.info(" === Cache Timer " + ++cacheTimerRunCount + " === ");
        MethodTimer.Method getCurrentCommittee = new MethodTimer.Method() {
            @Override
            public void run() throws Exception{
                committeeService.getCommittee(new CommitteeId(Chamber.SENATE, "test committee 1"));
            }
        };
        MethodTimer.Method getCommitteeAtTime = new MethodTimer.Method() {
            @Override
            public void run() throws Exception{
                committeeService.getCommittee(new CommitteeVersionId(Chamber.SENATE, "test committee 1", SessionYear.of(2009), LocalDateTime.now()));
            }
        };
        MethodTimer.Method getCommitteeList = new MethodTimer.Method() {
            @Override
            public void run() throws Exception {
                committeeService.getCommitteeList(Chamber.SENATE, LimitOffset.ALL);
            }
        };
        MethodTimer.Method getCommitteeHistory = new MethodTimer.Method() {
            @Override
            public void run() throws Exception {
                committeeService.getCommitteeHistory(new CommitteeId(Chamber.SENATE, "test committee 1"),
                                                        DateUtils.ALL_DATE_TIMES, LimitOffset.ALL, SortOrder.NONE);
            }
        };

        logger.info("getCurrentCommittee run 1:\t" + NumberFormat.getNumberInstance(Locale.US).format(MethodTimer.timeMethod(getCurrentCommittee)));
        logger.info("getCurrentCommittee run 2:\t" + NumberFormat.getNumberInstance(Locale.US).format(MethodTimer.timeMethod(getCurrentCommittee)));
        logger.info("getCommitteeAtTime run 1:\t" + NumberFormat.getNumberInstance(Locale.US).format(MethodTimer.timeMethod(getCommitteeAtTime)));
        logger.info("getCommitteeAtTime run 2:\t" + NumberFormat.getNumberInstance(Locale.US).format(MethodTimer.timeMethod(getCommitteeAtTime)));
        logger.info("getCommitteeList run 1:\t" + NumberFormat.getNumberInstance(Locale.US).format(MethodTimer.timeMethod(getCommitteeList)));
        logger.info("getCommitteeList run 2:\t" + NumberFormat.getNumberInstance(Locale.US).format(MethodTimer.timeMethod(getCommitteeList)));
        logger.info("getCommitteeHistory run 1:\t" + NumberFormat.getNumberInstance(Locale.US).format(MethodTimer.timeMethod(getCommitteeHistory)));
        logger.info("getCommitteeHistory run 2:\t" + NumberFormat.getNumberInstance(Locale.US).format(MethodTimer.timeMethod(getCommitteeHistory)));
    }

    @Test
    public void cacheEvictTimer(){
        deleteCommittees();
        insertAllCommittees();  // Reset test committees
        committeeService.deleteCommittee(testCommittees.getCommittee("test2").getId());
        cacheTimer();
        cacheTimer();
        committeeService.updateCommittee(testCommittees.getCommittee("test2"));
        cacheTimer();
        committeeService.deleteCommittee(testCommittees.getCommittee("test2").getId());
        cacheTimer();
    }

    private static class MethodTimer{
        public static interface Method{
            public void run() throws Exception;
        }
        public static long timeMethod(Method method){
            long startTime = System.nanoTime();
            try {
                method.run();
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
            long stopTime = System.nanoTime();
            return stopTime-startTime;
        }
    }
}
