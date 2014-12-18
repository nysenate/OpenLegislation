package gov.nysenate.openleg.dao.calendar;

import com.google.common.collect.Range;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.data.CalendarUpdatesDao;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.util.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class CalendarUpdatesDaoTests extends BaseTests {

    @Autowired
    private CalendarUpdatesDao calendarUpdatesDao;

    @Test
    public void getCalendarDigestTest() {
        calendarUpdatesDao.getUpdateDigests(new CalendarId(54, 2014),
                Range.openClosed(LocalDateTime.of(2012, 12, 01, 0, 0), DateUtils.THE_FUTURE.atStartOfDay()),
                SortOrder.ASC);
    }
}
