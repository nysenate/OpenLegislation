<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.xstream.*" contentType="text/plain" pageEncoding="utf-8"%><%
	String callback = (String)request.getParameter("callback");
	callback = callback == null ? "defaultCallback" : callback;
%><%=callback%>(<jsp:include page="/views2/v2-api.jsp"/>);