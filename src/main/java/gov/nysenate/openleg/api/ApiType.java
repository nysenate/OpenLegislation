package gov.nysenate.openleg.api;

import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.committee.Meeting;

public enum ApiType {
	BILL		("bill", 		Bill.class, 		new String[] {"html", "json", "mobile", "xml", 
																	"csv", "html-print", "lrs-print"}),
	CALENDAR	("calendar", 	Calendar.class, 	new String[] {"html", "json", "mobile", "xml"}),
	MEETING		("meeting", 	Meeting.class, 		new String[] {"html", "json", "mobile", "xml"}),
	TRANSCRIPT	("transcript", 	Transcript.class, 	new String[] {"html", "json", "mobile", "xml"}),
	ACTION		("action", 		BillEvent.class, 	new String[] {"html", "json", "mobile", "xml", 
																	"csv", "html-list", "rss"}),
	VOTE		("vote", 		Vote.class, 		new String[] {"html", "json", "mobile", "xml", 
																	"csv"});
	
	private String type;
	private Class<? extends SenateObject> clazz;
	private String[] formats;
	
	private ApiType(String type, Class<? extends SenateObject> clazz, String[] formats) {
		this.type = type;
		this.clazz = clazz;
		this.formats = formats;
	}
	
	public String type() {
		return type;
	}
	
	public Class<? extends SenateObject> clazz() {
		return clazz;
	}
	
	public String[] formats() {
		return formats;
	}
	
	public boolean isValidFormat(String format) {
		for(String f:formats) {
			if(f.equalsIgnoreCase(format)) return true;
		}
		return false;
	}
}
