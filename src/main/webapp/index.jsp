<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%
session.setAttribute("term","");
session.removeAttribute("mobile");

String appPath = request.getContextPath();

// The appPath used to be openleg, to avoid breaking URLs we still host at that context path
// and redirect to the new correct one (/legislation)
if (appPath.equals("/openleg")) {
	response.sendRedirect(request.getRequestURI().replace("/openleg","/legislation"));
	return;
}

%>
<!DOCTYPE html>
<html>
<head>
<title>The New York Senate Open Legislation Service</title>
<link rel="shortcut icon" href="<%=appPath%>/img/nys_favicon_0.ico" type="image/x-icon" />
<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/style.css"/> 

<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
<meta name="apple-mobile-web-app-capable" content="YES">
<link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="<%=appPath%>/feed" />
 
<script type="text/javascript" src="<%=appPath%>/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%=appPath%>/js/search.js"></script>
</head>

<body> 
    <div id="menu">
    	<div id="content-full" class="main-menu">
			<ul>
				<li><a href="<%=appPath%>/bills/" title="Browse and search Senate and Assembly bills by number, keyword, sponsor and more">Bills</a>
				<li><a href="<%=appPath%>/resolutions/" title="View senate and assembly resolutions.">Resolutions</a></li>
				<li><a href="<%=appPath%>/calendars/" title="View recent and search floor calendars and active lists by number or date (i.e. 1/07/2013)">Calendars</a></li>
				<li><a href="<%=appPath%>/meetings/" title="View upcoming and recent committee meetings, and search by committee, chairperson, location, date (i.e. 1/07/2013) and more.">Meetings</a></li>
				<li><a href="<%=appPath%>/transcripts/" title="View and search Senate floor full text transcripts">Transcripts</a></li>
				<li><a href="<%=appPath%>/actions/" title="View and filter Floor Actions on Bills from the Floor of the Senate">Actions</a></li>
				<li><a href="<%=appPath%>/senators">Sponsor</a></li>
				<li><a href="<%=appPath%>/committees">Committee</a></li>
			</ul>
		</div>
	</div>
	<div id="content" >	
		<div class="homelogo">
			<div><a href="<%=appPath%>"><img src="<%=appPath%>/img/openleglogo.gif" /></a></div>
			<div class="hometext">
				<div>
					<h2 class="homeText">Browse, search and share legislative<br/>
					information from the New York State Senate</h2>
				</div>
				<form method="get" action="<%=appPath%>/search/">
					<input type="text" id="txtSearchBox" style="width:300px" name="search" autocomplete="off">
					<input type="submit" value="Search"/> 
					<span style="color:#999;margin:3px;font-size:12px;">
						<a href="<%=appPath%>/advanced/">Advanced</a>
					</span>
					<div id="quickresult"></div>
				</form>
			</div>
		</div>
	</div>
<div id="content" >
	<div id="footer">
			<div class="footer-part">
				<ul>
				<li class="head">Open Legislation</li>
				<li><a href="<%=appPath%>/bills/" title="Browse and search Senate and Assembly bills by number, keyword, sponsor and more">Bills</a></li>
				<li><a href="<%=appPath%>/resolutions/" title="View senate and assembly resolutions.">Resolutions</a></li>
				<li><a href="<%=appPath%>/calendars/" title="View recent and search floor calendars and active lists by number or date (i.e. 11/07/2009)">Calendars</a></li>
				<li><a href="<%=appPath%>/meetings/" title="View upcoming and recent committee meetings, and search by committee, chairperson, location, date (i.e. 11/07/2009) and more.">Meetings</a></li>
				<li><a href="<%=appPath%>/transcripts/"  title="View and search Senate floor full text transcripts">Transcripts</a></li>
				<li><a href="<%=appPath%>/actions/" title="View and filter Floor Actions on Bills from the Floor of the Senate">Actions</a>
				<li><a href="<%=appPath%>/senators">Browse by Sponsor</a></li>
				<li><a href="<%=appPath%>/committees">Browse by Committee</a></li>
				</ul>
			</div>
			<div class="footer-part">
				<ul>
					<li class="head">Connect</li>
					<li><a href="http://billbuzz.nysenate.gov">BillBuzz</a></li>
					<li><a href="<%=appPath%>/comments/">View Comments</a></li>
					<li><a href="<%=appPath%>/feedback">Feedback</a></li>
					<li><a href="<%=appPath%>/developers">Developers</a></li>
				</ul>
			</div>
			<div class="footer-part">
				<ul>
					<li class="head">NYSenate.gov</li>
					<li><a href="http://nysenate.gov/senators">Senators</a></li>
					<li><a href="http://nysenate.gov/committees">Committees</a></li>
					<li><a href="http://nysenate.gov/issues-initiatives">Issues &amp; Initiatives</a></li>
					<li><a href="http://nysenate.gov/newsroom">Newsroom</a></li>
					<li><a href = "http://www.nysenate.gov/privacy-policy">Privacy Policy</a></li>
				</ul>
			</div>
			<div id="footer-part">
				<p>This content is licensed under <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/us/">Creative Commons BY-NC-ND 3.0</a>. Permissions beyond the scope of this license are available <a cc="http://creativecommons.org/ns#" href="http://www.nysenate.gov/copyright-policy" rel="morePermissions">here</a>.</p>
	 			<p>The <a href="https://github.com/nysenate/OpenLegislation">software</a> and <a href="http://openlegislation.readthedocs.org/en/latest/">services</a> provided under this site are offered under the BSD License and the GPL v3 License.</p>
	 		</div>
		</div>
	</div>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));

try {
    var pageTracker = _gat._getTracker("UA-8171983-6");
    pageTracker._trackPageview();
} catch(err) {}
</script>
</body>
</html>
