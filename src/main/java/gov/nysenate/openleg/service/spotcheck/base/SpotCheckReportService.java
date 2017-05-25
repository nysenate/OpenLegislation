package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.spotcheck.*;

import java.time.LocalDateTime;

/**
 * The SpotCheckReportService specifies the various methods that are available for use when
 * generating data quality reports. It is designed with a template parameter such that implementations
 * can use any object as the key to associate observations with (BillId, AgendaId, etc).
 *
 * @param <ContentKey> - The class that can uniquely identify an instance being checked
 *                       (e.g BaseBillId if checking Bill objects)
 */
public interface SpotCheckReportService<ContentKey>
{

    /**
     * @return The SpotCheckRefType that is used for this report
     */
    SpotCheckRefType getSpotcheckRefType();

    /**
     * Generate a SpotCheckReport across all available data. The reference data used for
     * comparison will be the most recent data between the given start and end range.
     *
     * @param start LocalDateTime - The reference data will be active after (or on) this date/time.
     * @param end LocalDateTime - The reference data will be active prior to (or on) this date/time.
     * @return SpotCheckReport<ContentKey>
     * @throws ReferenceDataNotFoundEx - If there is no reference data that can be used for this report.
     */
    SpotCheckReport<ContentKey> generateReport(LocalDateTime start, LocalDateTime end)
            throws Exception;

    /**
     * Saves the report into the backing store. The report will be saved such that mismatches from
     * prior reports are taken into account when setting the statuses. Any exception encountered when
     * saving this will propagate through.
     *
     * @param report SpotCheckReport<ContentKey> - The report to save in the backing store.
     */
    void saveReport(SpotCheckReport<ContentKey> report);

    /**
     * Get mismatches matching the given MismatchQuery.
     * Defaults to Not ignored open mismatches for the current session.
     * @param query Defines parameters to query by.
     * @return Paginated list of DeNormSpotCheckMismatch's
     */
    PaginatedList<DeNormSpotCheckMismatch> getMismatches(MismatchQuery query, LimitOffset limitOffset);
    /**
     * Gets mismatch content type summary information for the given datasource, as of the given summary date time.
     * @param dataSource
     * @param summaryDateTime
     * @param mismatchState
     * @param spotCheckMismatchType
     * @return mismatchStatusSummary
     */

    MismatchContentTypeSummary getMismatchContentTypeSummary(SpotCheckDataSource dataSource, LocalDateTime summaryDateTime, MismatchState mismatchState, SpotCheckMismatchType spotCheckMismatchType);

    /**
     * Gets mismatch type  summary information for the given datasource, as of the given summary date time.
     */
    MismatchTypeSummary getMismatchTypeSummary(SpotCheckDataSource dataSource, LocalDateTime summaryDateTime, MismatchState mismatchState);

    /**
     * Gets mismatch status summary information for the given datasource, as of the given summary date time.
     * @return
     */
    MismatchStatusSummary getMismatchStatusSummary(SpotCheckDataSource dataSource, MismatchStatus stautus, LocalDateTime reportEndDateTime);

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
     * Spotcheck Mismatch update Issue Id API
     * @param mismatchId  mismatch id
     * @param issueIds mismatch issues id separate by comma ,e.g 12,3,61
     *
     */
    void updateIssueId(int mismatchId, String issueIds);

    /**
     * Removes the given issue id from the tracked issue ids of the mismatch specified by the given mismatch id
     * @param mismatchId int
     * @param issueId String
     */
    void deleteIssueId(int mismatchId, String issueId);

    /**
 * Removes all issues corresponding to given mismatch id
 *
 * @param mismatchId int mismatch id
 */
    void deleteAllIssueId(int mismatchId);

}
