package gov.nysenate.openleg.common.dao;

import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.calendar.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import static gov.nysenate.openleg.common.dao.SqlBaseDao.addModPubDateParams;
import static gov.nysenate.openleg.common.util.DateUtils.toDate;

public final class CalendarParamUtils {
    private CalendarParamUtils() {}

    public static MapSqlParameterSource getCalSupplementalParams(CalendarSupplemental sup) {
        MapSqlParameterSource params = calendarIdParams(sup.getCalendarId())
                .addValue("supVersion", sup.getVersion().toString())
                .addValue("calendarDate", toDate(sup.getCalDate()))
                .addValue("releaseDateTime", toDate(sup.getReleaseDateTime()));
        addModPubDateParams(sup.getModifiedDateTime(), sup.getPublishedDateTime(), params);
        return params;
    }

    public static MapSqlParameterSource getCalSupEntryParams(CalendarSupplemental sup, CalendarSupplementalEntry entry) {
        MapSqlParameterSource params = calendarIdParams(sup.getCalendarId())
                .addValue("supVersion", sup.getVersion().toString())
                .addValue("sectionCode", entry.getSectionType().getCode())
                .addValue("billCalNo", entry.getBillCalNo());
        addBillIdParams(entry.getBillId(), params);
        BillId subBillId = entry.getSubBillId();
        return params.addValue("subPrintNo", (subBillId != null) ? subBillId.getBasePrintNo() : null)
                .addValue("subSession", (subBillId != null) ? subBillId.getSession().year() : null)
                .addValue("subAmendVersion", (subBillId != null) ? subBillId.getVersion().toString() : null)
                .addValue("high", entry.getBillHigh());
    }

    public static MapSqlParameterSource getCalActiveListParams(CalendarActiveList activeList) {
        MapSqlParameterSource params = calendarIdParams(activeList.getCalendarId())
                .addValue("sequenceNo", activeList.getSequenceNo())
                .addValue("calendarDate", toDate(activeList.getCalDate()))
                .addValue("releaseDateTime", toDate(activeList.getReleaseDateTime()))
                .addValue("notes", activeList.getNotes());
        addModPubDateParams(activeList.getModifiedDateTime(), activeList.getPublishedDateTime(), params);
        return params;
    }

    public static MapSqlParameterSource getCalActiveListEntryParams(CalendarActiveList actList,
                                                                     CalendarEntry entry) {
        MapSqlParameterSource params = calendarIdParams(actList.getCalendarId())
                .addValue("sequenceNo", actList.getSequenceNo())
                .addValue("billCalendarNo", entry.getBillCalNo());
        addBillIdParams(entry.getBillId(), params);
        return params;
    }

    public static MapSqlParameterSource getCalendarParams(Calendar calendar) {
        MapSqlParameterSource params = calendarIdParams(calendar.getId());
        addModPubDateParams(calendar.getModifiedDateTime(), calendar.getPublishedDateTime(), params);
        return params;
    }

    public static MapSqlParameterSource calendarIdParams(CalendarId calendarId) {
        return new MapSqlParameterSource().addValue("calendarNo", calendarId.getCalNo())
                .addValue("year", calendarId.getYear());
    }

    public static void addBillIdParams(BillId billId, MapSqlParameterSource params) {
        params.addValue("printNo", billId.getBasePrintNo())
                .addValue("session", billId.getSession().year())
                .addValue("amendVersion", billId.getVersion().toString());
    }
}
