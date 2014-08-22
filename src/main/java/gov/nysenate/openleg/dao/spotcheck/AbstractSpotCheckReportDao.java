package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.dao.spotcheck.SqlSpotCheckReportQuery.*;

public abstract class AbstractSpotCheckReportDao<ContentKey> extends SqlBaseDao
                                                             implements SpotCheckReportDao<ContentKey>
{
    /** --- Abstract Methods --- */

    /**
     * Subclasses should implement this conversion from a Map containing certain key/val pairs to
     * an instance of ContentKey. This is needed since the keys are stored as an hstore in the
     * database.
     *
     * @param keyMap Map<String, String>
     * @return ContentKey
     */
    public abstract ContentKey getKeyFromMap(Map<String, String> keyMap);

    /**
     * Subclasses should implement a conversion from an instance of ContentKey to a Map of
     * key/val pairs that fully represent that ContentKey.
     *
     * @param key ContentKey
     * @return Map<String, String>
     */
    public abstract Map<String, String> getMapFromKey(ContentKey key);

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<ContentKey> getReport(SpotCheckReportId id) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<SpotCheckReport<ContentKey>> getReports(SpotCheckRefType refType, LocalDateTime start, LocalDateTime end,
                                                        SortOrder dateOrder, LimitOffset limOff) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void saveReport(SpotCheckReport<ContentKey> report) {
        if (report == null) {
            throw new IllegalArgumentException("Supplied report cannot be null.");
        }

        ImmutableParams reportParams = ImmutableParams.from(getReportIdParams(report));
//        jdbcNamed.update(INSERT_REPORT)


    }

    private MapSqlParameterSource getReportIdParams(SpotCheckReport<ContentKey> report) {
        return new MapSqlParameterSource()
            .addValue("referenceType", report.getReferenceType().name())
            .addValue("reportDateTime", report.getReportDateTime());
    }
}
