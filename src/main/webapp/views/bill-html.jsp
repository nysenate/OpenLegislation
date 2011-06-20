<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.bill.*,gov.nysenate.openleg.model.committee.*,gov.nysenate.openleg.model.calendar.*,org.codehaus.jackson.map.ObjectMapper" contentType="text/html" pageEncoding="utf-8"%>
<%

String appPath = request.getContextPath();

Bill bill = (Bill)request.getAttribute("bill");

boolean active = bill.getLuceneActive();
  	
String titleText = "(no title)";
if (bill.getTitle()!=null)
	titleText = bill.getTitle();

String senateBillNo = bill.getSenateBillNo();

if (senateBillNo.indexOf("-")==-1)
	senateBillNo += "-" + bill.getYear();

String title = senateBillNo + " - NY Senate Open Legislation - " + titleText;

SimpleDateFormat calendarSdf = new SimpleDateFormat("MMM d, yyyy");

 %>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<br/>
     <h2><%=senateBillNo%>: <%if (bill.getTitle()!=null){ %><%=bill.getTitle()%><%} %></h2>
    <br/>
    
    <% if(!active) { %>
		<div class="amended">This bill has been amended.</div>
	<% } %>
    
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
	sameAsLink = appPath + "/bill/" + sameAs + "-" + bill.getYear();
	
	if (sameAs.length() == 0)
		continue;
	
	if (sameAs.equals(lastSameAs))
		continue;
	
	lastSameAs = sameAs;
%>
<a href="<%=sameAsLink%>"><%=sameAs.toUpperCase()%></a>
<%
if (st.hasMoreTokens()) {%><%}
} %>
/
<%} %>

<%

String sponsor = null;

if (bill.getSponsor()!=null)
		sponsor = bill.getSponsor().getFullname();

ArrayList<Bill> rBills = (ArrayList<Bill>)request.getAttribute("related-bill");
%>

<%if (rBills.size()>0) { %>
Versions: <%for (Bill rBill:rBills){
	
	
%><a href="/legislation/bill/<%=rBill.getSenateBillNo()%>"><%=rBill.getSenateBillNo()%></a> <%}%>
<%}

if (sponsor == null)
	sponsor = "";

%>




    
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
//String billText = bill.getFulltext();

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
  
<%if (billSummary!=null){ %>
<%=billSummary%>
 <%} %>
 <hr/>
 <b>Sponsor: </b>
 <a href="<%=appPath%>/sponsor/<%=java.net.URLEncoder.encode(sponsor,"utf-8")%>"  class="sublink"><%=sponsor%></a> 
<!--
<%if (bill.getActClause()!=null){ %>
 <%=bill.getActClause()%>
 <%} %>
 -->

 
 <%if (bill.getCoSponsors()!=null && bill.getCoSponsors().size()>0){%>
/ <b>Co-sponsor(s):</b>
 <%
 Iterator<Person> it = bill.getCoSponsors().iterator();
 Person cp = null;
 while (it.hasNext()){ 
 cp = it.next();
 %>
 <a href="<%=appPath%>/sponsor/<%=java.net.URLEncoder.encode(cp.getFullname(),"utf-8")%>" class="sublink"><%=cp.getFullname()%></a><%if (it.hasNext()){%>, <%} %><%} %>


 <%} %>

 <%if (bill.getCurrentCommittee()!=null){ %>
 / <b>Committee:</b> <a href="<%=appPath%>/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>" class="sublink"><%=bill.getCurrentCommittee()%></a>
<%} %>
<br/>
<%if (bill.getLawSection()!=null){ %>
 <b>Law Section:</b> <a href="<%=appPath%>/search/?term=<%=java.net.URLEncoder.encode("lawsection:\"" + bill.getLawSection()+"\"","utf-8")%>" class="sublink"><%=bill.getLawSection()%></a>
 <%} %>

  <%if (bill.getLaw()!=null){ %>
 / <b>Law:</b> <%=bill.getLaw()%>
 <%} %>

</div>
 
 

<%
ArrayList<BillEvent> rActions = (ArrayList<BillEvent>)request.getAttribute("related-action");
%>
<%if (rActions.size() > 0) { %>
<h3><%=senateBillNo%> Actions</h3>
<ul>
	<%
		ArrayList<BillEvent> events = BillCleaner.sortBillEvents(rActions);
		for (BillEvent be : events){ 
			
			%>
				<li><%=df.format(be.getEventDate().getTime())%>: <%=BillCleaner.formatBillEvent(bill.getSenateBillNo(), be.getEventText(), appPath)%></li>
			<%
		}%>
</ul>
<%}%>

<%

