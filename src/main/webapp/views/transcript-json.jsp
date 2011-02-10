<%@ page language="java" import="gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.transcript.*"  pageEncoding="utf-8" contentType="text/plain"%><%


Transcript transcript = (Transcript)request.getAttribute("transcript");

%><%=OriginalApiConverter.doXml(transcript) %>