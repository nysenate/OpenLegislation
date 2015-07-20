package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.spotcheck.*;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Collection;
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
    SpotCheckReport<ContentKey> getReport(SpotCheckReportId id) throws DataAccessException;

    /**
     * Get a list of the report ids that have been saved with options to filter the result set.
     *
     *
     * @param refType
     * @param start LocalDateTime - Retrieved reports will have been run after/on this date/time.
     * @param end LocalDateTime - Retrieved reports will have been run before/on this date/time.
     * @param dateOrder SortOrder - Order the results by the report date/time.
     * @return List<SpotCheckReportId>
     */
    List<SpotCheckReportSummary> getReportSummaries(SpotCheckRefType refType, LocalDateTime start,
                                                    LocalDateTime end, SortOrder dateOrder);

    /**
     * Get a map of all unresolved or recently resolved observations spanning all reports of the given refType
     * @param query OpenMismatchQuery
     * */
    SpotCheckOpenMismatches<ContentKey> getOpenObservations(OpenMismatchQuery query);

    /**
     * Save the report to the backing store. This process may add additional observations to the
     * report to account for mismatches from previously saved reports. The mismatch statuses are
     * also modified here using the context of prior reports.
     *
     * @param report SpotCheckReport<ContentKey> - The report to save into the backing store
     */
    void saveReport(SpotCheckReport<ContentKey> report) throws DataAccessException;

    /**
     * Delete a report via the report id
     *
     * @param reportId SpotCheckReportId
     */
    void deleteReport(SpotCheckReportId reportId);
}