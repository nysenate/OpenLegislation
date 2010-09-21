<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %>

<%
String requestPath = request.getRequestURI();

session.setAttribute("lastSearch",requestPath);

String cacheKey = (String)request.getAttribute("path");

String appPath = request.getContextPath();
String term = "";

if (request.getParameter("term")!=null)
	term = request.getParameter("term");
else if (session.getAttribute("term")!=null)
	term = (String)session.getAttribute("term");


String title = term + " - Search - NY Senate Open Legislation";

Collection<Bill> bills = (Collection<Bill>)request.getAttribute("bills");
int total = -1;
if (request.getAttribute("searchTotal")!=null)
	total = Integer.parseInt((String)request.getAttribute("searchTotal"));


int pageIdx = Integer.parseInt((String)session.getAttribute("pageIdx"));
int pageSize = Integer.parseInt((String)session.getAttribute("pageSize"));
int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + bills.size();

String prevUrl = null;
if (pageIdx-1 > 0)
{
	if (requestPath.indexOf("/" + pageIdx + "/")==-1)
	{
		prevUrl = requestPath + "/" + (pageIdx-1)  + "/";
	}
	else
		prevUrl = requestPath.replace("/" + pageIdx + "/","/" + (pageIdx-1) + "/");
}
	
String nextUrl = null;
if (bills.size() >= pageSize)
{

	if (requestPath.indexOf("/" + pageIdx + "/")==-1)
	{
		nextUrl = requestPath + "/" + (pageIdx+1) + "/";
	}
	else
		nextUrl = requestPath.replace("/" + pageIdx + "/","/" + (pageIdx+1) + "/");
	
}

String mode = (String)request.getAttribute("type");

requestPath = appPath + "/api/1.0/html/" + mode + '/' + term + '/' + pageIdx + '/' + pageSize;
String xmlUri = requestPath.replace("html","xml");
String rssUri = requestPath.replace("html","rss");

String csvUri = requestPath.replace("html","csv");	
String jsonUri = requestPath.replace("html","json");	
%>
<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/style.css"/> 
<style>
body
{
background:white;
}
</style>

<%
	DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
%>
<div id="content">
 <%
 	if (bills.size()>0){
 %>
 <div>
<%
	if (prevUrl!=null){
%><a href="<%=prevUrl%>">&lt; prev</a><%
	}
%>
Page <%=pageIdx%> (Results <%=startIdx+1%> - <%=endIdx%> of <%=total%>)
<%
	if (nextUrl!=null){
%><a href="<%=nextUrl%>">next &gt;</a><%
	}
%>
</div>
<hr/>
 <%
 	}
 %>

 <%
 	if (bills.size()==0){
 %>
 Sorry no results were found. 
 <br/><br/>
 <%
 	}
 %>
 <%
 	Iterator<Bill> it = bills.iterator();
  Bill bill = null;
  
  while (it.hasNext())
  {
 	bill = it.next();
 
 	request.setAttribute("bill",bill);	
 %>
 <jsp:include page="/views/bill-sub-html.jsp"/>
 <%} %>
 
 <hr/>
 <%if (bills.size()>0){ %>
 <div>
<%if (prevUrl!=null){%><a href="<%=prevUrl%>">&lt; prev</a><%}%>
Page <%=pageIdx%> (Results <%=startIdx+1%> - <%=endIdx%> of <%=total%>)
<%if (nextUrl!=null){%><a href="<%=nextUrl%>">next &gt;</a><%}%>
</div>
<%} %>

</div>

