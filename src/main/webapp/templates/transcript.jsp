<%@ page language="java" import="java.util.*,java.text.*,java.util.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.transcript.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%
	String appPath = request.getContextPath();
	
	Transcript transcript = (Transcript)request.getAttribute("transcript");
	
	String query = request.getParameter("term");
	
	String idKey = transcript.luceneOid();

 	DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);
 %>
<br/>

<h2>
	Transcript: <%=df.format(transcript.getTimeStamp()) %>
</h2>
<div style="float:right;">
		<script type="text/javascript"
			src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script>
</div>
<br style="clear: both;" />
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

		try {
			int number = Integer.parseInt(fullText.substring(0,1));
			isNumberedFormat = true;
		}
		catch (Exception e) {
			isNumberedFormat = false;
		}

		if (isNumberedFormat) {
			fullText = TextFormatter.removeLineNumbers(fullText);
			
			fullText = TextFormatter.addHyperlinks(fullText);
			
			if (query != null && query.length()>0) {
			
			if (query.startsWith("\""))
				query = query.replace("\"","");
				
			fullText = fullText.replace(query, "<a name=\"result\" style=\"background:yellow\">" + query + "</a>");
			
		}
			
		%><%=fullText%><%
		}
		else {
			StringTokenizer st = new StringTokenizer(fullText,"\n");
			
			while (st.hasMoreTokens()) {  %>
			<%=st.nextToken() %>
			<br/><br/>
		<%}
	} %>
</div>
<hr/>