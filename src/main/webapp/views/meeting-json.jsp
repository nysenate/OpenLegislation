<%@ page language="java" import="java.util.*,java.io.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/plain" pageEncoding="utf-8"%><%

 
Meeting meeting = (Meeting)request.getAttribute("meeting");


 %><%=JsonConverter.getJson(meeting).toString()%>