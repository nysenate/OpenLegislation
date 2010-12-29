<%@ page language="java" import="org.json.*,java.util.*,java.util.Map.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/xml" pageEncoding="utf-8" %><%

String appPath = "http://open.nysenate.gov" + request.getContextPath();

SearchResultSet srs = (SearchResultSet)request.getAttribute("results");
int resultCount = srs.getResults().size();

int total = srs.getTotalHitCount();

String term = (String)request.getAttribute("term");

String selfLink = appPath + "/search/?format=rss&amp;term=" + java.net.URLEncoder.encode(term);

%><?xml version="1.0" encoding="utf-8"?>
<rss version="2.0"  xmlns:atom="http://www.w3.org/2005/Atom">
  <channel>
        <title><%=term%> - Search - NY Senate Open Legislation</title>
    <description></description>
    <link>http://open.nysenate.gov/legislation</link>
<atom:link href="<%=selfLink%>" rel="self" type="application/rss+xml" />
    <copyright></copyright>
    <language>en-us</language>
<%
String pubDate = new Date().toGMTString();
%>
    <lastBuildDate><%=pubDate%></lastBuildDate>
    <pubDate><%=pubDate%></pubDate>
    <generator>Open Legislation v1.6</generator>
    <image>
        <title><%=term%> - Search - NY Senate Open Legislation</title>
      <url>http://open.nysenate.gov/legislation/img/openwordlogo.gif</url>
      <link>http://open.nysenate.gov/legislation</link>
      <description></description>
      <width>100</width>
      <height>40</height>
    </image>

<%

Iterator<SearchResult> it = srs.getResults().iterator();
SearchResult sr = null;
String score = "";

String[] attribs = {"billno","sponsor","cosponsors","notes","when","sameas","committee","status","location","session-type","chair","summary"};

String contentType = null;
String contentId = null;
String resultTitle = null;

int srIdx = 0;

while (it.hasNext())
{
		sr = it.next();
        srIdx++;
        
                score = sr.getScore() + "";
                
                
                contentType = sr.getType();
                contentId = sr.getId();
                resultTitle = sr.getTitle();
                                
                if (contentType.equals("vote"))
                {
                        contentType = "bill";
                        contentId = (String)sr.getFields().get("billno");
                }

                if (contentType.equals("action"))
                {
                        contentType = "bill";
                        contentId = (String)sr.getFields().get("billno");
                        
                        
                }
                
                 if (contentType.equals("transcript"))
                {
                        contentId = contentId + "?term=" + java.net.URLEncoder.encode(term,"UTF-8") + "&#result";
                        
                }
                
                if (contentType.equals("bill"))
                {
                
                	resultTitle = contentType.toUpperCase() + " - " + contentId + " - " + resultTitle;
                
                }
                else
                {
                	resultTitle = contentType.toUpperCase() + " - " + resultTitle;
                }
              
                                String resultPath = appPath + "/api/1.0/html/" + contentType + "/" + contentId;
                
                
                StringBuilder resultSummary = new StringBuilder();
           
              for (int i = 0; i < attribs.length; i++){
				if (sr.getFields().get(attribs[i])!=null){ 
					
					resultSummary.append(attribs[i].trim());
					resultSummary.append(":");
					resultSummary.append(sr.getFields().get(attribs[i]).trim());
					resultSummary.append("; ");
					
				} 
				}
              
              
              if (sr.getSummary() != null && sr.getSummary().trim().length()>0)
              {
              	resultSummary.append("Summary:");
              	resultSummary.append(sr.getSummary().trim());
              	resultSummary.append(";");
              }
              
                %><item>
                 <title><%=resultTitle%></title>
                 <description><![CDATA[<%=resultSummary%>]]></description>
                        <link><%=resultPath%></link>
                        <guid><%=resultPath%>#<%=srIdx%></guid>
                 <pubDate><%=sr.getLastModified().toGMTString()%></pubDate>
                 </item>
               <%
                
                

}

%>
</channel>
</rss>