<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %>

<%
	String requestPath = (String)session.getAttribute("path");
session.setAttribute("lastSearch",requestPath);

String appPath = request.getContextPath();

Bill bill = (Bill)request.getAttribute("bill");
Vote vote = (Vote)request.getAttribute("vote");

String voteType = "Floor";

if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
{
	voteType = "Committee";
	
}
	
DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

%>
 <div class="billSummary">
 <h4><a href="<%=appPath%>/bill/<%=bill.getSenateBillNo()%>"><%=bill.getSenateBillNo()%> - Vote</a>:
<%=df.format(vote.getVoteDate())%> - <%=voteType%>
<span>
<%if (vote.getAyes()!=null){ %>-
<%=vote.getAyes().size()%> Ayes <%} %>
<%if (vote.getNays()!=null){ %>
/ <%=vote.getNays().size()%> Nays
<%} %>
<%if (vote.getAbstains()!=null) { %> / <%=vote.getAbstains().size()%> Abstains<%} %>
<%if (vote.getExcused()!=null) { %> / <%=vote.getExcused().size()%> Excused<%} %>
</span>

 </h4>


<div class="billSummarySmall">
  <%if (bill.getSponsor()!=null && bill.getSponsor().getFullname()!=null){ %>
 Sponsor: <a href="<%=appPath%>/sponsor/<%=bill.getSponsor().getFullname()%>"><%=bill.getSponsor().getFullname()%></a>
 &nbsp;/&nbsp;
 <%} %>


  <%if (bill.getLaw()!=null){ %>
 Law: <%=bill.getLaw()%>
 &nbsp;/&nbsp;
 <%} %>
  <%if (bill.getLawSection()!=null){ %>
 Law Section: <%=bill.getLawSection()%>
 &nbsp;/&nbsp;
 <%} %>
  <%if (bill.getCurrentCommittee()!=null){ %>
 Committee: <a href="<%=appPath%>/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>"><%=bill.getCurrentCommittee()%></a>
 &nbsp;/&nbsp;
 <%} %>
 Formats: 
 <a href="<%=appPath%>/api/1.0/xml/bill/<%=bill.getSenateBillNo()%>">XML</a>,
 <a href="<%=appPath%>/api/1.0/csv/bill/<%=bill.getSenateBillNo()%>">CSV</a>,
 <a href="<%=appPath%>/api/1.0/json/bill/<%=bill.getSenateBillNo()%>">JSON</a>
 </div>

 

 </div>
 
