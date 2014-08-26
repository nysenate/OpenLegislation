package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportNotFoundEx;

import java.time.LocalDateTime;
import java.util.List;

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
     * Generate a SpotCheckReport across all available data. The reference data used for
     * comparison will be the most recent data prior to 'latestRefDateTime'. Note that
     * this report will not have knowledge of prior reports.
     *
     * @param start LocalDateTime - The reference data will be active after (or on) this date/time.
     * @param end LocalDateTime - The reference data will be active prior to (or on) this date/time.
     * @return SpotCheckReport<ContentKey>
     * @throws ReferenceDataNotFoundEx - If there is no reference data that can be used for this report.
     */
    public SpotCheckReport<ContentKey> generateReport(LocalDateTime start, LocalDateTime end)
                                                      throws ReferenceDataNotFoundEx;

    /**
     * Saves the report into the backing store. The report will be saved such that mismatches from
     * prior reports are taken into account when setting the statuses. Any exception encountered when
     * saving this will propagate through.
     *
     * @param report SpotCheckReport<ContentKey> - The report to save in the backing store.
     */
    public void saveReport(SpotCheckReport<ContentKey> report);

    /**
     * Obtain a SpotCheckReport from the backing store using the report id. The report obtained
     * by {@link #generateReport} differs from the same report returned via this method in that this
     * report will only contain observations that have mismatches and will also have data that's been
     * associated with the context of prior reports that have been saved.
     *
     * @param reportId SpotCheckReportId - Retrieve a previously saved report by id.
     * @return SpotCheckReport<ContentKey>
     */
    public SpotCheckReport<ContentKey> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx;

    /**
     * Return a list of saved report ids with options to filter the result set.
     *
     * @param start LocalDateTime - The earliest report date (inclusive)
     * @param end LocalDateTime - The latest report date (inclusive)
     * @param dateOrder SortOrder - Order the reports by report date
     * @param limOff LimitOffset - Limit/Offset the result set
     * @return List<SpotCheckReport<ContentKey>> - List of report ids
     */
    public List<SpotCheckReportId> getReportIds(LocalDateTime start, LocalDateTime end,
                                                SortOrder dateOrder, LimitOffset limOff);

    /**
     * Wipe a report as well as all of its associated observations and mismatches from the backing store.
     *
     * @param reportId SpotCheckReportId - Delete a previously saved report by id.
     */
    public void deleteReport(SpotCheckReportId reportId);
}
