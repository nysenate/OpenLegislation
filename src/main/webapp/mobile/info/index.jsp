<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*" pageEncoding="UTF-8"%>

<%

String userAgent = request.getHeader("user-agent").toLowerCase();
String header = "/header.jsp";

if (userAgent.indexOf("mobile") != -1 || userAgent.indexOf("wap") != -1 || userAgent.indexOf("blackberry")!=-1
|| userAgent.indexOf("wml")!=-1 || userAgent.indexOf("nokia")!=-1|| userAgent.indexOf("midp")!=-1 ||userAgent.indexOf("mobi")!=-1)
 {

      header = "/views/mobile-header.jsp";
}

 %>
<jsp:include page="<%=header%>"/>


<div id="content">

<p>
	You can access the legislative information that matters to you - wherever you are - using Open Legislation mobile tools. A mobile phone or an iPad is all you need to access all of the information available from the Open Legislation service.
</p>
<p>
	Here is the current list of mobile tools available:
</p>

<h2>NY Senate Services</h2>

<ul>
	<li>
		<b>NY Senate Mobile Apps</b>
		<p>
			The New York Senate has created the first state legislative mobile apps for Android, iPhone and the iPad. The apps provide quick access to the latest news, legislation, agendas, calendars, votes, videos and more. This isn't just brochure-ware, it is a real-time constituent mobile dashboard to the legislative process: connect with Senators, find and comment on bills, review votes and transcripts, watch full session and hearing videos.
		</p>
	</li>
	<li>
		<b>NY Senate Mobile Website</b>
		<p>
			Any mobile phone (including Blackberry, Nokia, Palm and Windows Mobile) with a web browser can access the same legislative data through the NY Senate Mobile Website at <a href="http://m.nysenate.gov">http://m.nysenate.gov</a>
		</p>
	</li>
</ul>
</div>

 <jsp:include page="../../footer.jsp"/>
   
    
