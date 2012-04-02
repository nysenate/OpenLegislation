<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.xstream.*" contentType="text/plain" pageEncoding="utf-8"%><%String appPath = request.getContextPath();
String format = (String)request.getAttribute("format");
out.write(XStreamBuilder.writeResponse(format,(SenateResponse)request.getAttribute("results")));%>
