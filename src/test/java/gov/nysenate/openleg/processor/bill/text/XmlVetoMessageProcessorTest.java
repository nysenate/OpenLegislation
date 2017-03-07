package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.sponsor.XmlVetoMessageProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by uros on 3/7/17.
 */
@Transactional
public class XmlVetoMessageProcessorTest extends BaseXmlProcessorTest {

    @Autowired private BillDao billDao;
    @Autowired private XmlVetoMessageProcessor vetoMessageProcessor;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return vetoMessageProcessor;
    }

    @Test
    public void processSimpleVetoMessage()  {
        String xmlPath = "processor/bill/vetomessage/2016-11-29-12.19.46.006100_VETOMSG_2016-00231.XML";
        processXmlFile(xmlPath);


    }
}
