<%@ page language="java" import="gov.nysenate.openleg.util.serialize.*,gov.nysenate.openleg.model.*" contentType="text/plain" pageEncoding="utf-8"%><%

String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

Bill bill = (Bill)request.getAttribute("bill");

%><%=OriginalApiConverter.doJson(bill)%>