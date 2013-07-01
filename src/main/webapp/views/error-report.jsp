<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.ArrayList,gov.nysenate.openleg.model.Error,gov.nysenate.openleg.util.OpenLegConstants,org.apache.abdera.model.Entry,org.apache.abdera.Abdera,org.apache.abdera.model.Feed,org.json.*"%>
<%
String appPath = request.getContextPath();

@SuppressWarnings("unchecked")
ArrayList<Error> errorLog= (ArrayList<Error>)request.getAttribute("errorList");
int resultCount = errorLog.size();

%>
<!DOCTYPE html>
<html>
<head>
    <title>Open Legislation Error Report</title>

    <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
    <meta name="apple-mobile-web-app-capable" content="YES"/>

    <link rel="shortcut icon" href="<%=appPath%>/static/img/nys_favicon_0.ico" type="image/x-icon" />
    <link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="<%=appPath%>/feed" />

    <link href="<%=request.getContextPath()%>/static/css/bootstrap.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/static/css/bootstrap-responsive.css" rel="stylesheet">

    <script type="text/javascript" src="<%=appPath%>/static/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="<%=appPath%>/static/js/bootstrap-2.3.1.js"></script>
    <script type="text/javascript" src="<%=appPath%>/static/js/search.js"></script>
    <script type="text/javascript" src="<%=appPath%>/static/js/diff.js"></script>
    
    <script type="text/javascript">
		$(document).ready(function(){
			$(".errorDetails").each(function() {
				var id = $(this).attr("id").substring(12);
				var diffCell = $("#errorHeader"+id+" .diff");
				var jsonCell = $(".json", this);
				var lbdcCell = $(".lbdc", this);
			    
			    if (jsonCell.html().length == 0) {
			    	diffCell.html(lbdcCell.val());
			    }
			    else {
			        diffCell.html(diffString(lbdcCell.html(),jsonCell.html()));
			    }
			});
		});
	</script>
	<style>
	#errorTable {
	    width:1000px;
	    margin: 0px auto;
	}
	
	del {
	   color:red;
	}
	</style>
</head>
<body>
<div>
    <table class="table table-condensed" id="errorTable">
        <thead>
            <tr>
				<th>Bill Id</th>
				<th class="sorttable_nosort">Error Info</th>
				<th class="sorttable_nosort">Difference</th>
			</tr>
        </thead>
        <tbody>
        <%
        for(Error e:errorLog) {
            
            String textColor = "black";
            String backgroundColor = "#FFF380";
            if(e.getJson().isEmpty()) {
                backgroundColor = "#FDD017";
                textColor = "red";
                
            } else if (e.getErrorType().equalsIgnoreCase("title")) {
                backgroundColor = "#6AFB92";
            
            } else if (e.getErrorType().equalsIgnoreCase("summary")) {
                backgroundColor = "lightblue";
            
            } else if (e.getErrorType().equalsIgnoreCase("sponsor")) {
                backgroundColor = "#9E7BFF";
                
            } else if(e.getErrorType().equalsIgnoreCase("cosponsor")) {
                backgroundColor = "#dcff7a";
                
            }
            %>
            <tr style="background-color:<%=backgroundColor%>" id="errorHeader<%=e.getId()%>">
			    <td class="span1" id="<%=e.getBillId()%>">
			        <a href="#<%=e.getBillId() %>" style="color:<%=textColor%>"><%=e.getBillId()%></a>
			    </td>
			    <td class ="span1" style="color:<%=textColor%>"><%=e.getErrorType().toUpperCase()%></td>
			    <td class="span10"><pre class="diff"></pre></td>
			</tr>
			<tr id="errorDetails<%=e.getId()%>" class="errorDetails">
	            <td class="span1"></td>
	            <td class="span1"></td>
	            <td class="span10" id="values">
			        <div class="accordion-group">
			            <div class="accordion-heading">
			                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#<%= e.getId() %>">
			                    View Details
			                </a>
			            </div>
			            <div id="<%= e.getId() %>" class="accordion-body collapse" style="height: 0px;">
                            <div class="accordion-inner">
			                    <label>LBDC Value:</label><pre class="lbdc"><%= e.getLbdc() %></pre><br> 
			                    <label>JSON Value:</label><pre class="json"><%= e.getJson() %></pre>
			                </div>
			            </div>
		            </div>
                </td>
            </tr>
        <% } %>
        </tbody>  
    </table>
</div>
</body>
</html>