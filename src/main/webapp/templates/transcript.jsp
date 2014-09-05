<%@ page language="java" import="java.util.*, java.util.regex.*, java.text.*,java.util.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%!
	// Big indents ended Jan 1st 2005
    public static long BIG_INDENT_END = 1104555600000L;

    // Big indents started Jan 1st 1999
    public static long BIG_INDENT_START = 915166800000L;

	public static String removeLineNumbers (String fullText, long date) {
        String htmlText = "";

        String TRANSCRIPT_INDENT = "             ";
        String BIG_TRANSCRIPT_INDENT = "                   ";
        String TRANSCRIPT_INDENT_REPLACE = "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        String LINE_NUM_INDENT = "<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp";

        for (String aLine: fullText.split("\n")) {
            gov.nysenate.openleg.util.TranscriptLine line = new TranscriptLine(aLine);
            String tmp = line.fullText();

            if (line.isTranscriptNumber()) {
                tmp = line.removeInvalidCharacters();
                htmlText += LINE_NUM_INDENT + " " + tmp.trim() + "<br/><br/>";
                continue;
            }
			if (tmp.trim().contains("Transcription Service, Inc."))
				continue;
			if (tmp.trim().contains("(518)"))
				continue;

            if (line.hasLineNumber())
                tmp = line.removeLineNumber();

            // Skip blank lines.
            if (tmp.trim().length() < 1)
                continue;

            String indent = TRANSCRIPT_INDENT;
            if (date > BIG_INDENT_START && date < BIG_INDENT_END) {
                indent = BIG_TRANSCRIPT_INDENT;
            }

            if (tmp.startsWith(indent))
                htmlText += TRANSCRIPT_INDENT_REPLACE;

            htmlText += " " + tmp.trim();

        }
		
		htmlText = htmlText.replace("SENATOR", "<br/>SENATOR");
		htmlText = htmlText.replace("REVEREND", "<br/>REVEREND");
		htmlText = htmlText.replace("ACTING", "<br/>ACTING");
		htmlText = htmlText.replace("REGULAR SESSION", "REGULAR SESSION<br/><br/>");
		
		return htmlText;
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
	String idKey = transcript.getOid();
 	DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);
%>
<div id="content">


    <div class="content-bg">
		<h2 class='page-title'>Transcript: <%=df.format(transcript.getTimeStamp()) %></h2>
	    <div class="item-meta">
	        <div id="subcontent" class="emptytitle">
	       		<div class="billmeta">
			     <ul>
			     	<li><span class="meta">Time: </span><span class="metadata">
                     <%=df.format(transcript.getTimeStamp()) %>
 					</span></li>
 					<li><span class="meta">Location: </span><span class="metadata">
                     <%=transcript.getLocation()%>
 					</span></li>
 					<li><span class="meta">Session: </span><span class="metadata">
		           		<%=transcript.getType()%>
 					</span></li>
		        </ul>  
 			</div>
 			<div class='item-actions'>
				<ul>
	        		<li><a href="#" onclick="window.print(); return false;">Print Page</a></li>
                    <li><a href="<%=JSPHelper.getLink(request, "/api/2.0/transcript/"+transcript.getOid()+".pdf") %>">Download (PDF)</a></li>
					<li><script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
				</ul>
			</div>
 		</div>
 		
 		<h3 class="section" ><a id="Transcript" href="#Transcript" class="anchor ui-icon ui-icon-link"></a> Transcript</h3>
 		<pre class='memo'>   <%
	        String fullText = transcript.getTranscriptText();

			try {
		        fullText = removeLineNumbers(fullText, transcript.getTimeStamp().getTime());
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
		</pre>
	     </div>
    </div>
</div>