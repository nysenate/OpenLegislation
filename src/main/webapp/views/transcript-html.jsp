<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ page import="gov.nysenate.openleg.model.transcript.Transcript" %>
<%
	Transcript transcript = (Transcript)request.getAttribute("transcript");
	String title = "NY Senate OpenLeg - Transcript " + transcript.getId();
%>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<jsp:include page="/templates/transcript.jsp" />
<jsp:include page="/footer.jsp"/>
