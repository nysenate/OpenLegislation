<%@ page language="java" import="java.util.Iterator,java.util.Date,java.util.ArrayList,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/xml" pageEncoding="utf-8"%><?xml version="1.0" encoding="utf-8"?>
<calendars>
<%


CachedContentManager.fillCache(request);
String term = (String)request.getAttribute("term");
ArrayList<Calendar> calendars = (ArrayList<Calendar>)request.getAttribute("calendars");

Calendar calendar = null;

String title = "Calendars";

String appPath = request.getContextPath();

 SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd'T'HHmm'00'" ) ;

Iterator<Calendar> itCals = calendars.iterator();

while (itCals.hasNext())
{
	calendar = itCals.next();
	if (calendar.getSupplementals() == null || calendar.getSupplementals().size() == 0)
		continue;
	
	
	try
	{
	
		
		Supplemental supp = calendar.getSupplementals().get(0);
		
		String start = dateFormat.format(supp.getReleaseDateTime());
	
	Date endDate = new Date(supp.getReleaseDateTime().getTime());
	endDate.setHours(endDate.getHours()+2);
	String end = dateFormat.format(endDate);
		
String link = "http://open.nysenate.gov/legislation/calendar/" + calendar.getId();
		
 %><calendar id="<%=calendar.getId()%>" start="<%=start%>" end="<%=end%>" htmlurl="<%=link%>"/>
 <%
}
catch (Exception e)
{
	System.err.println("Unable to render calendars xml: " + calendar.getId());
}
}%>
</calendars>