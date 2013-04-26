<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.ArrayList,gov.nysenate.openleg.model.Error,gov.nysenate.openleg.util.OpenLegConstants,org.apache.abdera.model.Entry,org.apache.abdera.Abdera,org.apache.abdera.model.Feed,org.json.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <link href="<%=request.getContextPath()%>/bootstrap.css" rel="stylesheet">
 <link href="<%=request.getContextPath()%>/bootstrap-responsive.css" rel="stylesheet">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/diff.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.3.2.js"></script>
<% ArrayList<Error> errorLog= (ArrayList<Error>)request.getAttribute("errorList");
	int resultCount = errorLog.size();
	
	 %>
		

<title>Error Report</title>
</head>
<jsp:include page="/reportHeader.jsp"/>

<body>

<form id="form1">

<table class="table table-hover">
<tr>
<th> Bill Id</th>
<th> Report Id</th>
<th> Error Info</th>
<th> Lbdc Value</th>
<th> Json Value</th>
</tr>
 <% for(Error e:errorLog){ 
       
      
      if(e.getJson().isEmpty()) {%>
       <tr style="background-color:#FDD017">
       <td class ="span1"><%= e.getBillId() %></td>
       <td class ="span1"><%= e.getReportId() %></td>
      <td class ="span2"><%= e.getErrorType().toUpperCase()%></td>
      <td class ="span4"><%= e.getLbdc() %></td>
      <td class ="span4"><label style="color:red"> Alert! No entry in json file.</label> </td>
      </tr>
    <% } else if(e.getErrorType().equalsIgnoreCase("title")) {%>
    <tr style="background-color:#6AFB92">
    <td class ="span1"><%= e.getBillId() %></td>
       <td class ="span1"><%= e.getReportId() %></td>
      <td class ="span2"><%= e.getErrorType().toUpperCase()%></td>
      <td class ="span4"><%= e.getLbdc() %></td>
      <td class ="span4"><%= e.getJson() %> </td>
    </tr>
    <%  }  else if(e.getErrorType().equalsIgnoreCase("summary")) { %>
    <tr style="background-color:lightblue">
      <td class ="span1"><%= e.getBillId() %></td>
       <td class ="span1"><%= e.getReportId() %></td>
      <td class ="span2"><%= e.getErrorType().toUpperCase()%></td>
      <td class ="span4"><%= e.getLbdc() %></td>
      <td class ="span4"><%= e.getJson() %> </td>
    </tr>
    <% } else if(e.getErrorType().equalsIgnoreCase("sponsor")) {%>
    <tr style="background-color:#9E7BFF">
    <td class ="span1"><%= e.getBillId() %></td>
       <td class ="span1"><%= e.getReportId() %></td>
      <td class ="span2"><%= e.getErrorType().toUpperCase()%></td>
      <td class ="span4"><%= e.getLbdc() %></td>
      <td class ="span4"><%= e.getJson() %> </td>
    </tr>
     <% } else if(e.getErrorType().equalsIgnoreCase("cosponsor")) {%>
    <tr style="background-color: #dcff7a">
    <td class ="span1"><%= e.getBillId() %></td>
       <td class ="span1"><%= e.getReportId() %></td>
      <td class ="span2"><%= e.getErrorType().toUpperCase()%></td>
      <td class ="span4"><%= e.getLbdc() %></td>
      <td class ="span4"><%= e.getJson() %> </td>
    </tr>
    <%  } else {%>
    <tr style="background-color:#FFF380">
    <td class ="span1"><%= e.getBillId() %></td>
       <td class ="span1"><%= e.getReportId() %></td>
      <td class ="span2"><%= e.getErrorType().toUpperCase()%></td>
      <td class ="span4"><%= e.getLbdc() %></td>
      <td class ="span4"><%= e.getJson() %> </td>
    </tr>
    <%  } 
      }%>   
 </table>
 </form>
</body>
</html>