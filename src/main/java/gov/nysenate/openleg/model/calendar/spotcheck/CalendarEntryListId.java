package gov.nysenate.openleg.model.calendar.spotcheck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;

/**
 * Created by PKS on 3/9/16.
 */
public class CalendarEntryListId extends CalendarId {

    protected CalendarType type;
    protected Version version;
    protected Integer sequenceNo;

    public CalendarEntryListId(CalendarId calendarId, CalendarType type, Version version, Integer sequenceNo) {
        super(calendarId);
        this.type = type;
        this.version = version;
        this.sequenceNo = sequenceNo;
    }

    public static CalendarEntryListId getActiveListId(CalendarId id, Integer sequenceNo) {
        return new CalendarEntryListId(id, CalendarType.ACTIVE_LIST, null, sequenceNo);
    }

    @JsonIgnore
    public CalendarId getCalendarId(){
        return this;
    }

    public CalendarType getType(){
        return type;
    }

    public Version getVersion(){
        return version;
    }

    public Integer getSequenceNo(){
        return sequenceNo;
    }

    public void setType(CalendarType type){
        this.type = type;
    }

    public void setVersion(Version version){
        this.version = version;
    }

    public void setSequenceNo(Integer sequenceNo){
        this.sequenceNo = sequenceNo;
    }
}
