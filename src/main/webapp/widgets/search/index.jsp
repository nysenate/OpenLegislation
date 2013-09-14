<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper"  contentType="text/html" pageEncoding="utf-8" %>
<script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/js/jquery-1.9.1.min.js")%>"></script>
<script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/js/search.js")%>"></script>
<link rel="stylesheet" type="text/css" media="screen" href="<%=JSPHelper.getLink(request, "/static/css/style.css")%>"/>
<br/><br/><br/>
<center>
<div>
	<form method="get" action="/legislation/search/">
		<input type="text" id="txtSearchBox" style="width:300px" name="term" autocomplete="off"/>
        <input type="submit" value="Search"/>
        <a href="http://open.nysenate.gov/legislation/advanced/">Advanced</a>
        <div id="quickresult"></div>
    </form>
</div>
<br/>
<br/>
<br/>
<hr/>
<em>add this box to your site!</em><br/>
<textarea cols=80 rows=10>
<script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/js/jquery-1.9.1.min.js")%>"></script>
<script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/js/search.js")%>"></script>
<link rel="stylesheet" type="text/css" media="screen" href="<%=JSPHelper.getLink(request, "/static/css/style.css")%>"/>
<div>
    <form method="get" action="http://open.nysenate.gov/legislation/search/">
        <input type="text" id="txtSearchBox" style="width:300px" name="search" autocomplete="off"/>
        <input type="submit" value="Search"/>
        <a href="http://open.nysenate.gov/legislation/advanced/">Advanced</a>
        <div id="quickresult"></div>
    </form>
</div>
</textarea>
</center>
