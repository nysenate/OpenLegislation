<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*" pageEncoding="UTF-8"%>

<%

String userAgent = request.getHeader("user-agent").toLowerCase();
String header = "/header.jsp";


 %>
<jsp:include page="<%=header%>"/>


 <div id="content">
Learn more here: <a href="http://wiki.github.com/nysenatecio/OpenLeg">http://wiki.github.com/nysenatecio/OpenLeg</a>
<br/><br/>
<iframe src="http://wiki.github.com/nysenatecio/OpenLeg" width="1000" height="800"></iframe>
</div>

 
   
 <jsp:include page="/footer.jsp"/>
   
