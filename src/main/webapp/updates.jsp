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
<script type="text/javascript">
function toggle_visibility(tbid,lnkid)
{
  document.getElementById(tbid).style.display = document.getElementById(tbid).style.display == "table" ? "none" : "table";
  document.getElementById(lnkid).value = document.getElementById(lnkid).value == "[-] Collapse" ? "[+] Expand" : "[-] Collapse";
 }
</script>
</head>
<body>
<form action="updates" id="date">
	Start: <input type="date" value=<%=request.getAttribute("startDay")%> name="startDay">&nbsp;&nbsp;
	End: <input type="date" value=<%=request.getAttribute("endDay")%> name="endDay">
  <input type="submit">
</form>
<form action="" id="type">
Filter by Type:
<select name="otype" onchange="window.open(this.value,'','');">
<option value="/legislation/updates?<%="startDay="+request.getAttribute("startDay")+"&endDay="+request.getAttribute("endDay")%>">All</option>
<option value="/legislation/updates?<%="startDay="+request.getAttribute("startDay")+"&endDay="+request.getAttribute("endDay")+"&otype=bill"%>">Bill</option>
<option value="/legislation/updates?<%="startDay="+request.getAttribute("startDay")+"&endDay="+request.getAttribute("endDay")+"&otype=calendar"%>">Calendar</option>
<option value="/legislation/updates?<%="startDay="+request.getAttribute("startDay")+"&endDay="+request.getAttribute("endDay")+"&otype=agenda"%>">Agenda</option>
<option value="/legislation/updates?<%="startDay="+request.getAttribute("startDay")+"&endDay="+request.getAttribute("endDay")+"&otype=meeting"%>">Meeting</option>
</select>
</form>
	<div id="updateTable">
<%
	TreeMap<Date, ArrayList<Update>> updates = (TreeMap<Date, ArrayList<Update>>)(request.getAttribute("updates")); 
if(updates != null){ 
	SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, ''yyyy");
	SimpleDateFormat linkFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	int i = 0;
	for(Map.Entry<Date, ArrayList<Update>> map : updates.entrySet()){
		ArrayList<Update> dayUpdates = map.getValue();
		String date = linkFormat.format(map.getKey());
%>
		<table class="update">
		<tr class="table" id="date">
		<td class="table" id="date" colspan="4"><a href="<%="/legislation/updates?startDay=" + date + "&endDay=" + date%>">
		<%=dateFormat.format(map.getKey())%></a></td>
		</tr>
		</table>
		<table class="update">
		<%	
			Date oldTime = new Date();
			for(Update update: dayUpdates){ 
				Date time = update.getDateObj();

				if (!time.equals(oldTime)){
					i++;
		%>
		</table>
		<table class="update">
		<tr>
		<td class="table" colspan="4" id="expand"><input id="lnk<%=i%>" type="button" value="[+] Expand" onclick="toggle_visibility('tbl<%=i%>','lnk<%=i%>');">&nbsp;<%=timeFormat.format(time)%>&nbsp;Sobi Changes. </td>
		</tr>
		</table>
		<table class="update" id="tbl<%=i%>">
		<%}%>
		<tr class="hide">
			<td class="table" id="time"><a href="#<%=update.getOid()%>"><%=timeFormat.format(time)%></a></td>
			<td class="table" id="otype"><%=update.getOtype()%></td>
			<%if(update.getOtype().equals("bill")){
				String  url = "http://open.nysenate.gov/legislation/bill/" + update.getOid();
			%>
				<td class="table" id="oid"><a name=<%=update.getOid()%>></a>
				<a href=<%=url%>><%=update.getOid()%></a></td>
			<%}
			else {%>
			<td class="table" id="oid"><a name=<%=update.getOid()%>></a><%=update.getOid() %></td>
			<%} %>
			<td class="table" id="status"><a href="<%="/legislation/updates?bill="+update.getOid()%>"><%=update.getStatus()%></td>
		</tr>
		<%
			oldTime = time;
			}%>

	<%}%>
	</table>
</div>
<%}%>
<br><br><br><br><br>
</body>
</html>