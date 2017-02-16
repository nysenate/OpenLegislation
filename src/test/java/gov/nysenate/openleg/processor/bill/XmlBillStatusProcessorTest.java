package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.junit.Test;

/**
 * Created by uros on 2/14/17.
 */
public class XmlBillStatusProcessorTest extends BaseTests {

    @Test
    public void test()  {
        process("billstatfile.xml");
    }

    private SobiFragment process(String filename)   {

        return null;
    }

}
