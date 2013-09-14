<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.TreeMap, java.util.Map.Entry,java.util.TreeMap,java.io.StringWriter, java.io.PrintWriter, java.util.Date,java.text.SimpleDateFormat, java.util.ArrayList,gov.nysenate.openleg.model.Change, gov.nysenate.openleg.util.JSPHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/admin/header.jsp">
    <jsp:param value="Change Log Reports - OpenLeg Admin" name="title"/>
</jsp:include>
<script>
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
<style>

#alert {
    background-color: #FFFFCC;
    padding: 10px;
    font-weight:bold;
    text-align:center;
}

#controls {
    padding: 10px;
    float: right;
}

    #start, #end {
        width: 95px;
    }
    
    #otype {
        width: 100px;
    }
    
    #oid {
        width: 80px;
    }
    
    #submit {
        padding: 3px;
    }

#updateTable {
    width:800px;
    margin:0px auto;
}

.field {
    float: left;
}

.day-block {
    margin-bottom: 20px;
}

.day-header {
    font-size: 2em;
    font-weight: bold;
    background-color: #D3D6FF;
    padding:10px;
}

.time-block {
    margin-left:15px;
    background-color: #EAEBFF;
}

.change {
    background-color: #FFFFFF;
    padding:3px 3px 3px 5px;
}

.field {
    width:30%;
}

</style>
<div id="section-header">
    <form action="" id="controls">
        Date Range: <input id="start" name="start" type="text" value="<c:out value="${param['start']}" default=""/>" />
        TO <input id="end" name="end" type="text" value="<c:out value="${param['end']}" default=""/>" />
        <label>Doc ID: <input id="oid" name="oid" type="text" value="<c:out value="${param['oid']}" default=""/>" /></label>
        <label>Doc Type:
            <select id="otype" name="otype">
                <option value="">All</option>
                <option value="bill">Bill</option>
                <option value="calendar">Calendar</option>
                <option value="agenda">Agenda</option>
                <option value="meeting">Meeting</option>
            </select>
        </label>
        <input id="submit" type="submit" value="Search" />
    </form>
    <div id="section-title">Change Log Entries</div>
    <div style="clear:both"></div>
</div>
<div class="container">
    <div id="alert">
        <%
        String warning = (String)request.getAttribute("warning");
        if (warning != null) {
            %><%=warning%><%
        }
        %>
    </div>
    <div id="updateTable">
        <%
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
        SimpleDateFormat linkFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        @SuppressWarnings("unchecked")
        TreeMap<Date, TreeMap<Date, ArrayList<Change>>> changes = (TreeMap<Date, TreeMap<Date, ArrayList<Change>>>)(request.getAttribute("changes"));
        if(changes != null) {
            int i = 0;
            for(Entry<Date, TreeMap<Date, ArrayList<Change>>> dayChangeEntry : changes.entrySet()) {
                TreeMap<Date, ArrayList<Change>> dayChanges = dayChangeEntry.getValue();
                String date = linkFormat.format(dayChangeEntry.getKey());
                %>
                <div class="day-block">
                    <div class="day-header">
                        <%=date%><!-- <a href="<%="/legislation/updates?startDay=" + date + "&endDay=" + date%>">(this date only)</a>  -->
                    </div>
                    <div>
                    <%
                    for (Entry<Date, ArrayList<Change>> timeChangeEntry : dayChanges.entrySet()) {
                        ArrayList<Change> timeChanges = timeChangeEntry.getValue();
                        String time = timeFormat.format(timeChangeEntry.getKey());
                        %>
                        <div class="time-block">
                            <div><%=time%> SOBI Changes</div>
                            <div>
                            <% for (Change change : timeChanges) {
                                 // TODO: Finish this bit of logic up!
                                 String url = JSPHelper.getLink(request, "/" + change.getOtype() + "/" + change.getOid());
                                 %>
                                 <div class="change">
                                    <div class="field otype"><%=change.getOtype().toUpperCase()%></div>
                                    <div class="field oid">
                                        <a id="<%=change.getOid()%>" href="<%=url%>"><%=change.getOid()%></a>
                                    </div>
                                    <div class="field status">
                                        <a href="<%="/legislation/updates?bill="+change.getOid()%>"><%=change.getStatus()%></a>
                                    </div>
                                    <div style="clear:both;"></div>
                                 </div>
                            <% } %>
                            </div>
                        </div>
                    <% } %>
                    </div>
                </div>
            <%
            }
        }
        %>
    </div>
</div>
<jsp:include page="/admin/footer.jsp" />