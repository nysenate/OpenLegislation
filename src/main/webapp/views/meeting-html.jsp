<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,javax.xml.bind.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%
	DateFormat df = new SimpleDateFormat("MMM d, yyyy");
	Meeting meeting = (Meeting) request.getAttribute("meeting");
	String title = "Committee Meeting: " + meeting.getCommitteeName() + " - " + df.format(meeting.getMeetingDateTime());
%>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>" />
</jsp:include>
<jsp:include page="/templates/meeting.jsp" />
<jsp:include page="/footer.jsp" />
