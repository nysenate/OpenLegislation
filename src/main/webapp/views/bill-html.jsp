<%@ page language="java" import="java.util.ArrayList, java.util.List, java.util.Collections, java.util.StringTokenizer, java.util.Iterator, java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,org.codehaus.jackson.map.ObjectMapper" contentType="text/html" pageEncoding="utf-8"%>
<%
	Bill bill = (Bill)request.getAttribute("bill");
	
	String titleText = "(no title)";
	if (bill.getTitle()!=null) {
		titleText = bill.getTitle();
	}
	
	String title = bill.getSenateBillNo() + " - NY Senate Open Legislation - " + titleText;
%>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<jsp:include page="/templates/bill.jsp" />
<jsp:include page="/templates/disqus.jsp">
	<jsp:param name="disqusUrl" value="<%=bill.getDisqusUrl()%>"/>
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<jsp:include page="/footer.jsp"/>
