<%@ page language="java" import="gov.nysenate.openleg.*,gov.nysenate.openleg.util.serialize.*,gov.nysenate.openleg.model.*,javax.xml.bind.*" contentType="text/plain" pageEncoding="utf-8"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);
 
 Calendar calendar = (Calendar)request.getAttribute("calendar");


 %><%=JsonConverter.getJson(calendar).toString()%>