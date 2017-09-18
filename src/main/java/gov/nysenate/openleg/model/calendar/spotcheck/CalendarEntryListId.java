package gov.nysenate.openleg.model.calendar.spotcheck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;

import java.time.LocalDate;

import static gov.nysenate.openleg.model.calendar.CalendarType.*;

/**
 * Created by PKS on 3/9/16.
 */
public class CalendarEntryListId extends CalendarId {

    protected CalendarType type;
    protected Version version;
    protected Integer sequenceNo;
    protected LocalDate calDate;

    public CalendarEntryListId(CalendarId calendarId, CalendarType type, Version version, Integer sequenceNo) {
        super(calendarId);
        this.version = version;
        this.sequenceNo = sequenceNo;
        this.type = type;
    }

    public CalendarEntryListId(CalendarId calendarId, CalendarType type, Version version, Integer sequenceNo, LocalDate calDate) {
        super(calendarId);
        this.version = version;
        this.sequenceNo = sequenceNo;
        this.calDate = calDate;
        this.type = type;
    }

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

    @JsonIgnore
    public CalendarId getCalendarId(){
        return this;
    }

    public CalendarType getType() { return type; }

    public Version getVersion(){
        return version;
    }

    public Integer getSequenceNo(){
        return sequenceNo;
    }

    public void setType(CalendarType type){
        this.type = type;
    }

    public void setCalDate(LocalDate localDate){this.calDate = localDate;}

    public LocalDate getCalDate(){return calDate;}

    public void setVersion(Version version){
        this.version = version;
    }

    public void setSequenceNo(Integer sequenceNo){
        this.sequenceNo = sequenceNo;
    }
}
