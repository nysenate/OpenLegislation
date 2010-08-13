<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="text/html" pageEncoding="utf-8"%>

<%

String appPath = request.getContextPath();
Transcript transcript = (Transcript)request.getAttribute("transcript");

String query = (String)request.getAttribute("query");
String summary = "";

if (query != null && query.length() > 0)
{
	int keyIdx = transcript.getTranscriptText().toUpperCase().indexOf(query.toUpperCase());
	
	if (keyIdx > 0)
	{
		summary = transcript.getTranscriptText().substring(keyIdx - 50,keyIdx + 200);
	
	}

}
else
{
		//StringTokenizer st = new StringTokenizer(transcript.getTranscriptText(),"\n");
	//	int lines = st.countTokens();
		summary = "";//lines + " lines of text";
}

String idKey = transcript.getId();

DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM,SimpleDateFormat.SHORT);

String dateString = "";

if (transcript.getTimeStamp()!=null)
	dateString = df.format(transcript.getTimeStamp());
 %>
 <div class="billSummary">
 <h4><a href="<%=appPath%>/transcript/<%=transcript.getId()%>?query=<%=query%>#result"><%=transcript.getType()%> <%=dateString%></a></h4>
<div style="font-size:90%">
Location:  <%=transcript.getLocation()%> / 
Formats:
<a href="<%=appPath%>/api/1.0/html/transcript/<%=idKey%>">Web</a>,
<a href="<%=appPath%>/api/1.0/html-plain/transcript/<%=idKey%>">Original</a>,
<a href="<%=appPath%>/api/1.0/xml/transcript/<%=idKey%>">XML</a>, <a href="<%=appPath%>/api/1.0/json/transcript/<%=idKey%>">JSON</a>
/ Size: <%=(transcript.getTranscriptText().length()/1000)%>kb
</div>
<%if (summary != null  && summary.length()>0){ %>
<p>excerpt:
<em>
<%=summary %></em>
</p> 
<%} %>
  </div>

