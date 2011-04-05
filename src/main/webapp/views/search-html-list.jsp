<%@ page language="java" import="org.json.*,java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %><%

response.setHeader("Access-Control-Allow-Origin","*");

SenateResponse sr = (SenateResponse)request.getAttribute("results");
int resultCount = sr.getResults().size();

int total = (Integer)sr.getMetadataByKey("totalresults");

Iterator<Result> it = sr.getResults().iterator();
Result r = null;

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
        
                r = it.next();
        
                contentType = r.getOtype();
                contentId = r.getOid();
                contentTitle = r.getTitle();

                if (contentType.equals("vote"))
                {
                        contentType = "bill";
                        contentId = (String)r.getFields().get("billno");
                }

                if (contentType.equals("action"))
                {
                        contentType = "bill";

                        contentId = (String)r.getFields().get("billno");
                        contentTitle = contentId + " - " + contentTitle;
                }
        
        %>
        <li class="quickresult_box"><a href="http://open.nysenate.gov/legislation/<%=contentType%>/<%=contentId%>" class="sublink">

        <%=r.getOtype().toUpperCase()%>:
        <%if (r.getOtype().equals("bill")){ %>
        <%=r.getOid()%> 
        <%} %>
        - <%=contentTitle%>
         <%if (r.getFields().get("sameas")!=null){ %>
        / Same as: <%=((String)r.getFields().get("sameas"))%>
        <%} %>
        <%if (r.getFields().get("sponsor")!=null && r.getFields().get("sponsor").length()>0){ %>
        (<%=((String)r.getFields().get("sponsor"))%>)
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
