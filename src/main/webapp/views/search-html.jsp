<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %><%

String requestPath = request.getRequestURI();

String appPath = request.getContextPath();

%>
<%
String term = (String)request.getAttribute("term");
String sortField = (String)request.getAttribute("sortField");
String type = (String)request.getAttribute("type");

boolean sortOrder = false;
if (request.getAttribute("sortOrder")!=null)
			sortOrder = Boolean.parseBoolean(request.getParameter("sortOrder"));
			
if (sortField == null)
	sortField = "";
	
if (type == null)
	type = "";

String title = term + " - Search - NY Senate Open Legislation";
%>

<jsp:include page="/header.jsp">
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
			prevUrl = "/legislation/search/?term=" + java.net.URLEncoder.encode(term,"UTF-8") 
		//	+ "&type=" + type 
			+ "&sort=" + sortField
			+ "&sortOrder=" + sortOrder
			 + "&pageIdx=" + (pageIdx-1);

}
	
String nextUrl = null;

if (total > endIdx)
{
	nextUrl = "/legislation/search/?term=" + java.net.URLEncoder.encode(term,"UTF-8")
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
<%if (sortField==null||(!sortField.equals(""))){%><a href="/legislation/search/?term=<%=term%>&sort=&sortOrder=">Best Match</a><%}
else{ %>Best Match<%}%>,

<%if (sortField==null||(!sortField.equals("sponsor"))){%><a href="/legislation/search/?term=<%=term%>&sort=sponsor&sortOrder=false">Sponsor</a><%}
else{ %>Sponsor<%}%>,
<%if (sortField==null||(!sortField.equals("title"))){%><a href="/legislation/search/?term=<%=term%>&sort=title_sortby&sortOrder=false">Title</a><%}
else{ %>Title<%}%>,
<%if (sortField==null||(!sortField.equals("when"))){%><a href="/legislation/search/?term=<%=term%>&sort=when&sortOrder=true">Most Recent</a><%}
else{ %>Most Recent<%}%>


</div>
<hr/>
 <%
 	}
 %>

 <%
Iterator<?> it = srs.getResults().iterator();
  SearchResult sresult = null;
  
  String resultType = null;
  String resultId = null;
  
  String contentType = null;
  String contentId = null;
String resultTitle = null;

  while (it.hasNext())
  {
                sresult = (SearchResult) it.next();
                resultType = sresult.getType();


                if (resultType.indexOf(".")!=-1)
                {
                        resultType = resultType.substring(resultType.lastIndexOf(".")+1);
                }


                resultId = sresult.getId();

                contentType = resultType;
                contentId = resultId;
				resultTitle = sresult.getTitle();
				
                if (contentType.equals("vote"))
                {
                        contentType = "bill";
                        contentId = (String)sresult.getFields().get("billno");
                }
                if (contentType.equals("action"))
                {
                        contentType = "bill";
                        contentId = (String)sresult.getFields().get("billno");
                        
                        resultTitle = contentId + " - " + resultTitle;
                }
                
                 if (contentType.equals("transcript"))
                {
                        contentId = contentId + "?term=" + java.net.URLEncoder.encode(term,"UTF-8") + "&#result";
                        
                }


String resultPath = appPath + "/api/1.0/html/" + contentType + "/" + contentId;

 %>
 <div class="billSummary" onmouseover="this.style.backgroundColor='#FFFFCC'" onmouseout="this.style.backgroundColor='#FFFFFF'" onclick="location.href='<%=resultPath%>'">
<a href="<%=resultPath%>"><%=sresult.getType().toUpperCase()%><%if (resultType.equals("bill")){ %> <%=sresult.getId()%><%} %>
- <%=resultTitle%></a>
<div style="font-size:90%;color:#777777;">


 <%if (sresult.getFields().get("sponsor")!=null){ %>
Sponsor:
 <a href="<%=appPath%>/search/?term=sponsor:<%=sresult.getFields().get("sponsor")%>" class="sublink"><%=sresult.getFields().get("sponsor")%></a>
 <%} %>
 
  <%if (sresult.getFields().get("chair")!=null){ %>
 Chairperson:
 <a href="<%=appPath%>/search/?term=chair:%22<%=java.net.URLEncoder.encode((String)sresult.getFields().get("chair"),"UTF-8")%>%22"  class="sublink"><%=sresult.getFields().get("chair")%></a>
 <%} %>
 
  <%if (sresult.getFields().get("committee")!=null){ %>
 Committee:
 <a href="<%=appPath%>/search/?term=committee:<%=sresult.getFields().get("committee")%>"  class="sublink"><%=sresult.getFields().get("committee")%></a>
 -
 <%} %>
 
 
  <%if (sresult.getFields().get("location")!=null){ %>
 Location:
 <a href="<%=appPath%>/search/?term=location:<%=java.net.URLEncoder.encode("\"" + sresult.getFields().get("location") + "\"")%>"  class="sublink"><%=sresult.getFields().get("location")%></a>
 -
 <%} %>
 
 <%if (sresult.getSummary()!=null && sresult.getSummary().length() > 0){ %>
 <%=sresult.getSummary() %>
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

