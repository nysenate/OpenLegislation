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

Bill bill = null;//PMF.getBill(pm, term.toUpperCase());
Collection<?> results = null;

if (bill == null)
{
    if (term.startsWith("s"))
        term = term.substring(1);
        
        results = (Collection<?>)PMF.queryBills("senateBillNo.indexOf(\"" + term.toUpperCase() + "\")!=-1",start,end).getResult();
        
        if (results.size()>0)
        {
                if (results.size()==1)
                {
                        bill = new ArrayList<Bill>((Collection<Bill>)results).get(0);
                }
                else
                {
                }
        }
        
}
else
{
        results = new ArrayList<Bill>();
        ((Collection<Bill>)results).add(bill);
}

if (results.size()==0)
        results = (Collection<?>)PMF.getBillFromSponsor(term,start,end,true).getResult();

if (results.size()==0)
        results = (Collection<?>)PMF.queryBills("currentCommittee",term,start,end).getResult();
        
if (results.size()==0)
        results = (Collection<?>)PMF.queryBills("summary.indexOf(\"" + term + "\")!=-1",start,end).getResult();
        
        if (results != null && results.size()>0)
        {

                if (results.size()==1)
                {
%>
<%=bill.getSponsor().getFullname()%> <%=bill.getCurrentCommittee()%> <%=bill.getTitle()%>
<%
	}
                else {

	                Iterator<?> it = results.iterator();
	
	                while (it.hasNext()){
	                	bill = (Bill)it.next();
						bill.getSenateBillNo();%>&nbsp;<%
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
