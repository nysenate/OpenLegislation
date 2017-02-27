package gov.nysenate.openleg.processor.bill.sponsor;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.dao.sobi.SobiDao;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.anact.AnActSobiProcessorTest;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * Created by robert on 2/22/17.
 */
@Transactional
public class SponsorSobiProcessorTest extends BaseXmlProcessorTest {
    @Autowired BillDao billDao;
    @Autowired SobiDao sobiDao;
    @Autowired SponsorSobiProcessor sponsorSobiProcessor;

    private static final Logger logger = LoggerFactory.getLogger(SponsorSobiProcessorTest.class);

    @Override
    protected SobiProcessor getSobiProcessor() {
        return sponsorSobiProcessor;
    }

    @Test
    public void processReplaceTest() throws Exception {
        boolean sponsor=false;
        boolean multi=true;
        boolean rule=false;
        boolean budget=false;
        if(sponsor){
            processReplaceSponsor("processor/bill/sponsor/2017-02-23-18.14.29.288826_LDSPON_A10756.XML");
        }
        if(multi){
            processReplaceSponsor("processor/bill/sponsor/2017-01-05-15.43.14.986330_LDSPON_A00289.XML");
        }
        if(rule){
            processReplaceSponsor("processor/bill/sponsor/2016-12-23-18.14.29.288826_LDSPON_A10756.XML");
        }
        if(budget){
            processReplaceSponsor("processor/bill/sponsor/2017-01-23-18.15.36.277999_LDSPON_S02002.XML");
        }
    }

    public void processReplaceSponsor(String file) throws Exception {
        processXmlFile(file);
    }
}
