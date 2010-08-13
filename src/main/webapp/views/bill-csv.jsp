<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,org.json.*,gov.nysenate.openleg.model.*"  pageEncoding="utf-8" contentType="text/plain"%><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %><%
String cacheKey = (String)request.getAttribute("path");
 int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;
%><cache:cache key="<%=cacheKey%>" time="<%=cacheTime %>" scope="application"><%

CachedContentManager.fillCache(request);
Bill bill = (Bill)request.getAttribute("bill");

DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
String action = "";


%>Year,SenateBillNo,SameAs,Sponsor,Committee,Title,Summary,Action
<%=bill.getYear()%>,<%=bill.getSenateBillNo()%>,<%=bill.getSameAs()%>,<%=bill.getSponsor().getFullname()%>,<%=bill.getCurrentCommittee()%>,<%=bill.getTitle()%>,<%=bill.getSummary()%>
</cache:cache>

