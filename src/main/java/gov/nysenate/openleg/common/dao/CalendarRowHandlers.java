package gov.nysenate.openleg.common.dao;

import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.*;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.common.dao.SqlBaseDao.*;

public final class CalendarRowHandlers {
    private CalendarRowHandlers() {}

    public static class CalendarRowMapper implements RowMapper<Calendar> {
        @Override
        public Calendar mapRow(ResultSet rs, int rowNum) throws SQLException {
            Calendar calendar = new Calendar(new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year")));
            setModPubDatesFromResultSet(calendar, rs);
            return calendar;
        }
    }

    public static class CalendarIdRowMapper implements RowMapper<CalendarId> {
        @Override
        public CalendarId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year"));
        }
    }

    public static class CalendarSupRowHandler implements RowCallbackHandler {
        private final CalendarSupRowMapper calendarSupRowMapper = new CalendarSupRowMapper();
        private final CalendarSupEntryRowMapper calendarSupEntryRowMapper = new CalendarSupEntryRowMapper();
        private final Map<Integer, CalendarSupplemental> resultMap = new LinkedHashMap<>();
        private final boolean isAlert;

        public CalendarSupRowHandler(boolean isAlert) {
            this.isAlert = isAlert;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Integer calSupId = rs.getInt(isAlert ? "calendar_sup_id": "sup_id");
            if (!resultMap.containsKey(calSupId)) {
                resultMap.put(calSupId, calendarSupRowMapper.mapRow(rs, rs.getRow()));
            }
            if (isAlert || rs.getInt("ent_id") > 0) {
                resultMap.get(calSupId).addEntry(calendarSupEntryRowMapper.mapRow(rs, rs.getRow()));
            }
        }

        public List<CalendarSupplemental> getCalendarSupplementals() {
            return new ArrayList<>(resultMap.values());
        }
    }

    public static class ActiveListRowHandler implements RowCallbackHandler {
        private final CalendarActiveListRowMapper calendarActiveListRowMapper;
        private final CalendarActiveListEntryRowMapper calendarActiveListEntryRowMapper
                = new CalendarActiveListEntryRowMapper();
        private final Map<Integer, CalendarActiveList> resultMap = new LinkedHashMap<>();
        private final boolean isAlertCal;

        public ActiveListRowHandler(boolean isAlertCal) {
            this.calendarActiveListRowMapper = new CalendarActiveListRowMapper(isAlertCal);
            this.isAlertCal = isAlertCal;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Integer calALId = rs.getInt(isAlertCal ? "calendar_active_list_id" : "al_id");
            if (!resultMap.containsKey(calALId)) {
                resultMap.put(calALId, calendarActiveListRowMapper.mapRow(rs, rs.getRow()));
            }
            if (isAlertCal || rs.getInt("ent_id") > 0) {
                resultMap.get(calALId).addEntry(calendarActiveListEntryRowMapper.mapRow(rs, rs.getRow()));
            }
        }

        public List<CalendarActiveList> getActiveLists() {
            return new ArrayList<>(resultMap.values());
        }
    }

    private static class CalendarSupRowMapper implements RowMapper<CalendarSupplemental> {
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

    private static class CalendarSupEntryRowMapper implements RowMapper<CalendarSupplementalEntry> {
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

    private record CalendarActiveListRowMapper(boolean hasNotes) implements RowMapper<CalendarActiveList> {
        @Override
        public CalendarActiveList mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarActiveList activeList = new CalendarActiveList();
            activeList.setSequenceNo(rs.getInt("sequence_no"));
            activeList.setCalendarId(new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year")));
            activeList.setCalDate(getLocalDateFromRs(rs, "calendar_date"));
            activeList.setReleaseDateTime(getLocalDateTimeFromRs(rs, "release_date_time"));
            if (hasNotes) {
                activeList.setNotes(rs.getString("notes"));
            }
            setModPubDatesFromResultSet(activeList, rs);
            return activeList;
        }
    }

    private static class CalendarActiveListEntryRowMapper implements RowMapper<CalendarEntry> {
        @Override
        public CalendarEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarEntry entry = new CalendarEntry();
            entry.setBillCalNo(rs.getInt("bill_calendar_no"));
            entry.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                    rs.getString("bill_amend_version")));
            return entry;
        }
    }
}
