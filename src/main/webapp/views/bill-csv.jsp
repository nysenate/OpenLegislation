<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,org.json.*,gov.nysenate.openleg.model.*"  pageEncoding="utf-8" contentType="text/plain"%>
<%
String cacheKey = (String)request.getAttribute("path");
 int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;
%>
<%

CachedContentManager.fillCache(request);
Bill bill = (Bill)request.getAttribute("bill");

DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
String action = "";


%>Year,SenateBillNo,SameAs,Sponsor,Committee,Title,Summary,Action
<%=bill.getYear()%>,<%=bill.getSenateBillNo()%>,<%=bill.getSameAs()%>,<%=bill.getSponsor().getFullname()%>,<%=bill.getCurrentCommittee()%>,<%=bill.getTitle()%>,<%=bill.getSummary()%>


