package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.spotcheck.*;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    SpotCheckOpenMismatches<ContentKey> getOpenMismatches(OpenMismatchQuery query);

    /**
     * Get a summary of type/status/ignore counts pertaining to the given query
     *
     * @param refTypes
     * @param observedAfter
     * @return OpenMismatchesSummary
     */
    OpenMismatchSummary getOpenMismatchSummary(Set<SpotCheckRefType> refTypes, LocalDateTime observedAfter);

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

    /**
     * Sets the ignore status for a spotcheck mismatch
     * @param mismatchId int
     * @param ignoreStatus SpotCheckMismatchIgnore
     */
    void setMismatchIgnoreStatus(int mismatchId, SpotCheckMismatchIgnore ignoreStatus);

    /**
     * Adds the given issue id to the tracked issue ids of mismatch specified by the given mismatch id
     * @param mismatchId int
     * @param issueId String
     */
    void addIssueId(int mismatchId, String issueId);

    /**
     * Removes the given issue id from the tracked issue ids of the mismatch specified by the given mismatch id
     * @param mismatchId int
     * @param issueId String
     */
    void deleteIssueId(int mismatchId, String issueId);
}