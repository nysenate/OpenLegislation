<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/html" pageEncoding="utf-8"%>
<%

String appPath = request.getContextPath();
String uri = request.getRequestURI();


 CachedContentManager.fillCache(request);
//Committee committee = (Committee)request.getAttribute("meetings");

String term = (String)request.getAttribute("term");
if (term == null)
        term = "";
term = term.trim();

String title = "Committee Meetings";

//if (committee.getName().length()>0)
	//title += ": " + committee.getName();
request.setAttribute("searchType","meeting");

 %>
 
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>

 <h2><%=title%>
 
 <small><em>Look for a particular day's meetings by entering a date above - i.e. 2/22/2010</em>
 </small></h2>

 <div id="content">

<%

Collection<Meeting> meetings = (Collection<Meeting>)request.getAttribute("meetings");

Iterator<Meeting> itMeeting = meetings.iterator();
	DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
Date cutOff = SimpleDateFormat.getDateInstance(DateFormat.SHORT).parse("7/09/09");

Meeting meeting = null;

while (itMeeting.hasNext()) {

try
{
	meeting = itMeeting.next();
	
	/*
	String calNo = "";
	String addendumId = "";
	
	if (meeting.getAddendums()!=null)
	{
		Addendum addendum = meeting.getAddendums().get(meeting.getAddendums().size()-1);
	
		Agenda agenda = addendum.getAgenda();
		calNo = agenda.getNumber() + "";
		addendumId = addendum.getAddendumId();
	}*/
	
	
String chair = meeting.getCommitteeChair();
chair = chair.replace("?","");
%> 
<div class="billSummary">
<h4><a href="<%=appPath%>/meeting/<%=meeting.getId()%>">Meeting: <%=meeting.getCommitteeName()%></a></h4>

<div>
When: <%=df.format(meeting.getMeetingDateTime())%>
/
Chair: <a href="<%=appPath%>/search/?searchType=meeting&term=chair:%22<%=java.net.URLEncoder.encode(meeting.getCommitteeChair(),OpenLegConstants.ENCODING)%>%22"><%=chair%></a>

<%if (meeting.getLocation()!=null){ %>
/ Location: <a href="<%=appPath%>/search/?searchType=meeting&location:%22<%=java.net.URLEncoder.encode(meeting.getLocation(),OpenLegConstants.ENCODING)%>%22"><%=meeting.getLocation()%></a>
<%} %>
</div>

</div>
<%
}
catch (Exception e2)
{
System.out.println("error rendering meeting: " + meeting.getId());
e2.printStackTrace();
}

%>
<%} %>
 <div id="formatBox">Formats: <a href="<%=appPath%>/api/1.0/xml/meetings/<%=term%>">XML</a>, <a href="<%=appPath%>/api/1.0/ical/meetings/<%=term%>">iCal</a>, RSS

</div>
 </div>
<div>
<a href="http://www.google.com/calendar/render?cid=http%3A%2F%2Fopen.nysenate.gov%2Flegislation%2Fapi%2F1.0%2Fical%2Fmeetings%2F" target="_blank"><img src="http://www.google.com/calendar/images/ext/gc_button6.gif" border=0></a>
</div>

<jsp:include page="/footer.jsp"/>
