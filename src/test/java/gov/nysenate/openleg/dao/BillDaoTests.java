package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BillDaoTests extends BaseTests{

    @Autowired
    private BillDao billDao;

    @Test
    public void billCommitteeTest(){
        BillId billi = new BillId("S3382",2013);
        Bill testBill = billDao.getBill(billi);


    }
}
