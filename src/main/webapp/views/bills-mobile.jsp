<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="utf-8" contentType="text/html"%><%



String appPath = request.getContextPath();
String term = "";

if (request.getParameter("term")!=null)
	term = request.getParameter("term");
else if (session.getAttribute("term")!=null)
	term = (String)session.getAttribute("term");
	
String requestPath = (String)session.getAttribute("path");
session.setAttribute("lastSearch",requestPath);

String title = "NY Senate OpenLeg - Search - " + term;

Collection<Bill> bills = (Collection<Bill>)request.getAttribute("bills");


int pageIdx = Integer.parseInt((String)session.getAttribute("pageIdx"));
int pageSize = 5;//Integer.parseInt((String)session.getAttribute("pageSize"));
int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + bills.size();

String prevUrl = null;
if (pageIdx-1 > 0)
{
	if (requestPath.indexOf("/" + pageIdx)==-1)
	{
		prevUrl = requestPath + "/" + (pageIdx-1);
	}
	else
		prevUrl = requestPath.replace("/" + pageIdx,"/" + (pageIdx-1));
}
	
String nextUrl = null;
if (bills.size() >= pageSize)
{

	if (requestPath.indexOf("/" + pageIdx)==-1)
	{
		nextUrl = requestPath + "/" + (pageIdx+1);
	}
	else
		nextUrl = requestPath.replace("/" + pageIdx,"/" + (pageIdx+1));
	
}
 %>
 
<jsp:include page="mobile-header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>

<%

DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

 %>
  <div>
<%if (prevUrl!=null){%><a href="<%=prevUrl%>">&lt; prev</a><%}%>
Page <%=pageIdx%> (Results <%=startIdx+1%> - <%=endIdx%>)
<%if (nextUrl!=null){%><a href="<%=nextUrl%>">next &gt;</a><%}%>
</div>
 <div id="content">


 <%
 Iterator<Bill> it = bills.iterator();
 Bill bill = null;
 
 while (it.hasNext())
 {
	bill = it.next();
	try
	{
  %>
 <div class="billSummary">
 <b><a href="<%=appPath%>/api/mobile/bill/<%=bill.getSenateBillNo()%>"><%=bill.getSenateBillNo()%><%if (bill.getSameAs()!=null){%>(Same as: <%=bill.getSameAs()%>)<%}%></a></b>
 
 <%if (bill.getTitle()!=null){ %>
<%=bill.getTitle()%>
<%} else if (bill.getSummary()!=null){ %>
 <%=bill.getSummary()%>
 <%} %>
 <br/>
 

  <%if (bill.getSponsor()!=null){ %>
 Sponsor: <a href="<%=appPath%>/api/mobile/sponsor/<%=java.net.URLEncoder.encode(bill.getSponsor().getFullname(),"utf-8")%>"><%=bill.getSponsor().getFullname()%></a>
 <%} %>
/ <%if (bill.getCurrentCommittee()!=null){ %>
 Committee: <a href="<%=appPath%>/api/mobile/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>"><%=bill.getCurrentCommittee()%></a>
 <%} %>

 

 </div>
 <%
 } catch (Exception e)
 {
 System.out.println("error rendering bill: " + e);
 }
 } %>
 <hr/>
  <div>
<%if (prevUrl!=null){%><a href="<%=prevUrl%>">&lt; prev</a><%}%>
Page <%=pageIdx%> (Results <%=startIdx+1%> - <%=endIdx%>)
<%if (nextUrl!=null){%><a href="<%=nextUrl%>">next &gt;</a><%}%>
</div>
</div>
<jsp:include page="/footer.jsp"/>
