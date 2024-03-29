package gov.nysenate.openleg.spotchecks.scraping.lrs.agenda;

import com.google.common.collect.Range;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveListId;
import gov.nysenate.openleg.spotchecks.model.ActiveListSpotcheckReference;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by kyle on 11/21/14.
 */
public interface ActiveListReferenceDAO {
    void addCalendarReference(ActiveListSpotcheckReference act);

    ActiveListSpotcheckReference getCalendarReference(CalendarActiveListId cal, LocalDateTime time);
    ActiveListSpotcheckReference getMostRecentReference(CalendarActiveListId cal);
    List<ActiveListSpotcheckReference> getMostRecentEachYear(int year);
    ActiveListSpotcheckReference getCurrentCalendar(CalendarActiveListId cal, Range<LocalDateTime> dateRange) throws DataAccessException;

    //public ActiveListSpotcheckReference getRange
    //DELETE
}
