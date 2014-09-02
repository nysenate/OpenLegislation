package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlBillDataServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillDataServiceTests.class);

    @Autowired
    BillDataService billData;

    @Test
    public void testGetBill() throws Exception {
        Bill S7273 = billData.getBill(new BaseBillId("S7273", 2013));
        logger.info("{}", S7273.getAmendPublishStatusMap());

    }
}
