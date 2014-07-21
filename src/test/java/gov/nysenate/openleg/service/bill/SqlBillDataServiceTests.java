package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.util.OutputHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlBillDataServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillDataServiceTests.class);

    @Autowired
    BillDataService sqlBillDataService;

    @Test
    public void testGetBill() throws Exception {
//        logger.info("{}", OutputHelper.toJson()));
        logger.info(OutputHelper.toJson(sqlBillDataService.getBill(new BillId("A837", 2013))));
//        logger.info("{}", OutputHelper.toJson(sqlBillDataService.getBill(new BillId("A2180", 2013))));
//
//        sqlBillDataService.saveBill(bill, null);
//        sqlBillDataService.getBill(new BillId("A2180", 2013));
    }
}
