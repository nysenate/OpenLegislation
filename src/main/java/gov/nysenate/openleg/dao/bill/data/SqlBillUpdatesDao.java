package gov.nysenate.openleg.dao.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.view.updates.UpdateDigestView;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateDigest;
import gov.nysenate.openleg.model.bill.BillUpdateField;
import gov.nysenate.openleg.model.bill.BillUpdateInfo;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.bill.data.SqlBillUpdatesQuery.*;
import static gov.nysenate.openleg.model.bill.BillUpdateField.*;

@Repository
public class SqlBillUpdatesDao extends SqlBaseDao implements BillUpdatesDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillUpdatesDao.class);

    /**
     * Simple object to hold table names and column values needed to filter by update types.
     */
    private static class BillUpdateTable
    {
        public SqlTable table;
        public List<String> columns;

        public BillUpdateTable(SqlTable table, String... columns) {
            this.table = table;
            this.columns = Arrays.asList(columns);
        }
    }

    private final static Map<BillUpdateField, BillUpdateTable> updateMappings = new HashMap<>();
    static {
        updateMappings.put(ACT_CLAUSE, new BillUpdateTable(SqlTable.BILL, "act_clause"));
        updateMappings.put(ACTION, new BillUpdateTable(SqlTable.BILL_AMENDMENT_ACTION));
        updateMappings.put(ACTIVE_VERSION, new BillUpdateTable(SqlTable.BILL, "active_version"));
        updateMappings.put(APPROVAL, new BillUpdateTable(SqlTable.BILL_APPROVAL));
        updateMappings.put(COSPONSOR, new BillUpdateTable(SqlTable.BILL_AMENDMENT_COSPONSOR));
        updateMappings.put(FULLTEXT, new BillUpdateTable(SqlTable.BILL_AMENDMENT, "full_text"));
        updateMappings.put(LAW_CODE, new BillUpdateTable(SqlTable.BILL_AMENDMENT, "law_code"));
        updateMappings.put(MEMO, new BillUpdateTable(SqlTable.BILL_AMENDMENT, "sponsor_memo"));
        updateMappings.put(MULTISPONSOR, new BillUpdateTable(SqlTable.BILL_AMENDMENT_MULTISPONSOR));
        updateMappings.put(SPONSOR, new BillUpdateTable(SqlTable.BILL_SPONSOR));
        updateMappings.put(STATUS, new BillUpdateTable(SqlTable.BILL, "status"));
        updateMappings.put(SUMMARY, new BillUpdateTable(SqlTable.BILL, "summary"));
        updateMappings.put(TITLE, new BillUpdateTable(SqlTable.BILL, "title"));
        updateMappings.put(VETO, new BillUpdateTable(SqlTable.BILL_VETO));
        updateMappings.put(VOTE, new BillUpdateTable(SqlTable.BILL_AMENDMENT_VOTE_INFO));
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateToken<BaseBillId>> getUpdateTokens(Range<LocalDateTime> dateTimeRange, BillUpdateField filter,
                                                                  SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);
        OrderBy orderBy = new OrderBy("last_update_date_time", dateOrder);

        String sqlQuery = SELECT_BILLS_UPDATED_DURING.getSql(schema(), orderBy, limOff);
        sqlQuery = queryReplace(sqlQuery, "updateFieldFilter", getUpdateFieldFilter(filter));

        PaginatedRowHandler<UpdateToken<BaseBillId>> handler =
            new PaginatedRowHandler<>(limOff, "total_updated", getBillUpdateTokenFromRs);
        jdbcNamed.query(sqlQuery, params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<BaseBillId>> getUpdateDigests(Range<LocalDateTime> dateTimeRange, BillUpdateField filter,
                                                                    SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);
        OrderBy orderBy = new OrderBy("action_date_time", dateOrder);

        String sqlQuery = SELECT_BILLS_UPDATED_DETAILED_DURING.getSql(schema(), orderBy, limOff);
        sqlQuery = queryReplace(sqlQuery, "updateFieldFilter", getUpdateFieldFilter(filter));

        PaginatedRowHandler<UpdateDigest<BaseBillId>> handler =
                new PaginatedRowHandler<>(limOff, "total_updated", getBillUpdateDigestFromRs);
        jdbcNamed.query(sqlQuery, params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public List<UpdateDigest<BaseBillId>> getUpdateDigests(BaseBillId billId, Range<LocalDateTime> dateTimeRange,
                                                           BillUpdateField filter, SortOrder dateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource("printNo", billId.getBasePrintNo())
            .addValue("session", billId.getSession().getYear());
        addDateTimeRangeParams(params, dateTimeRange);

        OrderBy orderBy = new OrderBy("action_date_time", dateOrder);
        String sqlQuery =  SELECT_UPDATES_FOR_BILL.getSql(schema(), orderBy, LimitOffset.ALL);
        sqlQuery = queryReplace(sqlQuery, "updateFieldFilter", getUpdateFieldFilter(filter));

        return jdbcNamed.query(sqlQuery, params, getBillUpdateDigestFromRs);
    }

    /** --- Internal --- */

    /**
     * Generates a sql fragment to be used in the 'where clause' based on the BillUpdateField.
     * E.g. given BillUpdateField.STATUS, it will return something like "table_name = 'bill' AND defined(data, 'status')"
     * which can be plugged into the sql query.
     */
    private String getUpdateFieldFilter(BillUpdateField field) {
        if (field != null && updateMappings.containsKey(field)) {
            BillUpdateTable updateTable = updateMappings.get(field);
            StringBuilder whereClause = new StringBuilder();
            whereClause.append("table_name = '").append(updateTable.table.getTableName()).append("'");
            if (!updateTable.columns.isEmpty()) {
                List<String> existKeys = updateTable.columns.stream()
                    .map(column -> "exist(data, '" + column + "')")
                    .collect(Collectors.toList());
                whereClause.append(" AND ").append(String.join(" AND ", existKeys));
            }
            return whereClause.toString();
        }
        // Return an always true conditional if no filters are specified.
        return "1 = 1";
    }

    private static final RowMapper<UpdateToken<BaseBillId>> getBillUpdateTokenFromRs = (rs, rowNum) ->
        new BillUpdateInfo(new BaseBillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")),
                            getLocalDateTimeFromRs(rs, "last_update_date_time"));

    private static final RowMapper<UpdateDigest<BaseBillId>> getBillUpdateDigestFromRs = (rs, rowNum) -> {
        // The key is the primary key for that table.
        Map<String, String> key = getHstoreMap(rs, "key");
        BaseBillId id = new BaseBillId(key.remove("bill_print_no"), Integer.parseInt(key.remove("bill_session_year")));
        UpdateDigest<BaseBillId> digest = new UpdateDigest<>(id, getLocalDateTimeFromRs(rs, "action_date_time"));
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
    };
}
