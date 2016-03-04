package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.spotcheck.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
     * Obtain a SpotCheckReport from the backing store using the report id. The report obtained
     * by {@link #generateReport} differs from the same report returned via this method in that this
     * report will only contain observations that have mismatches and will also have data that's been
     * associated with the context of prior reports that have been saved.
     *
     *
     * @param reportId
     */
    SpotCheckReport<ContentKey> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx;

    /**
     * Return a list of saved report ids with options to filter the result set.
     *
     * @param reportType
     * @param start LocalDateTime - The earliest report date (inclusive)
     * @param end LocalDateTime - The latest report date (inclusive)
     * @param dateOrder SortOrder - Order the reports by report date
     * @return List<SpotCheckReportId> - List of report ids
     */
    List<SpotCheckReportSummary> getReportSummaries(SpotCheckRefType reportType, LocalDateTime start, LocalDateTime end,
                                                    SortOrder dateOrder);

    /**
     * Get a map of all unresolved or recently resolved observations spanning all reports of the given refType
     * @param query OpenMismatchQuery
     * @return Map<ContentKey, SpotCheckObservation<ContentKey>>
     */
    SpotCheckOpenMismatches<ContentKey> getOpenObservations(OpenMismatchQuery query);

    /**
     * Get a summary of type/status/ignore counts pertaining to the given query
     *
     * @param refTypes
     * @param observedAfter
     * @return OpenMismatchesSummary
     */
    OpenMismatchSummary getOpenMismatchSummary(Set<SpotCheckRefType> refTypes, LocalDateTime observedAfter);

    /**
     * Wipe a report as well as all of its associated observations and mismatches from the backing store.
     *
     * @param reportId
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
