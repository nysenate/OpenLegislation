package gov.nysenate.openleg.util;

import java.util.Calendar;

public class SessionYear {
	
	private static Integer year = null;
	private static Long sessionStart = null;
	private static Long sessionEnd = null;
	
	public static int getSessionYear() {
		if(year == null) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			
			if(year % 2 != 1) {
				year--;
			}
		}
		return year;
	}
	
	public static long getSessionStart() {
		if(sessionStart == null) {
			Calendar cal = Calendar.getInstance();
			//Jan 1st 12:00:00am of session year
			cal.set(getSessionYear(), 0, 1, 0, 0, 0);
						
			sessionStart = cal.getTimeInMillis();
		}
		return sessionStart;
	}
	
	public static long getSessionEnd() {
		if(sessionEnd == null) {
			Calendar cal = Calendar.getInstance();
			//Dec 31st 11:59:59pm of sessionyear+1
			cal.set(getSessionYear()+1, 11, 31, 23, 59, 59);
						
			sessionEnd = cal.getTimeInMillis();
		}
		return sessionEnd;
	}
}
