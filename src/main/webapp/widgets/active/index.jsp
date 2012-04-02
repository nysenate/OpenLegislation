<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.api.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %><%

String requestPath = request.getRequestURI();


String term = "ctype:active";
String sortField = "when";
String type = "calendar";

boolean sortOrder = true;

int pageIdx = 1;
int pageSize = 1;


int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;



SearchEngine searchEngine = SearchEngine.getInstance();

ArrayList<Result> listResults =  ApiHelper.buildSearchResultList(searchEngine.search(term,"json",startIdx,pageSize,sortField,sortOrder));



String appPath = request.getContextPath();
%>	
<html>
  <head>

<title>Active List</title>
<link rel="shortcut icon" href="/legislation/img/nys_favicon_0.ico" type="image/x-icon" />
 
<link rel="stylesheet" type="text/css" media="screen" href="/legislation/style.css"/> 
<link rel="stylesheet" type="text/css" media="screen" href="/legislation/style-mobile.css"/> 

<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
 <meta name="apple-mobile-web-app-capable" content="YES">
<style>
body
{
font-size:80%;
}
ul
{
margin:0px;
padding:0px;
}
li
{
border-bottom:1px solid #ccc;
list-style-type:none;
margin:0px;
padding-top:3px;
}
.widget-narrow
{
width:150px;
}
</style>
  </head>
<body>  


 <div class="widget-narrow">

 <%
     String resultType = null;
   String resultId = null;
   
   String contentType = null;
   String contentId = null;
 String resultTitle = null;
 gov.nysenate.openleg.model.Calendar calendar = null;

  for (Result sresult : listResults)
  {
         resultType = sresult.getOtype();

 				calendar = (gov.nysenate.openleg.model.Calendar)sresult.getObject();
 				
         if (resultType.indexOf(".")!=-1)
         {
                 resultType = resultType.substring(resultType.lastIndexOf(".")+1);
         }


         resultId = sresult.getOid();

         contentType = resultType;
         contentId = resultId;
 		resultTitle = sresult.getTitle();
 %>

 <%} %>
 


<%


String resultPath = appPath + "/api/1.0/html/" + contentType + "/" + contentId;

String title = "Calendar " + calendar.getNo() + " " + calendar.getSessionYear();
	DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);

Iterator<Supplemental> itSupp = calendar.getSupplementals().iterator();
Supplemental supp = itSupp.next();

 %>
<h4>New York Senate<br/>Active List (Cal <%=calendar.getNo()%>)
for
<% if (supp.getCalendarDate()!=null){ %>
<%=df.format(supp.getCalendarDate())%>
<%} else if (supp.getSequences()!=null && !supp.getSequences().isEmpty()){%>
<%=df.format(supp.getSequences().get(0).getActCalDate())%>
<%}%>
</h4>
<div style="background:#00CCFF;font-size:7pt;padding:1px;">
Updated every 30 minutes or less
</div>
 <div id="content">
 

