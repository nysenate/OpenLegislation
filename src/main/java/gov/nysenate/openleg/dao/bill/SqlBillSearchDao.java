package gov.nysenate.openleg.dao.bill;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchResult;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.search.BillSearchField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

import static gov.nysenate.openleg.dao.bill.SqlBillSearchQuery.*;
import static gov.nysenate.openleg.service.bill.search.BillSearchField.*;

/**
 * This bill search implementation utilizes Postgres' full text search functions.
 * The reasoning behind using the in-database search as opposed to an external search service such as
 * ElasticSearch or SOLR is because the data in the search layer can easily be in sync with our master
 * data and Postgres FTS offers the essentials we need such as ranking.
 *
 * There is a separate search schema with triggers from the master schema set to propagate updates
 * from individual bill tables to their search counterpart.
 *
 * Refer to http://www.postgresql.org/docs/9.3/static/textsearch.html to better understand how to
 * perform full text searches.
 */
@Repository(value = "dbBillSearch")
public class SqlBillSearchDao extends SqlBaseDao implements BillSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillSearchDao.class);

    private static Map<BillSearchField, SqlTable> tableMap = new HashMap<>();
    private static Map<BillSearchField, String> columnMap = new HashMap<>();
    private static Map<SqlTable, Boolean> versionSpecific = new HashMap<>();

    /** Join statement for an additional search table, label the tables t2, t3, etc, for ease of use. */
    private static String tableJoin = "JOIN ${search_schema}.${search_table} AS t${table_num}\n";

    /** Sql fragment to join a search table to the bill info search table. */
    private static String joinOnBill = tableJoin +
        "ON t1.print_no = t${table_num}.bill_print_no AND t1.session_year = t${table_num}.bill_session_year\n";

    /** Sql fragment to join a search table to the bill amendment search table. */
    private static String joinOnAmend = tableJoin +
        "ON t2.bill_print_no = t${table_num}.bill_print_no AND t2.bill_session_year = t${table_num}.bill_session_year\n" +
        "AND t2.version = t${table_num}.bill_amend_version\n";


    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public SearchResults<BillId> searchAll(String query, LimitOffset limOff) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("query", query));
        OrderBy orderByRank = new OrderBy("rank", SortOrder.DESC);
        Integer resultCount = jdbcNamed.queryForObject(
                COUNT_GLOBAL_BASE_BILL.getSql(schema(), searchSchema()), params, Integer.class);
        List<SearchResult<BillId>> resultList = new ArrayList<>();
        if (resultCount > 0) {
            resultList = jdbcNamed.query(SEARCH_GLOBAL_BASE_BILL.getSql(schema(), searchSchema(), orderByRank, limOff), params,
                                         billIdSearchResultMapper);
        }
        return new SearchResults<>(resultCount, resultList, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BillId> searchAdvanced(Map<BillSearchField, String> query, LimitOffset limOff) {
        // Maintain a list of the tables that are involved in the query.
        Set<SqlTable> defaultSearchTables = Sets.newHashSet(SqlTable.BILL_INFO_SEARCH, SqlTable.BILL_AMENDMENT_SEARCH);
        Set<SqlTable> addtlSearchTables = Sets.newHashSet();

        // Also maintain a mapping of the column names and the queries associated with them.
        Map<String, String> columnQueries = new HashMap<>();

        // Determine the tables and the columns within those tables that are going to be referenced by this search.
        query.forEach((searchField, q) -> {
            if (!defaultSearchTables.contains(tableMap.get(searchField))) {
                addtlSearchTables.add(tableMap.get(searchField));
            }
            columnQueries.put(columnMap.get(searchField), q);
        });

        // Dynamically construct the search query
        Map<String, String> replacementMap = generateQueryMap(addtlSearchTables, columnQueries);

        String advSearchCount = StrSubstitutor.replace(COUNT_ADVANCED_BASE_BILL.getSql(), replacementMap);
        Integer resultCount = jdbc.queryForObject(SqlQueryUtils.getSqlWithSchema(advSearchCount, schema(), searchSchema()),
            columnQueries.values().toArray(), Integer.class);

        List<SearchResult<BillId>> resultList = new ArrayList<>();
        if (resultCount > 0) {
            String advancedSearch = StrSubstitutor.replace(SEARCH_ADVANCED_BASE_BILL.getSql(), replacementMap);
            OrderBy orderBy = new OrderBy("rank", SortOrder.DESC);
            resultList = jdbc.query(SqlQueryUtils.getSqlWithSchema(advancedSearch, schema(), searchSchema(), orderBy, limOff),
                    columnQueries.values().toArray(), billIdSearchResultMapper);
        }
        return new SearchResults<>(resultCount, resultList, limOff);
    }

    /** --- Internal Methods --- */

    /**
     * Since the base advanced search query doesn't perform a join by default on every bill search table, we have
     * to dynamically add the joins to the query. The 'addtlSearchTables' set should contain all the search
     * tables that are required for this search that are not already in the default query. The join conditions
     * are determined based on whether or not the table references a base bill or an amendment.
     *
     * @param addtlSearchTables Set<SqlTable>
     * @return String
     */
    protected String getAdditionalJoins(Set<SqlTable> addtlSearchTables) {
        int tableNum = 3;  // The first two tables are already provided by the base query
        StringBuilder joinQuery = new StringBuilder();
        for (SqlTable table : addtlSearchTables) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("search_table", table.getTableName());
            replacements.put("table_num", Integer.toString(tableNum));
            joinQuery.append(StrSubstitutor.replace((versionSpecific.get(table)) ? joinOnAmend : joinOnBill, replacements));
            tableNum++;
        }
        return joinQuery.toString();
    }

    /**
     *
     *
     * @param addtlSearchTables Set<SqlTable>
     * @param columnQueries Map<String, String>
     * @return Map<String, String>
     */
    protected Map<String, String> generateQueryMap(Set<SqlTable> addtlSearchTables, Map<String, String> columnQueries) {
        String joinQuery = getAdditionalJoins(addtlSearchTables);

        int queryNum = 1;
        List<String> queryConcatList = new ArrayList<>(),  // The ts_queries 'e.g. plainto_tsquery('search string')
                                                           // need to be concatenated '||' for ranking purposes.
                     queryDeclareList = new ArrayList<>(), // The ts_queries need to be declared in the FROM clause
                     whereList = new ArrayList<>();        // The actual filtering clause, take the search vector and
                                                           // perform the '@@' operation to find matches.
        for (Map.Entry<String, String> queryEntry : columnQueries.entrySet()) {
            queryConcatList.add("q" + queryNum);
            queryDeclareList.add("plainto_tsquery(?) AS q" + queryNum);
            whereList.add(queryEntry.getKey() + " @@ q" + queryNum);
            queryNum++;
        }

        // The replacement map is used to fill in the templated portions of the base SQL query.
        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.put("vectors", StringUtils.join(columnQueries.keySet(), " || "));
        replacementMap.put("queries", StringUtils.join(queryConcatList, " && "));
        replacementMap.put("additional_joins", joinQuery);
        replacementMap.put("queries_declaration",
            (!queryDeclareList.isEmpty()) ? ", " + StringUtils.join(queryDeclareList, ",") : "");
        replacementMap.put("filter_clause", StringUtils.join(whereList, " AND "));
        return replacementMap;
    }

    /** --- Static Mappings --- */

    /** Maps each result to a SearchResult containing the BillId and the rank. Does NOT include version. */
    protected static RowMapper<SearchResult<BillId>> billIdSearchResultMapper = (rs, row) -> {
        BillId billId = new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"));
        return new SearchResult<>(billId, rs.getBigDecimal("rank"));
    };

    /** Map the search fields to the search table that contains the data. */
    static {
        tableMap.put(SPONSOR, SqlTable.BILL_SPONSOR_SEARCH);
        tableMap.put(TITLE, SqlTable.BILL_INFO_SEARCH);
        tableMap.put(SUMMARY, SqlTable.BILL_INFO_SEARCH);
        tableMap.put(MEMO, SqlTable.BILL_INFO_SEARCH);
        tableMap.put(LAW_SECTION, SqlTable.BILL_INFO_SEARCH);
        tableMap.put(LAW_CODE, SqlTable.BILL_INFO_SEARCH);
        tableMap.put(PROGRAM_INFO, SqlTable.BILL_INFO_SEARCH);
        tableMap.put(COMMITTEE_NAME, SqlTable.BILL_INFO_SEARCH);
        tableMap.put(COSPONSORS, SqlTable.BILL_COSPONSOR_SEARCH);
        tableMap.put(MULTISPONSORS, SqlTable.BILL_MULTISPONSOR_SEARCH);
        tableMap.put(ACT_CLAUSE, SqlTable.BILL_AMENDMENT_SEARCH);
        tableMap.put(FULLTEXT, SqlTable.BILL_AMENDMENT_SEARCH);
        tableMap.put(ACTIONS, SqlTable.BILL_ACTION_SEARCH);
        tableMap.put(VOTE_INFO, SqlTable.BILL_AMEND_VOTE_SEARCH);
    }

    /** Map the search fields to the column that contains the data. */
    static {
        columnMap.put(SPONSOR, "sponsor");
        columnMap.put(TITLE, "title");
        columnMap.put(SUMMARY, "summary");
        columnMap.put(MEMO, "memo");
        columnMap.put(LAW_SECTION, "law_section");
        columnMap.put(LAW_CODE, "law_code");
        columnMap.put(PROGRAM_INFO, "program_info");
        columnMap.put(COMMITTEE_NAME, "committee_name");
        columnMap.put(COSPONSORS, "cosponsors");
        columnMap.put(MULTISPONSORS, "multisponsors");
        columnMap.put(ACT_CLAUSE, "act_clause");
        columnMap.put(FULLTEXT, "full_text");
        columnMap.put(ACTIONS, "actions");
        columnMap.put(VOTE_INFO, "vote_info");
    }

    /** Identify which search tables are keyed by an amendment version. */
    static {
        versionSpecific.put(SqlTable.BILL_INFO_SEARCH, false);
        versionSpecific.put(SqlTable.BILL_SPONSOR_SEARCH, false);
        versionSpecific.put(SqlTable.BILL_ACTION_SEARCH, false);
        versionSpecific.put(SqlTable.BILL_FULL_SEARCH, true);
        versionSpecific.put(SqlTable.BILL_AMEND_VOTE_SEARCH, true);
        versionSpecific.put(SqlTable.BILL_AMENDMENT_SEARCH, true);
        versionSpecific.put(SqlTable.BILL_COSPONSOR_SEARCH, true);
        versionSpecific.put(SqlTable.BILL_MULTISPONSOR_SEARCH, true);
    }
}