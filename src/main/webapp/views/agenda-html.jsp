<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/html" pageEncoding="utf-8"%><%


Agenda agenda = (Agenda)request.getAttribute("agenda");

String title = "Agenda: " + agenda.getNumber() + " " + agenda.getSessionYear();
 %>
 
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>


 <div id="content">
 <h1>Agenda <%=agenda.getNumber()%> (<%=agenda.getSessionYear()%> Session)</h1>
<h2></h2>
<%

%> 
 
 </div>

<jsp:include page="/footer.jsp"/>