<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.ArrayList,gov.nysenate.openleg.model.Error"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<% ArrayList<Error> errorLog= (ArrayList<Error>)request.getAttribute("errorList");
 %>
<form>
<table border="1">
 <% for(Error e:errorLog){  %>
    <tr>
    <td><%= e.getBillId() %></td>
    <td><%= e.getReportId() %></td>
    <td><%= e.getErrorInfo() %></td>
    <td><%= e.getJson() %></td>
    <td><%= e.getLbdc() %></td>
    </tr>
 <% } %>
 </table>
 </form>
</body>
</html>