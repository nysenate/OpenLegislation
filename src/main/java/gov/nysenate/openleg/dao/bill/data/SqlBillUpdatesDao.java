package gov.nysenate.openleg.dao.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateDigest;
import gov.nysenate.openleg.model.bill.BillUpdateToken;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.dao.bill.data.SqlBillUpdatesQuery.SELECT_BILLS_UPDATED_DURING;
import static gov.nysenate.openleg.dao.bill.data.SqlBillUpdatesQuery.SELECT_UPDATES_FOR_BILL;

@Repository
public class SqlBillUpdatesDao extends SqlBaseDao implements BillUpdatesDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillUpdatesDao.class);

    @Override
    public PaginatedList<BillUpdateToken> billsUpdatedDuring(Range<LocalDateTime> dateTimeRange, SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);
        OrderBy orderBy = new OrderBy("last_update_date_time", dateOrder);
        UpdateTokenListHandler handler = new UpdateTokenListHandler();
        jdbcNamed.query(SELECT_BILLS_UPDATED_DURING.getSql(schema(), orderBy, limOff), params, handler);
        return handler.getPaginatedList(limOff);
    }

    /** {@inheritDoc} */
    @Override
    public List<BillUpdateDigest> getUpdateDigests(BaseBillId billId, Range<LocalDateTime> dateTimeRange, SortOrder dateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", billId.getBasePrintNo())
              .addValue("session", billId.getSession().getYear());
        addDateTimeRangeParams(params, dateTimeRange);

        OrderBy orderBy = new OrderBy("action_date_time", dateOrder);
        return jdbcNamed.query(SELECT_UPDATES_FOR_BILL.getSql(schema(), orderBy, LimitOffset.ALL), params, (rs, rowNum) -> {
            // The key is the primary key for that table.
            Map<String, String> key = getHstoreMap(rs, "key");
            BaseBillId id = new BaseBillId(key.remove("bill_print_no"), Integer.parseInt(key.remove("bill_session_year")));
            BillUpdateDigest digest = new BillUpdateDigest(id, getLocalDateTimeFromRs(rs, "action_date_time"));
            Map<String, String> data = getHstoreMap(rs, "data");
            //We want to move over the non-bill id values (which were just removed) over into the data map.
            data.putAll(key);

            digest.setAction(rs.getString("action"));
            String fragmentId = rs.getString("sobi_fragment_id");
            digest.setSourceDataId(fragmentId);
            // Parse the date from the id instead of doing a costly sql join
            digest.setSourceDataDateTime(getLocalDateTimeFromSobiFragmentId(fragmentId));
            digest.setTable(rs.getString("table_name"));
            digest.setUpdates(data);
            return digest;
        });
    }

    /** --- Internal --- */

    private static class UpdateTokenListHandler implements RowCallbackHandler {
        private List<BillUpdateToken> tokens = new ArrayList<>();
        private int totalUpdated = 0;

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (totalUpdated == 0) {
                totalUpdated = rs.getInt("total_updated");
            }
            tokens.add(getBillUpdateTokenFromRs.mapRow(rs, 0));
        }

        public PaginatedList<BillUpdateToken> getPaginatedList(LimitOffset limOff) {
            return new PaginatedList<>(totalUpdated, limOff, tokens);
        }
    }

    private static final RowMapper<BillUpdateToken> getBillUpdateTokenFromRs = (rs, rowNum) ->
        new BillUpdateToken(new BaseBillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")),
                            getLocalDateTimeFromRs(rs, "last_update_date_time"));

    private void addDateTimeRangeParams(MapSqlParameterSource params, Range<LocalDateTime> dateTimeRange) {
        params.addValue("startDateTime", DateUtils.toDate(DateUtils.startOfDateTimeRange(dateTimeRange)))
              .addValue("endDateTime", DateUtils.toDate(DateUtils.endOfDateTimeRange(dateTimeRange)));
    }
}
