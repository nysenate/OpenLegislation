<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %>

<%
String requestPath = (String)session.getAttribute("path");
session.setAttribute("lastSearch",requestPath);

String appPath = request.getContextPath();

Bill bill = (Bill)request.getAttribute("bill");


DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);



 %>
 <div class="billSummary">
 <b>

 <a href="<%=appPath%>/bill/<%=bill.getSenateBillNo()%>"><%=bill.getSenateBillNo()%><%if (bill.getSameAs()!=null){ %> (Same as: <%=bill.getSameAs()%>)<%}%>:</a>
  
</b>

 <%if (bill.getTitle()!=null){ %>
<%=bill.getTitle()%>
<%} else if (bill.getSummary()!=null){ %>
 <%=bill.getSummary()%>
 <%} %>

 

 </div>
