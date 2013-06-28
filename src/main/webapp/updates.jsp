<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*"
    import="java.io.*"
    import="gov.nysenate.openleg.util.JSPHelper"
    import="gov.nysenate.openleg.model.Update"
    import="java.text.SimpleDateFormat"
    %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Recent Legislation Updates</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="<%=JSPHelper.getLink(request, "/jquery-ui-1.10.3.min.css")%>" />
    <script type="text/javascript" src="<%=JSPHelper.getLink(request, "/js/jquery-1.9.1.min.js")%>"></script>
    <script type="text/javascript" src="<%=JSPHelper.getLink(request, "/js/jquery-ui-1.10.3.min.js")%>"></script>

    <link rel="stylesheet" type="text/css" href="update.css">
    <script type="text/javascript">
        jQuery(function($) {
            $( "#start" ).datepicker({
                showOtherMonths: true,
                selectOtherMonths: true
            });
            $( "#end" ).datepicker({
                showOtherMonths: true,
                selectOtherMonths: true
            });
        });
    </script>
</head>
<body>
    <div id="exception">
        <%
        Exception exception = (Exception)request.getAttribute("exception");
        if (exception != null) {
            StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));
            %>
            <h2><%=exception.getMessage()%></h2>
            <pre><%=stackTrace.toString()%></pre>
            <%
        }
        %>
    </div>
    <form action="updates" id="date">
        <label>Start: <input id="start" name="start" type="text" value="<c:out value="${param['start']}" default=""/>" /></label>&nbsp;&nbsp;
        <label>End: <input id="end" name="end" type="text" value="<c:out value="${param['end']}" default=""/>" /></label>
        <label>Document Type:
            <select name="otype" onchange="window.open(this.value,'','');">
                <option value="">All</option>
                <option value="bill">Bill</option>
                <option value="calendar">Calendar</option>
                <option value="agenda">Agenda</option>
                <option value="meeting">Meeting</option>
            </select>
        </label>
        <input type="submit">
    </form>
    <div id="updateTable">
        <%
        @SuppressWarnings("unchecked")
        TreeMap<Date, ArrayList<Update>> updates = (TreeMap<Date, ArrayList<Update>>)(request.getAttribute("updates"));
        if(updates != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, ''yyyy");
            SimpleDateFormat linkFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            int i = 0;
            for(Map.Entry<Date, ArrayList<Update>> map : updates.entrySet()) {
                ArrayList<Update> dayUpdates = map.getValue();
                String date = linkFormat.format(map.getKey());
                %>
                <table class="update">
                    <tr class="table" id="date">
                        <td class="table" id="date" colspan="4">
                            <a href="<%="/legislation/updates?startDay=" + date + "&endDay=" + date%>"><%=dateFormat.format(map.getKey())%></a>
                        </td>
                    </tr>
                </table>
                <table class="update">
                    <%
                    Date oldTime = new Date();
                    for(Update update: dayUpdates) {
                        Date time = update.getTime();
                        if (!time.equals(oldTime)) {
                            oldTime = time;
                            i++;
                            %>
                            </table>
                            <table class="update">
                                <tr>
                                    <td class="table" colspan="4" id="expand">
                                        <input id="lnk<%=i%>" type="button" value="[+] Expand" onclick="toggle_visibility('tbl<%=i%>','lnk<%=i%>');">&nbsp;<%=timeFormat.format(time)%>&nbsp;Sobi Changes.
                                    </td>
                                </tr>
                            </table>
                            <table class="update" id="tbl<%=i%>">
                        <%}%>
                        <tr class="hide">
                            <td class="table" id="time"><a href="#<%=update.getOid()%>"><%=timeFormat.format(time)%></a></td>
                            <td class="table" id="otype"><%=update.getOtype()%></td>
                            <%if(update.getOtype().equals("bill")) {
                                String  url = "http://open.nysenate.gov/legislation/bill/" + update.getOid();
                                %>
                                <td class="table" id="oid">
                                    <a name=<%=update.getOid()%>></a>
                                    <a href=<%=url%>><%=update.getOid()%></a>
                                </td>
                            <% } else {%>
                                <td class="table" id="oid"><a name=<%=update.getOid()%>></a><%=update.getOid() %></td>
                            <%} %>
                            <td class="table" id="status"><a href="<%="/legislation/updates?bill="+update.getOid()%>"><%=update.getStatus()%></a></td>
                        </tr>
                    <% } %>
                </table>
            <%}%>
        <%}%>
    </div>
</body>
</html>