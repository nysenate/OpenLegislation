package gov.nysenate.openleg.processor.bill.anact;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.dao.sobi.SobiDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by robert on 2/15/17.
 */
@Transactional
public class AnActSobiProcessorTest extends BaseTests{
    @Autowired BillDao billDao;
    @Autowired  SobiDao sobiDao;
    @Autowired AnActSobiProcessor anActSobiProcessor;
    private static final Logger logger = LoggerFactory.getLogger(AnActSobiProcessorTest.class);

    @Test
    public void processTest() throws Exception {
        Bill a=billDao.getBill(new BillId("S08215",2015));

        File anactXmlFile = new File(getClass().getClassLoader().getResource(
                "processor/bill/anact/2016-12-02-09.16.10.220257_ANACT_S08215.XML").getFile());
        String contents=FileUtils.readFileToString(anactXmlFile);
        SobiFile sobiFileTester = new SobiFile(anactXmlFile);
        SobiFragment sobiFragmentTester=new SobiFragment(sobiFileTester, SobiFragmentType.ANACT,contents,1);
        sobiDao.updateSobiFile(sobiFileTester);
        sobiDao.updateSobiFragment(sobiFragmentTester);
        anActSobiProcessor.process(sobiFragmentTester);
        anActSobiProcessor.postProcess();


        Bill b=billDao.getBill(new BillId("S08215",2015));
        String expectedS="AN ACT to amend the executive law, in relation to the appointment of\n" +
                "interpreters to be used in parole board proceedings";
        String actualClause=b.getAmendment(Version.DEFAULT).getActClause();
        assertEquals(expectedS,actualClause);
    }

}

