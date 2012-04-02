<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,org.json.*,gov.nysenate.openleg.model.*"  pageEncoding="utf-8" contentType="text/plain"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

Collection<Bill> bills = (Collection<Bill>)request.getAttribute("bills");

DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

 %>Year,SenateBillNo,SameAs,Sponsor,Committee,Title,Summary,Action
<%
 Iterator<Bill> it = bills.iterator();
 Bill bill = null;
 String title, summary;
 while (it.hasNext())
 {
	bill = it.next();
	
	title = bill.getTitle();
	summary = bill.getSummary();
	
	
	try
	{
%><%=bill.getYear()%>,<%=bill.getSenateBillNo()%>,<%=bill.getSameAs()%>,<%=bill.getSponsor().getFullname()%>,<%=bill.getCurrentCommittee()%>,<%=title%>,<%=summary%>
<%
 } catch (Exception e){}
 } 
%>

