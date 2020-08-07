package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CalendarUtils {
    /**
     * Creates a generic calendar with no active lists or supplementals.
     * @return the simple calendar.
     */
    public static Calendar createGenericCalendar(CalendarId calId) {
        return createCalendar(calId, new ArrayList<>(), new HashSet<>());
    }

    /**
     * Creates a single Calendar for testing.
     * @param calId of calendar to create.
     * @param activeLists on the calendar.
     * @param supSet supplementals to the calendar.
     * @return the new Calendar.
     */
    public static Calendar createCalendar(CalendarId calId, List<CalendarActiveList> activeLists, Set<CalendarSupplemental> supSet) {
        Calendar cal = new Calendar(calId);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.ofYearDay(calId.getYear(), calId.getCalNo()+1), LocalTime.now());
        cal.setPublishedDateTime(dateTime);
        for (CalendarActiveList list: activeLists)
            cal.putActiveList(list);
        for (CalendarSupplemental calSup : supSet)
            cal.putSupplemental(calSup);
        return cal;
    }

    /**
     * Creates active lists for use in a calendar.
     * @param calId of Calendar this will later be added to.
     * @return the list of active lists.
     */
    public static List<CalendarActiveList> createActiveLists(int numEntriesPerList, int numLists, CalendarId calId) {
        List<CalendarActiveList> calActiveLists = new ArrayList<>();
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.ofYearDay(calId.getYear(), calId.getCalNo()+1), LocalTime.now());
        for (int i = 0; i < numLists; i++) {
            CalendarActiveList curr = new CalendarActiveList(calId, i+1, "", LocalDate.now(), LocalDateTime.now());
            curr.setSession(new SessionYear(calId.getYear()));
            curr.setPublishedDateTime(dateTime);
            curr.setNotes("notes" + i);
            for (int j = 0; j < numEntriesPerList; j++) {
                CalendarEntry calEntry = new CalendarEntry(j, new BillId("S" + j+1, LocalDate.now().getYear()));
                curr.addEntry(calEntry);
            }
            calActiveLists.add(curr);
        }
        return calActiveLists;
    }
}
