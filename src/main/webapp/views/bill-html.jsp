<%@ page language="java" import="javax.jdo.*,java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,gov.nysenate.openleg.model.calendar.*" contentType="text/html" pageEncoding="utf-8"%>
<%


String appPath = request.getContextPath();

String term = (String)request.getAttribute("term");

%>
<%

CachedContentManager.fillCache(request);
Bill bill = (Bill)request.getAttribute("bill");
			
String baseSenateId = bill.getSenateBillNo();
	    	
  	if (baseSenateId.matches(".*[A-Z]"))
  		baseSenateId = baseSenateId.substring(0,baseSenateId.length()-1);
  	
  	
String titleText = "";
if (bill.getTitle()!=null)
	titleText = bill.getTitle();
else if (bill.getSummary()!=null)
	titleText = bill.getSummary();

String title = bill.getSenateBillNo() + " - NY Senate Open Legislation - " + titleText;

 %>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<br/>
     <h2><%=bill.getSenateBillNo()%>: <%if (bill.getTitle()!=null){ %><%=bill.getTitle()%><%} %></h2>
    <br/>
     <div style="float:left;">
    
    <%if (bill.getSameAs()!=null){ 
%>
<b>Same as:</b>
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
<%
if (st.hasMoreTokens())
{
%>, <%
}
} %>

<%} %>


<%
try
{

	StringBuilder actionSearch = new StringBuilder();
	
	actionSearch.append("otype:bill AND (");
	
	//oid:" + baseSenateId + "{A to Z}";
	
		actionSearch.append("oid:");
	actionSearch.append(baseSenateId);
	actionSearch.append(" OR ");
	
	for (int i = 65; i < 91; i++)
	{
		actionSearch.append("oid:");
		actionSearch.append(baseSenateId);
		actionSearch.append((char)i);
		
		if ( i < 90)
			actionSearch.append(" OR ");
	}
	
	actionSearch.append(")");
	
	SearchResultSet srs = SearchEngine1.doSearch(actionSearch.toString(),0,1000,null,true);

	Iterator<SearchResult> itSrs = srs.getResults().iterator();
	SearchResult sresult = null;
	
	Hashtable<String,String> htAmends = new Hashtable<String,String>();
	
	if (itSrs.hasNext())
	{
	%>
	<b>Versions:</b>
	<%
	}
	
	while (itSrs.hasNext())
	{
		sresult = itSrs.next();
		
		
		
	%>
		<a href="<%=appPath%>/bill/<%=sresult.getId()%>"><%=sresult.getId()%></a><%if (itSrs.hasNext()){%>, <%} %>
	<%
	}
	
%>
		
<%
} catch (Exception e) {
e.printStackTrace();
}
	%>

    
    </div>
 <div style="float:right;">
 <a href="<%=appPath%>/api/1.0/html-print/bill/<%=bill.getSenateBillNo()%>" target="_new">Print Page</a>
 <a href="<%=appPath%>/api/1.0/lrs-print/bill/<%=bill.getSenateBillNo()%>" target="_new">Print Page</a>
 / <script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script>
  / <a href="#discuss">Read or Leave Comments</a>
  </div>
  <br style="clear:both;"/>
  

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
 
  <div class="billheader">
<%if (bill.getSponsor()!=null && bill.getSponsor().getFullname()!=null){ %>
 <b>Sponsor: </b>
 <a href="<%=appPath%>/sponsor/<%=java.net.URLEncoder.encode(bill.getSponsor().getFullname(),"utf-8")%>"  class="sublink"><%=bill.getSponsor().getFullname()%></a>
 <br/>
 <%} %>
 
