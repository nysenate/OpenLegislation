package gov.nysenate.openleg.legislation.calendar.dao;

import gov.nysenate.openleg.legislation.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReferenceId;
import gov.nysenate.openleg.spotchecks.alert.calendar.FloorCalendarSpotcheckReference;
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
    FloorCalendarSpotcheckReference getFCR(CalendarSupplementalId cal, SpotCheckReferenceId spot) throws DataAccessException;

    /**
     * @param cal CalendarSupplementalId
     * @return FloorCalendarSpotcheckReference
     */
    FloorCalendarSpotcheckReference getCurrentFCR(CalendarSupplementalId cal) throws DataAccessException;

    /**
     * @param year int
     * @return SessionYear
     */
    List<FloorCalendarSpotcheckReference> getFCRYear(int year) throws DataAccessException;

}
