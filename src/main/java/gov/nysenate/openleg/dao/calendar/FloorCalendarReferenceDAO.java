package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.model.spotcheck.calendar.FloorCalendarSpotcheckReference;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Created by kyle on 10/22/14.
 */
public interface FloorCalendarReferenceDAO {

    /**
     * @param cal CalendarSupplementalId
     * @param spot SpotCheckReferenceId
     * @return FloorCalendarSpotcheckReference
     */
    public FloorCalendarSpotcheckReference getFCR(CalendarSupplementalId cal, SpotCheckReferenceId spot) throws DataAccessException;

    /**
     * @param cal CalendarSupplementalId
     * @return FloorCalendarSpotcheckReference
     */
    public FloorCalendarSpotcheckReference getCurrentFCR(CalendarSupplementalId cal) throws DataAccessException;

    /**
     * @param year int
     * @return SessionYear
     */
    public List<FloorCalendarSpotcheckReference> getFCRYear(int year) throws DataAccessException;

}
