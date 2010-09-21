<%@ page language="java" import="java.util.*,java.text.*,java.util.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%

CachedContentManager.fillCache(request);

String appPath = request.getContextPath();
Transcript transcript = (Transcript)request.getAttribute("transcript");

String query = request.getParameter("query");

String title = "NY Senate OpenLeg - Transcript " + transcript.getId();

String idKey = transcript.getId();
		
String userAgent = request.getHeader("user-agent").toLowerCase();

if (userAgent.indexOf("mobile") != -1 || userAgent.indexOf("wap") != -1 || userAgent.indexOf("blackberry")!=-1 || userAgent.indexOf("wml")!=-1 || userAgent.indexOf("nokia")!=-1|| userAgent.indexOf("midp")!=-1 ||userAgent.indexOf("mobi")
!=-1)
{
        response.sendRedirect("/openleg/api/mobile/transcript/" + transcript.getId());
        return;
}


String cacheKey = (String)request.getAttribute("path");
int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;

%>

<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>


 
<%

DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

 %>

 <h2>Transcript: <%=df.format(transcript.getTimeStamp()) %></h2>

 <div id="content">
    <cache:cache key="<%=cacheKey%>" time="<%=cacheTime %>" scope="application">
 
 
 <b>Location: <%=transcript.getLocation()%></b> /
 <b>Session: <%=transcript.getType()%></b>
 <br/>
 <b>Formats:</b> 
 
 <a href="<%=appPath%>/api/1.0/html/transcript/<%=idKey%>">Web Format</a>,
 <a href="<%=appPath%>/api/1.0/html-plain/transcript/<%=idKey%>">Original Transcript</a>,
 <a href="<%=appPath%>/api/1.0/xml/transcript/<%=idKey%>">XML</a>, 
 <a href="<%=appPath%>/api/json/transcript/<%=idKey%>">JSON</a>

<hr/>
<%


String fullText = transcript.getTranscriptText();

if (query != null && query.length()>0)
fullText = fullText.replace(query, "<a name=\"result\">" + query + "</a>");
 %>
 <pre>
<%=fullText%>
</pre>
 <hr/>
 </cache:cache>
 
<div id="comments">
 <h3> Discuss!</h3>

 <div id="disqus_thread"></div><script type="text/javascript" src="http://disqus.com/forums/nysenateopenleg/embed.js"></script><noscript><a href="http://nysenateopenleg.disqus.com/?url=ref">View the discussion thread.</a></noscript><a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>
 </div>


  </div>

<%if (query!=null && query.length() > 0){ %>
<script type="text/javascript">
$('#content').highlight('<%=query%>');
</script>
<%} %>
<jsp:include page="/footer.jsp"/>

