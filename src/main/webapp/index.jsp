<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%
// We are here, they didn't want mobile?
// TOOD: What does the mobile attribute do?
session.removeAttribute("mobile");

// The appPath used to be openleg, to avoid breaking URLs we still host at that context path
// and redirect to the new correct one (/legislation)
if (request.getContextPath().equals("/openleg")) {
	response.sendRedirect(request.getRequestURI().replace("/openleg","/legislation"));
	return;
}
%>
<jsp:include page="/header.jsp">
    <jsp:param value="The New York Senate Open Legislation Service" name="title"/>
    <jsp:param value="false" name="useSearchBar"/>
</jsp:include>
<div id="content" >	
	<div class="homelogo">
		<div><a href="<%=JSPHelper.getLink(request, "")%>"><img src="<%=JSPHelper.getLink(request, "/static/img/openleglogo.gif")%>" /></a></div>
		<div class="hometext">
			<div>
				<h2 class="homeText">Browse, search and share legislative<br/>
				information from the New York State Senate</h2>
			</div>
			<form method="get" action="<%=JSPHelper.getLink(request, "/search/")%>">
				<input type="text" id="txtSearchBox" style="width:300px" name="search" autocomplete="off">
				<input type="submit" value="Search"/> 
				<span style="color:#999;margin:3px;font-size:12px;">
					<a href="<%=JSPHelper.getLink(request, "/advanced/")%>">Advanced</a>
				</span>
				<div id="quickresult"></div>
			</form>
		</div>
	</div>
</div>
<jsp:include page="/footer.jsp"/>
