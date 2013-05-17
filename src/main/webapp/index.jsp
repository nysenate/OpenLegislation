<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%><%
session.setAttribute("term","");
	session.removeAttribute("mobile");
	
String appPath = request.getContextPath();

if (appPath.equals("/openleg"))
{
	String newUri = request.getRequestURI();
	newUri = newUri.replace("/openleg","/legislation");
	response.sendRedirect(newUri);
	return;
}

Bill bill = null;
String last = null;
DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

String title = request.getParameter("title");

if (title == null)
	title = "The New York Senate Open Legislation Service";
	
String term = (String)session.getAttribute("term");
if (term == null)
	term = "";
	
String billkey = request.getParameter("billkey");
if (billkey == null)
	billkey = "";
 %>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">var _sf_startpt=(new Date()).getTime()</script>
<title><%=title%></title>
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
					<input type="text" id="txtSearchBox" style="width:300px" name="search" autocomplete="off" value="<%=term%>">	
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
					<div class="footer-third">
				<ul>
				<li class="head">Open Legislation</li>
				<li><a href="<%=appPath%>/bills/" title="Browse and search Senate and Assembly bills by number, keyword, sponsor and more">Bills</a></li>
				<li><a href="<%=appPath%>/calendars/" title="View recent and search floor calendars and active lists by number or date (i.e. 11/07/2009)">Calendars</a></li>
				<li><a href="<%=appPath%>/meetings/" title="View upcoming and recent committee meetings, and search by committee, chairperson, location, date (i.e. 11/07/2009) and more.">Meetings</a></li>
				<li><a href="<%=appPath%>/transcripts/"  title="View and search Senate floor full text transcripts">Transcripts</a></li>
				<li><a href="<%=appPath%>/actions/" title="View and filter Floor Actions on Bills from the Floor of the Senate">Actions</a>
				<li><a href="<%=appPath%>/senators">Browse by Sponsor</a></li>
				<li><a href="<%=appPath%>/committees">Browse by Committee</a></li>
				</ul>
			</div>
			<div class="footer-third">
				<ul>
					<li class="head">Connect</li>
					<li><a href="http://billbuzz.nysenate.gov">BillBuzz</a></li>
					<li><a href="<%=appPath%>/comments/">View Comments</a></li>
					<li><a href="<%=appPath%>/feedback">Feedback</a></li>
					<li><a href="<%=appPath%>/developers">Developers</a></li>
				</ul>
			</div>
			<div class="footer-third">
				<ul>
					<li class="head">NYSenate.gov</li>
					<li><a href="http://nysenate.gov/senators">Senators</a></li>
					<li><a href="http://nysenate.gov/committees">Committees</a></li>
					<li><a href="http://nysenate.gov/issues-initiatives">Issues &amp; Initiatives</a></li>
					<li><a href="http://nysenate.gov/newsroom">Newsroom</a></li>
					<li><a href = "http://www.nysenate.gov/privacy-policy">Privacy Policy</a></li>
				</ul>
			</div>
			<div id="footer-message"><p>
				<a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/us/">
				<img class="cc-logo" alt="Creative Commons License" src="http://i.creativecommons.org/l/by-nc-nd/3.0/us/88x31.png" align='left' />
				</a> This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/us/">Creative Commons Attribution-Noncommercial-No Derivative Works 3.0 United States License</a>.<br /> Permissions beyond the scope of this license are available at <a cc="http://creativecommons.org/ns#" href="http://www.nysenate.gov/copyright-policy" rel="morePermissions">http://www.nysenate.gov/copyright-policy</a>.
	 		</p>
	 		<p>The software and services provided under this site are offered under the BSD License and the GPL v3 License.</p>
	 		</div>
		</div>
	</div>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-8171983-6");
pageTracker._trackPageview();
} catch(err) {}</script>   
<br/><br/>

<script type="text/javascript">
var _sf_async_config={uid:2873,domain:"open.nysenate.gov"};
(function(){
  function loadChartbeat() {
    window._sf_endpt=(new Date()).getTime();
    var e = document.createElement('script');
    e.setAttribute('language', 'javascript');
    e.setAttribute('type', 'text/javascript');
    e.setAttribute('src',
       (("https:" == document.location.protocol) ? "https://s3.amazonaws.com/" : "http://") +
       "static.chartbeat.com/js/chartbeat.js");
    document.body.appendChild(e);
  }
  var oldonload = window.onload;
  window.onload = (typeof window.onload != 'function') ?
     loadChartbeat : function() { oldonload(); loadChartbeat(); };
})();

</script>
</body>
</html>
