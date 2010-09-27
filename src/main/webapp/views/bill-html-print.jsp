<%@ page language="java" import="javax.jdo.*,java.util.*,java.text.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,gov.nysenate.openleg.model.calendar.*" contentType="text/html" pageEncoding="utf-8"%>
<%

String cacheKey = (String)request.getAttribute("path");
int cacheTime = 0;//OpenLegConstants.DEFAULT_CACHE_TIME;
 
String appPath = request.getContextPath();



Bill bill = (Bill)request.getAttribute("bill");
			
String titleText = "";
if (bill.getTitle()!=null)
	titleText = bill.getTitle();
else if (bill.getSummary()!=null)
	titleText = bill.getSummary();

String title = bill.getSenateBillNo() + " - NY Senate Open Legislation - " + titleText;

 %>
 
 <html>
<head> <title>
</title>
<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/style-print.css"/> 

</head>
 
 <body>
     <h2><%=bill.getSenateBillNo()%>: <span style="font-size:80%"><%if (bill.getTitle()!=null){ %><%=bill.getTitle()%><%} %></span></h2>
    
 <div id="content">
  
<%



DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);


String billSummary = bill.getSummary();
String billMemo = bill.getMemo();
String billText = bill.getFulltext();

if (bill.getSponsor()!=null)
	title += " - " + bill.getSponsor().getFullname();

if (bill.getTitle()!=null)
{
	String billTitle = bill.getTitle();
 
    if (billTitle.length()>100)
    {
            billTitle = billTitle.substring(0,100) + "...";
    }
 
    title += " - " + billTitle;
	
}
%>
 
 <div class="blockFormats">

 </div>
  
<%if (bill.getSponsor()!=null && bill.getSponsor().getFullname()!=null){ %>
 <h4>Sponsor: 
 <a href="<%=appPath%>/sponsor/<%=java.net.URLEncoder.encode(bill.getSponsor().getFullname(),"utf-8")%>"><%=bill.getSponsor().getFullname()%></a></h4>
 <%} %>
 
 <%
 List<Bill> billAmendments = bill.getAmendments();
 
 //if (billAmendments == null || billAmendments.size()<=1)
 	billAmendments = PMF.getAmendments(bill);
 
 if (billAmendments!=null && billAmendments.size()>1)
 {
 %>
 <h4>Other Versions:
 <% 
	Bill billAmendment = null;
	Iterator<Bill> itAmendments = billAmendments.iterator(); 
	while (itAmendments.hasNext())
	{
		billAmendment = itAmendments.next();
		
		if (billAmendment.getSenateBillNo().equals(bill.getSenateBillNo()))
			continue;
		
		if (billSummary == null && billAmendment.getSummary()!=null)
			billSummary = billAmendment.getSummary();
			
		if (billMemo == null && billAmendment.getMemo()!=null)
			billMemo = billAmendment.getMemo();
			
		if (billText == null && billAmendment.getFulltext()!=null)
			billText = billAmendment.getFulltext();
			
  %>
<a href="<%=appPath%>/bill/<%=billAmendment.getSenateBillNo()%>"><%=billAmendment.getSenateBillNo()%></a>
<%} %>
</h4>
<%} %> 
  
<%if (bill.getSameAs()!=null){ 
%>
<h4>Same as:
<% 
StringTokenizer st = new StringTokenizer(bill.getSameAs(),",");
String sameAs = null;
String sameAsLink = null;
Bill sameAsBill = null;

while(st.hasMoreTokens())
{
	sameAs = st.nextToken().trim();
	sameAsLink = appPath + "/bill/" + sameAs;

%>
<a href="<%=sameAsLink%>"><%=sameAs.toUpperCase()%></a>
<%} %>
</h4>
<%} %>

 
 <%if (bill.getCoSponsors()!=null && bill.getCoSponsors().size()>0){%>
  <h4>Co-sponsor(s):
 <%
 Iterator<Person> it = bill.getCoSponsors().iterator();
 Person cp = null;
 while (it.hasNext()){ 
 cp = it.next();
 %>
 <a href="<%=appPath%>/sponsor/<%=java.net.URLEncoder.encode(cp.getFullname(),"utf-8")%>"><%=cp.getFullname()%></a>&nbsp;
 <%} %>
 </h4>
 <%} %>

 <%if (bill.getCurrentCommittee()!=null){ %>
 <h4>Committee: <a href="<%=appPath%>/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>"><%=bill.getCurrentCommittee()%></a>
<%} %>
</h4>
<h4>
  <%if (bill.getLaw()!=null){ %>
 Law: <%=bill.getLaw()%> <br/>
 <%} %>

  <%if (bill.getLawSection()!=null){ %>
 Law Section: <%=bill.getLawSection()%><br/>
 <%} %>
 </h4>

