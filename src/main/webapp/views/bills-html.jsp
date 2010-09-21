<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %><%

String requestPath = request.getRequestURI();


String appPath = request.getContextPath();

CachedContentManager.fillCache(request);

Collection<Bill> bills = (Collection<Bill>)request.getAttribute("bills");

String term = "";

if (request.getParameter("term")!=null)
	term = request.getParameter("term");
else if (request.getAttribute("term")!=null)
	term = (String)request.getAttribute("term");

String title = term + " - Search - NY Senate Open Legislation";

request.setAttribute("searchType","bill");

%>

<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
	<jsp:param name="showTypeFilter" value="true"/>
</jsp:include>

<%

int total = Integer.parseInt((String)request.getAttribute("searchTotal"));

int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));

int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;

if (total < endIdx)
	endIdx = total;

String mode = (String)request.getAttribute("type");

requestPath = appPath + "/api/1.0/html/" + mode + '/' + term + '/' + pageIdx + '/' + pageSize;
String xmlUri = requestPath.replace("html","xml");
String rssUri = requestPath.replace("html","rss");

String csvUri = requestPath.replace("html","csv");	
String jsonUri = requestPath.replace("html","json");	


	DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
	
	String prevUrl = null;
if (pageIdx-1 > 0)
{
	if (requestPath.indexOf("/" + pageIdx + "/")==-1)
	{
		prevUrl = requestPath + "/" + (pageIdx-1)  + "/";
	}
	else
		prevUrl = requestPath.replace("/" + pageIdx + "/","/" + (pageIdx-1) + "/");
}
	
String nextUrl = null;

if (total > endIdx)
{
	if (requestPath.indexOf("/" + pageIdx + "/")==-1)
	{
		nextUrl = requestPath + "/" + (pageIdx+1) + "/";
	}
	else
		nextUrl = requestPath.replace("/" + pageIdx + "/","/" + (pageIdx+1) + "/");	
}
	
%>

<h2>Bills
<small><em>Search by bill number, sponsor name or words in the bill memo and full text</em></small>
</h2>
 <div id="content">
 <%
 	if (bills.size()>0){
 %>
 <div>
<%


	if (prevUrl!=null){
%><a href="<%=prevUrl%>">&lt; prev</a><%
	}
%>
Page <%=pageIdx%> (Results <%=startIdx+1%> - <%=endIdx%> of <%=total%>)
<%
	if (nextUrl!=null){
%><a href="<%=nextUrl%>">next &gt;</a><%
	}
%>

</div>
<hr/>
 <%
 	}
 %>

 <%
 	if (bills.size()==0){
 %>
Sorry no results were found for that search.
<hr/>
There are currently five different distinct paths for searching on Open Legislation:
<br/><br/>

1) <b>By KEYWORD:</b> type one or more words in the text box at the top of any screen; this performs a keyword search on the Title, Summary, Memo, Bill Number and Sponsor fields of all bills (and resolutions), and any bill containing any of the keywords will be returned.  Highest numbered bills are returned first, at the top of the list.  TIP: you can also use the "AND" operator or "" (quotation marks) to narrow your keyword search to more precise matches.

<br/><br/>
2) <b>By RECENT ACTIONS:</b> click the "Recent Actions" hyperlink to return the bills with the most recent activity; you may then further refine this search by using the drop-down menu to filter by various types of recent actions; these filters search for the respective action keywords within the "action" text field.

<br/><br/>
3) <b>By RECENT VOTES:</b> click the "Recent Votes" hyperlink to return the bills most recently voted upon in the Senate; note that this search will not return Assembly bills.

<br/><br/>
4) <b>By SPONSOR:</b> click on the "By Sponsor" hyperlink to get a listing of all Senators, from which you can click on any Senator to see a full listing of bills they have sponsored.

<br/><br/>
5) <b>By COMMITTEE:</b> click on the "By Committee" hyperlink to get a listing of all Senate Standing Committees, from which you can click on any Committee to see a full listing of bills from that Committee; note that this search will not return any Assembly Committees or bills.
<br/><br/>
<hr/>
*Note: Except where otherwise noted, all searches return both Senate and Assembly bills.<br/>
*Note: when entering a Bill number, OMIT any leading zero (0).  <br/><br/>
 <%
 	}
 %>
 <%
 	Iterator<Bill> it = bills.iterator();
  Bill bill = null;
  
  while (it.hasNext())
  {
 	bill = it.next();
 
 	request.setAttribute("bill",bill);	
 %>
 <jsp:include page="/views/bill-sub-html.jsp"/>
 <%} %>
 
 <hr/>
 <%if (bills.size()>0){ %>
 <div>
<%if (prevUrl!=null){%><a href="<%=prevUrl%>">&lt; prev</a><%}%>
Page <%=pageIdx%> (Results <%=startIdx+1%> - <%=endIdx%> of <%=total%>)
<%if (nextUrl!=null){%><a href="<%=nextUrl%>">next &gt;</a><%}%>
/
Formats:
HTML,
<a href="<%=xmlUri%>">XML</a>, 
<a href="<%=rssUri%>">RSS</a>, 
<a href="<%=csvUri%>">CSV</a>,
<a href="<%=jsonUri%>">JSON</a>
</div>
<%} %>

</div>
<jsp:include page="/footer.jsp"/>

