package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.Version;
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
 * This class is responsible for testing all the type cases for the Anact Sobi Processor.
 *
 * Created by Robert Bebber on 2/15/17.
 */
@Category(IntegrationTest.class)
public class XmlAnActProcessorIT extends BaseXmlProcessorTest {

    @Autowired BillDataService billDataService;

    private static final Logger logger = LoggerFactory.getLogger(XmlAnActProcessorIT.class);

    @Test
    public void processReplaceTest() throws Exception {
        final String anactXmlFilePath = "processor/bill/anact/2016-12-02-09.16.10.220257_ANACT_S08215.XML";

        processXmlFile(anactXmlFilePath);

        Bill b = billDataService.getBill(new BaseBillId("S08215", 2015));
        String expectedS = "AN ACT to amend the executive law, in relation to the appointment of\n" +
                "interpreters to be used in parole board proceedings [altered]";
        String actualClause = b.getAmendment(Version.ORIGINAL).getActClause();
        assertEquals(expectedS, actualClause);
    }

    @Test
    public void processRemoveTest() throws Exception {
        final String anactXmlFilePath = "processor/bill/anact/2017-02-09-12.36.50.736583_ANACT_A05462.XML";

        processXmlFile(anactXmlFilePath);

        Bill b = billDataService.getBill(new BaseBillId("A05462", 2017));
        String expectedS = "";
        String actualClause = b.getAmendment(Version.ORIGINAL).getActClause();
        assertEquals(expectedS, actualClause);
    }

}

