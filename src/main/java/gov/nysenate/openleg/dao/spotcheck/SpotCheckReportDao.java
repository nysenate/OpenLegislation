package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.spotcheck.*;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;

/**
 * Dao interface for retrieving, saving, and deleting spot check reports. The interface is templated
 * to allow for a single implementation to handle various types of data types.
 *
 * @param <ContentKey> - The class that can uniquely identify the instances being checked in the reports
 */
public interface SpotCheckReportDao<ContentKey>
{

    DeNormSpotCheckMismatch getMismatch(int mismatchId);

    /**
     * Get mismatches matching the given query params.
     */
    PaginatedList<DeNormSpotCheckMismatch> getMismatches(MismatchQuery query, LimitOffset limitOffset);

    /**
     * Get mismatch status summary counts for given datasource and report date.
     *
     * @param reportDate The date of the report
     * @return OpenMismatchesSummary
     */
    MismatchStatusSummary getMismatchStatusSummary(SpotCheckDataSource datasource, LocalDate reportDate);

    /**
     * Get mismatch type summary counts for the given datasource and date.
     *
     * @return MismatchTypeSummary
     */
    MismatchTypeSummary getMismatchTypeSummary(SpotCheckDataSource datasource, LocalDate reportDate, MismatchStatus mismatchStatus);


    /**
     * Save the report to the backing store. This process may add additional observations to the
     * report to account for mismatches from previously saved reports. The mismatch statuses are
     * also modified here using the context of prior reports.
     *
     * @param report SpotCheckReport<ContentKey> - The report to save into the backing store
     */
    void saveReport(SpotCheckReport<ContentKey> report) throws DataAccessException;

    /**
     * Get mismatch type summary counts for the given datasource, date, mismatch status, and mismatch types.
     *
     * @return MismatchContentTypeSummary
     */
    MismatchContentTypeSummary getMismatchContentTypeSummary(SpotCheckDataSource datasource, LocalDate reportDate, MismatchStatus mismatchStatus, EnumSet mismatchTypes);

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