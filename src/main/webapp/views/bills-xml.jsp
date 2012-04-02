<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.serialize.*" pageEncoding="utf-8" contentType="text/xml"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

Collection<Bill> bills = (Collection<Bill>)request.getAttribute("bills");

%><%=BillRenderer.renderBills(bills,false)%>