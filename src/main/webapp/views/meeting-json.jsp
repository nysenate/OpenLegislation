<%@ page language="java" import="java.util.*,java.io.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.util.serialize.*,gov.nysenate.openleg.model.*,javax.xml.bind.*" contentType="text/plain" pageEncoding="utf-8"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);
 
Meeting meeting = (Meeting)request.getAttribute("meeting");


 %><%=JsonConverter.getJson(meeting).toString()%>