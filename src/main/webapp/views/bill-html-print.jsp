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

String title = bill.getSenateBillNo() + " - NY Senate Open Legislation - " + titleText;

 %>
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
<ul>
<%
Iterator<BillEvent> itActions = bill.getBillEvents().iterator();
BillEvent beAction = null;

while (itActions.hasNext())
{
	beAction = itActions.next();	
	%>
	<li><%=beAction.getEventText()%></li>
	<%
}
%>
</ul>

<h3><%=bill.getSenateBillNo()%> Committee Meetings</h3>




<h3><%=bill.getSenateBillNo()%> Calendars</h3>


<h3><%=bill.getSenateBillNo()%> Votes</h3>

<%if (bill.getVotes()!=null&&bill.getVotes().size()>0){ %>

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
  

  
<%if (billMemo!=null){%>
<h3><%=bill.getSenateBillNo()%> Memo</h3>
<%=TextFormatter.formatMemo (billMemo)%><%}%>
	
 <h3><%=bill.getSenateBillNo()%> Text</h3>
<%if (billText!=null){
  
  billText = TextFormatter.removeBillLineNumbers (billText);
  
  %><%=billText%><%} else{%>Not Available.<%}%>
 
 <br/>
  
 </div>
  
 
  

 

<jsp:include page="/footer.jsp"/>

