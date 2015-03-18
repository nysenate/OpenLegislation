package gov.nysenate.openleg.dao.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateField;
import gov.nysenate.openleg.model.updates.UpdateContentType;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.bill.data.SqlBillUpdatesQuery.SELECT_BILL_UPDATE_DIGESTS;
import static gov.nysenate.openleg.dao.bill.data.SqlBillUpdatesQuery.SELECT_BILL_UPDATE_TOKENS;
import static gov.nysenate.openleg.dao.bill.data.SqlBillUpdatesQuery.SELECT_UPDATE_DIGESTS_FOR_SPECIFIC_BILL;
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
        updateMappings.put(PUBLISHED_BILL, new BillUpdateTable(SqlTable.BILL, "published_date_time"));
        updateMappings.put(ACT_CLAUSE, new BillUpdateTable(SqlTable.BILL_AMENDMENT, "act_clause"));
        updateMappings.put(ACTION, new BillUpdateTable(SqlTable.BILL_AMENDMENT_ACTION));
        updateMappings.put(ACTIVE_VERSION, new BillUpdateTable(SqlTable.BILL, "active_version"));
        updateMappings.put(APPROVAL, new BillUpdateTable(SqlTable.BILL_APPROVAL));
        updateMappings.put(COSPONSOR, new BillUpdateTable(SqlTable.BILL_AMENDMENT_COSPONSOR));
        updateMappings.put(FULLTEXT, new BillUpdateTable(SqlTable.BILL_AMENDMENT, "full_text"));
        updateMappings.put(LAW, new BillUpdateTable(SqlTable.BILL_AMENDMENT, "law_code", "law_section"));
        updateMappings.put(MEMO, new BillUpdateTable(SqlTable.BILL_AMENDMENT, "sponsor_memo"));
        updateMappings.put(MULTISPONSOR, new BillUpdateTable(SqlTable.BILL_AMENDMENT_MULTISPONSOR));
        updateMappings.put(SPONSOR, new BillUpdateTable(SqlTable.BILL_SPONSOR));
        updateMappings.put(STATUS, new BillUpdateTable(SqlTable.BILL, "status", "status_date", "bill_cal_no",
                                                                      "committee_name", "committee_chamber"));
        updateMappings.put(STATUS_CODE, new BillUpdateTable(SqlTable.BILL, "status"));
        updateMappings.put(SUMMARY, new BillUpdateTable(SqlTable.BILL, "summary"));
        updateMappings.put(TITLE, new BillUpdateTable(SqlTable.BILL, "title"));
        updateMappings.put(VETO, new BillUpdateTable(SqlTable.BILL_VETO));
        updateMappings.put(VOTE, new BillUpdateTable(SqlTable.BILL_AMENDMENT_VOTE_INFO));
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateToken<BaseBillId>> getUpdates(Range<LocalDateTime> dateTimeRange, UpdateType type,
                                                             BillUpdateField filter, SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);

        String sqlQuery = getSqlQuery(false, null, type, filter, dateOrder, limOff);
        PaginatedRowHandler<UpdateToken<BaseBillId>> handler =
            new PaginatedRowHandler<>(limOff, "total_updated", getBillUpdateTokenFromRs);
        jdbcNamed.query(sqlQuery, params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<BaseBillId>> getDetailedUpdates(Range<LocalDateTime> dateTimeRange, UpdateType type,
                                                                      BillUpdateField filter, SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);

        String sqlQuery = getSqlQuery(true, null, type, filter, dateOrder, limOff);
        PaginatedRowHandler<UpdateDigest<BaseBillId>> handler =
            new PaginatedRowHandler<>(limOff, "total_updated", new BillUpdateDigestMapper(filter));
        jdbcNamed.query(sqlQuery, params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<BaseBillId>> getDetailedUpdatesForBill(
            BaseBillId billId, Range<LocalDateTime> dateTimeRange, UpdateType type, BillUpdateField filter,
            SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource("printNo", billId.getBasePrintNo())
                                                       .addValue("session", billId.getSession().getYear());
        addDateTimeRangeParams(params, dateTimeRange);

        String sqlQuery = getSqlQuery(true, billId, type, filter, dateOrder, limOff);
        PaginatedRowHandler<UpdateDigest<BaseBillId>> handler =
                new PaginatedRowHandler<>(limOff, "total_updated", new BillUpdateDigestMapper(filter));
        jdbcNamed.query(sqlQuery, params, handler);
        return handler.getList();
    }

    /** --- Internal --- */

    /**
     * Generates the appropriate sql query based on the args, to remove code duplication.
     */
    private String getSqlQuery(boolean detail, BaseBillId billId, UpdateType updateType, BillUpdateField fieldFilter,
                               SortOrder sortOrder, LimitOffset limOff) {
        String dateColumn = getDateColumnForUpdateType(updateType);
        OrderBy orderBy = getOrderByForUpdateType(updateType, sortOrder);
        String sqlQuery;
        if (billId != null) {
            sqlQuery = SELECT_UPDATE_DIGESTS_FOR_SPECIFIC_BILL.getSql(schema(), orderBy, limOff);
        }
        else {
            sqlQuery = (detail) ? SELECT_BILL_UPDATE_DIGESTS.getSql(schema(), orderBy, limOff)
                                : SELECT_BILL_UPDATE_TOKENS.getSql(schema(), orderBy, limOff);
        }
        sqlQuery = queryReplace(sqlQuery, "dateColumn", dateColumn);
        sqlQuery = queryReplace(sqlQuery, "updateFieldFilter", getUpdateFieldFilter(fieldFilter));
        return sqlQuery;
    }

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
                whereClause.append(" AND ").append("( ").append(String.join(" OR ", existKeys)).append(") ");
            }
            return whereClause.toString();
        }
        // Return an always true conditional if no filters are specified.
        return "1 = 1";
    }

    /** --- Row Mappers -- */

    private static final RowMapper<UpdateToken<BaseBillId>> getBillUpdateTokenFromRs = (rs, rowNum) ->
        new UpdateToken<>(new BaseBillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")), UpdateContentType.BILL,
            rs.getString("last_fragment_id"), getLocalDateTimeFromRs(rs, "last_published_date_time"),
            getLocalDateTimeFromRs(rs, "last_processed_date_time"));

    private static class BillUpdateDigestMapper implements RowMapper<UpdateDigest<BaseBillId>> {

        private BillUpdateField fieldFilter;

        public BillUpdateDigestMapper(BillUpdateField fieldFilter) {
            this.fieldFilter = fieldFilter;
        }

        @Override
        public UpdateDigest<BaseBillId> mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Construct the digest model
            UpdateDigest<BaseBillId> digest = new UpdateDigest<>(getBillUpdateTokenFromRs.mapRow(rs, rowNum));
            // Filter out the data values depending on the filter
            Map<String, String> data = getHstoreMap(rs, "data");
            if (fieldFilter != null) {
                BillUpdateTable updateTable = updateMappings.get(fieldFilter);
                if (updateTable != null && !updateTable.columns.isEmpty()) {
                    Set<String> columnSet = new HashSet<>(updateTable.columns);
                    data.keySet().retainAll(
                        data.keySet().stream().filter(col -> columnSet.contains(col)).collect(Collectors.toSet()));
                }
            }
            digest.setAction(rs.getString("action"));
            digest.setTable(rs.getString("table_name"));
            digest.setFields(data);
            return digest;
        }
    }
}