<!--
<%if (bill.getActClause()!=null){ %>
 <%=bill.getActClause()%>
 <%} %>
 -->

 
 <%if (bill.getCoSponsors()!=null && bill.getCoSponsors().size()>0){%>
<b>Co-sponsor(s):</b>
 <%
 Iterator<Person> it = bill.getCoSponsors().iterator();
 Person cp = null;
 while (it.hasNext()){ 
 cp = it.next();
 %>
 <a href="<%=appPath%>/sponsor/<%=java.net.URLEncoder.encode(cp.getFullname(),"utf-8")%>" class="sublink"><%=cp.getFullname()%></a><%if (it.hasNext()){%>, <%} %><%} %>
<br/>

 <%} %>

 <%if (bill.getCurrentCommittee()!=null){ %>
 <b>Committee:</b> <a href="<%=appPath%>/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>" class="sublink"><%=bill.getCurrentCommittee()%></a>
<br/>
<%} %>

  <%if (bill.getLaw()!=null){ %>
 <b>Law:</b> <a href="<%=appPath%>/search/?term=<%=java.net.URLEncoder.encode("\"" + bill.getLaw()+"\"","utf-8")%>" class="sublink"><%=bill.getLaw()%></a> /
 <%} %>

  <%if (bill.getLawSection()!=null){ %>
 <b>Law Section:</b> <a href="<%=appPath%>/search/?term=<%=java.net.URLEncoder.encode("\"" + bill.getLawSection()+"\"","utf-8")%>" class="sublink"><%=bill.getLawSection()%></a><br/>
 <%} %>

<h3><%=bill.getSenateBillNo()%> Summary</h3>

<%if (billSummary!=null){ %>
<%=billSummary%>
 <%} %>
 
</div>
 
 
<h3><%=bill.getSenateBillNo()%> Actions</h3>

<%
try
{

	
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
		
		actionString = actionString + " - " + df.format(aDate);
		
		if (htActions.get(actionString)!=null)
			continue;
		else
		{
			htActions.put(actionString,actionString);
		}
	%>
		<%=sresult.getFields().get("billno")%> - <%=actionString%>
		<!-- <%=sresult.getFields().get("oid")%>  -->
		<br/>
	<%
	}
	
%>
		
<%
} catch (Exception e) {}
	%>


<%

PersistenceManager pm = PMF.getPersistenceManager();
	Transaction trans = pm.currentTransaction();
	
try
{

	
	
	trans.begin();

 Query query = pm.newQuery(Meeting.class);
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
<a href="<%=appPath%>/meeting/<%=meeting.getId()%>" class="sublink"><%=meeting.getCommitteeName()%>: <%=df.format(meeting.getMeetingDateTime())%></a>: Chair: <%=meeting.getCommitteeChair()%>
/ Location: <%=meeting.getLocation()%>
</div>
<%}%>

<% }%>
<%

trans.commit();

} catch (Exception e){

if (trans.isActive())
	trans.rollback();

e.printStackTrace();

}

%>



<%

pm = PMF.getPersistenceManager();
trans = pm.currentTransaction();
	
	
try
{
trans.begin();
 Query query = pm.newQuery(CalendarEntry.class);
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
<a href="<%=appPath%>/calendar/<%=calId%>" class="sublink"><%=cEntryDateTime%>: <%=calType%> <%=calText%></a>
</div>
<%} catch (Exception e){
e.printStackTrace();
}%>
<%}
 }%>
<%
trans.commit();

} catch (Exception e){

e.printStackTrace();

if (trans.isActive())
	trans.rollback();

}%>



