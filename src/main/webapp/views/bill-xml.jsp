<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="utf-8" contentType="text/xml"%><%

CachedContentManager.fillCache(request);
Bill bill = (Bill)request.getAttribute("bill");
%><%=BillRenderer.renderBill(bill)%>
