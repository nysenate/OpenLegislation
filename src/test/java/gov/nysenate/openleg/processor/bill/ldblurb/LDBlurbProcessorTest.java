package gov.nysenate.openleg.processor.bill.ldblurb;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.anact.AnActSobiProcessorTest;
import gov.nysenate.openleg.processor.bill.ldblurb.LDBlurbProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

/**
 * Created by Robert Bebber on 3/16/17.
 */
@Transactional
public class LDBlurbProcessorTest extends BaseXmlProcessorTest {

    @Autowired
    BillDao billDao;
    @Autowired
    LDBlurbProcessor ldBlurbProcessor;

    private static final Logger logger = LoggerFactory.getLogger(AnActSobiProcessorTest.class);

    @Override
    protected SobiProcessor getSobiProcessor() {
        return ldBlurbProcessor;
    }

    @Test
    public void processReplaceTest() throws Exception {
        final String ldBlurbXmlFilePath = "processor/bill/ldblurb/2017-02-09-12.12.09.635487_LDBLURB_K00094.XML";

        processXmlFile(ldBlurbXmlFilePath);

        Bill b = billDao.getBill(new BillId("K00094", 2017));
        String expectedS = "Johnson, Charles Rodney-Death of";
        String actualClause = b.getLDBlurb();
        assertEquals(expectedS, actualClause);
    }

    @Test
    public void processRemoveTest() throws Exception {
        final String ldBlurbXmlFilePath = "processor/bill/ldblurb/2017-01-03-12.26.14.250488_LDBLURB_S03968.XML";

        processXmlFile(ldBlurbXmlFilePath);

        Bill b = billDao.getBill(new BillId("S03968", 2017));
        String expectedS = "";
        String actualClause = b.getLDBlurb();
        assertEquals(expectedS, actualClause);
    }
}
