package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

import java.time.LocalDateTime;
import java.util.List;

public interface SpotCheckReportService<ContentKey>
{
    public SpotCheckReport<ContentKey> generateReport(boolean priorContext);

    /**
     *
     *
     * @param priorContext
     * @param latestRefDateTime
     * @return
     */
    public SpotCheckReport<ContentKey> generateReport(boolean priorContext, LocalDateTime latestRefDateTime);

    /**
     *
     *
     * @param report
     * @return
     */
    public int saveReport(SpotCheckReport<ContentKey> report);

    /**
     *
     *
     * @param reportId
     * @return
     */
    public SpotCheckReport<ContentKey> getReport(int reportId);

    /**
     *
     *
     * @param dateOrder
     * @param limOff
     * @return
     */
    public List<SpotCheckReport<ContentKey>> getReports(SortOrder dateOrder, LimitOffset limOff);

    /**
     *
     *
     * @param start
     * @param end
     * @param dateOrder
     * @param limOff
     * @return
     */
    public List<SpotCheckReport<ContentKey>> getReports(LocalDateTime start, LocalDateTime end,
                                                        SortOrder dateOrder, LimitOffset limOff);

    /**
     *
     *
     * @param reportId
     */
    public void deleteReport(int reportId);
}
