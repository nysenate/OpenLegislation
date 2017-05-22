package gov.nysenate.openleg.dao.calendar.data;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Category(SillyTest.class)
public class SqlCalendarDaoTest extends BaseTests {

    Logger logger = LoggerFactory.getLogger(SqlCalendarDaoTest.class);

    @Autowired SqlCalendarDao calendarDao;

    @Test
    public void getActiveListTest() {
        List<CalendarActiveListId> activeListIds = calendarDao.getActiveListIds(2017, SortOrder.ASC, LimitOffset.ALL);
        logger.info("{}", activeListIds.size());
        activeListIds.forEach(calendarDao::getActiveList);
    }

}