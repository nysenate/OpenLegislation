package gov.nysenate.openleg.dao.activelist;

import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarActiveListEntry;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.spotcheck.ActiveListSpotcheckReference;

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

    //DELETE

}
