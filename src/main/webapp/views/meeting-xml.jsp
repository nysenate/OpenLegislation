<%@ page language="java" import="gov.nysenate.openleg.util.serialize.*,gov.nysenate.openleg.model.*" contentType="text/xml" pageEncoding="utf-8"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);
 
Meeting meeting = (Meeting)request.getAttribute("meeting");

 %><%= OriginalApiConverter.doXml(meeting)%>