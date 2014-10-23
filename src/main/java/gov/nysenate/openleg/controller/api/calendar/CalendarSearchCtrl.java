package gov.nysenate.openleg.controller.api.calendar;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/calendars", method = RequestMethod.GET)
public class CalendarSearchCtrl extends BaseCtrl{

    private static final Logger logger = LoggerFactory.getLogger(CalendarSearchCtrl.class);

    @Autowired
    private CalendarDataService calendarDataService;
}