ArrayList<Meeting> rMeetings = (ArrayList<Meeting>)request.getAttribute("related-meeting");
%>
<%if (rMeetings.size()>0) { %>
<h3><%=senateBillNo%> Meetings</h3>
<%
	for (Iterator<Meeting> itMeetings = rMeetings.iterator(); itMeetings.hasNext();){
		Meeting meeting = itMeetings.next();
		%>
		<a href="<%=appPath%>/meeting/<%=meeting.luceneOid()%>" class="sublink"><%=meeting.luceneTitle()%></a><%if (itMeetings.hasNext()){%>,<%}
		
	}
}
%>

<%

ArrayList<gov.nysenate.openleg.model.calendar.Calendar> rCals = (ArrayList<gov.nysenate.openleg.model.calendar.Calendar>)request.getAttribute("related-calendar");
%>
<%if (rCals.size()>0) { %>
<h3><%=senateBillNo%> Calendars</h3>
<%
for (Iterator<gov.nysenate.openleg.model.calendar.Calendar> itCals = rCals.iterator(); itCals.hasNext();)
{
	gov.nysenate.openleg.model.calendar.Calendar cal = itCals.next();
	Supplemental sup = cal.getSupplementals().get(0);
	
	sup.setCalendar(cal);
	
	String type = "";
	if (cal.getType().equals("active"))
		type = "Active List";
	else if (cal.getType().equals("floor"))
		type = "Floor Calendar";
	
	%>
<a href="<%=appPath%>/calendar/<%=sup.luceneOid()%>" class="sublink"><%=type%><%=sup.getCalendarDate() == null ? "" : ": " +  calendarSdf.format(sup.getCalendarDate())%></a><%if (itCals.hasNext()){%>,<%}

}
}
%>

<%

ArrayList<Vote> rVotes = (ArrayList<Vote>)request.getAttribute("related-vote");
%>
<%if (rVotes.size()>0) { %>
<h3><%=senateBillNo%> Votes</h3>
<%
for (Vote vote:rVotes){
   	
   	String voteType = "Floor Vote";
if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
	voteType = "Committee Vote";
	

  %>
   <div>
  <b>VOTE: <%=voteType.toUpperCase()%>:
  <%if (vote.getDescription()!=null){%>- <%=vote.getDescription()%><%} %>
  - <%=DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate())%></b>
  <blockquote>
  	<%Iterator<String> itVoter = null; String voter = null;%>
  	<%if (vote.getAyes()!=null && vote.getAyes().size()>0){ %>
 			<br/>
 	<b>Ayes (<%=vote.getAyes().size()%>):</b>
 	<% 
 		itVoter = vote.getAyes().iterator();
 		voter = null;
 		
 		while (itVoter.hasNext())
 		{
 			voter = itVoter.next();
 		%>
 		<a href="<%=appPath%>/sponsor/<%=voter%>" class="sublink"><%=voter%></a><%if (itVoter.hasNext()){%>,<%} %>
 		<% 
 		}
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
}
  %>
  

  
<%if (billMemo!=null && !billMemo.matches("\\s*")){%>
<h3><%=senateBillNo%> Memo</h3>
<pre><%=/*TextFormatter.formatMemo (billMemo)*/billMemo%></pre><%}%>
	
 <h3><%=senateBillNo%> Text</h3>
<%if (bill.getFulltext()!=null){
  
  String billText = TextFormatter.lrsPrinter(bill.getFulltext());
  billText = TextFormatter.removeBillLineNumbers (billText);
  
  %><pre><%=billText %></pre><%} else{%>Not Available.<%}%>
 
 <br/>
  
 </div>
  
 <%
 
 String disqusUrl = "";
 String disqusId = "";
 
 if (bill.getYear()==2009)
 {
	 disqusId = bill.getSenateBillNo().split("-")[0];
	 disqusUrl = "http://open.nysenate.gov/legislation/api/html/bill/" + disqusId;
 }
 else
 {
	 disqusId = bill.getSenateBillNo();
	 disqusUrl = "http://open.nysenate.gov/legislation/bill/" + disqusId;
 }
 %>
  
<div id="comments">
<b><p>*By contributing or voting you agree to the <a href = "http://nysenate.gov/legal">Terms of Participation</a> and <a href = "http://www.nysenate.gov/privacy-policy">Privacy Policy</a> and verify you are over 13.</p></b>
 <h3> <a name="discuss">Discuss!</a></h3>

<div id="disqus_thread"></div>
<script type="text/javascript">
    /* * * CONFIGURATION VARIABLES: EDIT BEFORE PASTING INTO YOUR WEBPAGE * * */
    var disqus_shortname = 'nysenateopenleg'; // required: replace example with your forum shortname

    // The following are highly recommended additional parameters. Remove the slashes in front to use.
     var disqus_identifier = '<%=disqusUrl%>';
     var disqus_url = '<%=disqusUrl%>';
     var disqus_developer = 0; // developer mode is off
     var disqus_title = '<%=title%>';


    /* * * DON'T EDIT BELOW THIS LINE * * */
    (function() {
        var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
        dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
    })();
</script>
<noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
<a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>

</div>

 

<jsp:include page="/footer.jsp"/>

