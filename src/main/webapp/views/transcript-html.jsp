<%@ page language="java" import="java.util.*,java.text.*,java.util.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.transcript.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>

<%

String appPath = request.getContextPath();

Transcript transcript = (Transcript)request.getAttribute("transcript");

String query = request.getParameter("term");

String title = "NY Senate OpenLeg - Transcript";

String idKey = transcript.luceneOid();

 	DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);

 %>
<jsp:include page="/header.jsp">
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
 <a href="<%=appPath%>/api/1.0/xml/transcript/<%=idKey%>">XML</a>,
 <a href="<%=appPath%>/api/1.0/json/transcript/<%=idKey%>">JSON</a>
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

<div id="disqus_thread"></div>
<script type="text/javascript">
    /* * * CONFIGURATION VARIABLES: EDIT BEFORE PASTING INTO YOUR WEBPAGE * * */
    var disqus_shortname = 'nysenateopenleg'; // required: replace example with your forum shortname

    // The following are highly recommended additional parameters. Remove the slashes in front to use.
   
    /* * * DON'T EDIT BELOW THIS LINE * * */
    (function() {
        var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
        dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
    })();
</script>
 </div>



<jsp:include page="/footer.jsp"/>

