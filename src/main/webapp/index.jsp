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
 
<script type="text/javascript" src="<%=appPath%>/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="<%=appPath%>/js/search.js"></script>
</head>

<body> 
<div id="header-home" style="margin-top:0px">
<div style="float:left;">
<a href="http://nysenate.gov">NYSenate.gov</a>:
<a href="http://nysenate.gov/senators">Senators</a>&nbsp;|&nbsp;
<a href="http://nysenate.gov/committees">Committees</a>&nbsp;|&nbsp;
<a href="http://nysenate.gov/issues-initiatives">Issues &amp; Initiatives</a>&nbsp;|&nbsp;
<!--
    <a href="http://nysenate.gov/legislation-open-senate">Open Senate</a>&nbsp;|&nbsp;
    
    <a href="http://nysenate.gov/about-us">About</a>&nbsp;|&nbsp;
    <a href="http://nysenate.gov/media">Photos &amp; Videos</a>&nbsp;|&nbsp;
-->
<a href="http://nysenate.gov/newsroom">Newsroom</a>
</div>
    
<div style="float:right;">
<a href="<%=appPath%>/feedback">FEEDBACK</a> &nbsp;|&nbsp;
<a href="<%=appPath%>/developers">Developers</a>
</div>
</div>

<br/><br/>
 
<center>
<div style="width:750px;text-align:left;">
<div><a href="<%=appPath%>"><img src="<%=appPath%>/img/openleglogo.gif" /></a></div>
</div>
  
<div style="text-align:left;width:500px;">
<h2 class="homeText">Browse, search and share legislative<br/>
information from the New York State Senate</h2>
</div>

<div style="text-align:left;width:500px;margin-top:20px;">
 
<form method="get" action="<%=appPath%>/search/">
<input type="text" id="txtSearchBox" style="width:300px" name="search" autocomplete="off" value="<%=term%>">	
<input type="submit" value="Search"/> 
<span style="color:#999;margin:3px;font-size:12px;">
<a href="<%=appPath%>/advanced/">Advanced</a>
</span>
<div id="quickresult"></div>

<div style="color:#999;margin:3px;font-size:12px;">
<!-- Search by bill, sponsor, committee or keyword from the 2009-2010 session -->
Browse Bills:
<a href="<%=appPath%>/senators">by Sponsor</a>,
<a href="<%=appPath%>/committees">by Committee</a>
&nbsp;
<br/>
View Recent:
<a href="<%=appPath%>/bills/">Bills</a>,
<a href="<%=appPath%>/calendars/">Calendars</a>,
<a href="<%=appPath%>/meetings/">Meetings</a>,
<a href="<%=appPath%>/transcripts/">Transcripts</a>, 
<a href="<%=appPath%>/actions/" title="Actions on Bills from the Floor of the Senate">Actions</a>
<br/>
Community: <a href="<%=appPath%>/comments">View and respond to recent comments</a>
<br/> <br/><br/>

</div>
</form>
</div>

<br style="clear:both;"/><br/>

</center>
 
<center>
<div id="footer" style="width:500px;text-align:left;font-size:8pt;line-height:14px">
<em>You are using Open Legislation v1.9.0, an <a href="<%=appPath%>/license">open-source project</a></em>
     
<div style="height:400px;float:left;padding:0px;width:96px;">
<a rel="license" href="http://www.nysenate.gov/copyright-policy"><img class="cc-logo" alt="Creative Commons License" src="http://i.creativecommons.org/l/by-nc-nd/3.0/us/88x31.png" align='left' /></a>
</div>

<div id="footer-message" style="width:370px;float:left;">
<p>This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/us/">Creative Commons License</a>.
 <br/> Permissions beyond the scope of this license are available <a cc="http://creativecommons.org/ns#" href="http://www.nysenate.gov/copyright-policy" rel="morePermissions">here</a>.
</p>   

</div>
</div>

<!-- 
   <div id="footer" style="width:700px;text-align:left;line-height:12px;">
 
   <div style="height:400px;float:left;padding:0px;width:96px;">
   <a rel="license" href="http://www.nysenate.gov/copyright-policy"><img class="cc-logo" alt="Creative Commons License" src="http://i.creativecommons.org/l/by-nc-nd/3.0/us/88x31.png" align='left' /></a>
   </div>
   <div id="footer-message" style="width:370px;line-height:9px;padding-top:3px;float:left;margin-right:10px;">
   <p>This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/us/">Creative Commons Attribution-Noncommercial-No Derivative Works 3.0 United States License</a>.
   <br/><br/>
   Permissions beyond the scope of this license are available at <a cc="http://creativecommons.org/ns#" href="http://www.nysenate.gov/copyright-policy" rel="morePermissions">http://www.nysenate.gov/copyright-policy</a>.
   <br/><br/>  
   The software and services provided under this site are offered under the BSD License and the GPL v3 License.<br/>
   </p>   

   </div>
   </div>
-->
   
</center>

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
