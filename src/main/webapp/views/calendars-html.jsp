<%@ page language="java" import="java.util.Iterator,java.util.ArrayList,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/html" pageEncoding="utf-8"%>
<%
String title = "Calendars";
String appPath = request.getContextPath();

String reqpath = (String)request.getAttribute("path");

if (reqpath != null && (reqpath.equals("/calendars")))
{
response.sendRedirect("/calendars");
return;
}
%>
  
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>

 <%
 
 request.setAttribute("searchType","calendar");
 
 
CachedContentManager.fillCache(request);
String term = (String)request.getAttribute("term");
ArrayList<Calendar> calendars = (ArrayList<Calendar>)request.getAttribute("calendars");
%>

<h2>Floor Calendars &amp; Active Lists: <%=term%></h2>
 
 <div id="content">

<%
DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

Iterator<Calendar> itCals = calendars.iterator();

Calendar calendar = null;

while (itCals.hasNext())
{
	calendar = itCals.next();
	try
	{
 %>
 <div class="billSummary">
<%if (calendar.getType().equals("floor")){%>
<h4><a href="<%=appPath%>/calendar/<%=calendar.getId()%>">Floor Calendar / no. <%=calendar.getNo()%> / Session: <%=calendar.getSessionYear()%></a></h4>
<%}else if (calendar.getType().equals("active")){%>
<h4><a href="<%=appPath%>/calendar/<%=calendar.getId()%>">Active List / no. <%=calendar.getNo()%> / Session: <%=calendar.getSessionYear()%></a></h4>

<%}%><div>
<%
Iterator<Supplemental> itSupp = calendar.getSupplementals().iterator();
Supplemental supp = null;

if (itSupp.hasNext()){

	supp = itSupp.next();
	
	if (calendar.getType().equals("floor")){ 
	
		%>
		
		<%if (supp.getCalendarDate() != null){ %>
		Calendar Date: <%=df.format(supp.getCalendarDate())%>
		<%} %>
		<%if (supp.getReleaseDateTime() != null){ %>
		(<em>Published: <%=df.format(supp.getReleaseDateTime())%></em>)
		<%} %>
		<br/>
	<%}else if (calendar.getType().equals("active") && supp.getSequence()!=null){ %>

	<%
	Sequence sequence = supp.getSequence();
	if (sequence != null) {
		 %>
		
		 Calendar Date: <%=df.format(sequence.getActCalDate())%> 
		 (<em>Published <%=df.format(sequence.getReleaseDateTime())%></em>)
		<br/>
		<% }%>
	
	<% }%>

 <%}%>

 </div>
 </div>
 <%
 } catch(Exception e)
 {
 }
 }%>
 
 
 <div id="formatBox">Formats: <a href="<%=appPath%>/api/1.0/xml/calendars/">XML</a>, <a href="<%=appPath%>/api/1.0/ical/calendars/">iCal</a>, RSS</div>
 
 </div>
 
<jsp:include page="/footer.jsp"/>
