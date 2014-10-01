<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.Date,java.text.SimpleDateFormat, java.util.ArrayList, gov.nysenate.openleg.model.admin.*, gov.nysenate.openleg.util.JSPHelper" %>
<jsp:include page="/admin/header.jsp">
    <jsp:param value="SpotCheck Reports - OpenLeg Admin" name="title"/>
</jsp:include>
<script>
jQuery(function($) {
    var dataTable = $("#reports").dataTable({
        // They can type in the filter box if they want
        "bLengthChange": false,
        "bPaginate": false,
        "bFilter": true,
        "bSort": true,
        "aaSorting": [[0,"desc"]],
        "aoColumnDefs": [
            { "sWidth":"9%", "aTargets":[1,2,3,4,5,6,7,8,9], "sClass":"data-cell" },
            { "sWidth":"19%", "aTargets":[0], "sClass":"title-cell" }
        ],
        "sDom": '<"hidden-controls"f>lrtip',
    });
    
    $("#report-filter input").keyup(function() {
        dataTable.fnFilter($(this).val(), 0);
    });
    
    $(".report-link").each(function() {
    	var link = $(this);
    	link.parent().click(function() {
    		window.location = link.attr("href");
    	}).addClass("clicky");
    });
});
</script>

<%!
    public boolean isErrorObservation(Report report, ReportObservation obs) {
        for (ReportError closedError : report.getClosedErrors()) {
            if (obs.getErrorId() == closedError.getId()) {
                return true;
            }
        }
        return false;
    }
%>
<style>

.clicky {
    cursor: pointer;
}

#report-filter {
    float: right;
    padding: 10px;
}

#reports {
    border-top: 1px solid #888;
}

#reports th, #reports td {
    border: solid #888;
    border-width: 0px 1px 0px 1px;
}

#reports tr {
    height:40px;
    border-bottom: 1px solid #888;
}

#reports a {
    color: #000;
    text-decoration: none;
}

#reports tr.odd {
    background-color: #F1F1F1;
}

th, .title-cell {
    text-align:center;
}

.data-cell {
    text-align:right;
}

.hidden-controls {
    display: none;
}

#reports_paginate {
    margin:10px 15px 0px 0px; 
}

#reports_info {
    margin:10px 0px 0px 15px;
}

.inline-icon {
    border:0px;
    display: inline-block;
}

</style>
<div id="section-header">
    <label id="report-filter">Filter: <input type="text"/></label>
    <div id="section-title">ErrorReports</div>
    <div style="clear:both"></div>
</div>
<div class="container">
    <table id="reports" class="dataTable">
        <thead>
            <tr>
                <th>Report</th>
                <th>Total</th>
                <th>Opened</th>
                <th>Closed</th>
                <th>Sponsor</th>
                <th>Cosponsor</th>
                <th>Title</th>
                <th>Summary</th>
                <th>Action</th>
                <th>Page</th>
                <th>Amendments</th>
            </tr>
        </thead>
        <tbody>
	     <%
	         @SuppressWarnings("unchecked")
    	     ArrayList<Report> reports = (ArrayList<Report>)request.getAttribute("reportList");
    	     
    	     for(Report report : reports) {
                 int total = report.getObservations().size() - report.getClosedErrors().size();
    	         int summaryTotal = 0;
    	         int titleTotal = 0;
    	         int actionTotal = 0;
    	         int sponsorTotal = 0;
    	         int cosponsorTotal = 0;
    	         int pagesTotal = 0;
                 int amendmentsTotal = 0;
                 for (ReportObservation error : report.getObservations()) {
                     if (!isErrorObservation(report, error)) {
                         switch (ReportError.FIELD.valueOf(error.getField())) {
                             case BILL_SUMMARY:
                                 summaryTotal++;
                                 break;
                             case BILL_TITLE:
                                 titleTotal++;
                                 break;
                             case BILL_ACTION:
                                 actionTotal++;
                                 break;
                             case BILL_SPONSOR:
                                 sponsorTotal++;
                                 break;
                             case BILL_COSPONSOR:
                                 cosponsorTotal++;
                                 break;
                             case BILL_TEXT_PAGE:
                                 pagesTotal++;
                                 break;
                             case BILL_AMENDMENT:
                                 amendmentsTotal++;
                                 break;
                         }
                     }

                 }
	     %>
            <tr>
	            <td class="report-column">
	                <a class="report-link" href="<%=JSPHelper.getLink(request, "/admin/reports/?id="+report.getId()) %>"><%=new SimpleDateFormat("yyyy-MM-dd").format(report.getTime())%> Report</a>
	            </td>
	            <td><%=total%></td>
                <td>
                    <%=report.getNewErrors().size()%> <div class="inline-icon ui-state-error" style="background:inherit; opacity:.75;" title="New Issues"><span class="ui-icon ui-icon-circle-arrow-n"></span></div>
                </td>
                <td>
                    <%=report.getClosedErrors().size()%> <div class="inline-icon ui-state-success" style="background:inherit; opacity:.75;" title="Closed Issues"><span class="ui-icon ui-icon-circle-arrow-s"></span></div>
                </td>
	            <td><%=sponsorTotal%></td>
	            <td><%=cosponsorTotal%></td>
	            <td><%=titleTotal%></td>
	            <td><%=summaryTotal%></td>
	            <td><%=actionTotal%></td>
	            <td><%=pagesTotal%></td>
	            <td><%=amendmentsTotal%></td>
	        </tr>
	     <% } %>            
	     </tbody>
    </table>
</div>
<jsp:include page="/admin/footer.jsp" />