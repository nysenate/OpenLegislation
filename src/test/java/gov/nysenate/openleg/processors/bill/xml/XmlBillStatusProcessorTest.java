package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.junit.Test;

/**
 * Created by uros on 2/14/17.
 */
public class XmlBillStatusProcessorTest extends BaseTests {

    @Test
    public void test()  {
        process("billstatfile.xml");
    }

    private LegDataFragment process(String filename)   {

        return null;
    }

}
