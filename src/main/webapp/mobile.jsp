<%@ page language="java" import="java.util.*, java.io.*, gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,javax.jdo.*" pageEncoding="UTF-8"%>
<%
	String term = request.getParameter("term");

PersistenceManager pm = PMF.getPersistenceManager();

long start = 0;
long end = 20;

if (term == null)
{
        term = request.getRequestURI();
        term = term.substring(term.lastIndexOf('/')+1);
}

Bill bill = PMF.getBill(pm, term.toUpperCase());
Collection<Bill> results = null;

if (bill == null)
{
    if (term.startsWith("s"))
        term = term.substring(1);
        
        results = PMF.queryBills("senateBillNo.indexOf(\"" + term.toUpperCase() + "\")!=-1",start,end).getResult();
        
        if (results.size()>0)
        {
                if (results.size()==1)
                {
                        bill = new ArrayList<Bill>(results).get(0);
                }
                else
                {
                }
        }
        
}
else
{
        results = new ArrayList<Bill>();
        results.add(bill);
}

if (results.size()==0)
        results = PMF.getBillFromSponsor(term,start,end,true).getResult();

if (results.size()==0)
        results = PMF.queryBills("currentCommittee",term,start,end).getResult();
        
if (results.size()==0)
        results = PMF.queryBills("summary.indexOf(\"" + term + "\")!=-1",start,end).getResult();
        
        if (results != null && results.size()>0)
        {

                if (results.size()==1)
                {
%>
<%=bill.getSponsor().getFullname()%> <%=bill.getCurrentCommittee()%> <%=bill.getTitle()%>
<%
	}
                else {

                Iterator<Bill> it = results.iterator();

                while (it.hasNext()){
                bill = it.next();
%>
                <%=bill.getSenateBillNo()%>&nbsp;
<%
                }
                        
                }
                return;
        }
        else
        {
%>
no results found
<%
        }

%>
