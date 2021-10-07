package gov.nysenate.openleg.api.legislation.calendar.view;

import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveList;
import gov.nysenate.openleg.legislation.calendar.CalendarSupplemental;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarViewFactory {
    private static final Logger logger = LoggerFactory.getLogger(CalendarViewFactory.class);

    @Autowired
    BillDataService billDataService;

    public CalendarView getCalendarView(Calendar calendar) {
        return new CalendarView(calendar, billDataService);
    }

    public ActiveListView getActiveListView(CalendarActiveList activeList) {
        return new ActiveListView(activeList, billDataService);
    }

    public CalendarSupView getCalendarSupView(CalendarSupplemental calendarSupplemental) {
        return new CalendarSupView(calendarSupplemental, billDataService);
    }
}
