<%@ page language="java" import="gov.nysenate.openleg.util.serialize.*,gov.nysenate.openleg.model.committee.*" contentType="text/xml" pageEncoding="utf-8"%><%

 
Meeting meeting = (Meeting)request.getAttribute("meeting");

 %><%= OriginalApiConverter.doXml(meeting)%>