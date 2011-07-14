<%@ page language="java" import="gov.nysenate.openleg.util.serialize.*,gov.nysenate.openleg.model.transcript.*" contentType="text/xml" pageEncoding="utf-8"%><%

Transcript transcript = (Transcript)request.getAttribute("transcript");

%><%=OriginalApiConverter.doXml(transcript) %>