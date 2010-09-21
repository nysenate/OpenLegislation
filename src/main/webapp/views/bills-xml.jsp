<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="utf-8" contentType="text/xml"%><%

CachedContentManager.fillCache(request);
Collection<Bill> bills = (Collection<Bill>)request.getAttribute("bills");

%><%=BillRenderer.renderBills(bills,false)%>