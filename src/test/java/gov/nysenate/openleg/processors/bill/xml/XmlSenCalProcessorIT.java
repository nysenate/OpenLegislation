package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.calendar.*;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDao;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by uros on 4/26/17.
 */
@Category(IntegrationTest.class)
public class XmlSenCalProcessorIT extends BaseXmlProcessorTest {

    @Autowired private CalendarDao calendarDao;

    @Test
    public void processCal()    {
        String xmlPath = "processor/bill/senCalendar/2017-01-30-17.47.57.088118_SENCAL_00008.XML";

        LegDataFragment legDataFragment = generateXmlSobiFragment(xmlPath);
        processFragment(legDataFragment);

        LocalDateTime modifiedDate = legDataFragment.getPublishedDateTime();

        CalendarId calendarId = new CalendarId(8,2017);

        Calendar actual = calendarDao.getCalendar(calendarId);

        Calendar except = new Calendar();
        except.setId(new CalendarId(8,2017));
        except.setYear(2017);
        except.setSession(SessionYear.of(2017));
        EnumMap<Version, CalendarSupplemental> supplementalTreeMap = new EnumMap<>(Version.class);
        CalendarSupplemental calendarSupplemental = new CalendarSupplemental(new CalendarId(8,2017), Version.ORIGINAL, LocalDate.of(2017,1,31), LocalDateTime.parse("2017-01-30T17:47"));
        calendarSupplemental.addEntry(new CalendarSupplementalEntry(96, CalendarSectionType.ORDER_OF_THE_FIRST_REPORT, new BillId("S249",2017),null,false));
        calendarSupplemental.addEntry(new CalendarSupplementalEntry(97, CalendarSectionType.ORDER_OF_THE_FIRST_REPORT, new BillId("S1635",2017),null,false));
        supplementalTreeMap.put(Version.ORIGINAL,calendarSupplemental);
        except.setSupplementalMap(supplementalTreeMap);

        /*
         * Comparsion
         */
        assertEquals(except.getId(), actual.getId());
        assertEquals(except.getCalDate(), actual.getCalDate());
        assertEquals(except.getSupplementalMap().get(Version.ORIGINAL).getCalDate(),actual.getSupplementalMap().get(Version.ORIGINAL).getCalDate());
        assertEquals(except.getSupplementalMap().get(Version.ORIGINAL).getCalendarId(),actual.getSupplementalMap().get(Version.ORIGINAL).getCalendarId());
        assertEquals(except.getSupplementalMap().get(Version.ORIGINAL).getSectionEntries(),actual.getSupplementalMap().get(Version.ORIGINAL).getSectionEntries());
        assertEquals(except.getSupplementalMap().get(Version.ORIGINAL).getAllEntries(),actual.getSupplementalMap().get(Version.ORIGINAL).getAllEntries());

    }
}
