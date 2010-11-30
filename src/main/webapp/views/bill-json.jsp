<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,org.json.*,gov.nysenate.openleg.model.*" contentType="text/plain" pageEncoding="utf-8"%>
<%
 

 	Bill bill = (Bill)request.getAttribute("bill");

DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

org.json.JSONStringer js = new org.json.JSONStringer();
	
JSONWriter mainObj = js.array();
		
JSONWriter locObj = mainObj.object();
								
locObj.key("year");
locObj.value(bill.getYear());

locObj.key("senateId");
locObj.value(bill.getSenateBillNo());

locObj.key("sponsor");
locObj.value(bill.getSponsor().getFullname());

locObj.key("cosponsors");
if (bill.getCoSponsors() != null)
{
	JSONWriter locObjCosponsors = mainObj.array();

	Iterator<Person> it = bill.getCoSponsors().iterator();

	while (it.hasNext())
	{
		JSONWriter locObjPerson = locObjCosponsors.object();
		
		locObjPerson.key("cosponsor");
		locObjPerson.value(it.next().getFullname());
		
		locObjPerson.endObject();
	}
	
	locObjCosponsors.endArray();

}

locObj.key("title");
if (bill.getTitle()!=null)
	locObj.value(bill.getTitle());
else
	locObj.value("");
	
locObj.key("summary");
if (bill.getSummary()!=null)
	locObj.value(bill.getSummary());
else
	locObj.value("");


locObj.key("actions");
	
	JSONWriter locObjActions = mainObj.array();
	
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
	
	SearchEngine2 searchEngine = new SearchEngine2();
	
	ArrayList<SearchResult> srs = APIServlet.buildSearchResultList(searchEngine.search(actionSearch.toString(),"json",0,1000,"when",true));

	Iterator<SearchResult> itSrs = srs.iterator();
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
		
		JSONWriter locObjAction = locObjActions.object();
		
		locObjActions.key("action");
		locObjActions.value(actionString);
		
		locObjActions.key("timestamp");
		locObjActions.value(aDate.getTime() +"");
		
		locObjAction.endObject();
		
	
	}
	
} catch (Exception e) {}

	
	locObjActions.endArray();
	


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
		Iterator<String> it = null;
	
		if (vote.getAyes()!=null)
		{
			it = vote.getAyes().iterator();
			
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
			it = vote.getNays().iterator();
			
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
		
			it = vote.getAbstains().iterator();
			
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
			it = vote.getExcused().iterator();
			
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

mainObj.endArray();

%><%=mainObj.toString()%>