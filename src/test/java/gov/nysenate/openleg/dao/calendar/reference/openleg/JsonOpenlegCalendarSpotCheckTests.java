package gov.nysenate.openleg.dao.calendar.reference.openleg;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.spotcheck.openleg.OpenlegCalendarReportService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class JsonOpenlegCalendarSpotCheckTests extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegCalendarSpotCheckTests.class);

    @Autowired
    OpenlegCalendarReportService openlegCalendarReportService;

    @Test
    public void testGetCalendarView() throws Exception {
        SpotCheckReport<CalendarEntryListId> spotCheckReport = openlegCalendarReportService.generateReport(LocalDateTime.parse("2017-01-01T00:00:00"),null);
        openlegCalendarReportService.saveReport(spotCheckReport);
    }
}
