package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.spotcheck.*;

import java.time.LocalDate;
import java.util.Set;

/**
 * Provides base functionality for implementors of SpotCheckReportService
 */
public abstract class BaseSpotCheckReportService<ContentKey> implements SpotCheckReportService<ContentKey> {

    /**
     * @return SpotCheckReportDao - the report dao that is used by the implementing report service
     */
    protected abstract SpotCheckReportDao<ContentKey> getReportDao();

    /** {@inheritDoc} */
    @Override
    public PaginatedList<DeNormSpotCheckMismatch> getMismatches(MismatchQuery query, LimitOffset limitOffset){
        return getReportDao().getMismatches(query, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public MismatchStatusSummary getMismatchStatusSummary(LocalDate reportDate, SpotCheckDataSource dataSource,
                                                          SpotCheckContentType contentType, Set<SpotCheckMismatchIgnore> ignoreStatuses) {
        return getReportDao().getMismatchStatusSummary(reportDate, dataSource, contentType, ignoreStatuses);
    }

    /** {@inheritDoc} */
    @Override
    public MismatchTypeSummary getMismatchTypeSummary(LocalDate reportDate, SpotCheckDataSource dataSource,
                                                      SpotCheckContentType contentType, MismatchStatus mismatchStatus,
                                                      Set<SpotCheckMismatchIgnore> ignoreStatuses) {
        return getReportDao().getMismatchTypeSummary(reportDate, dataSource, contentType, mismatchStatus, ignoreStatuses);
    }

    /** {@inheritDoc} */
    @Override
    public MismatchContentTypeSummary getMismatchContentTypeSummary(LocalDate reportDate, SpotCheckDataSource dataSource,
                                                                    Set<SpotCheckMismatchIgnore> ignoreStatuses) {
        return getReportDao().getMismatchContentTypeSummary(reportDate, dataSource, ignoreStatuses);
    }

    /** {@inheritDoc} */
    @Override
    public void saveReport(SpotCheckReport<ContentKey> report) {
        getReportDao().saveReport(report);
    }

    /** {@inheritDoc} */
    @Override
    public void setMismatchIgnoreStatus(int mismatchId, SpotCheckMismatchIgnore ignoreStatus) {
        getReportDao().setMismatchIgnoreStatus(mismatchId, ignoreStatus);
    }
    /** {@inheritDoc} */

    @Override
    public void addIssueId(int mismatchId, String issueId) {
        getReportDao().addIssueId(mismatchId, issueId);
    }

    /** {@inheritDoc} */
    @Override
    public void updateIssueId(int mismatchId, String issueIds) {
        getReportDao().updateIssueId(mismatchId, issueIds);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteIssueId(int mismatchId, String issueId) {
        getReportDao().deleteIssueId(mismatchId, issueId);
    }

    /**
     * Removes all issues corresponding to given mismatch id
     *
     * @param mismatchId int mismatch id
     */
    @Override
    public void deleteAllIssueId(int mismatchId) {
        getReportDao().deleteAllIssueId(mismatchId);
    }
}
