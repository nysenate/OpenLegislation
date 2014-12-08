package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;
import gov.nysenate.openleg.service.bill.data.BillDataService;
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
