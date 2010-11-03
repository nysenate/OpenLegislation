<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,gov.nysenate.openleg.model.calendar.*" contentType="text/html" pageEncoding="utf-8"%>
<%


String appPath = request.getContextPath();

String term = (String)request.getAttribute("term");


Bill bill = (Bill)request.getAttribute("bill");

  	
String titleText = "";
if (bill.getTitle()!=null)
	titleText = bill.getTitle();
else if (bill.getSummary()!=null)
	titleText = bill.getSummary();

String senateBillNo = bill.getSenateBillNo() + '-' + bill.getYear();

String title = senateBillNo + " - NY Senate Open Legislation - " + titleText;

 %>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<br/>
     <h2><%=senateBillNo%>: <%if (bill.getTitle()!=null){ %><%=bill.getTitle()%><%} %></h2>
    <br/>
     <div style="float:left;">
    
    <%if (bill.getSameAs()!=null){ 
%>
<b>Same as:</b>
<% 

StringTokenizer st = new StringTokenizer(bill.getSameAs(),",");
String sameAs = null;
String lastSameAs = "";
String sameAsLink = null;
Bill sameAsBill = null;

while(st.hasMoreTokens())
{
	
	sameAs = st.nextToken().trim();
	sameAsLink = appPath + "/bill/" + sameAs;
	
	if (sameAs.length() == 0)
		continue;
	
	if (sameAs.equals(lastSameAs))
		continue;
	
	lastSameAs = sameAs;
%>
<a href="<%=sameAsLink%>"><%=sameAs.toUpperCase()%></a>
<%
if (st.hasMoreTokens())
{
%> <%
}
} %>

<%} %>




    
    </div>
 <div style="float:right;">
 <a href="<%=appPath%>/api/1.0/html-print/bill/<%=senateBillNo%>" target="_new">Print HTML Page</a>
 /
 <a href="<%=appPath%>/api/1.0/lrs-print/bill/<%=senateBillNo%>" target="_new">Print Original Bill Format</a>
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

<h3><%=senateBillNo%> Summary</h3>

<%if (billSummary!=null){ %>
<%=billSummary%>
 <%} %>
 
</div>
 
 
<h3><%=senateBillNo%> Actions</h3>
<ul>
<%

ArrayList<BillEvent> rActions = (ArrayList<BillEvent>)request.getAttribute("related-action");

for (BillEvent beAction:rActions){
	%>
	<li><%=beAction.getEventText()%> - <%=df.format(beAction.getEventDate())%></li>
	<%
}
%>
</ul>

<h3><%=senateBillNo%> Committee Meetings</h3>
<%

ArrayList<Meeting> rMeetings = (ArrayList<Meeting>)request.getAttribute("related-meeting");

for (Meeting meeting:rMeetings){
	%>
	<div>
<a href="<%=appPath%>/meeting/<%=meeting.getId()%>" class="sublink"><%=meeting.getCommitteeName()%>: <%=df.format(meeting.getMeetingDateTime())%></a>: Chair: <%=meeting.getCommitteeChair()%>
/ Location: <%=meeting.getLocation()%>
</div>
	<%
}
%>



<h3><%=senateBillNo%> Calendars</h3>
<%

ArrayList<gov.nysenate.openleg.model.calendar.Calendar> rCals = (ArrayList<gov.nysenate.openleg.model.calendar.Calendar>)request.getAttribute("related-calendar");

for (gov.nysenate.openleg.model.calendar.Calendar cal:rCals){
	String cEntryDateTime = "";
	Supplemental supp = cal.getSupplementals().get(0);
	if (supp.getCalendarDate()!=null)
		cEntryDateTime = df.format(supp.getCalendarDate());
	else
		cEntryDateTime = df.format(supp.getSequence().getActCalDate());
	
	String calType = cal.getType();
	String calText = "";
	%>
	<div>
<a href="<%=appPath%>/calendar/<%=cal.getId()%>" class="sublink"><%=cEntryDateTime%>: <%=calType%> <%=calText%></a>
</div>
	<%
}
%>

<h3><%=senateBillNo%> Votes</h3>
<%

ArrayList<Vote> rVotes = (ArrayList<Vote>)request.getAttribute("related-vote");

for (Vote vote:rVotes){
   
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
 
 }
  %>
  

  
<%if (billMemo!=null){%>
<h3><%=senateBillNo%> Memo</h3>
<%=TextFormatter.formatMemo (billMemo)%><%}%>
	
 <h3><%=senateBillNo%> Text</h3>
<%if (billText!=null){
  
  billText = TextFormatter.removeBillLineNumbers (billText);
  
  %><%=billText%><%} else{%>Not Available.<%}%>
 
 <br/>
  
 </div>
  
 
<script type="text/javascript">
var disqus_url = "http://open.nysenate.gov/legislation/api/html/bill/<%=senateBillNo%>";
//var disqus_identifier = "http://open.nysenate.gov/legislation/api/1.0/html/bill/<%=senateBillNo%>";
 </script>
  
<div id="comments">
<b><p>*By contributing or voting you agree to the <a href = "http://nysenate.gov/legal">Terms of Participation</a> and <a href = "http://www.nysenate.gov/privacy-policy">Privacy Policy</a> and verify you are over 13.</p></b>
 <h3> <a name="discuss">Discuss!</a></h3>
 <div id="disqus_thread"></div><script type="text/javascript" src="http://disqus.com/forums/nysenateopenleg/embed.js"></script><noscript><a href="http://nysenateopenleg.disqus.com/?url=ref">View the discussion thread.</a></noscript><a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>
</div>

 

<jsp:include page="/footer.jsp"/>

