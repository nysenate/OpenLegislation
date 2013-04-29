<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*"
    import="gov.nysenate.openleg.model.Update"
    import="java.text.SimpleDateFormat"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="update.css">
<title>Recent Legislation Updates</title>
</head>
<body>
<form action="updates" id="date">
	Start: <input type="date" value=<%=request.getAttribute("startDay")%> name="startDay">&nbsp;&nbsp;
	End: <input type="date" value=<%=request.getAttribute("endDay")%> name="endDay">
  <input type="submit">
</form>
	<div id="updateTable">
		<table id="update">
<%
	TreeMap<Date, ArrayList<Update>> updates = (TreeMap<Date, ArrayList<Update>>)(request.getAttribute("updates")); 
if(updates != null){ 
	SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, ''yyyy");
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	for(Map.Entry<Date, ArrayList<Update>> map : updates.entrySet()){
		ArrayList<Update> dayUpdates = map.getValue();
%>
		<tr class="table" id="date">
		<td class="table" id="date" colspan="4"><%=dateFormat.format(map.getKey())%></td>
		</tr>
		<%
			for(Update update: dayUpdates){ 
			Date time = update.getDateObj();
		%>
		<tr class="table">
			<td class="table" id="time"><%=timeFormat.format(time)%>
			<td class="table" id="otype"><%=update.getOtype()%></td>
			<%if(update.getOtype().equals("bill")){
				String  url = "http://open.nysenate.gov/legislation/bill/" + update.getOid();
			%>
				<td class="table" id="oid"><a href=<%=url%>><%=update.getOid()%></a></td>
			<%} 
			else {%>
			<td class="table" id="oid"><%=update.getOid() %></td>
			<%} %>
			<td class="table" id="status"><%=update.getStatus()%></td>
		</tr>
		<%}%>

	<%}%>
	</table>
</div>
<%}%>
</body>
</html>