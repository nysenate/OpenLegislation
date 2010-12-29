<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%

String appPath = request.getContextPath();
session.removeAttribute("term");

String searchPath = appPath + "/sponsor";

Bill bill = null;
String last = null;
DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);


String legTypeFilter = request.getParameter("type");

String stringEventSearch = request.getParameter("action");

if (stringEventSearch == null)
	stringEventSearch = "Passed";
	
Date startDate = null;
Date endDate = null;

if (request.getParameter("year") != null)
{
	int year = Integer.parseInt(request.getParameter("year"));
	int month = Integer.parseInt(request.getParameter("month"));
	int day = Integer.parseInt(request.getParameter("day"));
	
	Calendar now = Calendar.getInstance();
	now.clear();
	now.set(year,month-1,day,0,0,0);
	startDate = now.getTime();
	
	now.set(year,month-1,day,11,59,59);
	endDate = now.getTime();
}

long start = 0;
long end = 25;

String actionSearchKey = "actionSearchKeyFoo";

%>

<jsp:include page="../header.jsp">
	<jsp:param name="title" value="Senators"/>
</jsp:include>
<style>
.views-row
{
float:left;
margin:6px;
width:140px;
height:130px;
font-size:9pt;
line-height:13px;
}

.contact, .social_buttons
{
display:none;
}
</style>

	<h2>Senators (2011-2012 Session)</h2>
 <div id="content">
  
 <em>Currently being updated / You can view the <a href="2009">2009-2010 Session Senators</a></em>
  
</div>

<br style="clear:both;"/>

 
   
 <jsp:include page="/footer.jsp"/>
   
    
