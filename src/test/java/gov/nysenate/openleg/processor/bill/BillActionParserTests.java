package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class BillActionParserTests extends BaseTests
{
    @Autowired BillDataService billDataService;

    private static final Logger logger = LoggerFactory.getLogger(BillActionParserTests.class);

    private static String actionsList1 =
        "01/28/09 referred to correction\n" +
        "03/17/09 reported referred to ways and means\n" +
        "04/28/09 reported\n" +
        "04/30/09 advanced to third reading cal.453\n" +
        "05/04/09 passed assembly\n" +
        "05/04/09 delivered to senate\n" +
        "05/04/09 REFERRED TO CODES\n" +
        "05/26/09 SUBSTITUTED FOR S4366\n" +
        "05/26/09 3RD READING CAL.391\n" +
        "06/02/09 recalled from senate\n" +
        "06/03/09 SUBSTITUTION RECONSIDERED\n" +
        "06/03/09 RECOMMITTED TO CODES\n" +
        "06/03/09 RETURNED TO ASSEMBLY\n" +
        "06/04/09 vote reconsidered - restored to third reading\n" +
        "06/04/09 amended on third reading 3664a\n" +
        "06/15/09 repassed assembly\n" +
        "06/16/09 returned to senate\n" +
        "06/16/09 COMMITTED TO RULES\n" +
        "07/17/09 SUBSTITUTED FOR S4366A\n" +
        "07/17/09 3RD READING CAL.391\n" +
        "07/16/09 RECOMMITTED TO RULES\n" +
        "01/06/10 DIED IN SENATE\n" +
        "01/06/10 RETURNED TO ASSEMBLY\n" +
        "01/06/10 ordered to third reading cal.276\n" +
        "01/19/10 committed to correction\n" +
        "01/26/10 amend and recommit to correction\n" +
        "01/26/10 print number 3664b";

    @Test
    public void testStatus() throws Exception {
        BillActionParser parser = new BillActionParser();
        List<BillAction> actions = parser.parseActionsList(new BillId("S3664B", 2013), actionsList1);
        logger.info("{}", actions);
    }
}
