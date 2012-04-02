<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="application/rss+xml" pageEncoding="UTF-8"%><?xml version="1.0" encoding="utf-8"?><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

String term = "";

if (request.getParameter("term")!=null)
        term = request.getParameter("term");
else if (session.getAttribute("term")!=null)
        term = (String)session.getAttribute("term");
%>
<rss version="2.0">
  <channel>
        <title><%=term%> - Search - NY Open Legislation</title>
    <description></description>
    <link>http://open.nysenate.gov/legislation</link>
    <copyright></copyright>
    <language>en-us</language>
<%
String pubDate = new Date().toGMTString();
%>
    <lastBuildDate><%=pubDate%></lastBuildDate>
    <pubDate><%=pubDate%></pubDate>
    <generator>OpenLeg (0.0.2)</generator>
    <image>
      <url></url>
      <title></title>
      <link></link>
      <description></description>
      <width></width>
      <height></height>
    </image>

<%


String appPath = "http://open.nysenate.gov/legislation";
Bill bill = null;
String last = null;
DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

%>
<%

Collection<Bill> bills = (Collection<Bill>)request.getAttribute("bills");

        Iterator<Bill> itEvents = bills.iterator();
        while (itEvents.hasNext())
        {
                try
                {
                bill = itEvents.next();
               
                %>
                   <item>
      <title>   <%=bill.getSenateBillNo()%>
                <%if (bill.getSameAs()!=null){ %> (Same as: <%=bill.getSameAs()%>)<%}%></title>
      <description><%if (bill.getTitle()!=null){ %>
<%=bill.getTitle()%>
<%} else if (bill.getSummary()!=null){ %>
 <%=bill.getSummary()%>
 <%} %>
  <%if (bill.getSponsor()!=null){ %>
 Sponsor: <%=bill.getSponsor().getFullname()%>
 <%} %>
 <%if (bill.getCurrentCommittee()!=null){ %>
 Committee: <%=bill.getCurrentCommittee()%>
<%} %>
 </description>

      <link><%=appPath%>/api/html/bill/<%=bill.getSenateBillNo()%></link>
      <pubDate><%=pubDate%></pubDate>
    </item>
                
                
        

                <%
                }
catch(Exception e) {}           
        }
        
        %>
    </channel>
</rss>
