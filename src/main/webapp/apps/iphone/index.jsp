<%@ page language="java" import="java.util.*, java.text.*,java.io.*,javax.jdo.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>
<%


String appPath = request.getContextPath();
Bill bill = null;
String last = null;
DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

PersistenceManager pm = PMF.getPersistenceManager();

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
         "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>NY Senate</title>
<meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<link rel="apple-touch-icon" href="iui/iui-logo-touch-icon.png" />
<meta name="apple-touch-fullscreen" content="YES" />
<style type="text/css" media="screen">@import "iui/iui.css";</style>
<script type="application/x-javascript" src="iui/iui.js"></script>
<script type="text/javascript">
	iui.animOn = true;
</script>


</head>

<body>
    <div class="toolbar">
        <h1 id="pageTitle"></h1>
        <a id="backButton" class="button" href="#"></a>
    </div>
    
    <ul title="NY Senate" selected="true">
    
    <%
ArrayList<BillEvent> eventList = PMF.getBillEvents(0,20);
BillEvent be = null;
Bill lastBill = null;

if (eventList != null)
{
	Iterator<BillEvent> itEvents = eventList.iterator();
	
	while (itEvents.hasNext())
	{
		be = itEvents.next();
		bill = PMF.getBill(pm,be.getBillId());
		
		if (lastBill != null && lastBill.getSenateBillNo().equals(bill.getSenateBillNo()))
			continue;
		
		%>
	<li>
<a style="background-color:#ffffcc" href="bill.jsp?id=<%=bill.getSenateBillNo()%>"><%=bill.getSenateBillNo()%><%if (bill.getSameAs()!=null){ %> (Same as: <%=bill.getSameAs()%>)<%}%></a>
 <span style="font-size:75%"><%=df.format(be.getEventDate())%> <%=be.getEventText()%>
<hr/>
<%if (bill.getTitle()!=null){ %>
<%=bill.getTitle()%>
<%} else if (bill.getSummary()!=null){ %>
 <%=bill.getSummary()%>
 <%} %>
 </span>
</li>
		<%
		
		lastBill = bill;
	}
	
}
	%>
    
       
        <li><a href="page1.html" target="_replace">Get 10 More...</a></li>
    </ul>

</body>
</html>
