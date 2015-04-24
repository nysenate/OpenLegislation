package gov.nysenate.openleg.dao.oldapi;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.client.view.oldapi.OldMeetingView;
import gov.nysenate.openleg.dao.agenda.oldapi.OldApiMeetingDao;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Month;

public class OldApiTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(OldApiTests.class);

    @Autowired
    OldApiMeetingDao meetingDao;

    @Test
    public void getMeetingTest() {
        CommitteeId commId = new CommitteeId(Chamber.SENATE, "Finance");
        LocalDate meetingDate = LocalDate.of(2015, Month.APRIL, 22);
        OldMeetingView meetingView = meetingDao.getMeeting(commId, meetingDate);
        logger.info("{}", OutputUtils.toJson(meetingView));
    }
}
