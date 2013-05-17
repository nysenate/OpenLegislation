<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.ArrayList,gov.nysenate.openleg.model.Error,gov.nysenate.openleg.util.OpenLegConstants,org.apache.abdera.model.Entry,org.apache.abdera.Abdera,org.apache.abdera.model.Feed,org.json.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <link href="<%=request.getContextPath()%>/bootstrap.css" rel="stylesheet">
 <link href="<%=request.getContextPath()%>/bootstrap-responsive.css" rel="stylesheet">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/diff.js"></script>
<% ArrayList<Error> errorLog= (ArrayList<Error>)request.getAttribute("errorList");
	int resultCount = errorLog.size();
	
	 %>
		

<title>Error Report</title>

</head>
<jsp:include page="/reportHeader.jsp"/>

<body  onload="validate()">

<form id="form1" name="report">

<table class="table table-condensed" id="errortable">
 <thead>
<tr>
<th> Bill Id</th>
<th class="sorttable_nosort"> Error Info</th>
<th class="sorttable_nosort"> Difference</th>
</tr>
</thead>
<tbody>
 <% for(Error e:errorLog){ 
       
       
      if(e.getJson().isEmpty()) {%>
        
       <tr style="background-color:#FDD017" class= "a">
       <td class ="span1" id="<%= e.getBillId() %>"><a href="#<%= e.getBillId() %>"><p style="color:red"><%= e.getBillId() %></p></a></td>
       <td class ="span1  "><p style="color:red"><%= e.getErrorType().toUpperCase()%></p></td>
       <td class="span10"> <%= e.getLbdc() %></td></tr>
       <tr><td class="span1"></td> <td class="span1"></td>
       <td class="span10" id="values">
       <div class="accordion-group">
         <div class="accordion-heading">
             <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#<%= e.getId() %>">
                  View Details
                </a>
              </div>
             
      
         
              <div id="<%= e.getId() %>" class="accordion-body collapse" style="height: 0px; ">
                <div class="accordion-inner">
                  LBDC Value:<label> <%= e.getLbdc() %>
                 </label>               <br> 
                JSON Value:<label>  <%= e.getJson() %></label>
                </div>
              </div>
                </div>
            </td>
     </tr>
     
    <% } else if(e.getErrorType().equalsIgnoreCase("title")) {%>
     
      <tr style="background-color:#6AFB92" class= "a">
      <td class ="span1" id="<%= e.getBillId() %>"><a href="#<%= e.getBillId() %>"><%= e.getBillId() %></a></td>
    <td class ="span1  "><%= e.getErrorType().toUpperCase()%></td>
    <td class="span10">  </td></tr>
       <tr><td class="span1"></td> <td class="span1"></td>
       <td class="span10" id="values">
       <div class="accordion-group">
         <div class="accordion-heading">
             <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#<%= e.getId() %>">
                  View Details
                </a>
              </div>
             
      
         
              <div id="<%= e.getId() %>" class="accordion-body collapse" style="height: 0px; ">
                <div class="accordion-inner">
                 LBDC Value:<label> <%= e.getLbdc() %>
                 </label>               <br> 
                JSON Value:<label>  <%= e.getJson() %></label>
                </div>
              </div>
                </div>
            </td>
     </tr>
   
    <%  }  else if(e.getErrorType().equalsIgnoreCase("summary")) { %>
  
    <tr style="background-color:lightblue" class= "a">
    <td class ="span1" id="<%= e.getBillId() %>"><a href="#<%= e.getBillId() %>"><%= e.getBillId() %></a></td>
    <td class ="span1  "><%= e.getErrorType().toUpperCase()%></td>
    <td class="span10">  </td></tr>
      <tr><td class="span1"></td> <td class="span1"></td>
       <td class="span10" id="values">
       <div class="accordion-group">
         <div class="accordion-heading">
             <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#<%= e.getId() %>">
                  View Details
                </a>
              </div>
             
      
         
              <div id="<%= e.getId() %>" class="accordion-body collapse" style="height: 0px; ">
                <div class="accordion-inner">
                LBDC Value:<label> <%= e.getLbdc() %>
                 </label>               <br> 
                JSON Value:<label>  <%= e.getJson() %>
                </label>
                </div>
              </div>
                </div>
            </td>
     </tr>
    <% } else if(e.getErrorType().equalsIgnoreCase("sponsor")) {%>
    <tr style="background-color:#9E7BFF" class= "a">
    <td class ="span1" id="<%= e.getBillId() %>"><a href="#<%= e.getBillId() %>"><%= e.getBillId() %></a></td>
    <td class ="span1  "><%= e.getErrorType().toUpperCase()%></td>
    <td class="span10">  </td></tr>
     <tr><td class="span1"></td> <td class="span1"></td>
       <td class="span10" id="values">
       <div class="accordion-group">
         <div class="accordion-heading">
             <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#<%= e.getId() %>">
                  View Details
                </a>
              </div>
             
      
         
              <div id="<%= e.getId() %>" class="accordion-body collapse" style="height: 0px; ">
                <div class="accordion-inner">
                  LBDC Value:<label> <%= e.getLbdc() %>
                 </label>               <br> 
                JSON Value:<label>  <%= e.getJson() %></label>
                </div>
              </div>
                </div>
            </td>
     </tr>
     <% } else if(e.getErrorType().equalsIgnoreCase("cosponsor")) {%>
    <tr style="background-color: #dcff7a" class= "a">
   <td class ="span1" id="<%= e.getBillId() %>"><a href="#<%= e.getBillId() %>"><%= e.getBillId() %></a></td>
    <td class ="span1  "><%= e.getErrorType().toUpperCase()%></td>
    <td class="span10">  </td></tr>
       <tr><td class="span1"></td> <td class="span1"></td>
       <td class="span10" id="values">
       <div class="accordion-group">
         <div class="accordion-heading">
             <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#<%= e.getId() %>">
                  View Details
                </a>
              </div>
             
      
         
              <div id="<%= e.getId() %>" class="accordion-body collapse" style="height: 0px; ">
                <div class="accordion-inner">
                  LBDC Value:<label> <%= e.getLbdc() %>
                 </label>               <br> 
                JSON Value:<label>  <%= e.getJson() %></label>
                </div>
              </div>
                </div>
            </td>
     </tr>
    <%  } else {%>
    <tr style="background-color:#FFF380" class= "a">
    <td class ="span1" id="<%= e.getBillId() %>"><a href="#<%= e.getBillId() %>"><%= e.getBillId() %></a></td>
    <td class ="span1  "><%= e.getErrorType().toUpperCase()%></td>
    <td class="span10">  </td></tr>
      <tr><td class="span1"></td> <td class="span1"></td>
       <td class="span10" id="values">
       <div class="accordion-group">
         <div class="accordion-heading">
             <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#<%= e.getId() %>">
                  View Details
                </a>
              </div>
         
              <div id="<%= e.getId() %>" class="accordion-body collapse" style="height: 0px; ">
                <div class="accordion-inner">
                 LBDC Value:<label> <%= e.getLbdc() %>
                 </label>               <br> 
                JSON Value:<label>  <%= e.getJson() %></label>
                </div>
              </div>
                </div>
            </td>
     </tr>
    <%  } 
      }%> 
      </tbody>  
 </table>
 </form>
 <script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/bootstrap-2.3.1.js"></script>
 <script type="text/javascript">

function validate()
{
	 var table = document.getElementById('errortable');
     for (var r = 1, n = table.rows.length; r < n; r=r+2) {
    		
    	 if((document.getElementsByTagName('label')[r+1].firstChild.data).length!=0){
            var lbdc= document.getElementsByTagName('label')[r].firstChild.data;
            var json=document.getElementsByTagName('label')[r+1].firstChild.data;
           
             table.rows[r].cells[2].innerHTML= diffString(lbdc,json);
    	 }
     }
 }

</script>

</body>
</html>