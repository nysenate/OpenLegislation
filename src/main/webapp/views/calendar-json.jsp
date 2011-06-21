<%@ page language="java" import="gov.nysenate.openleg.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.calendar.*,javax.xml.bind.*" contentType="text/plain" pageEncoding="utf-8"%><%

 
 Calendar calendar = (Calendar)request.getAttribute("calendar");


 %><%=JsonConverter.getJson(calendar).toString()%>