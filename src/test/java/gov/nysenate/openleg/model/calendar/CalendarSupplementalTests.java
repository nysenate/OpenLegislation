package gov.nysenate.openleg.model.calendar;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.TreeMap;

import static gov.nysenate.openleg.model.calendar.CalendarSectionType.ORDER_OF_THE_FIRST_REPORT;
import static gov.nysenate.openleg.model.calendar.CalendarSectionType.ORDER_OF_THE_SECOND_REPORT;

public class CalendarSupplementalTests
{
    private static final Logger logger = LoggerFactory.getLogger(CalendarSupplementalTests.class);

    @Test
    public void testCalSupEqualityInMaps() throws Exception {
        CalendarId calId1 = new CalendarId(1, 2014);
        CalendarId calId2 = new CalendarId(1, 2014);
        LocalDateTime date = LocalDateTime.now();

        CalendarSupplemental sup1 = new CalendarSupplemental(calId1, Version.of("P"), date.toLocalDate(), date);
        sup1.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_FIRST_REPORT, new BillId("S1234", 2013), null, false));
        sup1.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_SECOND_REPORT, new BillId("S1235", 2013), null, true));

        CalendarSupplemental sup2 = new CalendarSupplemental(calId2, Version.of("A"), date.toLocalDate(), date);
        sup2.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_FIRST_REPORT, new BillId("S1234", 2013), null, false));
        sup2.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_SECOND_REPORT, new BillId("S1235", 2013), null, true));
        sup2.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_SECOND_REPORT, new BillId("S1236", 2013), null, true));

        CalendarSupplemental sup3 = new CalendarSupplemental(calId1, Version.of("S"), date.toLocalDate(), date);
        sup3.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_FIRST_REPORT, new BillId("S1234", 2013), null, false));
        sup3.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_SECOND_REPORT, new BillId("S1235", 2013), null, true));

        CalendarSupplemental sup4 = new CalendarSupplemental(calId2, Version.of("A"), date.toLocalDate(), date);
        sup4.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_FIRST_REPORT, new BillId("S1234", 2013), null, false));
        sup4.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_SECOND_REPORT, new BillId("S1235", 2013), null, true));
        sup4.addEntry(new CalendarSupplementalEntry(1, ORDER_OF_THE_SECOND_REPORT, new BillId("S1231", 2013), null, true));

        TreeMap<Version, Object> map1 = new TreeMap<>();
        TreeMap<Version, Object> map2 = new TreeMap<>();

        map1.put(sup1.getVersion(), sup1);
        map1.put(sup2.getVersion(), sup2);

        map2.put(sup3.getVersion(), sup3);
        map2.put(sup4.getVersion(), sup4);

        MapDifference<Version, Object> diff = Maps.difference(map1, map2);
        logger.info("Only Left: {}", diff.entriesOnlyOnLeft().keySet());
        logger.info("Only Right: {}", diff.entriesOnlyOnRight().keySet());
        logger.info("Differing: {}", diff.entriesDiffering().keySet());
    }
}
