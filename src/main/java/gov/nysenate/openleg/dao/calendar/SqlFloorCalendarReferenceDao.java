package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.model.spotcheck.calendar.FloorCalendarSpotcheckReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

/**
 * Created by kyle on 10/24/14.
 */
public class SqlFloorCalendarReferenceDao extends SqlBaseDao implements FloorCalendarReferenceDAO{
    private static final Logger logger = LoggerFactory.getLogger(SqlFloorCalendarReferenceDao.class);


    // todo fix
    @Override
    public FloorCalendarSpotcheckReference getFCR(CalendarSupplementalId cal, SpotCheckReferenceId spot) {
/*
        ImmutableParams params = ImmutableParams.from(getCalendarSupplementalIdParams(cal));
        CalendarSupRowHandler calendarSupRowHandler = new CalendarSupRowHandler();
        jdbcNamed.query(SELECT_CALENDAR_SUP.getSql(schema()), params, calendarSupRowHandler);
        try {
            return calendarSupRowHandler.getCalendarSupplementals().get(0);
        }
        catch (IndexOutOfBoundsException ex) {
            throw new EmptyResultDataAccessException(1);
        }*/
        return null;
    }

    protected static MapSqlParameterSource getCalendarSupplementalIdParams(CalendarSupplementalId calendarSupplementalId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(calendarSupplementalId, params);
        params.addValue("supVersion", calendarSupplementalId.getVersion().getValue());
        return params;
    }



    @Override
    public FloorCalendarSpotcheckReference getCurrentFCR(CalendarSupplementalId cal) {
        return null;
    }

    @Override
    public List<FloorCalendarSpotcheckReference> getFCRYear(int year) {
        return null;
    }

     /* -- Helper Classes -- */
/*
    protected class CalendarRowMapper implements RowMapper<CalendarSupplementalId> {
        @Override
        public CalendarSupplementalId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarSupplementalId(rs.getInt("calendar_no"), rs.getInt("calendar_year"), Version.of(rs.getString("sup_version")));
        }
    }
*/
/*
    protected class CalendarSupRowHandler implements RowCallbackHandler
    {
        protected CalendarSupRowMapper calendarSupRowMapper;
        protected CalendarSupEntryRowMapper calendarSupEntryRowMapper;

        protected Map<Integer, FloorCalendarSpotcheckReference> resultMap;

        public CalendarSupRowHandler() {
            calendarSupRowMapper = new CalendarSupRowMapper();
            calendarSupEntryRowMapper = new CalendarSupEntryRowMapper();
            resultMap = new LinkedHashMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Integer calSupId = rs.getInt("calendar_sup_id");
            if (!resultMap.containsKey(calSupId)) {
                resultMap.put(calSupId, calendarSupRowMapper.mapRow(rs, rs.getRow()));
            }
            resultMap.get(calSupId).addEntry(calendarSupEntryRowMapper.mapRow(rs, rs.getRow()));
        }

        public ArrayList<FloorCalendarSpotcheckReference> getCalendarSupplementals() {
            return new ArrayList<>(resultMap.values());
        }
    }
        //param source method
    protected class CalendarSupRowMapper implements RowMapper<FloorCalendarSpotcheckReference>
    {
        @Override
        public CalendarSupplemental mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarId calendarId = new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year"));
            Version version = Version.of(rs.getString("sup_version"));
            LocalDate calDate = getLocalDateFromRs(rs, "calendar_date");
            LocalDateTime releaseDateTime = getLocalDateTimeFromRs(rs, "release_date_time");
            CalendarSupplemental calSup = new CalendarSupplemental(calendarId, version, calDate, releaseDateTime);
            setModPubDatesFromResultSet(calSup, rs);
            return calSup;
        }
    }

    protected class CalendarSupEntryRowMapper implements RowMapper<CalendarSupplementalEntry>
    {
        @Override
        public CalendarSupplementalEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarSectionType sectionType = CalendarSectionType.valueOfCode(rs.getInt("section_code"));
            int billCalNo = rs.getInt("bill_calendar_no");
            BillId billId = new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                    rs.getString("bill_amend_version"));
            BillId subBillId = null;
            if (rs.getString("sub_bill_print_no") != null) {
                subBillId = new BillId(rs.getString("sub_bill_print_no"), rs.getInt("sub_bill_session_year"),
                        rs.getString("sub_bill_amend_version"));
            }
            boolean high = rs.getBoolean("high");
            return new CalendarSupplementalEntry(billCalNo, sectionType, billId, subBillId, high);
        }
    }



    /** --- /Param Source Methods --- */


    protected static void addCalendarIdParams(CalendarId calendarId, MapSqlParameterSource params) {
        params.addValue("calendarNo", calendarId.getCalNo());
        params.addValue("year", calendarId.getYear());
    }

}
