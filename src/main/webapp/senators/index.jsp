<%@ page language="java" import="org.json.*,java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%


%>

<jsp:include page="/header.jsp">
	<jsp:param name="title" value="Senators"/>
</jsp:include>
<style>
.senatorBlock
{
width:300px;
height:100px;
float:left;
font-size:14px;
line-height:16px;
margin:3px;
}
</style>

	<h2>Senators (2011-2012 Session)</h2>
 <div id="content">
  
 
 <%
 ArrayList<JSONObject> districts =  (ArrayList<JSONObject>)request.getAttribute("districts");
 
 for (JSONObject jDistrict : districts)
 {
	 JSONObject jSenator = jDistrict.getJSONObject("senator");
	 
	 String senatorKey = jSenator.getString("key");
	 
	 
	 String searchUrl = "/legislation/search/?term=" + java.net.URLEncoder.encode("sponsor:\"" + senatorKey + "\"", "UTF-8");
	 %>
	 <div class="senatorBlock">
	 	
	 	<img src="<%=jSenator.get("imageUrl")%>" style="float:left;margin-right:3px"/>
	 	
	 	<%=jSenator.get("name")%><br/>
	 	<%=jDistrict.get("district")%><br/>
	 	<a href="<%=searchUrl%>">View Legislation</a><br/>
	 	<a href="<%=jSenator.get("url")%>">Visit Website</a><br/>
	 </div>
	 <%
 }
 
 %> 
  <br style="clear:both;"/>
  


<hr/>
 <em>You can also view the archived <a href="2009">2009-2010 Session Senators</a></em>

 </div>
   
 <jsp:include page="/footer.jsp"/>
   
    
