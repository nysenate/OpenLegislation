package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.spotchecks.model.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReport;

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
     * Get the running mode of the report service.
     *
     * Default to {@link SpotCheckReportRunMode#EVENT_DRIVEN}
     * @return {@link SpotCheckReportRunMode}
     */
    default SpotCheckReportRunMode getRunMode() {
        return SpotCheckReportRunMode.EVENT_DRIVEN;
    }

}
