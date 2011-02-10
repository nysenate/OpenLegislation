<%@ page language="java" import="javax.jdo.*,java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.xstream.*" contentType="text/plain" pageEncoding="utf-8"%><%String appPath = request.getContextPath();
String term = (String)request.getAttribute("term");
String type = (String)request.getAttribute("type");
String format = (String)request.getAttribute("format");
String pageIdx = (String)request.getAttribute("pageIdx");
String pageSize = (String)request.getAttribute("pageSize");
out.write(XStreamBuilder.writeResponse(format,(SenateResponse)request.getAttribute("results")));%>
