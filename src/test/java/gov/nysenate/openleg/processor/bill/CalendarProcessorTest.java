package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.dao.calendar.data.CalendarDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.calendar.CalendarProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by uros on 4/26/17.
 */
@Transactional
public class CalendarProcessorTest extends BaseXmlProcessorTest {

    @Autowired private CalendarProcessor calendarProcessor;
    @Autowired private CalendarDao calendarDao;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return calendarProcessor;
    }

    @Test
    public void processCal()    {
        String xmlPath = "processor/bill/senCalendar/2017-01-30-17.47.57.088118_SENCAL_00008.XML";
        processXmlFile(xmlPath);

        CalendarId calendarId = new CalendarId(8,2017);

        Calendar calendar = calendarDao.getCalendar(calendarId);

        Calendar testObject = new Calendar();

    }
}
