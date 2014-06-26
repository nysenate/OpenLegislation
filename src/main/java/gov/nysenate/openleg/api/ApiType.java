package gov.nysenate.openleg.api;

import gov.nysenate.openleg.model.BaseLegContent;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.transcript.Transcript;

public enum ApiType {
    BILL		("bill", 		Bill.class),
    CALENDAR	("calendar", 	Calendar.class),
    TRANSCRIPT	("transcript", 	Transcript.class),
    ACTION		("action", 		BillAction.class),
    VOTE		("vote", 		BillVote.class);

    private String type;
    private Class<? extends BaseLegContent> clazz;

    private ApiType(String type, Class<? extends BaseLegContent> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public String type() {
        return type;
    }

    public Class<? extends BaseLegContent> clazz() {
        return clazz;
    }
}
