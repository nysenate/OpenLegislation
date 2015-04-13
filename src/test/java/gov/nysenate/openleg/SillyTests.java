package gov.nysenate.openleg;

import gov.nysenate.openleg.service.spotcheck.agenda.AgendaSpotcheckRunService;
import org.elasticsearch.common.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SillyTests extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    /** --- Your silly tests go here --- */

    /*
                      __     __,
                      \,`~"~` /
      .-=-.           /    . .\
     / .-. \          {  =    Y}=
    (_/   \ \          \      /
           \ \        _/`'`'`b
            \ `.__.-'`        \-._
             |            '.__ `'-;_
             |            _.' `'-.__)
              \    ;_..--'/     //  \
              |   /  /   |     //    |
              \  \ \__)   \   //    /
               \__)        './/   .'
                             `'-'`
    */

    @Autowired
    AgendaSpotcheckRunService agendaSpotcheckRunService;

    @Test
    public void agendaAlertTest() {
        logger.info("collated {} agenda alerts", agendaSpotcheckRunService.collate());
    }

    @Test
    public void dateFormatTest() {
        String dateString = "March 2, 2015";
        String formatPattern = "MMMM d, yyyy";
        logger.info("{}", LocalDate.parse(dateString, DateTimeFormatter.ofPattern(formatPattern)));
    }
}