package gov.nysenate.openleg.processor.activelist;

import gov.nysenate.openleg.model.spotcheck.ActiveListSpotcheckReference;
import gov.nysenate.openleg.model.spotcheck.ActiveListHTMLParser;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by kyle on 10/29/14.
 */
public class ActiveListHTMLParserTest {
    private static final Logger logger = LoggerFactory.getLogger(ActiveListHTMLParserTest.class);



    //File input = new File("/home/kyle/Tests/ActiveListTest");
    File input = new File("/data/openleg/scraped/CALENDAR/D20141117.T130213.senate_cal_no_54_active_list_2014-06-20T04:28.html");

    @Test
    public void parseActiveLists() throws Exception{
        ActiveListHTMLParser.getSpotcheckReference(input);
        //ActiveListSpotcheckReference ref = ActiveListHTMLParserTest.getSpotcheckReference(input);
        //logger.info(OutputUtils.toJson(ref));
    }

}