<%if (bill.getVotes()!=null&&bill.getVotes().size()>0){ %>
<h3><%=bill.getSenateBillNo()%> Votes</h3>

 <%
  	Iterator<Vote> itVotes = bill.getVotes().iterator();
   
   Vote vote = null;
  // String voteDesc = null;
   
   while (itVotes.hasNext())
   {
   	vote = itVotes.next();
   	
   	String voteType = "Floor";
if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
	voteType = "Committee";
	

  %>
   <div>
  <b>Vote: <%=voteType%> 
  <%if (vote.getDescription()!=null){%>- <%=vote.getDescription()%><%} %>
  - <%=DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate())%></b>
  <blockquote>
 	<b>Ayes (<%=vote.getAyes().size()%>):</b>
 	<% 
 		Iterator<String> itVoter = vote.getAyes().iterator();
 		String voter = null;
 		
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		<% 
 		}
 		%>
 	<%if (vote.getAyeswr()!=null && vote.getAyeswr().size()>0){ %>
 			<br/>
 		<b>Ayes W/R (<%=vote.getAyeswr().size()%>):</b>
 	<% 
 		itVoter = vote.getAyeswr().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
 		<% 
 		}}
 		%>
 		<%if (vote.getNays()!=null && vote.getNays().size()>0){ %>
 			<br/>
 		<b>Nays (<%=vote.getNays().size()%>):</b>
 	<% 
 		itVoter = vote.getNays().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
 		<% 
 		}}
 		%>
 			<%if (vote.getAbstains()!=null && vote.getAbstains().size() > 0){ %>
 		<br/>
 		<b>Abstains (<%=vote.getAbstains().size()%>):</b>
 	<% 
 		itVoter = vote.getAbstains().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
 		<% 
 		}}
 		%>
 		<%if (vote.getExcused()!=null && vote.getExcused().size() > 0){ %>
 		<br/>
 		<b>Excused (<%=vote.getExcused().size()%>):</b>
 	<% 
 		itVoter = vote.getExcused().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
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
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
 		<% 
 		}
 		%>
 	
 	<%if (vote.getAyeswr()!=null && vote.getAyeswr().size()>0){ %>
 			<br/>
 		<b>Ayes W/R (<%=vote.getAyeswr().size()%>):</b>
 	<% 
 		itVoter = vote.getAyeswr().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
 		<% 
 		}}
 		%>
 		<%if (vote.getNays()!=null && vote.getNays().size() > 0){ %>
 			<br/>
 		<b>Nays (<%=vote.getNays().size()%>):</b>
 	<% 
 		itVoter = vote.getNays().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
 		<% 
 		}}
 		%>
 			<%if (vote.getAbstains()!=null && vote.getAbstains().size() > 0){ %>
 		<br/>
 		<b>Abstains (<%=vote.getAbstains().size()%>):</b>
 	<% 
 		itVoter = vote.getAbstains().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
 		<% 
 		}}
 		%>
 		<%if (vote.getExcused()!=null && vote.getExcused().size() > 0){ %>
 		<br/>
 		<b>Excused (<%=vote.getExcused().size()%>):</b>
 	<% 
 		itVoter = vote.getExcused().iterator();
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		
 		<% 
 		}}
 		%>
 		</blockquote>
 		
 		</div>
 	
 		<%
 
 }
 
 }}}
  %>
  
  
  
	<%} %>
<%if (billMemo!=null){%>
<h3><%=bill.getSenateBillNo()%> Memo</h3>
<%=TextFormatter.formatMemo (billMemo)%><%}%>
	
 <h3><%=bill.getSenateBillNo()%> Text</h3>
<%if (billText!=null){
  
  billText = TextFormatter.removeBillLineNumbers (billText);
  
  %><%=billText%><%} else{%>Not Available.<%}%>
 
 <br/>
  
 </div>
  
 
<script type="text/javascript">
var disqus_url = "http://open.nysenate.gov/legislation/api/html/bill/<%=bill.getSenateBillNo()%>";
//var disqus_identifier = "http://open.nysenate.gov/legislation/api/1.0/html/bill/<%=bill.getSenateBillNo()%>";
 </script>
  
<div id="comments">
<b><p>*By contributing or voting you agree to the <a href = "http://nysenate.gov/legal">Terms of Participation</a> and <a href = "http://www.nysenate.gov/privacy-policy">Privacy Policy</a> and verify you are over 13.</p></b>
 <h3> <a name="discuss">Discuss!</a></h3>
 <div id="disqus_thread"></div><script type="text/javascript" src="http://disqus.com/forums/nysenateopenleg/embed.js"></script><noscript><a href="http://nysenateopenleg.disqus.com/?url=ref">View the discussion thread.</a></noscript><a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>
</div>

 

<jsp:include page="/footer.jsp"/>

