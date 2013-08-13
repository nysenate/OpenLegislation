<%@ page language="java" import="java.util.Iterator,java.util.Collection,java.text.DateFormat,java.text.SimpleDateFormat,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,javax.xml.bind.*" contentType="text/html" pageEncoding="utf-8"%>
<%
    Calendar calendar = (Calendar) request.getAttribute("calendar");
	String title = "Calendar " + calendar.getNo() + " " + calendar.getSession();
%>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>" />
</jsp:include>
<% if (calendar.getType().equals("active")) { %>
    <jsp:include page="/templates/activelist.jsp" />
<% } else { %>
    <jsp:include page="/templates/calendar.jsp" />
<% } %>
<jsp:include page="/footer.jsp" />
