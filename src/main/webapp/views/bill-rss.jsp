<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="utf-8" contentType="text/xml"%><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %><%
CachedContentManager.fillCache(request);

Bill bill = (Bill)request.getAttribute("bill");
String cacheKey = (String)request.getAttribute("path");
int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;

%><cache:cache key="<%=cacheKey%>" time="<%=cacheTime %>" scope="application"><%=BillRenderer.renderBill(bill)%></cache:cache>