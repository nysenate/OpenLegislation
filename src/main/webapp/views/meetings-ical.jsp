<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/plain" pageEncoding="utf-8"%><%

String appPath = request.getContextPath();


CachedContentManager.fillCache(request);
Committee committee = (Committee)request.getAttribute("meetings");
String title = "NY Senate Committee Meetings: " + committee.getName();
 

 %>BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//hacksw/handcal//NONSGML v1.0//EN
X-WR-CALNAME:<%=title%>
CALSCALE:GREGORIAN
METHOD:PUBLISH
X-WR-TIMEZONE:America/New_York
X-WR-CALDESC:
BEGIN:VTIMEZONE
TZID:America/New_York
X-LIC-LOCATION:America/New_York
BEGIN:DAYLIGHT
TZOFFSETFROM:-0500
TZOFFSETTO:-0400
TZNAME:EDT
DTSTART:19700308T020000
RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=2SU
END:DAYLIGHT
BEGIN:STANDARD
TZOFFSETFROM:-0400
TZOFFSETTO:-0500
TZNAME:EST
DTSTART:19701101T020000
RRULE:FREQ=YEARLY;BYMONTH=11;BYDAY=1SU
END:STANDARD
END:VTIMEZONE
<%
Iterator<Meeting> itMeeting = committee.getMeetings().iterator();
	DateFormat df = SimpleDateFormat.getDateTimeInstance();

Meeting meeting = null;
 SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd'T'HHmm'00'" ) ;


while (itMeeting.hasNext()) {

	meeting = itMeeting.next();
	
	String start = dateFormat.format(meeting.getMeetingDateTime());
	
	Date endDate = new Date(meeting.getMeetingDateTime().getTime());
	endDate.setHours(endDate.getHours()+1);
	String end = dateFormat.format(endDate);
	
	
	String link = "http://open.nysenate.gov/legislation/meeting/" + java.net.URLEncoder.encode(meeting.getId(),OpenLegConstants.ENCODING);
	
%>BEGIN:VEVENT
DTSTART;TZID=America/New_York:<%=start%>
DTEND;TZID=America/New_York:<%=end%>
CONTACT:<%=meeting.getCommitteeChair()%>
DESCRIPTION:<%=meeting.getCommitteeName()%> Meeting - <%=link%>
URL:<%=link%>
LOCATION:<%=meeting.getLocation()%>, Capitol Hill, Albany, New York
SUMMARY:<%=meeting.getCommitteeName()%> Meeting
LAST-MODIFIED:<%=start%>Z
UID:<%=meeting.getId()%>
END:VEVENT
<%} %>
END:VCALENDAR
