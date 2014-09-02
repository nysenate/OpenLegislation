package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.util.UnpublishListManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class UnpublishListManagerTest {

    private static Logger logger = Logger.getLogger(UnpublishListManagerTest.class);

    private static final String listDirectory = "/tmp";

    private UnpublishListManager unpublishListManager;

    @Test
    public void initManager(){
        unpublishListManager = new UnpublishListManager(listDirectory);
    }

    @Test
    public void addPrintNos(){
        unpublishListManager = new UnpublishListManager(listDirectory);
        unpublishListManager.addUnpublishedBill("A1234-2013");
        unpublishListManager.addUnpublishedBill("A5678-2013");
    }

    @Test
    public void addOtherPrintNos(){
        unpublishListManager = new UnpublishListManager(listDirectory);
        unpublishListManager.addUnpublishedBill("S1111-2013");
        unpublishListManager.addUnpublishedBill("S2222-2013");
    }

    @Test
    public void getUnpublishedBills(){
        unpublishListManager = new UnpublishListManager(listDirectory);
        Set<String> unpublishedBills = unpublishListManager.getUnpublishedBills();
        logger.info("Unpublished Bills:");
        for(String bill : unpublishedBills){
            logger.info('\t' + bill);
        }
    }
}
