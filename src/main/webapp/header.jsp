<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%><%
	String appPath = request.getContextPath();
	String title = request.getParameter("title");
	if (title == null)
		title = "Open Legislation Service";
	title += " - New York State Senate";
		
	String term = (String)request.getAttribute("term");
	
	if (term == null)
		term = "";
	else
	{
		term = term.replaceAll("\"","&quot;");
	}
	
	String search = (String)request.getAttribute("search");
	if(search == null) {
		search = "";
	}
	else {
		search = search.replaceAll("\"","&quot;");
	}
	
	String searchType = (String)request.getAttribute("type");
	if (searchType == null)
		searchType = "";
%>
<!DOCTYPE html>
<html>
	<head>
		<title><%=title%></title>
		
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
		<meta name="apple-mobile-web-app-capable" content="YES"/>
		
		<link rel="shortcut icon" href="<%=appPath%>/img/nys_favicon_0.ico" type="image/x-icon" />
		<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/style.css"/>
		<link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="<%=appPath%>/feed" />
		
		<script type="text/javascript" src="<%=appPath%>/js/jquery-1.3.2.min.js"></script>
		<script type="text/javascript" src="<%=appPath%>/js/search.js"></script>
	 
		<script type="text/javascript">
			searchType = "<%=searchType%>";

			$(document).ready(function() {
				var clearOnFocus = function(element, text) {
	                element.focus(function() {
	                    var self = $(this);
	                    if (self.val() == text) {
	                        self.val("");
	                    }
	                }).blur(function() {
	                    var self = $(this);
	                    if (self.val() == "") {
	                        self.val(text);
	                    }
	                }).blur();
	            };

	            clearOnFocus($("input[name=email]"), "enter email");
			});

		</script>
	</head>
	<body>  
		<div id="header-home" style="margin-top:0px">
	    	<div style="float:left;">
	     		<a href="http://nysenate.gov">NYSenate.gov</a>:
	    		<a href="http://nysenate.gov/senators">Senators</a>&nbsp;|&nbsp;
	    		<a href="http://nysenate.gov/committees">Committees</a>&nbsp;|&nbsp;
	    		<a href="http://nysenate.gov/issues-initiatives">Issues &amp; Initiatives</a>&nbsp;|&nbsp;
	    		<a href="http://nysenate.gov/newsroom">Newsroom</a>
	    	</div>
	      	<div style="float:right;">
				<a href="<%=appPath%>/feedback">Feedback</a> &nbsp;|&nbsp;  
				<a href="<%=appPath%>/developers">Developers</a>
	   		</div>
		</div>

    	<div id="header">
   		 	<div id="logobox"><a href="<%=appPath%>/"><img src="<%=appPath%>/img/openwordlogo.gif" /></a></div>
				<div style="font-size:9pt;line-height:16px;">

					<div style="float:left;">
						<%if (searchType.startsWith("bill")||searchType.equals("search")||searchType.equals("sponsor")||searchType.equals("committee")){ %>
							<a href="<%=appPath%>/bills/"  class="linkActivated" title="Browse and search Senate and Assembly bills by number, keyword, sponsor and more">Bills</a>
						<%}else{ %>
							<a href="<%=appPath%>/bills/" title="Browse and search Senate and Assembly bills by number, keyword, sponsor and more">Bills</a>
						<%} %>&nbsp;
                                                <a href="<%=appPath%>/resolutions/"  <%if (searchType.startsWith("resolution")){%>class="linkActivated"<%} %> title="View recent senate and assembly resolutions.">Calendars</a>
						<a href="<%=appPath%>/calendars/"  <%if (searchType.startsWith("calendar")){%>class="linkActivated"<%} %> title="View recent and search floor calendars and active lists by number or date (i.e. 11/07/2009)">Calendars</a>
						&nbsp;
						<a href="<%=appPath%>/meetings/"  <%if (searchType.startsWith("meeting")){%>class="linkActivated"<%} %> title="View upcoming and recent committee meetings, and search by committee, chairperson, location, date (i.e. 11/07/2009) and more.">Meetings</a>
						&nbsp;
						<a href="<%=appPath%>/transcripts/" <%if (searchType.startsWith("transcript")){%>class="linkActivated"<%} %> title="View and search Senate floor full text transcripts">Transcripts</a>
						&nbsp;
						<a href="<%=appPath%>/actions/"  <%if (searchType.startsWith("action")){%>class="linkActivated"<%} %> title="View and filter Floor Actions on Bills from the Floor of the Senate">Actions</a>
						<% if(searchType.matches("(sponsor|bill|calendar|meeting|transcript|action|vote).*?")) { term = ""; } %>
						|	Browse by:
							<a href="<%=appPath%>/senators">Sponsor</a>,
							<a href="<%=appPath%>/committees">Committee</a>
	
						<br/>
						<form method="get" action="<%=appPath%>/search/" style="padding-bottom:3px">
							<input type="text" id="txtSearchBox"  name="search" value="<%=search%>" autocomplete="off">	
							<input type="hidden" name="searchType" value="<%=searchType%>">	
							<input type="submit" value="Search"/> | <a href="<%=appPath%>/advanced/">Advanced</a>
							| <a href="<%=appPath%>/comments/">View Comments</a>
							<div id="quickresult" class="quickresult-header"></div>
						</form>
					</div>

					<div style="margin-left:10px;border-left:1px solid #aaa;height:50px;padding-left:10px;float:left;">
						Be notified of updates to this page:<br/>
						<form style="margin-bottom:0;" action="http://www.changedetection.com/detect.html" method="get" target="ChangeDetectionWiz">
							<input style="width:100px;" TYPE="TEXT" NAME="email"  value="enter email">
							<input type="submit" name="enter" value=" OK "
								onclick="somewin=window.open('http://www.changedetection.com/detect.html', 'ChangeDetectionWiz','resizable=yes,scrollbars=yes,width=624,height=460');somewin.focus()">
							<br/>
							<small>
								<a href="http://www.changedetection.com/privacy.html" target="ChangeDetectionPrivacy"
								onclick="somewin=window.open('http://www.changedetection.com/privacy.html', 'ChangeDetectionPrivacy','resizable=yes,scrollbars=yes,width=624,height=390');somewin.focus()">
								<span style="font-size: 10px">Privacy</span></a> | 
								<a  href="http://www.changedetection.com/">Third-Party Service Info</a>
							</small>
						</form>
					</div>
					<div style="margin-left:10px;border-left:1px solid #aaa;height:50px;padding-left:10px;float:left;">
						Use <a href="http://billbuzz.nysenate.gov">BillBuzz</a> to read what's<br/>
						 being said about legislation
					</div>
			   </div>
			   <br style="clear:left;"/>
   
   
