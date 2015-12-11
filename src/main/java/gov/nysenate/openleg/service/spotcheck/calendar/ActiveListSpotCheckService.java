package gov.nysenate.openleg.service.spotcheck.calendar;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.activelist.ActiveListReferenceDAO;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeSet;

/**
 * Created by kyle on 1/20/15.
 */
public class ActiveListSpotCheckService<ContentKey, ContentType, ReferenceType> implements
        SpotCheckService<CalendarActiveListId, CalendarActiveList, ActiveListSpotcheckReference> {

    ActiveListReferenceDAO activeListDAO;
    @Override
    public SpotCheckObservation<CalendarActiveListId> check(CalendarActiveList content) throws ReferenceDataNotFoundEx {
        return check(content, DateUtils.LONG_AGO.atStartOfDay(), LocalDateTime.now());
    }

    @Override
    public SpotCheckObservation<CalendarActiveListId> check(CalendarActiveList content, LocalDateTime start,
                                                            LocalDateTime end) throws ReferenceDataNotFoundEx {
        if (content == null) {
            throw new IllegalArgumentException("Supplied calendar cannot be null");
        }
        Range<LocalDate> dateRange = Range.closed(start.toLocalDate(), end.toLocalDate());
        try {
            ActiveListSpotcheckReference reference = null;// = activeListDAO.getCurrentCalendar();
            //ActiveListSpotcheckReference reference =

            //DaybreakBill daybreakBill = daybreakDao.getCurrentDaybreakBill(bill.getBaseBillId(), dateRange);
            return check(content, reference);
        }
        catch (DataAccessException ex) {
            throw new ReferenceDataNotFoundEx();
        }
    }

    @Override
    public SpotCheckObservation<CalendarActiveListId> check(CalendarActiveList content, ActiveListSpotcheckReference reference) {
        if (content == null) {
            throw new IllegalArgumentException("Active list cannot be null when performing spot check");
        }
        TreeSet referenceSet = new TreeSet();
        TreeSet contentSet = new TreeSet();
        for (CalendarEntry entry : reference.getEntries()) referenceSet.add(entry);

        for (CalendarEntry entry : content.getEntries()) contentSet.add(entry);


        SpotCheckReferenceId referenceId = reference.getReferenceId();

        Integer calDate = Integer.parseInt(content.getCalDate().toString());
        CalendarActiveListId calendarActId = new CalendarActiveListId(calDate, content.getYear(), content.getSequenceNo());
        SpotCheckObservation<CalendarActiveListId> observation = new SpotCheckObservation<>(referenceId, calendarActId);

        checkCalDate(content, reference, observation);
        checkReleaseDateTime(content, reference, observation);
        checkCalendarMismatch(content, reference, observation);



        return null;
    }
    public void checkCalDate(CalendarActiveList content, ActiveListSpotcheckReference reference,
                             SpotCheckObservation<CalendarActiveListId> obsrv){
        if (!content.getCalDate().equals(reference.getCalDate())){
            obsrv.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.LIST_CAL_DATE,
                    content.getCalDate().toString(), reference.getCalDate().toString()));
        }
    }
    public void checkReleaseDateTime(CalendarActiveList content, ActiveListSpotcheckReference reference,
                                     SpotCheckObservation<CalendarActiveListId> obsrv) {
        if (!content.getReleaseDateTime().equals(reference.getReleaseDateTime())) {
            obsrv.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.LIST_RELEASE_DATE_TIME,
                    content.getReleaseDateTime().toString(), reference.getReleaseDateTime().toString()));
        }
    }
    public void checkCalendarMismatch(CalendarActiveList content, ActiveListSpotcheckReference reference,
                                   SpotCheckObservation<CalendarActiveListId> obsrv) {
        if (!content.getCalendarId().equals(reference.getCalendarId())) {
            obsrv.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.LIST_CALENDAR_MISMATCH,
                    content.getCalendarId().toString(), reference.getCalendarId().toString()));
        }
    }
    public void checkEntryMismatch(TreeSet referenceSet, TreeSet contentSet,
                                   SpotCheckObservation<CalendarActiveListId> obsrv){
        if (!contentSet.equals(referenceSet)){
            obsrv.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.LIST_ENTRY_MISMATCH,
                    contentSet.toString(), referenceSet.toString()));
        }
    }


}
