package gov.nysenate.openleg.model.calendar.spotcheck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.*;

import static gov.nysenate.openleg.model.calendar.CalendarType.ACTIVE_LIST;
import static gov.nysenate.openleg.model.calendar.CalendarType.SUPPLEMENTAL_CALENDAR;

/**
 * Identifies a data type that contains a calendar entry list
 * Can be either a {@link CalendarActiveListId} or {@link CalendarSupplementalId}
 */
public class CalendarEntryListId extends CalendarId {

    /** Indicates type of calendar entry list */
    protected CalendarType type;
    /** Indicates Version if the entry list is for a supplemental calendar */
    protected Version version;
    /** Indicates sequence number if the entry list is for an active list */
    protected Integer sequenceNo;

    /* --- Constructors --- */

    public CalendarEntryListId(CalendarId calendarId, CalendarType type, Version version, Integer sequenceNo) {
        super(calendarId);
        this.type = type;
        this.version = version;
        this.sequenceNo = sequenceNo;
    }

    public CalendarEntryListId(CalendarActiveListId activeListId) {
        this(activeListId, ACTIVE_LIST, null, activeListId.getSequenceNo());
    }

    public CalendarEntryListId(CalendarSupplementalId calSupId) {
        this(calSupId, SUPPLEMENTAL_CALENDAR, calSupId.getVersion(), null);
    }

    /* --- Functional Getters --- */

    @JsonIgnore
    public CalendarId getCalendarId(){
        return this;
    }

    /* --- Overridden Methods --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarEntryListId)) return false;
        if (!super.equals(o)) return false;

        CalendarEntryListId that = (CalendarEntryListId) o;

        if (type != that.type) return false;
        if (version != that.version) return false;
        return sequenceNo != null ? sequenceNo.equals(that.sequenceNo) : that.sequenceNo == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (sequenceNo != null ? sequenceNo.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(CalendarId o) {
        int result = super.compareTo(o);
        switch (result){
            case 1:
            case -1:
                return result;
            case 0:
                if(o instanceof CalendarEntryListId){
                    CalendarEntryListId c = (CalendarEntryListId) o;
                    return ComparisonChain.start()
                            .compare(this.getType(),c.getType())
                            .compare(this.getSequenceNo(),c.getSequenceNo())
                            .compare(this.getVersion(),c.getVersion())
                            .result();
                }else return result;
            default:
                throw new IllegalArgumentException("Invalid Comparision Result" + result);
        }
    }

    @Override
    public String toString() {
        String calendarStr = super.toString();
        switch (type){
            case ALL:
                return calendarStr;
            case ACTIVE_LIST:
                return calendarStr + " " + sequenceNo;
            case FLOOR_CALENDAR:
                return calendarStr + " " + "FLOOR";
            case SUPPLEMENTAL_CALENDAR:
                return calendarStr + " " + version;
            default:
                throw new IllegalArgumentException("Invalid Calendar Type" + type);
        }
    }

    /* --- Basic Getters --- */

    public CalendarType getType(){
        return type;
    }

    public Version getVersion(){
        return version;
    }

    public Integer getSequenceNo(){
        return sequenceNo;
    }
}
