package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.dao.calendar.data.CalendarDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.calendar.XmlSenCalProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by uros on 4/26/17.
 */
@Transactional
public class XmlSenCalProcessorTest extends BaseXmlProcessorTest {

    @Autowired private XmlSenCalProcessor xmlSenCalProcessor;
    @Autowired private CalendarDao calendarDao;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return xmlSenCalProcessor;
    }

    @Test
    public void processCal()    {
        String xmlPath = "processor/bill/senCalendar/2017-01-30-17.47.57.088118_SENCAL_00008.XML";

        SobiFragment sobiFragment = generateXmlSobiFragment(xmlPath);
        processFragment(sobiFragment);

        LocalDateTime modifiedDate = sobiFragment.getPublishedDateTime();

        CalendarId calendarId = new CalendarId(8,2017);

        Calendar actual = calendarDao.getCalendar(calendarId);

        Calendar except = new Calendar();
        except.setId(new CalendarId(8,2017));
        except.setYear(2017);
        except.setSession(SessionYear.of(2017));
        TreeMap<Version, CalendarSupplemental> supplementalTreeMap = new TreeMap<>();
        CalendarSupplemental calendarSupplemental = new CalendarSupplemental(new CalendarId(8,2017), Version.DEFAULT, LocalDate.of(2017,1,31), LocalDateTime.parse("2017-01-30T17:47"));
        calendarSupplemental.addEntry(new CalendarSupplementalEntry(96, CalendarSectionType.ORDER_OF_THE_FIRST_REPORT, new BillId("S249",2017),null,false));
        calendarSupplemental.addEntry(new CalendarSupplementalEntry(97, CalendarSectionType.ORDER_OF_THE_FIRST_REPORT, new BillId("S1635",2017),null,false));
        supplementalTreeMap.put(Version.DEFAULT,calendarSupplemental);
        except.setSupplementalMap(supplementalTreeMap);

        /**
         * Comparsion
         */
        assertEquals(except.getId(), actual.getId());
        assertEquals(except.getCalDate(), actual.getCalDate());
        assertEquals(except.getActiveListMap(), actual.getActiveListMap());
        assertEquals(except.getSupplementalMap().get(Version.DEFAULT).getCalDate(),actual.getSupplementalMap().get(Version.DEFAULT).getCalDate());
        assertEquals(except.getSupplementalMap().get(Version.DEFAULT).getCalendarId(),actual.getSupplementalMap().get(Version.DEFAULT).getCalendarId());
        assertEquals(except.getSupplementalMap().get(Version.DEFAULT).getSectionEntries(),actual.getSupplementalMap().get(Version.DEFAULT).getSectionEntries());
        assertEquals(except.getSupplementalMap().get(Version.DEFAULT).getAllEntries(),actual.getSupplementalMap().get(Version.DEFAULT).getAllEntries());

    }
}
