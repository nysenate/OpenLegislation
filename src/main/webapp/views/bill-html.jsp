<%@ page language="java" import="java.util.ArrayList, java.util.List, java.util.Collections, java.util.StringTokenizer, java.util.Iterator, java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,org.codehaus.jackson.map.ObjectMapper" contentType="text/html" pageEncoding="utf-8"%>
<%
	String appPath = request.getContextPath();
	
	Bill bill = (Bill)request.getAttribute("bill");
	
	String titleText = "(no title)";
	if (bill.getTitle()!=null)
		titleText = bill.getTitle();
	
	String senateBillNo = bill.getSenateBillNo();
	
	String title = senateBillNo + " - NY Senate Open Legislation - " + titleText;
%>

<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<jsp:include page="/templates/bill.jsp" />

<%
	String disqusUrl = null;
	String disqusId = null;
	
	if (bill.getYear()==2009) {
		disqusId = bill.getSenateBillNo().split("-")[0];
		disqusUrl = "http://open.nysenate.gov/legislation/api/html/bill/" + disqusId;
	}
	else {
		disqusId = bill.getSenateBillNo();
		disqusUrl = "http://open.nysenate.gov/legislation/bill/" + disqusId;
	}
%>

<jsp:include page="/templates/disqus.jsp">
	<jsp:param name="disqusUrl" value="<%=disqusUrl%>"/>
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>

<jsp:include page="/footer.jsp"/>

