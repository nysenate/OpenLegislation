<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="utf-8" contentType="text/xml"%>
<%
CachedContentManager.fillCache(request);

Bill bill = (Bill)request.getAttribute("bill");
String cacheKey = (String)request.getAttribute("path");
int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;

%><%=BillRenderer.renderBill(bill)%>