<%if (supp.getSequences()!=null){%>

<%


List<Sequence> seqs = supp.getSequences();

for(Sequence seq:seqs) {
%>
<%

 %>
<base target="_blank"/>
<%if (seq.getNo().length() == 0){%>
<h2>Active List</h2>
<%}else{%>
<!--<h2>Sequence <%=seq.getNo()%></h2>-->
<h2 style="font-size:12pt;background-color:#ccc;">Supplemental</h2>
<%
}%>
	<ul>
<%
Iterator<CalendarEntry> itCals = seq.getCalendarEntries().iterator();
	while (itCals.hasNext()){
	CalendarEntry calEnt = itCals.next();
		try 
		{
	%>
		
		<li>
		Cal No. <%=calEnt.getNo()%>
		<%if (calEnt.getBill()!=null && calEnt.getBill().getSenateBillNo()!=null ){%>
		/ Bill: <a href="<%=appPath%>/api/1.0/html/bill/<%=calEnt.getBill().getSenateBillNo()%>" target="_blank"><%=calEnt.getBill().getSenateBillNo()%></a>
		/ <a href="<%=appPath%>/search?term=sponsor:<%=calEnt.getBill().getSponsor().getFullname()%>" target="_blank"><%=calEnt.getBill().getSponsor().getFullname()%></a>
		<%if (calEnt.getSubBill()!=null){%>(Sub-bill Sponsor: <a href="<%=appPath%>/search?term=sponsor:<%=calEnt.getSubBill().getSponsor().getFullname()%>"><%=calEnt.getSubBill().getSponsor().getFullname()%></a>)<%}%>
				<%if (calEnt.getBillHigh()!=null){ %><b style="color:green">HIGH</b><%}%>

				<%if (calEnt.getSubBill()!=null){%>(Sub-bill: <a href="<%=appPath%>/api/1.0/mobile/bill/<%=calEnt.getSubBill().getSenateBillNo()%>"><%=calEnt.getSubBill().getSenateBillNo()%></a>)<%}%>
		
		
		<div style="font-size:80%">
		<%if (calEnt.getBill().getTitle()!=null){%><%=calEnt.getBill().getTitle()%><%}else if (calEnt.getSubBill()!=null && calEnt.getSubBill().getTitle()!=null){%> <%=calEnt.getSubBill().getTitle()%><%}%>
		</div>
		<%}%>
		
	<%

} catch (Exception e)  {
e.printStackTrace();
}
%>
</li>
<%
}%>
	</ul>
<%}

}%>	
	
</div>


<%if (supp.getSections()!=null&&supp.getSections().size()>0){%>
<hr/>
<%
Iterator<Section> itSection = supp.getSections().iterator();
while (itSection.hasNext()){
Section section = itSection.next();
%>
<h4>Section:<%=section.getName()%> (<%=section.getType()%> / <%=section.getCd()%>)</h4>
<div class="billSummary">
	<ul>
<%
Iterator<CalendarEntry> itCals = section.getCalendarEntries().iterator();
	while (itCals.hasNext()){
	CalendarEntry calEnt = itCals.next();
	%>
		
		<li>
		Calendar: <%=calEnt.getNo()%>
		<%if (calEnt.getBill()!=null){%>
		/ Sponsor: <a href="<%=appPath%>/search?format=mobile&term=sponsor:<%=calEnt.getBill().getSponsor().getFullname()%>"><%=calEnt.getBill().getSponsor().getFullname()%></a>
		<%if (calEnt.getSubBill()!=null){%>(Sub-bill Sponsor: <a href="<%=appPath%>/search?format=mobile&term=sponsor:<%=calEnt.getSubBill().getSponsor().getFullname()%>"><%=calEnt.getSubBill().getSponsor().getFullname()%></a>)<%}%>
		/ Printed No.: <a href="<%=appPath%>/api/1.0/mobile/bill/<%=calEnt.getBill().getSenateBillNo()%>"><%=calEnt.getBill().getSenateBillNo()%></a>
				<%if (calEnt.getBillHigh()!=null){ %><b style="color:green">HIGH</b><%}%>
				<%if (calEnt.getSubBill()!=null){%>(Sub-bill: <a href="<%=appPath%>/api/1.0/mobile/bill/<%=calEnt.getSubBill().getSenateBillNo()%>"><%=calEnt.getSubBill().getSenateBillNo()%></a>)<%}%>
		
		<%}%>
		
		<%if (calEnt.getBill().getTitle()!=null){%><br/>Title: <%=calEnt.getBill().getTitle()%><%}else if (calEnt.getSubBill()!=null && calEnt.getSubBill().getTitle()!=null){%><br/>Title: <%=calEnt.getSubBill().getTitle()%><%}%>
		
</li>
	<%}%>
	</ul>
<%}%>
<%}%>	
	


</div>
  

</div> 
</body>
</html>
