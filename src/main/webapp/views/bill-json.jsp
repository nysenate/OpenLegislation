<%@ page language="java" import="gov.nysenate.openleg.util.serialize.*,gov.nysenate.openleg.model.bill.*" contentType="text/plain" pageEncoding="utf-8"%>
<%

Bill bill = (Bill)request.getAttribute("bill");

%><%=OriginalApiConverter.doJson(bill)%>