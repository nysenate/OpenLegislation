package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.dao.base.SqlQueryUtils;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchParameters;
import gov.nysenate.openleg.util.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.calendar.SqlCalendarSearchQuery.*;

@Repository(value = "sqlCalendarSearchDao")
public class SqlCalendarSearchDao extends SqlBaseDao implements CalendarSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlCalendarSearchDao.class);

    /** {@inheritDoc} */
    @Override
    public int getCalendarCountforQuery(CalendarSearchParameters calendarSearchParameters) throws DataAccessException{
        String query = getSqlSearchQuery(calendarSearchParameters, true);
        query = SqlQueryUtils.getSqlWithSchema(query, schema());
        MapSqlParameterSource params = getCalendarSearchParamMap(calendarSearchParameters);
        return jdbcNamed.queryForObject(query, params, Integer.class);
    }

    /** {@inheritDoc} */
    @Override
    public List<CalendarId> getCalendars(CalendarSearchParameters calendarSearchParameters,
                                         OrderBy orderBy, LimitOffset limitOffset)
                                                     throws DataAccessException {
        String query = getSqlSearchQuery(calendarSearchParameters);
        query = SqlQueryUtils.getSqlWithSchema(query, schema(), orderBy, limitOffset);
        MapSqlParameterSource params = getCalendarSearchParamMap(calendarSearchParameters);
        return jdbcNamed.query(query, params, new CalendarIdRowMapper());
    }

    @Override
    public List<CalendarSupplementalId> getFloorCalendars(CalendarSearchParameters calendarSearchParameters,
                                                          OrderBy orderBy, LimitOffset limitOffset)
                                                                       throws DataAccessException {
        String query = getSqlSearchQuery(calendarSearchParameters);
        query = SqlQueryUtils.getSqlWithSchema(query, schema(), orderBy, limitOffset);
        MapSqlParameterSource params = getCalendarSearchParamMap(calendarSearchParameters);
        return jdbcNamed.query(query, params, new CalendarSupplementalIdRowMapper());
    }

    @Override
    public List<CalendarActiveListId> getActiveLists(CalendarSearchParameters calendarSearchParameters,
                                                     OrderBy orderBy, LimitOffset limitOffset)
                                                                        throws DataAccessException {
        String query = getSqlSearchQuery(calendarSearchParameters);
        query = SqlQueryUtils.getSqlWithSchema(query, schema(), orderBy, limitOffset);
        MapSqlParameterSource params = getCalendarSearchParamMap(calendarSearchParameters);
        return jdbcNamed.query(query, params, new CalendarActiveListIdRowMapper());
    }

    private String getSqlSearchQuery(CalendarSearchParameters searchParams) throws IllegalArgumentException {
        return getSqlSearchQuery(searchParams, false);
    }

    private String getSqlSearchQuery(CalendarSearchParameters searchParams, boolean getCount) throws InvalidDataAccessApiUsageException {

        // If the params are invalid, throw an exception
        if (!searchParams.isValid()) {
            throw new InvalidDataAccessApiUsageException("Invalid calendar search parameters:\n" + searchParams);
        }

        StringBuilder queryBuilder = new StringBuilder();

        // shorthand variables for sanity
        boolean billSearch = searchParams.getBillPrintNo() != null;
        boolean billCalNoSearch = searchParams.getBillCalendarNo() != null;
        boolean sectionCodeSearch = searchParams.getSectionCode() != null;

        CalendarType calendarType = searchParams.getCalendarType();
        boolean includeActiveList = calendarType == CalendarType.ACTIVE_LIST || calendarType == CalendarType.ALL;
        boolean includeSupplemental = calendarType == CalendarType.FLOOR || calendarType == CalendarType.ALL;

        // Declare additional sub-queries if searching for bill print numbers, bill calendar numbers, or section codes
        if (billSearch || billCalNoSearch || sectionCodeSearch) {
            queryBuilder.append(WITH);
            if (billSearch) {
                if(includeActiveList) {
                    queryBuilder.append(ACTIVE_LIST_BILL_PRINT_NO_SUBQUERY);
                }
                if(calendarType == CalendarType.ALL) {
                    queryBuilder.append(",");
                }
                if(includeSupplemental) {
                    queryBuilder.append(SUPPLEMENTAL_BILL_PRINT_NO_SUBQUERY);
                }
                if (billCalNoSearch || sectionCodeSearch) {
                    queryBuilder.append(",");
                }
            }
            if (billCalNoSearch) {
                if(includeActiveList) {
                    queryBuilder.append(ACTIVE_LIST_CALENDAR_NO_SUBQUERY);
                }
                if(calendarType == CalendarType.ALL) {
                    queryBuilder.append(",");
                }
                if(includeSupplemental) {
                    queryBuilder.append(SUPPLEMENTAL_CALENDAR_NO_SUBQUERY);
                }
                if (sectionCodeSearch) {
                    queryBuilder.append(",");
                }
            }
            if (sectionCodeSearch && includeSupplemental) {
                queryBuilder.append(SECTION_CODE_SUBQUERY);
            }
            queryBuilder.append("\n");
        }

        // Select a SELECT statement
        if(getCount) {
            queryBuilder.append(SELECT_COUNT);
        }
        else {
            switch (calendarType) {
                case ALL: queryBuilder.append(SELECT_BASE); break;
                case ACTIVE_LIST: queryBuilder.append(SELECT_ACTIVE_LIST); break;
                case FLOOR: queryBuilder.append(SELECT_SUPPLEMENTAL); break;
            }
        }

        // Add table sources

        // Generate calendar type sub queries
        String activeListSubQuery = "";
        String supplementalSubQuery = "";
        if (includeActiveList) {        // Active List sub query
            StringBuilder activeListQueryBuilder = new StringBuilder();
            // Select statement
            activeListQueryBuilder.append(ACTIVE_LIST_BASE_SELECT);
            if (searchParams.getCalendarType() == CalendarType.ACTIVE_LIST) {
                activeListQueryBuilder.append(ACTIVE_LIST_SEQUENCE_NO_SELECTOR);
            }
            if (searchParams.getDateRange() != null) {
                activeListQueryBuilder.append(ACTIVE_LIST_DATE_SELECTOR);
            }
            if (billSearch) {
                activeListQueryBuilder.append(ACTIVE_LIST_PRINT_NO_SELECTOR);
            }
            if (billCalNoSearch) {
                activeListQueryBuilder.append(ACTIVE_LIST_BILL_CALENDAR_NO_SELECTOR);
            }
            // From
            activeListQueryBuilder.append(ACTIVE_LIST_FROM);
            // Joins
            if (billSearch) {
                activeListQueryBuilder.append(ACTIVE_LIST_PRINT_NO_JOIN);
            }
            if (billCalNoSearch) {
                activeListQueryBuilder.append(ACTIVE_LIST_BILL_CALENDAR_NO_JOIN);
            }
            activeListSubQuery = activeListQueryBuilder.toString();
        }
        if (includeSupplemental) {      // Supplemental sub query
            StringBuilder supplementalQueryBuilder = new StringBuilder();
            // Select statement
            supplementalQueryBuilder.append(SUPPLEMENTAL_BASE_SELECT);
            if (searchParams.getCalendarType() == CalendarType.FLOOR) {
                supplementalQueryBuilder.append(SUPPLEMENTAL_VERSION_SELECTOR);
            }
            if (searchParams.getDateRange() != null) {
                supplementalQueryBuilder.append(SUPPLEMENTAL_DATE_SELECTOR);
            }
            if (billSearch) {
                supplementalQueryBuilder.append(SUPPLEMENTAL_PRINT_NO_SELECTOR);
            }
            if (billCalNoSearch) {
                supplementalQueryBuilder.append(SUPPLEMENTAL_BILL_CALENDAR_NO_SELECTOR);
            }
            if (sectionCodeSearch) {
                supplementalQueryBuilder.append(SUPPLEMENTAL_SECTION_CODE_SELECTOR);
            }
            // From
            supplementalQueryBuilder.append(SUPPLEMENTAL_FROM);
            // Joins
            if (billSearch) {
                supplementalQueryBuilder.append(SUPPLEMENTAL_PRINT_NO_JOIN);
            }
            if (billCalNoSearch) {
                supplementalQueryBuilder.append(SUPPLEMENTAL_BILL_CALENDAR_NO_JOIN);
            }
            if (sectionCodeSearch) {
                supplementalQueryBuilder.append(SUPPLEMENTAL_BILL_SECTION_CODE_JOIN);
            }
            supplementalSubQuery = supplementalQueryBuilder.toString();
        }

        // Add subqueries as a table source

        switch (calendarType) {
            case ALL:
                queryBuilder.append(String.format(FROM_UNION.getSql(),
                                        activeListSubQuery, supplementalSubQuery));
                break;
            case ACTIVE_LIST:
                queryBuilder.append(String.format(FROM_SINGLE_SOURCE.getSql(),
                                        activeListSubQuery));
                break;
            case FLOOR:
                queryBuilder.append(String.format(FROM_SINGLE_SOURCE.getSql(),
                                        supplementalSubQuery));
                break;
        }

        // Add a WHERE clause if there are any search parameters
        if (searchParams.paramCount() > 0) {
            queryBuilder.append(WHERE);
            int remainingParams = searchParams.paramCount();
            if (searchParams.getYear() != null) {
                queryBuilder.append(WHERE_YEAR);
                conjunctionQueryHelper(--remainingParams, AND.getSql(), queryBuilder);
            }
            if (searchParams.getDateRange() != null) {
                queryBuilder.append("\n\t");
                queryBuilder.append(WHERE_DATE_RANGE);
                conjunctionQueryHelper(--remainingParams, AND.getSql(), queryBuilder);
            }
            if (searchParams.getBillPrintNo() != null && searchParams.getBillPrintNo().size() > 0) {
                queryBuilder.append("\n\t( ");
                int remainingBillSets = searchParams.getBillPrintNo().keySet().size();

                for (Integer setId : searchParams.getBillPrintNo().keySet()) {
                    queryBuilder.append(String.format(WHERE_PRINT_NOS.getSql(), setId));
                    conjunctionQueryHelper(--remainingBillSets, OR.getSql(), queryBuilder);
                }
                queryBuilder.append(") ");
                conjunctionQueryHelper(--remainingParams, AND.getSql(), queryBuilder);
            }
            if (searchParams.getBillCalendarNo() != null && searchParams.getBillCalendarNo().size() > 0) {
                queryBuilder.append("\n\t( ");
                int remainingCalNoSets = searchParams.getBillCalendarNo().keySet().size();
                for (Integer setId : searchParams.getBillCalendarNo().keySet()) {
                    queryBuilder.append(String.format(WHERE_BILL_CAL_NOS.getSql(), setId));
                    conjunctionQueryHelper(--remainingCalNoSets, OR.getSql(), queryBuilder);
                }
                queryBuilder.append(") ");
                conjunctionQueryHelper(--remainingParams, AND.getSql(), queryBuilder);
            }
            if (searchParams.getSectionCode() != null && searchParams.getSectionCode().size() > 0) {
                queryBuilder.append("\n\t( ");
                int remainingSCodeSets = searchParams.getSectionCode().keySet().size();
                for (Integer setId : searchParams.getSectionCode().keySet()) {
                    queryBuilder.append(String.format(WHERE_SECTION_CODE.getSql(), setId));
                    conjunctionQueryHelper(--remainingSCodeSets, OR.getSql(), queryBuilder);
                }
                queryBuilder.append(") ");
                conjunctionQueryHelper(--remainingParams, AND.getSql(), queryBuilder);
            }
        }

        logger.debug(queryBuilder.toString());

        return queryBuilder.toString();
    }

    /**
     * Used to aid generation of the conjunctions in the getSqlSearchQuery
     * appends the specified conjunction to the query if remainingItems is positive
     */
    private void conjunctionQueryHelper(int remainingItems, String conjunction, StringBuilder queryBuilder) {
        if (remainingItems > 0) {
            queryBuilder.append(conjunction);
        }
    }

    /** --- Row Mappers --- */

    private class CalendarIdRowMapper implements RowMapper<CalendarId> {
        @Override
        public CalendarId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarId(rs.getInt("year"), rs.getInt("calendar_no"));
        }
    }

    private class CalendarActiveListIdRowMapper implements RowMapper<CalendarActiveListId> {
        @Override
        public CalendarActiveListId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarActiveListId(rs.getInt("year"), rs.getInt("calendar_no"), rs.getInt("al_sequence_no"));
        }
    }

    private class CalendarSupplementalIdRowMapper implements RowMapper<CalendarSupplementalId> {
        @Override
        public CalendarSupplementalId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarSupplementalId(rs.getInt("year"), rs.getInt("calendar_no"), Version.of(rs.getString("sup_version")));
        }
    }

    /** --- Parameter Mappers --- */

    private MapSqlParameterSource getCalendarSearchParamMap(CalendarSearchParameters searchParams) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (searchParams.getYear() != null) {
            params.addValue("year", searchParams.getYear());
        }
        if (searchParams.getDateRange() != null) {
            params.addValue("dateRangeStart", DateUtils.toDate(DateUtils.startOfDateRange(searchParams.getDateRange())));
            params.addValue("dateRangeEnd", DateUtils.toDate(DateUtils.endOfDateRange(searchParams.getDateRange())));
        }
        if (searchParams.getBillPrintNo() != null) {
            params.addValue("billSessionYear", searchParams.getBillPrintNoSession().getYear());
            Set<String> allBillIds = new HashSet<>();
            for(Integer setId : searchParams.getBillPrintNo().keySet()) {
                Set<String> printNos = searchParams.getBillPrintNo().get(setId).parallelStream()
                        .map(BillId::getBasePrintNo)
                        .collect(Collectors.toSet());
                params.addValue("billSet" + setId, printNos);
                allBillIds.addAll(printNos);
            }
            params.addValue("allBills", allBillIds);
        }
        if (searchParams.getBillCalendarNo() != null) {
            Set<Integer> allCalendarNos = new HashSet<>();
            for(Integer setId : searchParams.getBillCalendarNo().keySet()) {
                params.addValue("calNoSet" + setId, searchParams.getBillCalendarNo().get(setId));
                allCalendarNos.addAll(searchParams.getBillCalendarNo().get(setId));
            }
            params.addValue("allBillCalNos", allCalendarNos);
        }
        if (searchParams.getSectionCode() != null) {
            Set<Integer> allSectionCodes = new HashSet<>();
            for(Integer setId : searchParams.getSectionCode().keySet()) {
                params.addValue("codeSet" + setId, searchParams.getSectionCode().get(setId));
                allSectionCodes.addAll(searchParams.getSectionCode().get(setId));
            }
            params.addValue("allSectionCodes", allSectionCodes);
        }
        return params;
    }

}
