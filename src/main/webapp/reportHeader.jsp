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
				<a href="<%=appPath%>/mobile/info">Mobile Access</a> &nbsp;|&nbsp; 
				<a href="http://www.nysenate.gov/developers">Developers</a>
	   		</div>
		</div>

    	<div id="header">
   		 	<div id="logobox"><a href="<%=appPath%>/"><img src="<%=appPath%>/img/openwordlogo.gif" /></a></div>
				<div style="font-size:9pt;line-height:16px;">
                     
					<div style="float:left;">
						
	
						<br/>
						
					</div>

					</div>
					<div style="margin-left:10px;border-left:1px solid #aaa;height:50px;padding-left:10px;float:left;">
					<label style="font-size:36pt;color:#0000A0;">Error Report</label>
					</div>
			   </div>
			   <br style="clear:left;"/>
   
   
