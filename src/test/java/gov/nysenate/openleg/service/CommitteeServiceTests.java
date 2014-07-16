package gov.nysenate.openleg.service;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.entity.TestCommittees;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;
import gov.nysenate.openleg.service.entity.CommitteeService;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.util.Date;
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
            committeeService.deleteCommittee(committee);
        }
    }

    @Test
    public void insertCommittee(){
        logger.info("Inserting new committee");
        committeeService.updateCommittee(testCommittees.getCommittee("test1"));
    }

    @Test
    public void cacheTimer(){
        for(Committee committee : testCommittees.getCommittees()){
            committeeService.updateCommittee(committee);
        }
        MethodTimer.Method getCurrentCommittee = new MethodTimer.Method() {
            @Override
            public void run() throws Exception{
                committeeService.getCommittee("test committee 1", Chamber.SENATE);
            }
        };
        MethodTimer.Method getCommitteeAtTime = new MethodTimer.Method() {
            @Override
            public void run() throws Exception{
                committeeService.getCommittee("test committee 1", Chamber.SENATE, 2009, new Date());
            }
        };
        MethodTimer.Method getCommitteeList = new MethodTimer.Method() {
            @Override
            public void run() throws Exception {
                committeeService.getCommitteeList(Chamber.SENATE);
            }
        };
        MethodTimer.Method getCommitteeHistory = new MethodTimer.Method() {
            @Override
            public void run() throws Exception {
                committeeService.getCommitteeHistory("test committee 1", Chamber.SENATE);
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
                throw new RuntimeException();
            }
            long stopTime = System.nanoTime();
            return stopTime-startTime;
        }
    }
}
