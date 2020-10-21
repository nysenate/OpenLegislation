package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * Created by Robert Bebber on 3/16/17.
 */
@Category(IntegrationTest.class)
public class XmlLDBlurbProcessorIT extends BaseXmlProcessorTest {

    @Autowired BillDataService billDataService;

    private static final Logger logger = LoggerFactory.getLogger(XmlAnActProcessorIT.class);

    @Test
    public void processReplaceTest() throws Exception {
        final String ldBlurbXmlFilePath = "processor/bill/ldblurb/2017-02-09-12.12.09.635487_LDBLURB_K00094.XML";

        processXmlFile(ldBlurbXmlFilePath);

        Bill b = billDataService.getBill(new BaseBillId("K00094", 2017));
        String expectedS = "Johnson, Charles Rodney-Death of";
        String actualClause = b.getLDBlurb();
        assertEquals(expectedS, actualClause);
    }

    @Test
    public void processRemoveTest() throws Exception {
        final String ldBlurbXmlFilePath = "processor/bill/ldblurb/2017-01-03-12.26.14.250488_LDBLURB_S03968.XML";

        processXmlFile(ldBlurbXmlFilePath);

        Bill b = billDataService.getBill(new BaseBillId("S03968", 2017));
        String expectedS = "";
        String actualClause = b.getLDBlurb();
        assertEquals(expectedS, actualClause);
    }
}
