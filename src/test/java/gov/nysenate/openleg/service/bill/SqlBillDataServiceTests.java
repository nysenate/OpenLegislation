package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.time.StopWatch;
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
        StopWatch sw = new StopWatch();
        billData.getBill(new BaseBillId("S1236", 2013));
        sw.start();
        billData.getBill(new BaseBillId("S1234", 2013));
        sw.stop();
        logger.info("{}", sw.getTime());
        sw.reset();
        sw.start();
        billData.getBill(new BaseBillId("S1234", 2013));;
        sw.stop();
        logger.info("{}", sw.getTime());


    }
}