<%if (billSummary!=null){ %>

<h3><%=bill.getSenateBillNo()%> Summary</h3>
<span><%=billSummary%></span>
 <%} %>
<%if (bill.getLaw()!=null ){ %>
<p><b>Law:</b> 
 <%=bill.getLawSection()%> /
<%=bill.getLaw()%>
   </p>
 <%} %>
<%if (bill.getActClause()!=null){ %>
<p><b>Act:</b>
 <%=bill.getActClause()%>
   </p>
 <%} %>
 
<h3><%=bill.getSenateBillNo()%> Actions</h3>
<%
try
{
String baseSenateId = bill.getSenateBillNo();
	    	
  	if (baseSenateId.matches(".*[A-Z]"))
  		baseSenateId = baseSenateId.substring(0,baseSenateId.length()-1);
  	
	
	StringBuilder actionSearch = new StringBuilder();
	
	actionSearch.append("otype:action AND (");
	actionSearch.append("billno:");
	actionSearch.append(baseSenateId);
	actionSearch.append(" OR ");
	
	for (int i = 65; i < 91; i++)
	{
		actionSearch.append("billno:");
		actionSearch.append(baseSenateId);
		actionSearch.append((char)i);
		
		if ( i < 90)
			actionSearch.append(" OR ");
	}
	
	actionSearch.append(")");
	SearchResultSet srs = SearchEngine1.doSearch(actionSearch.toString(),0,1000,"when",true);

	Iterator<SearchResult> itSrs = srs.getResults().iterator();
	SearchResult sresult = null;
	
	Hashtable<String,String> htActions = new Hashtable<String,String>();
	
	
	while (itSrs.hasNext())
	{
		sresult = itSrs.next();
		
		Date aDate = new Date(Long.parseLong((String)sresult.getFields().get("when")));
		
		String actionString = sresult.getTitle();
		
		if (actionString.indexOf('-')!=-1)
			actionString = actionString.substring(actionString.indexOf('-')+1).trim();
			
		actionString = actionString + " - " + df.format(aDate);
		
		if (htActions.get(actionString)!=null)
			continue;
		else
		{
			htActions.put(actionString,actionString);
		}
	%>
		<%=sresult.getFields().get("billno")%> - <%=actionString%><br/>
	<%
	}
	
%>
		
<%
} catch (Exception e) {}
	%>


<%
try
{
 Query query = PMF.getPersistenceManager().newQuery(Meeting.class);
  query.declareVariables("gov.nysenate.openleg.model.Bill bill;");
  query.setFilter("this.bills.contains(bill) && bill.senateBillNo==\"" + bill.getSenateBillNo() + "\"");
  Collection result = (Collection)query.execute();

if (result.size()>0){
%>

<h3><%=bill.getSenateBillNo()%> Committee Meetings</h3>

<%
Iterator itMeetings = result.iterator();
while (itMeetings.hasNext()){
 Meeting meeting = (Meeting)itMeetings.next();
 %>
<div>
<h4><a href="<%=appPath%>/meeting/<%=meeting.getId()%>"><%=meeting.getCommitteeName()%>: <%=df.format(meeting.getMeetingDateTime())%></a> </h4>
Chair: <%=meeting.getCommitteeChair()%>
/ Location: <%=meeting.getLocation()%>
</div>
<%}%>

<% }%>
<%
} catch (Exception e){}

%>



<%
try
{
 Query query = PMF.getPersistenceManager().newQuery(CalendarEntry.class);
  //query.declareVariables("gov.nysenate.openleg.model.Bill bill;");
  query.setFilter("this.bill.senateBillNo==\"" + bill.getSenateBillNo() + "\"");
  Collection result = (Collection)query.execute();

if (result.size()>0){
%>

<h3><%=bill.getSenateBillNo()%> Calendars</h3>

<%
Iterator itCals = result.iterator();
while (itCals.hasNext()){

 try
  {

 CalendarEntry cEntry = (CalendarEntry)itCals.next();
 
 String cEntryDateTime = null;
 gov.nysenate.openleg.model.calendar.Calendar cal = null;
 
 String calText = null;
 
 if (cEntry.getSection()!=null)
 {
 	cEntryDateTime = df.format(cEntry.getSection().getSupplemental().getCalendarDate());
 	cal = cEntry.getSection().getSupplemental().getCalendar();
 	calText = " - " + cEntry.getSection().getName();
 }
 else
 {
 	cEntryDateTime = df.format(cEntry.getSequence().getActCalDate());
 	cal = cEntry.getSequence().getSupplemental().getCalendar();
 	calText = " LIST";
 }
 
String calType = cal.getType().toUpperCase();
String calId = cal.getId();
 
 %>
<div>
<h4><a href="<%=appPath%>/calendar/<%=calId%>"><%=cEntryDateTime%>: <%=calType%> <%=calText%></a> </h4>
</div>
<%} catch (Exception e){}%>
<%} }%>
<%} catch (Exception e){}%>



