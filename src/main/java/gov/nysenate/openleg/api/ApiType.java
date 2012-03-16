package gov.nysenate.openleg.api;

import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.bill.Action;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.model.transcript.Transcript;

public enum ApiType {
    BILL		("bill", 		Bill.class),
    CALENDAR	("calendar", 	Calendar.class),
    MEETING		("meeting", 	Meeting.class),
    TRANSCRIPT	("transcript", 	Transcript.class),
    ACTION		("action", 		Action.class),
    VOTE		("vote", 		Vote.class);

    private String type;
    private Class<? extends SenateObject> clazz;

    private ApiType(String type, Class<? extends SenateObject> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public String type() {
        return type;
    }

    public Class<? extends SenateObject> clazz() {
        return clazz;
    }
}
