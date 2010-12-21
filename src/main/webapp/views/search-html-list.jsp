<%@ page language="java" import="org.json.*,java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %><%

response.setHeader("Access-Control-Allow-Origin","*");

SearchResultSet srs = (SearchResultSet)request.getAttribute("results");
int resultCount = srs.getResults().size();

int total = srs.getTotalHitCount();

Iterator<SearchResult> it = srs.getResults().iterator();
SearchResult sr = null;

String term = java.net.URLEncoder.encode((String)request.getAttribute("term"),"UTF-8");
%>
<li>
<em><%=total%> total results... (<a href="http://open.nysenate.gov/legislation/search/?term=<%=term%>">view all</a>)</em>
</li>
<%
String contentType = null;
String contentId = null;
String contentTitle = null;

while (it.hasNext())
{

        
        try
        {
        
                sr = it.next();
        
                contentType = sr.getType();
                contentId = sr.getId();
                contentTitle = sr.getTitle();

                if (contentType.equals("vote"))
                {
                        contentType = "bill";
                        contentId = (String)sr.getFields().get("billno");
                }

                if (contentType.equals("action"))
                {
                        contentType = "bill";

                        contentId = (String)sr.getFields().get("billno");
                        contentTitle = contentId + " - " + contentTitle;
                }
        
        %>
        <li class="quickresult_box"><a href="http://open.nysenate.gov/legislation/<%=contentType%>/<%=contentId%>" class="sublink">

        <%=sr.getType().toUpperCase()%>:
        <%if (sr.getType().equals("bill")){ %>
        <%=sr.getId()%> 
        <%} %>
        - <%=contentTitle%>
         <%if (sr.getFields().get("sameas")!=null){ %>
        / Same as: <%=((String)sr.getFields().get("sameas"))%>
        <%} %>
        <%if (sr.getFields().get("sponsor")!=null && sr.getFields().get("sponsor").length()>0){ %>
        (<%=((String)sr.getFields().get("sponsor"))%>)
        <%} %>
       
        </a>
        </li>
        <%
                
        
        }
        catch (Exception e)
        {
                //error with this bill
        }
        
        
        

}

%>
