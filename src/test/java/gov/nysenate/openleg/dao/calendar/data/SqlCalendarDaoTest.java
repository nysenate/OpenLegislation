package gov.nysenate.openleg.dao.calendar.data;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class SqlCalendarDaoTest extends BaseTests {

    Logger logger = LogManager.getLogger();

    @Autowired SqlCalendarDao calendarDao;

    @Test
    public void getActiveListTest() {
        List<CalendarActiveListId> activeListIds = calendarDao.getActiveListIds(2017, SortOrder.ASC, LimitOffset.ALL);
        logger.info(activeListIds.size());
        activeListIds.forEach(calendarDao::getActiveList);
    }

}