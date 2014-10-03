package gov.nysenate.openleg.service.calendar.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Range;
import com.google.common.collect.SetMultimap;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.service.base.SearchParameters;
import gov.nysenate.openleg.util.DateUtils;

import java.time.LocalDate;

/** A class that contains values that are used to search for calendars */
public class CalendarSearchParameters implements SearchParameters
{
    /** Get calendars of a particular type */
    private CalendarType calendarType;

    /** Get calendars for a particular year */
    private Integer year;

    /** Get calendars within a range of dates */
    private Range<LocalDate> dateRange;

    /** Get calendars pertaining to a set of bills designated by print number.   */
    private SetMultimap<Integer, BillId> billPrintNo;

    /** Get calendars pertaining to a set of bills designated by calendar number. */
    private SetMultimap<Integer, Integer> billCalendarNo;

    /** Get calendars for a specific set of section codes. */
    private SetMultimap<Integer, Integer> sectionCode;

    /** --- Constructors --- */

    public CalendarSearchParameters() {
        this.calendarType = CalendarType.ALL;
        this.year = null;
        this.dateRange = null;
        this.billPrintNo = null;
        this.billCalendarNo = null;
        this.sectionCode = null;
    }

    /** --- Interface Methods --- */

    /** Returns false if there are any conflicts within the parameters, otherwise returns true */
    @Override
    public boolean isValid() {
        // There needs to be a return type for the query
        if (this.calendarType == null) {
            return false;
        }

        // Active list calendars do not contain section codes
        if (sectionCode != null && calendarType==CalendarType.ACTIVE_LIST) {
            return false;
        }

        // The specified year must fit inside the specified date range
        if (dateRange != null && year != null) {
            if (year < DateUtils.startOfDateRange(dateRange).getYear() ||
                year > DateUtils.endOfDateRange(dateRange).getYear() ) {
                return false;
            }
        }

        // The session year for the print numbers must fit into the date range
        if (billPrintNo != null && dateRange != null) {
            if (!getBillPrintNoSession().asDateRange().isConnected(dateRange)) {
                return false;
            }
        }

        // The specified year must fit into the session year for the print numbers
        if (year != null && billPrintNo != null) {
            if (year < getBillPrintNoSession().getSessionStartYear() ||
                year > getBillPrintNoSession().getSessionEndYear()) {
                return false;
            }
        }

        // All bill ids must be from the same session year
        if (billPrintNo != null) {
            SessionYear sessionYear = null;
            for (BillId billId : billPrintNo.values()) {
                if (sessionYear == null) {
                    sessionYear = billId.getSession();
                }
                else if (!sessionYear.equals(billId.getSession())) {
                    return false;
                }
            }
        }

        return true;
    }

    /** Returns the number of set parameters */
    @Override
    public int paramCount() {
        int count = 0;
        if(year != null) count++;
        if(dateRange != null) count++;
        if(billPrintNo != null) count++;
        if(billCalendarNo != null) count++;
        if(sectionCode != null) count++;
        return count;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("calendarType", calendarType)
                .add("year", year)
                .add("dateRange", dateRange)
                .add("billPrintNo", billPrintNo)
                .add("billCalendarNo", billCalendarNo)
                .add("sectionCode", sectionCode)
                .toString();
    }

    /** --- Functional Getters / Setters --- */

    @JsonIgnore
    public SessionYear getBillPrintNoSession() {
        if (billPrintNo != null && billPrintNo.size() > 0) {
            return billPrintNo.values().iterator().next().getSession();
        }
        return null;
    }

    /** --- Getters Setters --- */

    public CalendarType getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(CalendarType calendarType) {
        this.calendarType = calendarType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Range<LocalDate> getDateRange() {
        return dateRange;
    }

    public void setDateRange(Range<LocalDate> dateRange) {
        this.dateRange = dateRange;
    }

    public SetMultimap<Integer, BillId> getBillPrintNo() {
        return billPrintNo;
    }

    public void setBillPrintNo(SetMultimap<Integer, BillId> billPrintNo) {
        this.billPrintNo = billPrintNo;
    }

    public SetMultimap<Integer, Integer> getBillCalendarNo() {
        return billCalendarNo;
    }

    public void setBillCalendarNo(SetMultimap<Integer, Integer> billCalendarNo) {
        this.billCalendarNo = billCalendarNo;
    }

    public SetMultimap<Integer, Integer> getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(SetMultimap<Integer, Integer> sectionCode) {
        this.sectionCode = sectionCode;
    }
}
