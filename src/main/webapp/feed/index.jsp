<%@ page language="java" import="java.util.*, java.text.*,java.io.*,javax.jdo.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="text/xml" pageEncoding="UTF-8"%><?xml version="1.0" encoding="utf-8"?>
<rss version="2.0"  xmlns:atom="http://www.w3.org/2005/Atom">
  <channel>
    <title>NY Senate Open Legislation</title>
    <description>Latest actions from the floor of the NY Senate</description>
    <link>http://open.nysenate.gov/legislation</link>
    <language>en-us</language>
<atom:link href="http://open.nysenate.gov/legislation/feed" rel="self" type="application/rss+xml" />
<%
String legTypeFilter = request.getParameter("type");

String stringEventSearch = request.getParameter("action");

String sponsor = request.getParameter("sponsor");

if (stringEventSearch == null)
        stringEventSearch = "";

Date startDate = null;
Date endDate = null;

int year = -1;
int month = -1;
int day = -1;

if (request.getParameter("year") != null)
{
        year = Integer.parseInt(request.getParameter("year"));
        month = Integer.parseInt(request.getParameter("month"));
        day = Integer.parseInt(request.getParameter("day"));

        Calendar now = Calendar.getInstance();
        now.clear();
        now.set(year,month-1,day,0,0,0);
        startDate = now.getTime();

        now.set(year,month-1,day,11,59,59);
        endDate = now.getTime();
}
else
{
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();

        //now.set(now.getY,month-1,day,11,59,59);
        now.set(Calendar.YEAR,now.get(Calendar.YEAR)-1);
        startDate = now.getTime();

}

long start = 0;
long end = 50;

 String cacheKey = "rss-" + sponsor + '-' + stringEventSearch + '-' + start + '-' + end + '-' + legTypeFilter + '-' + year + '-' + month + '-' + day;
 int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;

String pubDate = new Date().toGMTString();
%>
    <lastBuildDate><%=pubDate%></lastBuildDate>
    <pubDate><%=pubDate%></pubDate>
    <generator>OpenLeg (0.0.2)</generator>
    <image>
      <url>http://open.nysenate.gov/legislation/img/nysenatelogo100.png</url>
      <title>NY Senate Open Legislation</title>
      <link>http://open.nysenate.gov/legislation</link>
      <description>NY Senate Open Legislation</description>
      <width>100</width>
      <height>100</height>
    </image>
   <cache:cache key="<%=cacheKey%>" time="<%=cacheTime %>"  scope="application">
<%


String appPath = "http://open.nysenate.gov/legislation";
Bill bill = null;
String last = null;
DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

%>
<%
PersistenceManager pm = PMF.getPersistenceManager();
Transaction trans = pm.currentTransaction();

trans.begin();

try
{

ArrayList<BillEvent> eventList = null;

if (sponsor == null)
{
	eventList = PMF.searchBillEvent(pm,startDate, endDate, stringEventSearch, start, end, legTypeFilter);

}
else
{
	eventList = PMF.getBillEventsBySponsor(sponsor, 50);
}

BillEvent be = null;
Bill lastBill = null;

if (eventList != null)
{
	Iterator<BillEvent> itEvents = eventList.iterator();
	
	while (itEvents.hasNext())
	{
		be = itEvents.next();
		bill = PMF.getDetachedBill(be.getBillId());

		if (bill == null)
			continue;
		
		%>
		   <item>
      <title>	<%=bill.getSenateBillNo()%>
		<%if (bill.getSameAs()!=null){ %> (Same as: <%=bill.getSameAs()%>)<%}%><%if (bill.getSponsor()!=null){%> - <%=bill.getSponsor().getFullname()%><%}%>: <%=be.getEventText()%></title>
      <description><%if (bill.getTitle()!=null){ %>
<%=bill.getTitle()%>
<%} else if (bill.getSummary()!=null){ %>
 <%=bill.getSummary()%>
 <%} %>
 </description>

      <link><%=appPath%>/api/html/bill/<%=bill.getSenateBillNo()%></link>
      <guid><%=appPath%>/api/html/bill/<%=bill.getSenateBillNo()%>?<%=be.getBillEventId()%></guid>
      <pubDate><%=be.getEventDate().toGMTString() %></pubDate>
    </item>
		
		
	

		<%
		
		lastBill = bill;
	}
	
}

trans.commit();

}
catch (Exception e)
{
trans.rollback();
}
	%>
</cache:cache>
    </channel>
</rss>
