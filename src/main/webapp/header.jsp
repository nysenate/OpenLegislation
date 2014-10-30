<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, java.util.*, java.util.Map.Entry, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String search = (String)request.getAttribute("search");
	if(search == null) {
		search = "";
	}
	else {
		search = search.replaceAll("\"","&quot;");
	}
	
	String searchType = (String)request.getAttribute("type");
	if (searchType == null) {
		searchType = "";
	}
%>
<!DOCTYPE html>
<html>
	<head>
		<title><c:out value="${param.title}" default=""/> - New York State Senate</title>
		
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
		<meta name="apple-mobile-web-app-capable" content="YES"/>
		
		<link rel="shortcut icon" href="<%=JSPHelper.getLink(request, "/static/img/nys_favicon_0.ico")%>" type="image/x-icon" />

        <link rel="stylesheet" type="text/css" href="<%=JSPHelper.getLink(request, "/static/vendor/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css")%>" />
        <link rel="stylesheet" type="text/css" media="screen" href="<%=JSPHelper.getLink(request, "/static/css/style.css")%>"/>
        <link rel="stylesheet" type="text/css" media="print" href="<%=JSPHelper.getLink(request, "/static/css/style-print.css")%>"/>
        
        <%
        @SuppressWarnings("unchecked")
        HashMap<String, String> feeds = (HashMap<String, String>)request.getAttribute("feeds");
        if (feeds != null) {
            for (Entry<String, String> feed : feeds.entrySet()) { %>
            <link rel="alternate" type="application/atom+xml" title="<%=feed.getKey()%>" href="<%=feed.getValue()%>" />
            <% }
        } %>

        <script> window.ctxPath = "<%= request.getContextPath() %>";</script>
		<script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/vendor/jquery-1.10.2/jquery.min.js")%>"></script>
		<script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/vendor/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js")%>"></script>
		<script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/js/search.js")%>"></script>
		<script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/js/app.js")%>"></script>

        <%
        @SuppressWarnings("unchecked")
        HashMap<String, String> twitterMetaTags = (HashMap<String, String>)request.getAttribute("twitterMetaTags");
        if (twitterMetaTags != null) {
            for (Entry<String, String> entry : twitterMetaTags.entrySet()) {
                %><meta name="<%=entry.getKey()%>" content="<%=entry.getValue()%>" />
            <% }
        }
        %>

	</head>
	<body>
    <div id="menu">
    	<div id="content-full" class="main-menu">
    		<ul>
			<%if (searchType.startsWith("bill")||searchType.equals("search")||searchType.equals("sponsor")||searchType.equals("committee")){ %>
				<li><a href="<%=JSPHelper.getLink(request, "/bills/")%>"  class="linkActivated" title="Browse and search Senate and Assembly bills by number, keyword, sponsor and more">Bills</a></li>
			<%}else{ %>
				<li><a href="<%=JSPHelper.getLink(request, "/bills/")%>" title="Browse and search Senate and Assembly bills by number, keyword, sponsor and more">Bills</a></li>
			<%} %>
			<li><a href="<%=JSPHelper.getLink(request, "/resolutions/")%>" <%if (searchType.startsWith("resolution")) {%>class="linkActivated"<%} %> title="View senate and assembly resolutions.">Resolutions</a></li>
				<li><a href="<%=JSPHelper.getLink(request, "/calendars/")%>"  <%if (searchType.startsWith("calendar")){%>class="linkActivated"<%} %> title="View recent and search floor calendars and active lists by number or date (i.e. 1/07/2013)">Calendars</a></li>
				<li><a href="<%=JSPHelper.getLink(request, "/meetings/")%>"  <%if (searchType.startsWith("meeting")){%>class="linkActivated"<%} %> title="View upcoming and recent committee meetings, and search by committee, chairperson, location, date (i.e. 1/07/2013) and more.">Meetings</a></li>
				<li><a href="<%=JSPHelper.getLink(request, "/transcripts/")%>" <%if (searchType.startsWith("transcript")){%>class="linkActivated"<%} %> title="View and search Senate floor full text transcripts">Transcripts</a></li>
				<li><a href="<%=JSPHelper.getLink(request, "/actions/")%>"  <%if (searchType.startsWith("action")){%>class="linkActivated"<%} %> title="View and filter Floor Actions on Bills from the Floor of the Senate">Actions</a></li>
				<li><a href="<%=JSPHelper.getLink(request, "/senators")%>">Sponsor</a></li>
				<li><a href="<%=JSPHelper.getLink(request, "/committees")%>">Committee</a></li>
			</ul>
		</div>
		<c:if test="${param.useSearchBar != 'false'}">
		<div id="content" class='searbar'>
			<div id="logobox">
				<a href="<%=JSPHelper.getLink(request, "/")%>"><img src="<%=JSPHelper.getLink(request, "/static/img/openwordlogo.gif")%>" /></a>
			</div>
			<div class='searchbox'>
			<form method="get" action="<%=JSPHelper.getLink(request, "/search/")%>">
				<input type="text" id="txtSearchBox"  name="search" value="<%=search%>" autocomplete="off">	
				<input type="hidden" name="searchType" value="<%=searchType%>">	
				<input type="submit" value="Search"/> | <a href="<%=JSPHelper.getLink(request, "/advanced/")%>">Advanced</a>
				<div id="quickresult" class="quickresult-header"></div>
			</form>
			</div>
		</div>
		</c:if>
	</div>
