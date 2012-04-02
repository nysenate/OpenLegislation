<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,org.json.*,gov.nysenate.openleg.model.*"  pageEncoding="utf-8" contentType="text/plain"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

Collection<Bill> bills = (Collection<Bill>)request.getAttribute("bills");
Bill bill = null;

org.json.JSONStringer js = new org.json.JSONStringer();
	
JSONWriter mainObj = js.array();

Iterator<Bill> it = bills.iterator();

while (it.hasNext())
{

	JSONWriter locObj = mainObj.object();

	try
	{
	
		bill = it.next();
										
		locObj.key("year");
		locObj.value(bill.getYear());
		
		locObj.key("senateId");
		locObj.value(bill.getSenateBillNo());
		
		locObj.key("sponsor");
		locObj.value(bill.getSponsor() != null && bill.getSponsor().getFullname() != null ? bill.getSponsor().getFullname() : "");
		
		locObj.key("cosponsors");
		JSONWriter locObjCosponsors = mainObj.array();
		if (bill.getCoSponsors() != null)
		{
		
			Iterator<Person> itCosponsors = bill.getCoSponsors().iterator();
		
			while (itCosponsors.hasNext())
			{
				JSONWriter locObjPerson = locObjCosponsors.object();
				
				locObjPerson.key("cosponsor");
				locObjPerson.value(itCosponsors.next().getFullname());
				
				locObjPerson.endObject();
			}
		
		}
		locObjCosponsors.endArray();


		locObj.key("assemblySameAs");
		
		if (bill.getSameAs()!=null)
	locObj.value(bill.getSameAs());
		else
	locObj.value("");
		
		locObj.key("summary");
		
		if (bill.getSummary()!=null)
	locObj.value(bill.getSummary());
		else
	locObj.value("");
		
		locObj.key("committee");	
	
		if (bill.getCurrentCommittee() != null)
		{
	locObj.value(bill.getCurrentCommittee());
		}
		else
	locObj.value("");
	
	}
	catch (Exception e)
	{
		//error with this bill
	}
	

if (bill.getVotes()!=null && bill.getVotes().size()>0)
{ 
	
	locObj.key("votes");
	
	JSONWriter locObjVotes = mainObj.array();
	
	Iterator<Vote> itVotes = bill.getVotes().iterator();
   
	Vote vote = null;
   
	while (itVotes.hasNext())
    {
    	JSONWriter locObjVote = locObjVotes.object();
    
		vote = itVotes.next();
		
		locObjVote.key("timestamp");
		locObjVote.value(vote.getVoteDate().getTime() + "");
		
		if (vote.getAyes()!=null)
		{
			locObjVote.key("ayes");
			locObjVote.value(vote.getAyes().size()+"");
		}
		
		if (vote.getNays()!=null)
		{
			locObjVote.key("nays");
			locObjVote.value(vote.getNays().size()+"");
		}
		
		if (vote.getAbstains()!=null)
		{
			locObjVote.key("abstains");
			locObjVote.value(vote.getAbstains().size()+"");
		}
		
		if (vote.getExcused()!=null)
		{
			locObjVote.key("excused");
			locObjVote.value(vote.getExcused().size()+"");
		}
		
		locObj.key("voters");
	
		JSONWriter locObjVoters = mainObj.array();
		Iterator<String> itVote  = null;
	
		if (vote.getAyes()!=null)
		{
			itVote = vote.getAyes().iterator();
			
			while (it.hasNext())
			{
				JSONWriter locObjVoter = locObjVoters.object();
			
				locObjVoter.key("name");
				locObjVoter.value(it.next());
				
				locObjVoter.key("vote");
				locObjVoter.value("aye");
				
				locObjVoter.endObject();
			}
		}
		
		if (vote.getNays()!=null)
		{
			itVote = vote.getNays().iterator();
			
			while (it.hasNext())
			{
				JSONWriter locObjVoter = locObjVoters.object();
			
				locObjVoter.key("name");
				locObjVoter.value(it.next());
				
				locObjVoter.key("vote");
				locObjVoter.value("nay");
				
				locObjVoter.endObject();
			}
		}
		
		if (vote.getAbstains()!=null)
		{
		
			itVote = vote.getAbstains().iterator();
			
			while (it.hasNext())
			{
				
				JSONWriter locObjVoter = locObjVoters.object();
			
				locObjVoter.key("name");
				locObjVoter.value(it.next());
				
				locObjVoter.key("vote");
				locObjVoter.value("abstain");
				
				locObjVoter.endObject();
			}
		}
		
		if (vote.getExcused()!=null)
		{
			itVote = vote.getExcused().iterator();
			
			while (it.hasNext())
			{
				JSONWriter locObjVoter = locObjVoters.object();
			
				locObjVoter.key("name");
				locObjVoter.value(it.next());
				
				locObjVoter.key("vote");
				locObjVoter.value("excused");
				
				locObjVoter.endObject();
			}
		}
		
		locObjVoters.endArray();
			
		locObjVote.endObject();
		

    }
	
	locObjVotes.endArray();
}
	
	
	locObj.endObject();

}

mainObj.endArray();
%><%=mainObj.toString()%>