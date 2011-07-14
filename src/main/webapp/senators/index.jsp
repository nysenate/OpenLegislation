<%@ page language="java" import="org.json.*,java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%


%>

<jsp:include page="/header.jsp">
	<jsp:param name="title" value="Senators"/>
</jsp:include>
<style>
.senatorColumn
{
float:left;
width:350px;

}

.senatorBlock
{
width:300px;
height:100px;
font-size:14px;
line-height:16px;
margin:3px;
}
</style>

	<h2>Senators (2011-2012 Session)</h2>
 <div id="content">
  
 <div class="senatorColumn">
 <%
 ArrayList<JSONObject> districts =  (ArrayList<JSONObject>)request.getAttribute("districts");
 int idx = 0;
 
 for (JSONObject jDistrict : districts)
 {
	 idx++;
	 JSONObject jSenator = jDistrict.getJSONObject("senator");
	 
	 String senatorKey = jSenator.getString("key");
	 
	 String searchUrl = "/legislation/sponsor/" + java.net.URLEncoder.encode(senatorKey, "utf-8") + "?filter=oid:s*";
	 String searchUrlMeetings = "/legislation/search/?term=" + java.net.URLEncoder.encode("chair:\"" + jSenator.get("name") + "\"", "UTF-8");
		
	 String district =(String)jDistrict.get("district");
	 
	 if (district.indexOf("State Senate")!=-1)
		 district = district.substring(12).trim();
	 %>
	 <div class="senatorBlock">
	 	
	 	<a href="<%=searchUrl%>"><img src="<%=jSenator.get("imageUrl")%>" style="float:left;margin-right:12px"/></a>
	 	
	 	<a href="<%=searchUrl%>"><%=jSenator.get("name")%></a>
	 	<span style="color:#333">|
	 		<a href="<%=jSenator.get("url")%>" style="text-decoration:none;">Contact</a>
	 		</span>
	 	<br/>
	 	<%=district%><br/>
	 	<a href="<%=searchUrl%>">Sponsored Bills</a>
	 	
	 <br/>
	 </div>
	 <%
	 if (idx > 20)
	 {
		 idx = 0;
		 %>
	</div>
	 <div class="senatorColumn">
	
		 <%
	 }
	 
	 
 }
 
 %> 
 </div>
  <br style="clear:both;"/>
  


<hr/>
 <em>You can also view the archived <a href="/legislation/senators/2009">2009-2010 Session Senators</a></em>

 </div>
   
 <jsp:include page="/footer.jsp"/>
   
    
