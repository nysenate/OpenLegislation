<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="text/plain" pageEncoding="utf-8"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

Transcript transcript = (Transcript)request.getAttribute("transcript");
%>
<%=transcript.getTranscriptText()%>