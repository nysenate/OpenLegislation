<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, gov.nysenate.openleg.util.OpenLegConstants,gov.nysenate.openleg.search.SenateResponse,gov.nysenate.openleg.search.Result,java.text.SimpleDateFormat, java.util.Iterator"  contentType="text/html" pageEncoding="utf-8" %><%

String requestPath = request.getRequestURI();

String appPath = request.getContextPath();

String term = (String)request.getAttribute("term");

String sortField = (String)request.getAttribute("sortField");
String type = (String)request.getAttribute("type");
String search = (String)request.getAttribute("search");
String urlPath = (String)request.getAttribute("urlPath");
String filter = (String) request.getAttribute("filter");

if(search != null)
	term = search;

term = java.net.URLEncoder.encode(term, "UTF-8");

boolean sortOrder = true;
if (request.getAttribute("sortOrder")!=null)
			sortOrder = Boolean.parseBoolean((String)request.getAttribute("sortOrder"));
			
if (sortField == null)
	sortField = "";
	
if (type == null)
	type = "";

int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));

int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;

SenateResponse sr = (SenateResponse)request.getAttribute("results");
int resultCount = sr.getResults().size();

int total = (Integer)sr.getMetadataByKey("totalresults");

if (total < endIdx) {
	endIdx = total;
}

String mode = (String)request.getAttribute("type");
		
	SimpleDateFormat sdf = new SimpleDateFormat();
	sdf.applyPattern("EEE, MMM d, yyyy");
	
	String prevUrl = null;
if (pageIdx-1 > 0)
{
	if(urlPath != null) {
		prevUrl = urlPath + (pageIdx-1) + (pageSize != 20 ? "/" + pageSize:"") + (filter != null ? "?filter=" + filter:"");
	}
	else {
		prevUrl = "/legislation/search/" + term + "/" + (pageIdx - 1) + "/" + pageSize
			+ "?sort=" + sortField
			+ "&sortOrder=" + sortOrder;
	}
}
	
String nextUrl = null;

if (total > endIdx)
{
	if(urlPath != null) {
		nextUrl = urlPath + (pageIdx+1) + (pageSize != 20 ? "/" + pageSize:"") + (filter != null ? "?filter=" + filter:"");
	}
	else {
		nextUrl = "/legislation/search/" + term + "/" + (pageIdx + 1) + "/" + pageSize
			+ "?sort=" + sortField
			+ "&sortOrder=" + sortOrder;
	}
}
	
%>
<h2 class='page-title'><%=type.toUpperCase()%> SEARCH RESULTS</h2>

<% String encodedTerm = java.net.URLEncoder.encode(term,"UTF-8"); %>
<div class='formats'>
	Formats:
	<a href="/legislation/api/atom/search/<%=encodedTerm%>/">ATOM</a>,
	<a href="/legislation/api/rss/search/<%=encodedTerm%>/">RSS</a>,
	<a href="/legislation/api/json/search/<%=encodedTerm%>/<%=pageIdx%>/<%=pageSize%>">JSON</a>,
	<a href="/legislation/api/xml/search/<%=encodedTerm%>/<%=pageIdx%>/<%=pageSize%>">XML</a>
