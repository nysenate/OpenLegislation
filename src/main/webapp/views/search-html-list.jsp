<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %>
<%
    response.setHeader("Access-Control-Allow-Origin","*");

	SenateResponse sr = (SenateResponse)request.getAttribute("results");
	int total = (Integer)sr.getMetadataByKey("totalresults");
	String term = java.net.URLEncoder.encode((String)request.getAttribute("term"), "UTF-8");
%>
<li><em><%=total%> total results... (<a href="<%=JSPHelper.getLink(request, "/search/?term="+term)%>">view all</a>)</em></li>
<%
    Iterator<Result> it = sr.getResults().iterator();
    while (it.hasNext()) {
        try {
            Result r = it.next();
            String contentType = r.getOtype();
            String contentId = r.getOid().toUpperCase();
            String contentTitle = r.getTitle();

            if (contentType.equals("vote")) {
                contentType = "bill";
	            contentId = (String)r.getFields().get("billno");
	        }

            if (contentType.equals("action")) {
                contentType = "bill";
	            contentId = (String)r.getFields().get("billno");
	            contentTitle = contentId + " - " + contentTitle;
            }

            String liLink = JSPHelper.getLink(request, "/"+contentType+"/"+contentId);
            String liText = r.getOtype().toUpperCase()+": ";
            if (r.getOtype().equals("bill")) {
                liText += contentId+" ";
            }
            liText += " - "+contentTitle;
            if (r.getFields().get("sameas")!=null) {
                liText += " / Same as: "+r.getFields().get("sameas")+" ";
            }
            if (r.getFields().get("sponsor")!=null && r.getFields().get("sponsor").length()>0) {
                liText += "("+r.getFields().get("sponsor")+")";
            }

            %><li class="quickresult_box"><a href="<%=liLink%>" class="sublink"><%=liText%></a></li><%
        }
        catch (Exception e) {}
    }
%>
