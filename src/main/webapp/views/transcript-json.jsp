<%@ page language="java" import="gov.nysenate.openleg.util.serialize.*,gov.nysenate.openleg.model.*"  pageEncoding="utf-8" contentType="text/plain"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

Transcript transcript = (Transcript)request.getAttribute("transcript");

%><%=OriginalApiConverter.doJson(transcript) %>