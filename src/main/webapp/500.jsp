<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"/>
<div id="content">
	<div class="content-bg">
		<h2 class='page-title'>Oops, something went wrong!</h2>
		<br/>
		<p>From time to time the Open Legislation system might have a hickup. We apologize for the inconvenience.<br/>
		The best thing to do is reload the page or try your search again. If you would think you've found a serious bug, <a href="<%=JSPHelper.getLink(request, "/feedback")%>">you may submit it here!</a>.
		<br/>
		<br/>
		Otherwise, be assured that we're working away like eager beavers <a href="http://www.nysenate.gov/department/cio">over here</a> to improve this service day by day.
		</p>
		<a href="http://www.nysl.nysed.gov/emblems/animal.htm"><img src="<%=JSPHelper.getLink(request, "/static/img/beaver.gif")%>"/></a>
		<br/>
		<a href="http://www.nysl.nysed.gov/emblems/animal.htm">Learn more about the New York State Beaver!</a>
		<hr/>
    </div>
</div>
<jsp:include page="footer.jsp"/>
