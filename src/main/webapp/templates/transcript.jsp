<%@ page language="java" import="java.util.*, java.util.regex.*, java.text.*,java.util.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%!
	public final static String TRANSCRIPT_INDENT = "             ";
	public final static String TRANSCRIPT_INDENT_REPLACE = "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	
	public static String removeLineNumbers (String input) {
		StringBuffer resp = new StringBuffer();
		
		StringTokenizer st = new StringTokenizer (input,"\n");
		String line = null;
		int breakIdx = -1;
		
		while (st.hasMoreTokens()) {
			line = st.nextToken().trim();
			
			breakIdx = line.indexOf(' ');
		
			if (breakIdx != -1) {
				
				line = line.substring(breakIdx+1);
				
				if (line.startsWith("Transcription Service, Inc."))
					continue;
				if (line.startsWith("371-8910"))
					continue;
				
				if (line.startsWith(TRANSCRIPT_INDENT))
					resp.append(TRANSCRIPT_INDENT_REPLACE);
				
				line = line.trim();
				
				resp.append(' ');
				resp.append(line);
			}
		}
		
		String output =  resp.toString();
		output = output.replace("SENATOR", "<br/>SENATOR");
		output = output.replace("REVEREND", "<br/>REVEREND");
		output = output.replace("ACTING", "<br/>ACTING");
		output = output.replace("REGULAR SESSION", "REGULAR SESSION<br/><br/>");
		
		return output;
	}

	public static String addHyperlinks (String input) {
		Pattern pattern = null;
		Matcher matcher = null;
		
		pattern = Pattern.compile("(SENATOR\\s)");
		matcher = pattern.matcher(input);
		input = matcher.replaceAll("<b>$1</b>");
		
		pattern = Pattern.compile("(ACTING PRESIDENT\\s)");
		matcher = pattern.matcher(input);
		input = matcher.replaceAll("<b>$1</b>");
		
		
		pattern = Pattern.compile("(THE SECRETARY)");
		matcher = pattern.matcher(input);
		input = matcher.replaceAll("<b>$1</b>");
		
		return input;
	}
%>
<%
	Transcript transcript = (Transcript)request.getAttribute("transcript");
	String query = request.getParameter("term");
	String idKey = transcript.luceneOid();
 	DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);
%>
<div id="content">
    <h2 class='page-title'>
	Transcript: <%=df.format(transcript.getTimeStamp()) %>
	</h2>

    <div class="content-bg">
		<div class="title-block">
			<div class='item-actions'>
				<ul>
	        		<li><a href="#" onclick="window.print(); return false;">Print Page</a></li>
					<li><script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
				</ul>
			</div>
			<h3 class='item-title'><%=transcript.getType()%>, <%=df.format(transcript.getTimeStamp()) %></h3>
	
		</div>
		
	    <div class="item-meta">
	        <div id="subcontent">
		       <div class="billheader"> 
			     <div>
		           <span class="meta">Location:</span> 
		           <%=transcript.getLocation()%>
		        </div> 
				<div>
		           <span class="meta">Session:</span> 
		           <%=transcript.getType()%>
		        </div>  
 			</div>
 		</div>
 		
 <h3 id="section">Transcript: </h3>
		    <%
	        String fullText = transcript.getTranscriptText().trim();

			try {
			    int number = Integer.parseInt(fullText.substring(0,1));
		        fullText = removeLineNumbers(fullText);
		        fullText = addHyperlinks(fullText);

		        if (query != null && query.length()>0) {
		            if (query.startsWith("\"")) {
		                query = query.replace("\"","");
		            }
		            fullText = fullText.replace(query, "<a name=\"result\" style=\"background:yellow\">" + query + "</a>");
		        }

		        %><%=fullText%><%
			}
			catch (Exception e) {
		        StringTokenizer st = new StringTokenizer(fullText,"\n");
		        while (st.hasMoreTokens()) {
		           %><%=st.nextToken()%><br/><br/><%
		        }
			} %>
	     </div>
    </div>
</div>