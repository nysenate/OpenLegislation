<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %>

<%
	String requestPath = (String)session.getAttribute("path");
session.setAttribute("lastSearch",requestPath);

String appPath = request.getContextPath();

Bill bill = (Bill)request.getAttribute("bill");
BillEvent be =(BillEvent)request.getAttribute("billEvent");


DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);




if (bill == null)
	return;
	
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
 
