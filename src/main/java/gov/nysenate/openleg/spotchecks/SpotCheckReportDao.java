package gov.nysenate.openleg.spotchecks;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.spotchecks.model.*;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.Set;

/**
 * Dao interface for retrieving, saving, and deleting spot check reports. The interface is templated
 * to allow for a single implementation to handle various types of data types.
 */
public interface SpotCheckReportDao
{

    DeNormSpotCheckMismatch<?> getMismatch(int mismatchId);

    /**
     * Get mismatches matching the given query params.
     */
    PaginatedList<DeNormSpotCheckMismatch<?>> getMismatches(MismatchQuery query, LimitOffset limitOffset);

    /**
     * Get mismatch status summary counts for given datasource and report date.
     *
     * @param reportDate The date of the report
     * @param contentType
     * @return OpenMismatchesSummary
     */
    MismatchStatusSummary getMismatchStatusSummary(LocalDate reportDate, SpotCheckDataSource datasource,
                                                   SpotCheckContentType contentType, Set<SpotCheckMismatchIgnore> ignoreStatuses);

    /**
     * Get mismatch type summary counts for the given date, data source, content type, and mismatch status.
     *
     * @return MismatchTypeSummary
     */
    MismatchTypeSummary getMismatchTypeSummary(LocalDate reportDate,
                                               SpotCheckDataSource datasource,
                                               SpotCheckContentType contentType,
                                               MismatchStatus mismatchStatus,
                                               Set<SpotCheckMismatchIgnore> ignoreStatuses);


    /**
     * Get mismatch type summary counts for the given datasource, date, mismatch status, and mismatch types.
     *
     * @return MismatchContentTypeSummary
     */
    MismatchContentTypeSummary getMismatchContentTypeSummary(LocalDate reportDate, SpotCheckDataSource datasource,
                                                             Set<SpotCheckMismatchIgnore> ignoreStatuses);

    /**
     * Save the report to the backing store. This process may add additional observations to the
     * report to account for mismatches from previously saved reports. The mismatch statuses are
     * also modified here using the context of prior reports.
     *
     * @param report SpotCheckReport - The report to save into the backing store
     */
    void saveReport(SpotCheckReport<?> report) throws DataAccessException;

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