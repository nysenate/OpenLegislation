<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %><%

String requestPath = request.getRequestURI();

String appPath = request.getContextPath();

String term = (String)request.getAttribute("term");
String sortField = (String)request.getAttribute("sortField");
String type = (String)request.getAttribute("type");

boolean sortOrder = true;
if (request.getAttribute("sortOrder")!=null)
			sortOrder = Boolean.parseBoolean(request.getParameter("sortOrder"));
			
if (sortField == null)
	sortField = "";
	
if (type == null)
	type = "";

String title = term + " - Search - NY Senate Open Legislation";
%>

<jsp:include page="/views/mobile-header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
	<jsp:param name="showTypeFilter" value="true"/>
</jsp:include>
<br/>
<%

int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));

int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;

SearchResultSet srs = (SearchResultSet)request.getAttribute("results");
int resultCount = srs.getResults().size();

int total = srs.getTotalHitCount();

if (total < endIdx)
	endIdx = total;

String mode = (String)request.getAttribute("type");
	


	DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
	
	String prevUrl = null;
if (pageIdx-1 > 0)
{
			prevUrl = "/legislation/search/?format=mobile&term=" + java.net.URLEncoder.encode(term,"UTF-8") 
		//	+ "&type=" + type 
			+ "&sort=" + sortField
			+ "&sortOrder=" + sortOrder
			 + "&pageIdx=" + (pageIdx-1);

}
	
String nextUrl = null;

if (total > endIdx)
{
	nextUrl = "/legislation/search/?format=mobile&term=" + java.net.URLEncoder.encode(term,"UTF-8")
//	 + "&type=" + type 
	 + "&sort=" + sortField
	+ "&sortOrder=" + sortOrder		
	 + "&pageIdx=" + (pageIdx+1);

}
	
%>
<div style="float:left">
<h4><%=type.toUpperCase()%> SEARCH RESULTS</h4>
</div>

<div style="float:right">

Formats:
<a href="/legislation/search/?term=<%=term%>&format=xml">XML</a>,
<a href="/legislation/search/?term=<%=term%>&format=json">JSON</a>,
<a href="/legislation/search/?term=<%=term%>&format=rss">RSS</a>
</div>
<br style="clear:both;"/>
 <div id="content">
 <%
 	if (resultCount>0){
 %>
 <div>
<%
	if (prevUrl!=null){
%><a href="<%=prevUrl%>">&lt; prev</a> - <%
	}
%>

Page <%=pageIdx%> (Results <%=startIdx+1%> - <%=endIdx%> of <%=total%>) -


<%
	if (nextUrl!=null){
%><a href="<%=nextUrl%>">next &gt;</a> |<%
	}
	
	term = java.net.URLEncoder.encode(term,"UTF-8");
%>


Order by: 
<%if (sortField.equals("when") && sortOrder){%>Recent Updates<%}
else{ %><a href="/legislation/search/?format=mobile&term=<%=term%>&sort=when&sortOrder=true">Recent Updates</a><%}%>,

<%if (sortField.equals("when") && (!sortOrder)){%>Oldest Updates<%}
else{ %><a href="/legislation/search/?format=mobile&term=<%=term%>&sort=when&sortOrder=false">Oldest Updates</a><%}%>,

<%if (sortField.equals("committee")){%>Committee<%}
else{ %><a href="/legislation/search/?format=mobile&term=<%=term%>&sort=committee&sortOrder=false">Committee</a><%}%>,


<%if (sortField.equals("")){%>Best Match<%}
else{ %><a href="/legislation/search/?format=mobile&term=<%=term%>">Best Match</a><%}%>



</div>
<hr/>
 <%
 	}
 %>

 <%
Iterator it = srs.getResults().iterator();
  SearchResult sresult = null;
  
  String resultType = null;
  String resultId = null;
  
  String contentType = null;
  String contentId = null;