<%if (bill.getVotes()!=null&&bill.getVotes().size()>0){ %>
<h3><%=bill.getSenateBillNo()%> Votes</h3>

 <%
  	Iterator<Vote> itVotes = bill.getVotes().iterator();
   
   Vote vote = null;
   
   while (itVotes.hasNext())
   {
   	vote = itVotes.next();
   	
   	String voteType = "Floor";
if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
	voteType = "Committee";
  %>
   <div>
  <b>Vote: <%=voteType%> - <%=DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate())%></b>
  <blockquote>
 	<b>Ayes (<%=vote.getAyes().size()%>):</b>
 	<% 
 		Iterator<String> itVoter = vote.getAyes().iterator();
 		String voter = null;
 		
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>"><%=voter%></a>
 		
 		<% 
 		}
 		%>
 		<br/><br/>
 		<%if (vote.getNays()!=null){ %>
 		<b>Nays (<%=vote.getNays().size()%>):</b>
 	<% 
 		itVoter = vote.getNays().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>"><%=voter%></a>
 		
 		<% 
 		}}
 		%>
 			<%if (vote.getAbstains()!=null){ %>
 		<br/><br/>
 		<b>Abstains (<%=vote.getAbstains().size()%>):</b>
 	<% 
 		itVoter = vote.getAbstains().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>"><%=voter%></a>
 		
 		<% 
 		}}
 		%>
 		<%if (vote.getExcused()!=null){ %>
 		<br/><br/>
 		<b>Excused (<%=vote.getExcused().size()%>):</b>
 	<% 
 		itVoter = vote.getExcused().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>"><%=voter%></a>
 		
 		<% 
 		}}
 		%>
 		</blockquote>
 		
 		</div>
 		<%
 
 }}
  %>
  
 <%
			if (bill.getSameAs()!=null)
		{
			StringTokenizer st = new StringTokenizer(bill.getSameAs(),",");
			String sameAs = null;
			while(st.hasMoreTokens())
			{
				sameAs = st.nextToken();
				
				Bill sameAsBill = PMF.getDetachedBill(sameAs);
				if (sameAsBill!= null)
				{
  
if (sameAsBill.getVotes()!=null&&sameAsBill.getVotes().size()>0){ %>
<blockquote>
<h3>Same As: <%=sameAsBill.getSenateBillNo()%> Votes</h3>

 <%
  	Iterator<Vote> itVotes = sameAsBill.getVotes().iterator();
   
   Vote vote = null;
   
   while (itVotes.hasNext())
   {
   	vote = itVotes.next();
   	String voteType = "Floor";
if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
	voteType = "Committee";
  %>
   <div>
  <b>Vote: <%=voteType%> - <%=DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate())%></b>
  <blockquote>
 	<b>Ayes (<%=vote.getAyes().size()%>):</b>
 	<% 
 		Iterator<String> itVoter = vote.getAyes().iterator();
 		String voter = null;
 		
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>"><%=voter%></a>
 		
 		<% 
 		}
 		%>
 		<br/><br/>
 		<%if (vote.getNays()!=null){ %>
 		<b>Nays (<%=vote.getNays().size()%>):</b>
 	<% 
 		itVoter = vote.getNays().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>"><%=voter%></a>
 		
 		<% 
 		}}
 		%>
 			<%if (vote.getAbstains()!=null){ %>
 		<br/><br/>
 		<b>Abstains (<%=vote.getAbstains().size()%>):</b>
 	<% 
 		itVoter = vote.getAbstains().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>"><%=voter%></a>
 		
 		<% 
 		}}
 		%>
 		<%if (vote.getExcused()!=null){ %>
 		<br/><br/>
 		<b>Excused (<%=vote.getExcused().size()%>):</b>
 	<% 
 		itVoter = vote.getExcused().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>"><%=voter%></a>
 		
 		<% 
 		}}
 		%>
 		</blockquote>
 		
 		</div>
 		</blockquote>
 		<%
 
 }
 
 }}}
  %>
  
  
  
	<%} %>
<h3><%=bill.getSenateBillNo()%> Memo</h3>
 <pre><%if (billMemo!=null){%><%=billMemo%><%}else{%>Not Available.<%} %></pre>
	
 <h3><%=bill.getSenateBillNo()%> Text</h3>
 <pre><%if (billText!=null){
  
  
  %><%=billText%><%} else{%>Not Available.<%}%>
  </pre>
 <br/>
  
 </div>
  <script type="text/javascript">
  setTimeout("window.print();",2000);
  </script>
 </body>
 </html>
 
 