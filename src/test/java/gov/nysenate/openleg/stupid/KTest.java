package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.service.bill.data.CachedBillDataService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kyle on 10/8/14.
 */
public class KTest {
    @Autowired
    private CachedBillDataService billDataService;

    @Test
    public void test() throws Exception {
        BaseBillId bid = new BaseBillId("S2", SessionYear.of(2015));
        Bill bill;
        bill = billDataService.getBill(bid);
        System.out.println("________________________________");
        //System.out.println("print no:::: "+ bill.getBasePrintNo());
        //System.out.println("Amend:::: "+ bill.getBaseBillId().getVersion());
        System.out.println("Session Year:::: "+ bill.getSession());
        System.out.println("summary:::: "+ bill.getSummary());

        //System.out.println("memo:::: "+ bill.getActiveVersion().getMemo());
        //System.out.println("text:::: "+ bill.getActiveVersion().getFullText());

    }

}