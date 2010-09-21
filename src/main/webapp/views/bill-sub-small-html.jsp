<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %>
<%
	String requestPath = (String)session.getAttribute("path");
session.setAttribute("lastSearch",requestPath);

String appPath = request.getContextPath();

Bill bill = (Bill)request.getAttribute("bill");
DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

%>
 <div class="billSummary" style="font-size:.9em">
 <h4><a href="<%=appPath%>/api/html/bill/<%=bill.getSenateBillNo()%>"><%=bill.getSenateBillNo()%><%if (bill.getSameAs()!=null){ %> (Same as: <%=bill.getSameAs()%>)<%}%></a>

 </h4>
<div class="billSummarySmall">
  <%if (bill.getSponsor()!=null){ %>
 Sponsor: <a href="<%=appPath%>/api/html/sponsor/<%=bill.getSponsor().getFullname()%>"><%=bill.getSponsor().getFullname()%></a>
 &nbsp;/&nbsp;
 <%} %>

  <%if (bill.getCurrentCommittee()!=null){ %>
 Committee: <a href="<%=appPath%>/api/html/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>"><%=bill.getCurrentCommittee()%></a>
 
 <%} %>
 </div>

 

 </div>
 