String resultTitle = null;

	String senateType = null;
	
  while (it.hasNext())
  {
                sresult = (SearchResult) it.next();
                resultType = sresult.getType();

				//System.out.println("got result type: " + resultType);
                if (resultType.indexOf(".")!=-1)
                {
                        resultType = resultType.substring(resultType.lastIndexOf(".")+1);
                }

                resultId = sresult.getId();

                contentType = resultType;
                contentId = java.net.URLEncoder.encode(resultId,"UTF-8");
				resultTitle = sresult.getTitle();
				senateType = (String)sresult.getFields().get("type").toUpperCase();
				
                if (contentType.equals("vote"))
                {
                        contentType = "bill";
                        contentId = (String)sresult.getFields().get("billno");
                }
                if (contentType.equals("action"))
                {
                        contentType = "bill";
                        contentId = (String)sresult.getFields().get("billno");
                        
                }
                
                 if (contentType.equals("transcript"))
                {
                        contentId = contentId + "?term=" + java.net.URLEncoder.encode(term,"UTF-8") + "&#result";
                        
                }
                 
                 if (resultTitle == null)
                	 resultTitle = "(no title)";
                 

String resultPath = appPath + "/api/1.0/mobile/" + contentType + "/" + contentId;

 %>
 <div class="billSummary" onmouseover="this.style.backgroundColor='#FFFFCC'" onmouseout="this.style.backgroundColor='#FFFFFF'" onclick="location.href='<%=resultPath%>'">
<a href="<%=resultPath%>"><%=senateType%>: <%=resultTitle%></a>
<div style="font-size:90%;color:#777777;">


 <%if (sresult.getSummary()!=null && sresult.getSummary().length() > 0){ %>
 <%=sresult.getSummary() %> /
 <%} %>
 
 <%if (sresult.getFields().get("sameAs")!=null && sresult.getFields().get("sameAs").length()>0){ %>
Same As: <a href="<%=appPath%>/search/?format=mobile&term=oid:%22<%=sresult.getFields().get("sameAs")%>%22" class="sublink"><%=sresult.getFields().get("sameAs")%></a>
 <%} %>
 
  <%if (sresult.getFields().get("billno")!=null && sresult.getFields().get("billno").length()>0){ %>
Bill: <a href="<%=appPath%>/search/?format=mobile&term=oid:%22<%=sresult.getFields().get("billno")%>%22" class="sublink"><%=sresult.getFields().get("billno")%></a>
 <%} %>
 
 <%if (sresult.getFields().get("sponsor")!=null && sresult.getFields().get("sponsor").length()>0){ %>
Sponsor: <a href="<%=appPath%>/search/?format=mobile&term=sponsor:%22<%=sresult.getFields().get("sponsor")%>%22" class="sublink"><%=sresult.getFields().get("sponsor")%></a>
 <%} %>
 
  <%if (sresult.getFields().get("chair")!=null){ %>
Chairperson: <a href="<%=appPath%>/search/?format=mobile&term=chair:%22<%=java.net.URLEncoder.encode((String)sresult.getFields().get("chair"),"UTF-8")%>%22"  class="sublink"><%=sresult.getFields().get("chair")%></a>
 <%} %>
 
  <%if (sresult.getFields().get("committee")!=null){ %>
Committee: <a href="<%=appPath%>/search/?format=mobile&term=committee:%22<%=sresult.getFields().get("committee")%>%22"  class="sublink"><%=sresult.getFields().get("committee")%></a>
 <%} %>
 
 
  <%if (sresult.getFields().get("location")!=null){ %>
Location: <a href="<%=appPath%>/search/?format=mobile&term=location:<%=java.net.URLEncoder.encode("\"" + sresult.getFields().get("location") + "\"")%>"  class="sublink"><%=sresult.getFields().get("location")%></a>
 <%} %>
 
   <%if (sresult.getFields().get("date")!=null){ %>
Date: <a href="<%=appPath%>/search/?format=mobile&term=<%=java.net.URLEncoder.encode("\"" + sresult.getFields().get("date") + "\"")%>"  class="sublink"><%=sresult.getFields().get("date")%></a>
 <%} %>
 
 
</div>
 <%if (sortField==null){ %>
 <em style="color:#999;font-size:90%">Search Score: <%=sresult.getScore()%>
 </em>
 <%} %>
</div>

 <%} %>
 
 <hr/>
 <%if (resultCount>0){ %>
 <div>
<%if (prevUrl!=null){%><a href="<%=prevUrl%>">&lt; prev</a><%}%>
Page <%=pageIdx%> (Results <%=startIdx+1%> - <%=endIdx%> of <%=total%>)
<%if (nextUrl!=null){%><a href="<%=nextUrl%>">next &gt;</a><%}%>
</div>
<%} %>

</div>
<jsp:include page="/footer.jsp"/>

