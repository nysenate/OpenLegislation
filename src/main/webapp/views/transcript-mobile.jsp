<%@ page language="java" import="java.util.*,java.text.*,java.util.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>

<%

String appPath = request.getContextPath();

Transcript transcript = (Transcript)request.getAttribute("transcript");

String query = request.getParameter("term");

String title = "NY Senate OpenLeg - Transcript " + transcript.getId();

String idKey = transcript.getId();

 	DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);

 %>
<jsp:include page="/views/mobile-header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<br/>

 <h2>Transcript: <%=df.format(transcript.getTimeStamp()) %></h2>
<br/>

 <div id="content">
 
 <b>Location: <%=transcript.getLocation()%></b> /
 <b>Session: <%=transcript.getType()%></b>
 <br/>
 <div class="blockFormats">
 <b>Formats:</b> 
 <a href="<%=appPath%>/api/1.0/html/transcript/<%=idKey%>">Web Format</a>,
 <a href="<%=appPath%>/api/1.0/html-plain/transcript/<%=idKey%>">Original Transcript</a>,
 <a href="<%=appPath%>/api/1.0/xml/transcript/<%=idKey%>">XML</a>,
 <a href="<%=appPath%>/api/json/transcript/<%=idKey%>">JSON</a>
</div>
<hr/>
<%



String fullText = transcript.getTranscriptText().trim();

boolean isNumberedFormat = false;

try
{
	int number = Integer.parseInt(fullText.substring(0,1));
	isNumberedFormat = true;
}
catch (Exception e)
{
	isNumberedFormat = false;
}

if (isNumberedFormat)
{
	fullText = TextFormatter.removeLineNumbers(fullText);
	
	fullText = TextFormatter.addHyperlinks(fullText);
	
	if (query != null && query.length()>0)
	{
	
	if (query.startsWith("\""))
		query = query.replace("\"","");
		
	fullText = fullText.replace(query, "<a name=\"result\" style=\"background:yellow\">" + query + "</a>");
	
	}
	
	%><%=fullText%><%
	
}else
{

StringTokenizer st = new StringTokenizer(fullText,"\n");
while (st.hasMoreTokens())
{
 %>
 <%=st.nextToken() %><br/><br/>
 <%} %>
 
 <%} %>

</div>
 <hr/>
 
  
<div id="comments">
 <h3> Discuss!</h3>
 <div id="disqus_thread"></div><script type="text/javascript" src="http://disqus.com/forums/nysenateopenleg/embed.js"></script><noscript><a href="http://nysenateopenleg.disqus.com/?url=ref">View the discussion thread.</a></noscript><a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>
 </div>



<jsp:include page="/footer.jsp"/>

