package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.spotcheck.*;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.List;
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
    public SpotCheckReport<ContentKey> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId cannot be null");
        }
        try {
            return getReportDao().getReport(reportId);
        } catch (EmptyResultDataAccessException ex) {
            throw new SpotCheckReportNotFoundEx(reportId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SpotCheckReportSummary> getReportSummaries(SpotCheckRefType reportType,
                                                           LocalDateTime start, LocalDateTime end, SortOrder dateOrder) {
        return getReportDao().getReportSummaries(reportType, start, end, dateOrder);
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckOpenMismatches<ContentKey> getOpenObservations(OpenMismatchQuery query) {
        return getReportDao().getOpenMismatches(query);
    }

    /** {@inheritDoc}
     * @param refTypes
     * @param observedAfter*/
    @Override
    public OpenMismatchSummary getOpenMismatchSummary(Set<SpotCheckRefType> refTypes, LocalDateTime observedAfter) {
        return getReportDao().getOpenMismatchSummary(refTypes, observedAfter);
    }

    /** {@inheritDoc} */
    @Override
    public void saveReport(SpotCheckReport<ContentKey> report) {
        getReportDao().saveReport(report);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteReport(SpotCheckReportId reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId to delete cannot be null");
        }
        getReportDao().deleteReport(reportId);
    }

    /** {@inheritDoc} */
    @Override
    public void setMismatchIgnoreStatus(int mismatchId, SpotCheckMismatchIgnore ignoreStatus) {
        getReportDao().setMismatchIgnoreStatus(mismatchId, ignoreStatus);
    }

    @Override
    public void addIssueId(int mismatchId, String issueId) {
        getReportDao().addIssueId(mismatchId, issueId);
    }

    @Override
    public void deleteIssueId(int mismatchId, String issueId) {
        getReportDao().deleteIssueId(mismatchId, issueId);
    }
}
