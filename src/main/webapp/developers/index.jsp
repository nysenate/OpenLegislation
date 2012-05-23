<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*" pageEncoding="UTF-8"%>

<%

String userAgent = request.getHeader("user-agent").toLowerCase();
String header = "/header.jsp";


 %>
<jsp:include page="<%=header%>"/>


<div id="content">
Learn more at the following links:<br/>
<br/>
<a href="http://github.com/nysenate/OpenLegislation">http://github.com/nysenate/OpenLegislation</a><br/>
<br/>
<a href="http://openlegislation.readthedocs.org/">http://openlegislation.readthedocs.org/</a><br/>
<br/><br/>
</div>

<jsp:include page="/footer.jsp"/>
   
