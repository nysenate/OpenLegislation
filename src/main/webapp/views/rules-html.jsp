<%@ page language="java" contentType="text/html" pageEncoding="utf-8"%>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="Rules of the Senate 2015-2016 - NY Senate Open Legislation"/>
</jsp:include>
<br/>
<div id="content">
	<div class="content-bg">
		<pre style="overflow-x: none;">
<%=request.getAttribute("rules")%>
		</pre>
	</div>
</div>
<jsp:include page="/footer.jsp"/>
