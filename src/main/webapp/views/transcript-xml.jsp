<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*" contentType="text/xml" pageEncoding="utf-8"%><%

String appPath = request.getContextPath();
Transcript transcript = (Transcript)request.getAttribute("transcript");

String title = "NY Senate OpenLeg - Transcript " + transcript.getId();

String idKey = transcript.getId();
		
String userAgent = request.getHeader("user-agent").toLowerCase();

if (userAgent.indexOf("mobile") != -1 || userAgent.indexOf("wap") != -1 || userAgent.indexOf("blackberry")!=-1 || userAgent.indexOf("wml")!=-1 || userAgent.indexOf("nokia")!=-1|| userAgent.indexOf("midp")!=-1 ||userAgent.indexOf("mobi")
!=-1)
{
        response.sendRedirect("/openleg/api/mobile/transcript/" + transcript.getId());
        return;
}


DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

 %><?xml version="1.0" encoding="utf-8"?>
<transcript id="<%=transcript.getId()%>">
 <timestamp><%=transcript.getTimeStamp().toLocaleString() %></timestamp>
<location><%=transcript.getLocation()%></location>
<session><%=transcript.getType()%></session>
<text>
<![CDATA[
<%=TextFormatter.clean(EncodingCleaner.CleanInvalidXmlChars(transcript.getTranscriptText()))%>
]]>
</text>
</transcript>