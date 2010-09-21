<%@ page language="java" import="java.util.Iterator,java.util.Date,java.util.ArrayList,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/plain" pageEncoding="utf-8"%><%
 
  


CachedContentManager.fillCache(request);
String term = (String)request.getAttribute("term");
ArrayList<Calendar> calendars = (ArrayList<Calendar>)request.getAttribute("calendars");

Calendar calendar = null;

String title = "Calendars";

String appPath = request.getContextPath();

 SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd'T'HHmm'00'" ) ;


 %>BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//hacksw/handcal//NONSGML v1.0//EN
X-WR-CALNAME:<%=title%>
<%
Iterator<Calendar> itCals = calendars.iterator();

while (itCals.hasNext())
{
	calendar = itCals.next();
	try
	{
	
	if (calendar.getSupplementals() == null || calendar.getSupplementals().size() == 0)
		continue;
		
		Supplemental supp = calendar.getSupplementals().get(0);
		
		String start = dateFormat.format(supp.getReleaseDateTime());
	
	Date endDate = new Date(supp.getReleaseDateTime().getTime());
	endDate.setHours(endDate.getHours()+2);
	String end = dateFormat.format(endDate);
		
String link = "http://open.nysenate.gov/legislation/calendar/" + calendar.getId();
		
 %>BEGIN:VEVENT
DTSTART:<%=start%>
DTEND:<%=end%>
SUMMARY:Calendar no. <%=calendar.getNo()%> (<%=calendar.getType()%> ) / Session: <%=calendar.getSessionYear()%>
URL:<%=link%>
UID:<%=calendar.getId()%>
END:VEVENT
<%
}
catch (Exception e)
{
	System.err.println("Unable to render calendar ical: " + calendar.getId());
}

}%>
END:VCALENDAR