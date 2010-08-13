<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="utf-8" contentType="text/xml"%><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %><%

String cacheKey = (String)request.getAttribute("path");
int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;
 
%><cache:cache key="<%=cacheKey%>" time="<%=cacheTime %>" scope="application"><%
CachedContentManager.fillCache(request);
Bill bill = (Bill)request.getAttribute("bill");
%><%=BillRenderer.renderBill(bill)%></cache:cache>