</div>
<div class="content-bg">

    <div id="subcontent">
 <%
 	if (resultCount>0){
 %>
 <div class="search-nav">
<%-- <%
	if (prevUrl!=null){
%><a href="">&lt; prev</a> - <%
	}
%> --%>
<div class="result-totals">
Showing Results <%=startIdx+1%> - <%=endIdx%> of <%=total%>
</div>
<div class="pagination">
<ul>
<%
	int totalPages = (total+20-1)/20;
	System.out.println(totalPages);
	int currentPage = ((startIdx+20-1)/20)+1;
	System.out.println(currentPage);
	int back4 = (currentPage-4);
	int back3 = (currentPage-3);
	int back2 = (currentPage-2);
	int back1 = (currentPage-1);
	int forward1 = (currentPage+1);
	int forward2 = (currentPage+2);
	int forward3 = (currentPage+3);
	int forward4 = (currentPage+4);

 	System.out.println(totalPages-currentPage);
		
	if (prevUrl!=null){%>
		<li><a href="<%=prevUrl%>" title="Previous page">«</a></li>
	<%}else{%>
		<li class="disabled"><a>«</a></li>
	<%}
	if (currentPage >= totalPages && currentPage > 5){%>
		<li><a href="1">1</a></li>
		<li><a>...</a></li>
	<%}
	if (back4 >= 1 && ((totalPages-currentPage) < 3) ){%>
		<li><a href="<%=back4%>"><%=back4%></a></li>
	<%}
	if (back3 >= 1 && ((totalPages-currentPage) < 2) ){%>
		<li><a href="<%=back3%>"><%=back3%></a></li>
	<%}
	if (back2 >= 1){%>
		<li><a href="<%=back2%>"><%=back2%></a></li>
	<%}
	if (back1 >= 1){%>
		<li><a href="<%=back1%>"><%=back1%></a></li>
	<%}
	 %>
     <li class="active"><a href="<%=currentPage%>"><%=currentPage%></a></li>
     <%
     if (forward1 < totalPages){%>
		<li><a href="<%=forward1%>"><%=forward1%></a></li>
	<%}
     if (forward2 < totalPages){%>
		<li><a href="<%=forward2%>"><%=forward2%></a></li>
	<%}
     if (forward3 < totalPages && currentPage < 3 ){%>
		<li><a href="<%=forward3%>"><%=forward3%></a></li>
	<%}
     if (forward4 < totalPages && currentPage < 2){%>
		<li><a href="<%=forward4%>"><%=forward4%></a></li>
	<%}
     if (forward3 < totalPages){%>
		<li><a>...</a></li>
	<%}
     if (currentPage < totalPages){%>
		<li><a href="<%=totalPages%>"><%=totalPages%></a></li>
	<%}	
	if (nextUrl!=null){%>
		<li><a href="<%=nextUrl%>" title="Next page">»</a></li>
	<%}else{%>
		<li class="disabled"><a>»</a></li>
	<%}
%>
                
 	</ul>
 </div>

            <div class='sortby'>
                Order by:
                <% if (sortField.equals("modified") && sortOrder){%>
                    Recent Updates
                <%} else{ %>
                    <a href="/legislation/search/<%=term%>?sort=modified&sortOrder=true">Recent Updates</a>
                <%}%>,

				<%if (sortField.equals("modified") && (!sortOrder)){%>Oldest Updates<%}
				else{ %><a href="/legislation/search/<%=term%>?sort=modified&sortOrder=false">Oldest Updates</a><%}%>,
				
				<%if (sortField.equals("committee")){%>Committee<%}
				else{ %><a href="/legislation/search/<%=term%>?sort=committee&sortOrder=false">Committee</a><%}%>,
				
				<%if (sortField.equals("")){%>Best Match<%}
				else{ %><a href="/legislation/search/<%=term%>?sort=oid&sortOrder=false"">Best Match</a><%}%>
				</div>
            </div>
            <hr/>
        <% }

		Iterator it = sr.getResults().iterator();
		Result sresult = null;
		  
		String resultType = null;
		String resultId = null;
		 
		String contentType = null;
		String contentId = null;
		String resultTitle = null;

		String senateType = null;
	
	    while (it.hasNext()) {
	        sresult = (Result) it.next();	
            resultType = sresult.getOtype();
            
            //System.out.println("got result type: " + resultType);
            if (resultType.indexOf(".")!=-1) {
                resultType = resultType.substring(resultType.lastIndexOf(".")+1);
            }

            resultId = sresult.getOid();

            contentType = resultType;
            contentId = java.net.URLEncoder.encode(resultId,"UTF-8");
            resultTitle = sresult.getTitle();
            senateType = (String)sresult.getFields().get("type").toUpperCase();

            if (contentType.equals("vote")) {
	            contentType = "bill";
	            contentId = (String)sresult.getFields().get("billno");
            }
            else if (contentType.equals("action")) {
                contentType = "bill";
                contentId = (String)sresult.getFields().get("billno");
            }
             
            if (resultTitle == null) {
                resultTitle = "(no title)";
            } else {
            	if(contentType.equals("bill")) {
            	    senateType += " " + sresult.getFields().get("billno");
                }
            }

            String resultPath = appPath + "/" + contentType + "/" + contentId; %>
            <div class="row" onclick="location.href='<%=resultPath%>'">
                <a href="<%=resultPath%>"><%=senateType%>: <%=resultTitle%></a>
                <span style="font-size:90%;color:#777777;">
                <%if (sresult.getSummary()!=null && sresult.getSummary().length() > 0){ %>
                    <br/>
                    <%=sresult.getSummary() %>
                <%} %>
 
			    <%if (sresult.getFields().get("sameAs")!=null && sresult.getFields().get("sameAs").length()>0){ %>
			        <br/>
			        Same As: <a href="<%=appPath%>/search/?term=oid:%22<%=sresult.getFields().get("sameAs")%>%22" class="sublink"><%=sresult.getFields().get("sameAs")%></a>
			    <%} %>
			 
			    <%if ((!contentType.equals("bill")) && sresult.getFields().get("billno")!=null && sresult.getFields().get("billno").length()>0){ %>
			        <br/>
			        Bill: <a href="<%=appPath%>/search/?term=oid:%22<%=sresult.getFields().get("billno")%>%22" class="sublink"><%=sresult.getFields().get("billno")%></a>
			    <%} %>
			 
			    <%if (sresult.getFields().get("sponsor")!=null && sresult.getFields().get("sponsor").length()>0){ %>
			        <br/>
			        <% if (sresult.getFields().get("billno").equals("J375-2013")) { %>
				        Sponsors: 
				        <a href="<%=appPath%>/sponsor/STEWART-COUSINS" class="sublink">STEWART-COUSINS</a>,
				        <a href="<%=appPath%>/sponsor/SKELOS" class="sublink">SKELOS</a>,
				        <a href="<%=appPath%>/sponsor/KLEIN" class="sublink">KLEIN</a>
				    <% } else {
				        if (sresult.getFields().get("otherSponsors").isEmpty()) { %>
				            Sponsor: <a href="<%=appPath%>/sponsor/<%=sresult.getFields().get("sponsor")%>" class="sublink"><%=sresult.getFields().get("sponsor")%></a>    
				        <% }
				        else { %>
				            Sponsors: <a href="<%=appPath%>/sponsor/<%=sresult.getFields().get("sponsor")%>" class="sublink"><%=sresult.getFields().get("sponsor")%></a>,<%=JSPHelper.getSponsorLinks(sresult.getFields().get("otherSponsors").split(", ?"), appPath) %>
				        <% } %>  
				    <% } %>
			    <%} %>
			 
			    <%if (sresult.getFields().get("chair")!=null){ %>
			        <br/>
			        Chairperson: <a href="<%=appPath%>/search/?term=chair:%22<%=java.net.URLEncoder.encode((String)sresult.getFields().get("chair"),"UTF-8")%>%22"  class="sublink"><%=sresult.getFields().get("chair")%></a>
			    <%} %>
 
			    <%if (sresult.getFields().get("committee")!=null && !sresult.getFields().get("committee").isEmpty()){ %>
			        <br/>
			        Committee: <a href="<%=appPath%>/committee/<%=sresult.getFields().get("committee").replaceAll(" ","-")%>"  class="sublink"><%=sresult.getFields().get("committee")%></a>
			    <%} %>
			 
			 
			    <%if (sresult.getFields().get("location")!=null){ %>
			        <br/>
			        Location: <a href="<%=appPath%>/search/?term=location:<%=java.net.URLEncoder.encode("\"" + sresult.getFields().get("location") + "\"")%>"  class="sublink"><%=sresult.getFields().get("location")%></a>
			    <%} %>
			 
			    <%if (sresult.getFields().get("date")!=null){ %>
			        <br/>
			        Date: <a href="<%=appPath%>/search/?term=<%=java.net.URLEncoder.encode("\"" + sresult.getFields().get("date") + "\"")%>"  class="sublink"><%=sresult.getFields().get("date")%></a>
			    <%} %>
                </span>
            </div>
        <% } %>
        <hr/>
        <% if (resultCount>0) { %>
<div class="footer-pagination">

<div class="pagination">
<ul>
<%
int totalPages = (total+20-1)/20;
int currentPage = ((startIdx+20-1)/20)+1;
int back4 = (currentPage-4);
int back3 = (currentPage-3);
int back2 = (currentPage-2);
int back1 = (currentPage-1);
int forward1 = (currentPage+1);
int forward2 = (currentPage+2);
int forward3 = (currentPage+3);
int forward4 = (currentPage+4);

	System.out.println(totalPages-currentPage);
		
	if (prevUrl!=null){%>
		<li><a href="<%=prevUrl%>" title="Previous page">«</a></li>
	<%}else{%>
		<li class="disabled"><a>«</a></li>
	<%}
	if (currentPage >= totalPages && currentPage > 5){%>
		<li><a href="1">1</a></li>
		<li><a>...</a></li>
	<%}
	if (back4 >= 1 && ((totalPages-currentPage) < 3) ){%>
		<li><a href="<%=back4%>"><%=back4%></a></li>
	<%}
	if (back3 >= 1 && ((totalPages-currentPage) < 2) ){%>
		<li><a href="<%=back3%>"><%=back3%></a></li>
	<%}
	if (back2 >= 1){%>
		<li><a href="<%=back2%>"><%=back2%></a></li>
	<%}
	if (back1 >= 1){%>
		<li><a href="<%=back1%>"><%=back1%></a></li>
	<%}
	 %>
     <li class="active"><a href="<%=currentPage%>"><%=currentPage%></a></li>
     <%
     if (forward1 < totalPages){%>
		<li><a href="<%=forward1%>"><%=forward1%></a></li>
	<%}
     if (forward2 < totalPages){%>
		<li><a href="<%=forward2%>"><%=forward2%></a></li>
	<%}
     if (forward3 < totalPages && currentPage < 3 ){%>
		<li><a href="<%=forward3%>"><%=forward3%></a></li>
	<%}
     if (forward4 < totalPages && currentPage < 2){%>
		<li><a href="<%=forward4%>"><%=forward4%></a></li>
	<%}
     if (forward3 < totalPages){%>
		<li><a>...</a></li>
	<%}
     if (currentPage < totalPages){%>
		<li><a href="<%=totalPages%>"><%=totalPages%></a></li>
	<%}	
	if (nextUrl!=null){%>
		<li><a href="<%=nextUrl%>" title="Next page">»</a></li>
	<%}else{%>
		<li class="disabled"><a>»</a></li>
	<%}
%>
                
 	</ul>
 </div>
        <% } %>
    </div>
