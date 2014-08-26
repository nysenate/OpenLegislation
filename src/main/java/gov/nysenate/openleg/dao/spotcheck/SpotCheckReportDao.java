package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dao interface for retrieving, saving, and deleting spot check reports. The interface is templated
 * to allow for a single implementation to handle various types of data types.
 *
 * @param <ContentKey> - The class that can uniquely identify the instances being checked in the reports
 */
public interface SpotCheckReportDao<ContentKey>
{
    /**
     * Retrieve a previously saved report from the backing store. The fetched record will only
     * contain observations that have mismatches to reduce clutter.
     *
     * @param id SpotCheckReportId
     * @return SpotCheckReport<ContentKey> or DataAccessException if no matching report was found
     */
    public SpotCheckReport<ContentKey> getReport(SpotCheckReportId id) throws DataAccessException;

    /**
     * Get a list of the report ids that have been saved with options to filter the result set.
     *
     * @param refType SpotCheckRefType - The type of reference data used for the report
     * @param start LocalDateTime - Retrieved reports will have been run after/on this date/time.
     * @param end LocalDateTime - Retrieved reports will have been run before/on this date/time.
     * @param dateOrder SortOrder - Order the results by the report date/time.
     * @param limOff LimitOffset - Restrict the result set.s
     * @return List<SpotCheckReportId>
     */
    public List<SpotCheckReportId> getReportIds(SpotCheckRefType refType, LocalDateTime start,
                                                LocalDateTime end, SortOrder dateOrder, LimitOffset limOff);

    /**
     * Save the report to the backing store. This process may add additional observations to the
     * report to account for mismatches from previously saved reports. The mismatch statuses are
     * also modified here using the context of prior reports.
     *
     * @param report SpotCheckReport<ContentKey> - The report to save into the backing store
     */
    public void saveReport(SpotCheckReport<ContentKey> report) throws DataAccessException;

    /**
     * Delete a report via the report id
     *
     * @param reportId SpotCheckReportId
     */
    public void deleteReport(SpotCheckReportId reportId);
}