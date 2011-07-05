<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>

<%

String appPath = request.getContextPath();
String title = request.getParameter("title");

if (title == null)
	title = "NY Senate OpenLeg";
	
String term = (String)session.getAttribute("term");
if (term == null)
	term = "";
	
	session.setAttribute("mobile","true");
 %>
<html>
  <head>
  
<title><%=title%></title>
<link rel="shortcut icon" href="<%=appPath%>/img/nys_favicon_0.ico" type="image/x-icon" />
 
<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/style.css"/> 
<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/style-mobile.css"/> 

<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
 <meta name="apple-mobile-web-app-capable" content="YES">
  </head>
<body>  
 
 <div id="header">
   <h2> <a href="<%=appPath%>"><img src="<%=appPath%>/img/nysenatelogo50.png" style="vertical-align:middle;"/></a>
 OpenLeg Mobile</h2> 
 <div style="clear:left;">
	<form method="get" action="<%=appPath%>/search/">
		Search all legislative data by keyword:<br/>
		<input type="text" name="term" value="<%=term%>"><input type="submit" value="go"/>
			<input type="hidden" name="format" value="mobile"/><br/>
			<em>Example: S1234, Taxes, Budget, Transcript, John Doe, Passed Senate</em>
</form>
<hr/>

<p class="small">
or browse:
<a href="senators.jsp">Sponsors</a>,

<a href="/legislation/search/?format=mobile&type=bill&sort=when">Bills</a>,
<a href="/legislation/search/?format=mobile&type=calendar&sort=when">Calendars</a>,
<a href="/legislation/search/?format=mobile&type=meeting&sort=when">Meetings</a>,
<a href="/legislation/search/?format=mobile&type=transcript&sort=when">Transcripts</a>,
<a href="/legislation/search/?format=mobile&type=action&sort=when">Bill Actions</a>,
<a href="/legislation/search/?format=mobile&type=vote&sort=when">Votes</a>
</p>
<hr/>
<p class="small">

<a href="<%=appPath%>/mobile/info">Mobile Help</a>
</p>
</div>
   </div>
</body>
</html>