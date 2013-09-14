<%@ page language="java" import="gov.nysenate.openleg.util.*, java.util.*, gov.nysenate.openleg.model.*"  contentType="application/rss+xml" pageEncoding="utf-8" %><%

String term = (String)request.getAttribute("term");
SenateResponse sr = (SenateResponse)request.getAttribute("results");

// Use the most recently modified result data if available
Date lastBuildDate = new Date();
if (sr.getResults().size() > 0) {
    lastBuildDate = new Date(sr.getResults().get(0).getLastModified());
}

%><?xml version="1.0" encoding="utf-8"?>
<rss version="2.0"  xmlns:atom="http://www.w3.org/2005/Atom">
  <channel>
    <title>Feed - <%=term%> - NY Senate Open Legislation</title>
    <description></description>
    <link>http://open.nysenate.gov/legislation</link>
    <atom:link rel="self" type="application/rss+xml" href="<%=JSPHelper.getFullLink(request, "/search/?format=rss&amp;term=" + java.net.URLEncoder.encode(term, "UTF-8"))%>" />
    <copyright></copyright>
    <language>en-us</language>
    <lastBuildDate><%=lastBuildDate.toGMTString()%></lastBuildDate>
    <pubDate><%=new Date().toGMTString()%></pubDate>
    <generator>Open Legislation v1.9</generator>

    <image>
      <title>Feed - <%=term%> - NY Senate Open Legislation</title>
      <url>http://open.nysenate.gov/legislation/static/img/openwordlogo.gif</url>
      <link>http://open.nysenate.gov/legislation</link>
      <description></description>
      <width>100</width>
      <height>40</height>
    </image>
<%
String[] attribs = {"billno","sponsor","cosponsors","notes","when","sameas","committee","status","location","session-type","chair","summary"};

int srIdx = 0;
for (Result r : sr.getResults()) {
    srIdx++;
    String contentType = r.getOtype();
    String contentId = r.getOid();
    String oid = contentId;
    String otype = contentType;

    if (contentType.equals("vote")) {
        contentType = "bill";
        contentId = (String)r.getFields().get("billno");
    }

    else if (contentType.equals("action")) {
        contentType = "bill";
        contentId = (String)r.getFields().get("billno");
    }

    String resultTitle = contentType.toUpperCase() + " - " + r.getTitle();
    if (contentType.equals("bill")) {
        resultTitle = contentType.toUpperCase() + " - " + contentId + " - " + r.getTitle();
    }

    StringBuilder resultSummary = new StringBuilder();
    for (int i = 0; i < attribs.length; i++){
        if (r.getFields().get(attribs[i])!=null) {
            resultSummary.append(attribs[i].trim());
            resultSummary.append(":");
            resultSummary.append(r.getFields().get(attribs[i]).trim());
            resultSummary.append("; ");
        }
    }

    if (r.getSummary() != null && r.getSummary().trim().length()>0) {
        resultSummary.append("Summary:");
        resultSummary.append(r.getSummary().trim());
        resultSummary.append(";");
    }

    String resultPath = JSPHelper.getFullLink(request, "/" + contentType + "/" + contentId);
    %>
    <item>
      <title><%=resultTitle%></title>
      <description><![CDATA[<%=resultSummary%>]]></description>
      <link><%=resultPath%></link>
      <guid><%=resultPath%>#<%=srIdx%></guid>
      <pubDate><%=new Date(r.getLastModified()).toGMTString()%></pubDate>
    </item>
<% } %>
</channel>
</rss>