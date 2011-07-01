<%@ page language="java" import="java.util.Iterator,java.util.Collection,java.text.DateFormat,java.text.SimpleDateFormat,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/html" pageEncoding="utf-8"%><%


String appPath = request.getContextPath();

Calendar calendar = (Calendar)request.getAttribute("calendar");
String title = "Calendar " + calendar.getNo() + " " + calendar.getSessionYear();
	DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);

 %>
<jsp:include page="/views/mobile-header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>

<jsp:include page="/templates/calendar.jsp" />

<jsp:include page="/footer.jsp"/>
