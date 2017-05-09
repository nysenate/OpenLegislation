package gov.nysenate.openleg.processor.entry;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.entity.committee.data.CommitteeDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.entity.CommitteeProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.processor.spotcheck.calendar.CalendarAlertProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Chenguang He on 5/8/2017.
 */
public class CommitteeProcessorTest  extends BaseXmlProcessorTest {
    @Autowired
    private CommitteeProcessor process;

    @Autowired
    private CommitteeDao committeeDao;


  private String path  = "processor/bill/senCommitte/2017-01-31-04.51.42.526466_SENCOMM_BSCXB_SENATE.XML";

    @Test
    public void processCommittee() throws ParseError {
        CommitteeId committeeId = new CommitteeId(Chamber.SENATE,"Agriculture");
//        CommitteeVersionId committeeVersionId = new CommitteeVersionId(new CommitteeSessionId(committeeId,SessionYear.of(2017)), LocalDateTime.of(2017,2,1,0,0));
        processXmlFile(path);
        Committee actual = committeeDao.getCommittee(committeeId);
        System.out.println();
    }

    @Override
    protected SobiProcessor getSobiProcessor() {
        return process;
    }
}
