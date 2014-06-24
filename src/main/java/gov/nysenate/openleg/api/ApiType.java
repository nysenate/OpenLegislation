package gov.nysenate.openleg.api;

import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.transcript.Transcript;

public enum ApiType {
    BILL		("bill", 		Bill.class),
    CALENDAR	("calendar", 	Calendar.class),
    TRANSCRIPT	("transcript", 	Transcript.class),
    ACTION		("action", 		BillAction.class),
    VOTE		("vote", 		Vote.class);

    private String type;
    private Class<? extends BaseObject> clazz;

    private ApiType(String type, Class<? extends BaseObject> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public String type() {
        return type;
    }

    public Class<? extends BaseObject> clazz() {
        return clazz;
    }
}
