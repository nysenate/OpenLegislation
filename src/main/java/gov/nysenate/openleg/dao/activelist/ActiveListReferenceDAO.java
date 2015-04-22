package gov.nysenate.openleg.dao.activelist;

import com.google.common.collect.Range;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.spotcheck.ActiveListSpotcheckReference;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by kyle on 11/21/14.
 */
public interface ActiveListReferenceDAO {
    public void addCalendarReference(ActiveListSpotcheckReference act);

    public ActiveListSpotcheckReference getCalendarReference(CalendarActiveListId cal, LocalDateTime time);
    public ActiveListSpotcheckReference getMostRecentReference(CalendarActiveListId cal);
    public List<ActiveListSpotcheckReference> getMostRecentEachYear(int year);
    public ActiveListSpotcheckReference getCurrentCalendar(CalendarActiveListId cal, Range<LocalDateTime> dateRange) throws DataAccessException;

    //public ActiveListSpotcheckReference getRange
    //DELETE

}
