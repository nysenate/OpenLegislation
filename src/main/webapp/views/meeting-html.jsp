<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/html" pageEncoding="utf-8"%><%

String appPath = request.getContextPath();
	DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);

 CachedContentManager.fillCache(request);
Meeting meeting = (Meeting)request.getAttribute("meeting");

String title = "Committee Meeting: " + meeting.getCommitteeName() + " - " + df.format(meeting.getMeetingDateTime());
String calNo = null;

Addendum addendum = meeting.getAddendums().get(meeting.getAddendums().size()-1);

Agenda agenda = addendum.getAgenda();
calNo = agenda.getNumber() + "";

 %>
 
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>

<br/>
<h2><%=meeting.getCommitteeName()%> - <%=df.format(meeting.getMeetingDateTime())%></h2>
<br/>
 <div id="content">
  

<%

String chair = meeting.getCommitteeChair();
chair = chair.replace("?","");

%>  
<div>
<b>Agenda:</b> <a href="<%=appPath%>/meetings/<%=calNo%>"><%=calNo%></a> /
<b>Chair:</b> <a href="<%=appPath%>/meetings/<%=java.net.URLEncoder.encode(meeting.getCommitteeChair(),OpenLegConstants.ENCODING)%>"><%=chair%></a> / 
<b>Location:</b> <%=meeting.getLocation()%>

<script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script>
<br/>

<b>Addendum:</b> <%=addendum.getAddendumId() %> / 
<b>Published:</b> <%=addendum.getPublicationDateTime()%> /
<b>Week of:</b> <%=addendum.getWeekOf()%>

</div>
<%if (meeting.getNotes()!=null && meeting.getNotes().trim().length()>0){%>
<div>
<h3>Notes</h3>
<%
String meetingNotes = meeting.getNotes();

meetingNotes = meetingNotes.replaceAll("([A-Z,a-z,\\s,\\:,\\(,\\),\\.])(nn)([A-Z])","$1<br/><br/>$3");
meetingNotes = meetingNotes.replaceAll("([A-Z,a-z,\\s,\\:,\\(,\\),\\.])(n)([A-Z])","$1<br/>$3");

StringTokenizer st = new StringTokenizer(meetingNotes,"\n");

while (st.hasMoreTokens())
{
%><%=st.nextToken()%><%} %>
<%} %>
<div>
<h3>Bills on the Agenda</h3>
<%

Iterator<Bill> itBills = meeting.getBills().iterator();
Bill bill = null;
while (itBills.hasNext()){
bill = itBills.next();
try
{
request.setAttribute("bill",bill);	
	%>
	 <div class="billSummary">
 <b>

 <a href="<%=appPath%>/bill/<%=bill.getSenateBillNo()%>"><%=bill.getSenateBillNo()%><%if (bill.getSameAs()!=null){ %> (Same as: <%=bill.getSameAs()%>)<%}%>:</a>
  
</b>

 <%if (bill.getTitle()!=null){ %>
<%=bill.getTitle()%>
<%} else if (bill.getSummary()!=null){ %>
 <%=bill.getSummary()%>
 <%} %>

 


	
	<%
	if (bill.getVotes()!=null && bill.getVotes().size()>0)
	{
	
	Iterator<Vote> itVotes = bill.getVotes().iterator();
	%>
	<ul>
	<%
		while (itVotes.hasNext())
		{
			Vote vote = itVotes.next();
		
			if (vote.getVoteType()==Vote.VOTE_TYPE_COMMITTEE && vote.getVoteDate().equals(meeting.getMeetingDateTime()))
			{
			
					request.setAttribute("vote",vote);
					
					String voteType = "Floor";
					if (vote.getVoteType() == Vote.VOTE_TYPE_FLOOR)
						voteType = "Floor";
					else if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
						voteType = "Committee";
			%>
		<li>		
		Vote: <%=voteType%> 
		<%if (vote.getDescription()!=null){ %>(<%=vote.getDescription()%>)<%} %>
		<%=df.format(vote.getVoteDate())%>:

<%if (vote.getAyes()!=null){ %>
<%=vote.getAyes().size()%> Ayes <%} %>
<%if (vote.getAyeswr()!=null){ %>
/ <%=vote.getAyeswr().size()%> Ayes W/R <%} %>
<%if (vote.getNays()!=null){ %>
/ <%=vote.getNays().size()%> Nays
<%} %>
<%if (vote.getAbstains()!=null) { %> / <%=vote.getAbstains().size()%> Abstains<%} %>
<%if (vote.getExcused()!=null) { %> / <%=vote.getExcused().size()%> Excused<%} %>
</li>

				<%
			}
		}
	%>
	</ul>
	<% 
		
	
	}
	%>
	 </div>
	<%
	
	}
	catch (Exception e)
	{
		System.err.println("couldn't render bill: " + bill.getSenateBillNo());
	}
%>

<% 
}%>

</div>
 
 <div id="formatBox">
<b>Formats:</b> <a href="<%=appPath%>/api/1.0/xml/meeting/<%=meeting.getId()%>">XML</a>
</div>
 </div>


<jsp:include page="/footer.jsp"/>

