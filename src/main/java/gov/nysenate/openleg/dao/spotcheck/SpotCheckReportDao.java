package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;

import java.time.LocalDateTime;
import java.util.List;

public interface SpotCheckReportDao<ContentKey>
{
    /**
     *
     *
     * @param id
     * @return
     */
    public SpotCheckReport<ContentKey> getReport(SpotCheckReportId id);

    /**
     *
     *
     * @param refType
     * @param start
     * @param end
     * @param dateOrder
     * @param limOff
     * @return
     */
    public List<SpotCheckReport<ContentKey>> getReports(SpotCheckRefType refType, LocalDateTime start,
                                                        LocalDateTime end, SortOrder dateOrder, LimitOffset limOff);

    /**
     *
     *
     * @param report
     */
    public void saveReport(SpotCheckReport<ContentKey> report);
}
