<%@ page language="java" import="java.util.*,java.text.*,javax.jdo.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>


<%

String appPath = request.getContextPath();
String billid = request.getParameter("id");

PersistenceManager pm = PMF.getPersistenceManager();

Bill bill = PMF.getBill(pm,billid);


%>


<%

String billSummary = bill.getSummary();
String billMemo = bill.getMemo();
String billText = bill.getFulltext();


if (bill.getTitle()!=null)
{
	String billTitle = bill.getTitle();
 
    if (billTitle.length()>100)
    {
            billTitle = billTitle.substring(0,100) + "...";
    }
 
	
}
 %>
 
<%

DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

 %>

 <div id="content">
 <h2>Bill <%=bill.getSenateBillNo()%>: <span style="font-size:80%"><%if (bill.getTitle()!=null){ %><%=bill.getTitle()%><%} %></span></h2>
  <hr/>
 <h2>Summary</h2>
 
 <%
 List<Bill> billAmendments = bill.getAmendments();
 
 if (billAmendments == null)
 	billAmendments = PMF.getAmendments(bill);
 
 if (billAmendments!=null && billAmendments.size()>0)
 {
 %>
 <h4>Versions / Amendments:
 <% 
	Bill billAmendment = null;
	Iterator<Bill> itAmendments = billAmendments.iterator(); 
	while (itAmendments.hasNext())
	{
		billAmendment = itAmendments.next();
		
		if (billSummary == null && billAmendment.getSummary()!=null)
			billSummary = billAmendment.getSummary();
			
		if (billMemo == null && billAmendment.getMemo()!=null)
			billMemo = billAmendment.getMemo();
			
		if (billText == null && billAmendment.getFulltext()!=null)
			billText = billAmendment.getFulltext();
			
  %>
<a href="bill.jsp?id=<%=billAmendment.getSenateBillNo()%>"><%=billAmendment.getSenateBillNo()%></a>
<%} %>
</h4>
<%} %> 
<h4>Same as:  
<%if (bill.getSameAs()!=null){ 

StringTokenizer st = new StringTokenizer(bill.getSameAs(),",");
String sameAs = null;
String sameAsLink = null;
Bill sameAsBill = null;

while(st.hasMoreTokens())
{
	sameAs = st.nextToken().trim();
	sameAsLink = "http://assembly.state.ny.us/leg/?bn=" + sameAs;
	
	if ((sameAsBill = PMF.getBill(pm,sameAs))!=null)
	{
		sameAsLink = "bill.jsp?id=" + sameAs;
		
		if (sameAsBill.getSameAs()==null)
		{
			sameAsBill.setSameAs(bill.getSenateBillNo());
		
			
		}
		
		if (billSummary == null && sameAsBill.getSummary()!=null)
			billSummary = sameAsBill.getSummary();
			
		if (billMemo == null && sameAsBill.getMemo()!=null)
			billMemo = sameAsBill.getMemo();
			
		if (billText == null && sameAsBill.getFulltext()!=null)
			billText = sameAsBill.getFulltext();
	}
%>
<a href="<%=sameAsLink%>"><%=sameAs%></a>
<%} %>
<%} else { %>No same as<%} %></h4>
 <h4>Sponsor: 
 <%if (bill.getSponsor()!=null && bill.getSponsor().getFullname()!=null){ %>
 <a href="<%=appPath%>/api/html/sponsor/<%=java.net.URLEncoder.encode(bill.getSponsor().getFullname(),"utf-8")%>"><%=bill.getSponsor().getFullname()%></a>
 (<a href="http://www.nysenate.gov/search/node/<%=java.net.URLEncoder.encode(bill.getSponsor().getFullname(),"utf-8")%>">visit nysenate.gov</a>)
 </h4>
 <%} %>
 <h4>Co-sponsor(s): 
 <%if (bill.getCoSponsors()!=null){%>
 <%
 Iterator<Person> it = bill.getCoSponsors().iterator();
 Person cp = null;
 while (it.hasNext()){ 
 cp = it.next();
 %>
 <a href="<%=appPath%>/api/html/sponsor/<%=java.net.URLEncoder.encode(cp.getFullname(),"utf-8")%>"><%=cp.getFullname()%></a>&nbsp;
 <%} %>
 <%} %></h4>
 <h4>Current Committee: 
 <%if (bill.getCurrentCommittee()!=null){ %>
 <a href="<%=appPath%>/api/html/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>"><%=bill.getCurrentCommittee()%></a>
<%} %>
</h4>
<br/>

<%if (billSummary!=null){ %>
<p>
 <%=billSummary%>
   </p>
 <%} %>
<%if (bill.getLaw()!=null){ %>
<p><b>Law:</b>
 <%=bill.getLawSection()%> / <%=bill.getLaw()%>
   </p>
 <%} %>
<%if (bill.getActClause()!=null){ %>
<p><b>Act:</b>
 <%=bill.getActClause()%>
   </p>
 <%} %>
 <br/>
<h2><%=bill.getSenateBillNo()%> Actions</h2>

<br/>
<h2><%=bill.getSenateBillNo()%> Votes</h2>
 <p>
 
 <%
 Iterator<Vote> itVotes = bill.getVotes().iterator();
 
 Vote vote = null;
 
 while (itVotes.hasNext())
 {
 	vote = itVotes.next();
 	
 	%>
 	Ayes: <%=vote.getAyes().size()%>
 	Nays: <%=vote.getNays().size()%>
 	<% 
 
 }
 
  %>
 </p>
 <br/>
<h2><%=bill.getSenateBillNo()%> Memo</h2>
 <pre><%if (billMemo!=null){%><%=billMemo%><%}else{%>none.<%} %></pre>
 <br/>
 <h2><%=bill.getSenateBillNo()%> Text</h2>
  <pre><%if (billText!=null){%><%=billText%><%} else{%>none.<%}%></pre>
 <br/>
  
 <hr/>
 
<div id="comments">
 <h3> Discuss!</h3>
 <div id="disqus_thread"></div><script type="text/javascript" src="http://disqus.com/forums/nysenateopenleg/embed.js"></script><noscript><a href="http://nysenateopenleg.disqus.com/?url=ref">View the discussion thread.</a></noscript><a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>
</div>

  </div>


