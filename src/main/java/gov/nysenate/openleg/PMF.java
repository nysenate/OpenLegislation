package gov.nysenate.openleg;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.BillEvent;
import gov.nysenate.openleg.model.Committee;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;

public final class PMF implements OpenLegConstants {
   
    private static PersistenceManagerFactory pmfInstance = null;
    
	private static Logger logger = Logger.getLogger(PMF.class);

  
    private PMF() {}
    
    {
    	getPersistenceManager ();
    }
    
    public static void closePersistenceManager () {
    	if (pmfInstance != null) {
    		pmfInstance.close();
    		pmfInstance = null;
    	}
    }
    
    public synchronized static PersistenceManager getPersistenceManager () {
    	if (pmfInstance == null) {
    		pmfInstance = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
    	}
		return pmfInstance.getPersistenceManager();
    }

    public static Bill getBill(PersistenceManager pm,String id, int year){
    	return _getBill(pm,id,year);
    }
    
    public static Bill getDetachedBill(String id, int year) {
    	return _getBill(null,id,year);
    }
    
	public static Transcript getTranscript (PersistenceManager pm,String id) {
		return _getTranscript(pm,id);
    }
	
	public static Transcript getDetachedTranscript(String id) {
		return _getTranscript(null,id);
	}
	
	public static Committee getCommittee (PersistenceManager pm, String name) {
		return _getCommittee(pm,name);
	}
	
	public static Committee getDetachedCommittee(String name) {
		return _getCommittee(null,name);
	}

	public static Person getPerson (PersistenceManager pm, String name) {
		return _getPerson(pm,name);
    }
	
	public static Person getDetachedPerson (String name) {
		return _getPerson(null,name);
	}
	
	public static BillEvent getBillEvent (PersistenceManager pm, Bill bill, Date eventDate, String eventText) throws Exception {
		return _getBillEvent(pm,bill,eventDate,eventText);
    }
	
	public static BillEvent getDetachedBillEvent(Bill bill, Date eventDate, String eventText) throws Exception {
		return _getBillEvent(null,bill,eventDate,eventText);
	}
	
    public static Vote getVote(PersistenceManager pm, Bill bill, Date voteDate, int ayeCount, int nayCount) {
    	return _getVote(pm,bill,voteDate,ayeCount,nayCount);
    }
    
    public static Vote getDetachedVote(Bill bill, Date voteDate, int ayeCount, int nayCount) {
    	return _getVote(null,bill,voteDate,ayeCount,nayCount);
    }
    
    
    
    private static Vote _getVote(PersistenceManager pm, Bill bill, Date voteDate, int ayeCount, int nayCount) {
    	String voteId = Vote.buildId(bill,voteDate,ayeCount,nayCount);
    	if(pm!=null)
			return (Vote) getObject(pm,Vote.class,"id",voteId,null);
    	return (Vote) getDetachedObject(Vote.class,"id",voteId,null);
    }
	private static Person _getPerson(PersistenceManager pm, String name) {
		String queryName = name.replace("'", "\\'");
		if (pm!=null)
			return (Person) getObject(pm,Person.class,"fullname",queryName,null);
		return (Person) getDetachedObject(Person.class,"fullname",queryName,null);
	}
	
	private static BillEvent _getBillEvent(PersistenceManager pm, Bill bill, Date eventDate, String eventText) throws Exception {
		String searchId = bill.getSenateBillNo() + "-" + eventDate.getTime() + "-" + URLEncoder.encode(eventText,"utf-8");
		if(pm!=null)
			return (BillEvent) getObject(pm,BillEvent.class,"billEventId",searchId,null);
		return (BillEvent) getDetachedObject(BillEvent.class,"billEventId",searchId,null);
	}
	
	private static Committee _getCommittee(PersistenceManager pm, String name) {
		String queryName = name.replace("'", "\\'");
		if (pm!=null)
			return (Committee)getObject(pm,Committee.class,"name",queryName,null);
		return (Committee)getDetachedObject(Committee.class,"name",queryName,null);
	}
	
    private static Transcript _getTranscript(PersistenceManager pm, String id) {
    	if (pm!=null)
    		return (Transcript) getObject(pm,Transcript.class,"id",id,"id descending");
    	return (Transcript) getDetachedObject(Transcript.class,"id",id,"id descending");
    }
    
    private static Bill _getBill(PersistenceManager pm,String billId, int year){
    	Bill bill = null;
    	billId = billId.replaceAll("[. ;]|%20","").trim().toUpperCase();
    	
    	String[] keys = {"senateBillNo","year"};
    	Object[] vals = {billId,new Integer(year)};
    	String[] types = {"String","int"};
    	
    	
    	if (pm!=null)
    		bill = (Bill) getObject(pm,Bill.class,keys,types,vals,null);
    	else
			bill = (Bill) getDetachedObject(Bill.class,keys,types,vals,null);
    	if (bill!=null)
    		fixSameAsDups(bill);
    	return bill;
    }
    
    @SuppressWarnings("unchecked")
	public static Object getDetachedObject(Class objectClass,String[] keys,String[] types,Object[] values, String orderBy) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		Object result = null;
		
		try {
			tx.begin();

	        result = getObject(pm,objectClass,keys,types,values,orderBy);
	        result = pm.detachCopy(result);
	        
	        //Don't need to close query because closing the pm will kill it?
	        tx.commit();
        }
        catch (Exception e) {
        	        	
        	logger.info("Unable to access object: " + e);
        	
        }
        finally {
        	if (tx.isActive())
        		tx.rollback();
        	if (!pm.isClosed())
      	      pm.close();
        }
        return result;
	}
    
    @SuppressWarnings("unchecked")
	public static Object getDetachedObject(Class objectClass,String primaryKey,String value, String orderBy) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		Object result = null;
		
		try {
			tx.begin();

	        result = getObject(pm,objectClass,primaryKey,value,orderBy);
	        result = pm.detachCopy(result);
	        
	        //Don't need to close query because closing the pm will kill it?
	        tx.commit();
        }
        catch (Exception e) {
        	        	
        	logger.info("Unable to access object: " + e);
        	
        }
        finally {
        	if (tx.isActive())
        		tx.rollback();
        	if (!pm.isClosed())
      	      pm.close();
        }
        return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Object getObject(PersistenceManager pm,Class objectClass, String key, String value, String orderBy ) {
		
		pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
        pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
        
        Extent e = pm.getExtent(objectClass,true);
        Query query = pm.newQuery(e,key + ".matches(key0)");
        
        query.declareParameters("String key0");
        
        if (orderBy != null) {
        	query.setOrdering(orderBy);
        }
        
        Collection results = (Collection) query.execute("(?i)" + value);
        if (results!=null) {
	        java.util.Iterator<Object> iter = results.iterator();
	        if (iter.hasNext())
	        	return iter.next();
	        return null;
        }
        return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Object getObject(PersistenceManager pm,Class objectClass, String[] keys, String[] types, Object[] values, String orderBy ) {
		
		pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
        pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
        
        Extent e = pm.getExtent(objectClass,true);
        
        StringBuilder squery = new StringBuilder();
        StringBuilder sparams = new StringBuilder();

        for (int i = 0; i < keys.length; i++)
        {
        	if (i != 0)
        	{
        		squery.append(" && ");
        		sparams.append(",");
        	}
        	
        	squery.append(keys[i]);
        	
        	if (types[i].equals("String"))
        		squery.append(".matches(key" + i + ")");
        	else
        		squery.append(" == key" + i);
        	
        	sparams.append(types[i]);
        	sparams.append(" key" + i);
        	
        }
        Query query = pm.newQuery(e,squery.toString());
        
        query.declareParameters(sparams.toString());
        
        if (orderBy != null) {
        	query.setOrdering(orderBy);
        }
        
       // Collection results = (Collection) query.execute("(?i)" + value);
        Collection results = (Collection) query.executeWithArray(values);
        if (results!=null) {
	        java.util.Iterator<Object> iter = results.iterator();
	        if (iter.hasNext())
	        	return iter.next();
	        return null;
        }
        return null;
	}
    
	private static void fixSameAsDups (Bill bill) {
		try {
			if (bill.getSameAs()!=null) {
				StringTokenizer st = new StringTokenizer(bill.getSameAs(),",");
				
				String sameAs = null;
				String lastSameAs = null;
				StringBuilder newSameAs = new StringBuilder();
				
				while (st.hasMoreTokens()) {
					sameAs = st.nextToken().trim().replace(" ", "");
					
					if (lastSameAs != null && sameAs.equals(lastSameAs))
						continue;
				
					newSameAs.append(sameAs);
					
					if (st.hasMoreTokens())
						newSameAs.append(", ");
					lastSameAs = sameAs;
				}
				
				bill.setSameAs(newSameAs.toString());
			}
		}
		catch (Exception e) {
			logger.warn("error fixing same as for: " + bill.getSenateBillNo(),e);
		}
	}

	public static Collection<?> getDetachedObjects (Class<?> someClass, String orderBy, int start, int end) 
    {
		Collection<?> results = null;
		
	    PersistenceManager pm = getPersistenceManager();
        
        Transaction tx = pm.currentTransaction();
        
        try
        {
        	tx.begin();
        	
	        // 4. mark object as persistent
	        //pm.makePersistent(billMap);
	        Extent<?> e=pm.getExtent(someClass,true);
	        pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
	        pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
	       
	        Query query = pm.newQuery(e);
	        
	        if (orderBy != null)
	        	query.setOrdering(orderBy);
	        
	        query.setRange(start, end);
	        
	        // 3. perform query
	        results = (Collection<?>)query.execute();
	
	        results = pm.detachCopyAll(results);

	        query.closeAll();
	        
	        pm.flush();
	        
	        tx.commit();
        }
        catch (Exception e)
        {
        	logger.info("error occured: " + e);
        	
        	
    
        }
        finally
        {
        	if (tx.isActive())
        		tx.rollback();
        	pm.close();
        }

        return results;
    }
	
	public static Collection<?> getDetachedObjects (Class<?> someClass, String key, String value, String orderBy, int start, int end) 
    {
		Collection<?> results = null;
		
	    PersistenceManager pm = getPersistenceManager();
        
        Transaction tx = pm.currentTransaction();
        
        try
        {
        	tx.begin();
        	
	        // 4. mark object as persistent
	        //pm.makePersistent(billMap);
	        Extent<?> e=pm.getExtent(someClass,true);
	        pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
	        pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
	       
	        Query query = pm.newQuery(e,key + ".matches(key0)");
	        query.declareParameters("String key0");

	        if (orderBy != null)
	        	query.setOrdering(orderBy);
	        
	        query.setRange(start, end);
	        
	        // 3. perform query
	        results = (Collection<?>)query.execute("(?i)" + value);
	
	        results = pm.detachCopyAll(results);

	        query.closeAll();
	        
	        
	        tx.commit();
        }
        catch (Exception e)
        {
        	logger.info("error occured: " + e);
        	
        	
    
        }
        finally
        {
        	if (tx.isActive())
        		tx.rollback();
        	if (!pm.isClosed())
      	      pm.close();
        }


        return results;
    }

	private static Transaction beginTransaction (PersistenceManager pm) throws Exception
	{
		Transaction tx = pm.currentTransaction();
		
    	if (tx.isActive())
    		tx.rollback();
    	
    	tx.begin();
    	
    	return tx;
	}
	
	public static Collection<?> getDetachedObjects (Class<?> someClass, String key, long idValue, String orderBy, int start, int end) 
    {
		Collection<?> results = null;
	    PersistenceManager pm = getPersistenceManager();
        
        Transaction tx = pm.currentTransaction();
        
        try
        {
        	tx.begin();
	        // 4. mark object as persistent
	        //pm.makePersistent(billMap);
	        Extent<?> e=pm.getExtent(someClass,true);
	        pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
	        pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
	       
	        Query query = pm.newQuery(e,key + "==" + idValue);
	       
	        query.setRange(start, end);

	        query.setOrdering(orderBy);
	        
	        // 3. perform query
	        results = (Collection<?>)query.execute();
	
	        results = pm.detachCopyAll(results);
	        
	        query.closeAll();
	        
	        tx.commit();
        }
        catch (Exception e)
        {
        	logger.info("error occured: " + e);
        	
        	
        }
        finally
        {
        	if (tx.isActive())
        		tx.rollback();
        	if (!pm.isClosed())
      	      pm.close();
        }


        return results;
    }
	
	public static Collection<?> getDetachedObjects (Class<?> someClass, String key, Date dateVal, String orderBy, int start, int end) 
    {
		Collection<?> results = null;
		
	    PersistenceManager pm = getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        
        try
        {
        	
        	tx.begin();
        	
        	Date startDate = new Date(dateVal.getTime());
        	Date endDate = new Date(dateVal.getTime());
        	endDate.setHours(23);
        	endDate.setMinutes(59);
        	
	        // 4. mark object as persistent
	        //pm.makePersistent(billMap);
	        Extent<?> e=pm.getExtent(someClass,true);
	        pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
	        pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
	        
	        Query query = pm.newQuery(e,key + ">= start_date && " + key + " < end_date");
	        query.declareImports("import java.util.Date");
	        query.declareParameters("java.util.Date start_date,java.util.Date end_date");
	        
	        query.setRange(start, end);
	        
	      //  query.setOrdering("endDate descending");
	        results = (Collection<?>)query.execute(startDate,endDate);

	        results = pm.detachCopyAll(results);

	        query.setOrdering(orderBy);

		      query.closeAll();
	
	       tx.commit();
	        
        }
        catch (Exception e)
        {
        	logger.info("error occured: " + e);
        	
        	
        }
        finally
        {
        	if (tx.isActive())
        		tx.rollback();
        	if (!pm.isClosed())
      	      pm.close();
        }
        


        return results;
    }
	
	@SuppressWarnings("unchecked")
	public static void deleteBillEvents (PersistenceManager pm, Bill bill) throws Exception
    {
        
    	BillEvent bEvent = null;

        	String searchId = bill.getSenateBillNo() + "-.*";
        	
	        // 4. mark object as persistent
	        //getPersistenceManager().makePersistent(billMap);
	        Extent<?> e= pm.getExtent(BillEvent.class,true);
	        Query query = pm.newQuery(e,"billEventId.matches('(?i)" + searchId + "')");
	       // query.setOrdering("senateBillNo ascending");
	        
	        // 3. perform query
	        // 4. now iterate over the result to print each
	        // product and finish tx
	        Collection<BillEvent> result = (Collection<BillEvent>)query.execute();
	       
	        
	        if (result != null)
	        {
		        java.util.Iterator<BillEvent> iter = result.iterator();
		       
		        while (iter.hasNext())
		        {
		        	bEvent = (BillEvent)(iter.next());
		          //  log.info("found bill: " + bill.getSenateBillNo());
		        	pm.deletePersistent(bEvent);
		        }
		        
		         // 5. commit transaction
	        }
	        
	        query.closeAll();
	   
    }
	
	public static QueryResult getBillByKeywords(String key, long start, long end)
	{
		PersistenceManager pm = getPersistenceManager();
		
		Transaction tx = null;
		

		Collection<?> results = null;

		
		int total = 0;
		
		try
		{
			tx = beginTransaction(pm);
			
		
			String keyToken = " ";
			
			if (key.indexOf("\"")!=-1)
				keyToken = "\"";
			
			
			 StringBuffer queryString = new StringBuffer();
	
				StringBuffer params = new StringBuffer();
				Object[] paramArray = null;
			
				
				
			key = key.trim();
			
			
			if (key.length() > 0)
			{
				if (key.toLowerCase().indexOf(" and ")!=-1 || key.toLowerCase().indexOf(" or ")!=-1)
				{
					
					StringTokenizer stKey = new StringTokenizer(key,keyToken);
					String subKey = null;
					String lastKey = null;
				
					 paramArray = new Object[stKey.countTokens()-1];//setup the array for all the keys
					 int paramArrayIdx = 0;
					 String keyId = null;
					
					 queryString.append(" (");
					 
					while (stKey.hasMoreTokens())
					{
						 subKey = stKey.nextToken();
						
						 
						 if (subKey.equalsIgnoreCase("AND"))
						 {
							 queryString.append(')');
							 queryString.append(SQL_AND);
							 queryString.append('(');
							 
						 }
						 else if (subKey.equalsIgnoreCase("OR"))
						 {
							 queryString.append(')');
							 queryString.append(SQL_OR);
							 queryString.append('(');
						 }
						 else
						 {
							 paramArray[paramArrayIdx] = "(?i).*" + subKey + ".*";
							 keyId = "key" + paramArrayIdx;
							 params.append("String " + keyId);
							 if (stKey.hasMoreTokens())
								 params.append(", ");
							 paramArrayIdx++;
							 
							 if (lastKey != null && (!lastKey.equalsIgnoreCase("and")) && (!lastKey.equalsIgnoreCase("or")) )
							 {
								 queryString.append(SQL_OR);
							 }
							 
							 queryString.append(SQL_TITLE_MATCHES);
							 queryString.append(keyId);
							 queryString.append(SQL_CLOSE_PAREN);
							 
							 queryString.append(SQL_OR);
							 
							 queryString.append(SQL_SUMMARY_MATCHES);
							 queryString.append(keyId);
							 queryString.append(SQL_CLOSE_PAREN);
							 
							 queryString.append(SQL_OR);
							
							 queryString.append(SQL_MEMO_MATCHES);
							 queryString.append(keyId);
							 queryString.append(SQL_CLOSE_PAREN);
							 
							 queryString.append(SQL_OR);
							 
							 queryString.append(SQL_SPONSOR_MATCHES);
						     queryString.append(keyId);
						     queryString.append(SQL_CLOSE_PAREN);
						        
						     queryString.append(SQL_OR);
								 
							  queryString.append(SQL_COMM_MATCHES);
							  queryString.append(keyId);
							  queryString.append(SQL_CLOSE_PAREN);
						        
						        
						       
						 }
						 
						 lastKey = subKey;
					}
					
					queryString.append(") ");
					 
				}
				else
				{
				
					String fullKey = key.replace("\"", "").trim();
					
					String subKeyDelim = " ";
					 
					 if (key.indexOf("\"")!=-1)
						 subKeyDelim = "\"";
					 StringTokenizer stKey = new StringTokenizer(key,subKeyDelim);
					 
					 if (stKey.countTokens()>1)
						 paramArray = new Object[stKey.countTokens()+1];//setup the array for all the keys
					 else
						 paramArray = new Object[stKey.countTokens()];//setup the array for all the keys
					 
					 int paramArrayIdx = 0;
					 paramArray[paramArrayIdx] = "(?i).*" + fullKey + ".*";
					 String keyId = "key" + paramArrayIdx;
					 params.append("String " + keyId);
					 if (stKey.hasMoreTokens())
						 params.append(", ");
					 paramArrayIdx++;
					 
					 queryString.append(SQL_TITLE_MATCHES);
					 queryString.append(keyId);
					 queryString.append(SQL_CLOSE_PAREN);
					 
					 queryString.append(SQL_OR);
					 
					 queryString.append(SQL_SUMMARY_MATCHES);
					 queryString.append(keyId);
					 queryString.append(SQL_CLOSE_PAREN);
					 
					 queryString.append(SQL_OR);
					
					 queryString.append(SQL_MEMO_MATCHES);
					 queryString.append(keyId);
					 queryString.append(SQL_CLOSE_PAREN);
					 
					 queryString.append(SQL_OR);
						
					 queryString.append(SQL_BILL_MATCHES);
					 queryString.append(keyId);
					 queryString.append(SQL_CLOSE_PAREN);
					 
					 queryString.append(SQL_OR);
					 
					 queryString.append(SQL_SPONSOR_MATCHES);
				     queryString.append(keyId);
				     queryString.append(SQL_CLOSE_PAREN);
				        
				     queryString.append(SQL_OR);
						 
					  queryString.append(SQL_COMM_MATCHES);
					  queryString.append(keyId);
					  queryString.append(SQL_CLOSE_PAREN);
					 
					
					 String subKey = null;
					
					 if (stKey.countTokens()>1)
					 {
						 queryString.append(SQL_OR);
						 
						 while (stKey.hasMoreTokens())
						 {
							 subKey = stKey.nextToken().trim();
							 paramArray[paramArrayIdx] = "(?i).*" + subKey + ".*";
							 keyId = "key" + paramArrayIdx;
							 params.append("String " + keyId);
							 if (stKey.hasMoreTokens())
								 params.append(", ");
							 
							 paramArrayIdx++;
							 
							 queryString.append('(');
							 
							 queryString.append(SQL_TITLE_MATCHES);
							 queryString.append(keyId);
							 queryString.append(SQL_CLOSE_PAREN);
							 
							 queryString.append(SQL_OR);
							 
							 queryString.append(SQL_SUMMARY_MATCHES);
							 queryString.append(keyId);
							 queryString.append(SQL_CLOSE_PAREN);
							 
							 queryString.append(SQL_OR);
							 
							 queryString.append(SQL_MEMO_MATCHES);
							 queryString.append(keyId);
							 queryString.append(SQL_CLOSE_PAREN);
							 
							 queryString.append(SQL_OR);
							 
							 queryString.append(SQL_BILL_MATCHES);
							 queryString.append(keyId);
							 queryString.append(SQL_CLOSE_PAREN);
						
							 queryString.append(SQL_OR);
							 
							 queryString.append(SQL_SPONSOR_MATCHES);
						     queryString.append(keyId);
						     queryString.append(SQL_CLOSE_PAREN);
						        
						     queryString.append(SQL_OR);
								 
							  queryString.append(SQL_COMM_MATCHES);
							  queryString.append(keyId);
							  queryString.append(SQL_CLOSE_PAREN);
							        
							 queryString.append(')');
							 
							 if (stKey.hasMoreTokens())
								 queryString.append(SQL_OR);
						 }
						 
						 
					 }
				}
				 
				 
				logger.info("queryBills: " + queryString);
		    	
				//"parameters String lastNameParam"
				
		        // 4. mark object as persistent
		        //pm.makePersistent(billMap);
				
				
		        Query query = pm.newQuery(Bill.class,queryString.toString());
		        query.declareParameters(params.toString());
		       // query.declareVariables("Bill bill");
		        query.setOrdering(SORTINDEX_DESCENDING);
		        pm.getFetchPlan().setMaxFetchDepth(1);
		        results = (Collection<?>)query.executeWithArray(paramArray);
		        
		        total = results.size();
		        logger.info("queryBills: total count=" + total);
		        query.closeAll();
		        
		        if (start != -1)
		        	query.setRange(start,end);
		       
		        results = (Collection<?>)query.executeWithArray(paramArray);
	
		        results = pm.detachCopyAll(results);
		        
		        query.closeAll();
		        
		        tx.commit();
		       
			}
			else
			{
	
				 total = getResultCount("senateBillNo","gov.nysenate.openleg.model.Bill");
				 
				 Query query = pm.newQuery(Bill.class);
				 query.setOrdering(SORTINDEX_DESCENDING);
				 
		        if (start != -1)
		        	query.setRange(start,end);
		        
				results = (Collection<?>)query.execute();
				
		        results = pm.detachCopyAll(results);
		        query.closeAll();
			      
		        tx.commit();
			}
		}
		catch (Exception e)
		{
			logger.warn("error in keyword query",e);
			
			
		}
	    finally
	    {
	    	if (tx.isActive())
	    		tx.rollback();
			if (!pm.isClosed())
	      	      pm.close();
	    }
	        
        logger.info("queryBills: results=" + results.size());
        
        QueryResult qr = new QueryResult();
        qr.setResult(new ArrayList<Bill>((Collection<Bill>)results));
         
        qr.setTotal(total);
        
        return qr;

				
			
	}
    
    public static QueryResult getBillFromSponsor (String sponsor, long start, long end, boolean fuzzy) {
    	return getBillFromPerson("sponsor",sponsor,start,end, fuzzy);
    }
    
    public static QueryResult getBillFromCosponsor (String sponsor, long start, long end,boolean fuzzy) {
    	return getBillFromPerson("cosponsor",sponsor,start,end,fuzzy);
    }
    
    public static QueryResult getBillFromPerson (String personType, String sponsor, long start, long end, boolean fuzzy) {
    	logger.info("getBillFromPerson: " + personType + "=" + sponsor);
   
        String querySponsor = sponsor.replace("'","\\'");
        querySponsor = querySponsor.replace(".","\\\\.");
        
        StringBuffer queryString = new StringBuffer();
        
        queryString.append("( ");
        
        queryString.append(personType);
        queryString.append(".fullname.matches('(?i)");
        queryString.append(querySponsor);
        queryString.append("') ");
        
        if (sponsor.indexOf(' ')!=-1 && fuzzy)
	    {
        	queryString.append(" || ");
        	queryString.append('(');
        	
	        StringTokenizer st = new StringTokenizer(querySponsor," ");
	        String subKey = null;
	        
	        while (st.hasMoreTokens())
	        {
	        
	        	subKey = st.nextToken();
	        		 
	        	queryString.append(personType);
		        queryString.append(".fullname.matches('(?i)");
		        queryString.append(subKey);
		        queryString.append("')");
		        
		        if (st.hasMoreTokens())
		        	queryString.append(" || ");
			       
	        }
	        
	        queryString.append(')');
        	
	    }
	        
        queryString.append(" )");
        
        
        
        QueryResult qr = queryBills(queryString.toString(),start,end,null);
        
        logger.info("getBillFromPerson:[" + queryString.toString() + "] total results:" + qr.getTotal());
       
        return qr;
    }
    
    
    
    public static QueryResult queryBills (String key, String value) {
    	return queryBills (key + ".matches('(?i).*" + value + ".*')",-1,-1,null);
    }
    
    public static QueryResult queryBills (String queryString, long start, long end) {
    	return queryBills (queryString, start, end, null);
    }
    
    public static QueryResult queryBills (String queryString, long start, long end, String orderBy) {
    	logger.info("queryBills: " + queryString);
    	 QueryResult qr = null;
    	 
    	PersistenceManager pm = getPersistenceManager();
    	pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
	    pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
	       
    	Transaction tx = pm.currentTransaction();
    	
    	tx.begin();
    	
    	try
    	{
	        // 4. mark object as persistent
	        //pm.makePersistent(billMap);
	        Query query = pm.newQuery(Bill.class,queryString);
	        
	        if (orderBy == null)
	        	query.setOrdering(SORTINDEX_DESCENDING);
	        else
	        	query.setOrdering(orderBy);
	        
	        if (start != -1)
	        	query.setRange(start,end);
	        
	
	      //  ArrayList<Bill> results = new ArrayList<Bill>();
	        
	        // 3. perform query
	     
	        Collection<?> results = (Collection<?>)query.execute();
	        
	        results = pm.detachCopyAll(results);
	        
	        query.closeAll();
	        tx.commit();
	        
	        qr = new QueryResult();
	        qr.setResult(new ArrayList<Bill>((Collection<Bill>)results));
	        
	        int total = getResultCount("senateBillNo","gov.nysenate.openleg.model.Bill",queryString);
	        
	        qr.setTotal(total);
        
	      
	        
    	}
    	catch (Exception e)
    	{
    		
    	}
    	finally
    	{
    		if (tx.isActive())
    			tx.rollback();
			if (!pm.isClosed())
	      	      pm.close();
    	    
    	}
    	
    	return qr;
    }
    
    public static int getResultCount (String field, String type) {
    	return getResultCount (field, type, null);
    }
    
    public static int getResultCount (String field, String type, String queryString) {
	    PersistenceManager pm = getPersistenceManager();
	    pm.getFetchPlan().setMaxFetchDepth(1);
	    
	    Transaction tx = pm.currentTransaction();
	    tx.begin();
    	String countQuery = "SELECT count(" + field + ") FROM " + type;
    	
    	if (queryString != null)
    		countQuery += " where " + queryString;
    	
    	Query query = pm.newQuery(countQuery);
    	Long results = (Long) query.execute();
    	query.closeAll();
    	tx.commit();
    	if (!pm.isClosed())
 	      	      pm.close();
 	   
    	int intResult = results.intValue();
    	
    	logger.info(type + ":" + field + " = " + queryString + ";results=" + intResult);

    	
    	return intResult;
    }
    
    
	public static Object makePersistent (PersistenceManager pm, Object changedObj)
    {
        Object newObj = null;
        
        try
        {
        	newObj = pm.makePersistent(changedObj);
        
        }
        catch (Exception e)
        {
        	logger.warn("unable to persist object: ",e);
        	e.printStackTrace();
        }
        
        return newObj;
    }
    
    
    public static ArrayList<Bill> getBillRange (long start, long end)
    {
    	PersistenceManager pm = PMF.getPersistenceManager();
        
    	Transaction tx = pm.currentTransaction();
    	
    	tx.begin();
    	
        // 4. mark object as persistent
        //pm.makePersistent(billMap);
        Query query = pm.newQuery(Bill.class);
        query.setRange(start, end);
        query.setOrdering(SORTINDEX_DESCENDING);
        
        // 3. perform query
        
        Collection<?> results = (Collection<?>)query.execute();
        results = pm.detachCopyAll(results);
        ArrayList<Bill> allBills = new ArrayList<Bill>((Collection<Bill>)results);
        query.closeAll();
        tx.commit();
        
        if (!pm.isClosed())
        	pm.close();
        
        return allBills;
    }
    
    public static void resetBillSortIdx ()
    {
    	Bill bill = null;

    	String queryString = "";

    	long start = 0;
    	long end = 100;

    	PersistenceManager pm = PMF.getPersistenceManager();
    	Transaction trans = pm.currentTransaction();
    	trans.begin();
    		
    	Collection<?> alBills = null;
    	QueryResult qr = PMF.queryBills(queryString,start,end);
    	alBills = qr.getResult();

    	while (alBills.size()>0) {
    		
    		Iterator<?> it = alBills.iterator();
    		
    		while (it.hasNext()) {
    		
    			bill = (Bill)it.next();
    			
				String senateId = bill.getSenateBillNo();
				
				//remove leg type code
				senateId = senateId.substring(1);
				
				//remove amendment
				int amendIdx = senateId.length()-1;
				
				if (!Character.isDigit(senateId.charAt(amendIdx)))
				{
					senateId = senateId.substring(0,amendIdx);
				}
				
				bill.setSortIndex(Integer.parseInt(senateId));
				
				logger.info("setting sortIdx for bill: " + bill.getSenateBillNo() + " idx=" + bill.getSortIndex());
    		}
    		
    		start+=100;
    		end+=100;	
    		
    		alBills = PMF.queryBills(queryString,start,end).getResult();
    		
    	}

    	trans.commit();
    	
    	if (!pm.isClosed())
         		pm.close();
    }
    
    public static boolean removePersistedObject (PersistenceManager pm, Class<?> objClass, String objectId) {
		boolean removed = false;
		
		try {
			Object idInstance = pm.newObjectIdInstance(objClass, objectId);
			Object objInstance = pm.getObjectById(idInstance);
			
			if (objInstance != null) {
				pm.deletePersistent(objInstance);
				logger.info("SUCCESS! removed object: " +  objClass.getName() + ":" + objectId);
				
				removed = true;
			}
			else {
				logger.warn("could not find - " + objClass.getName() + ":" + objectId);
			}
		}
		catch (JDOObjectNotFoundException nfe) {
			logger.warn("could not find - " + objClass.getName() + ":" + objectId);
		}
		catch (Exception e) {
			logger.error("removePersistedObject(): could not remove persisted object: " + objectId,e);
		}
		finally {}
	
		return removed;
	}
    
    public static Object getPersistedObject (PersistenceManager pm, Class<?> objClass, String objectId, boolean closePersistenceManager)
	{
    	Object returnObject = null;
		if (pm == null)
			pm = PMF.getPersistenceManager();

		try{
			Object idInstance = pm.newObjectIdInstance(objClass, objectId);
			returnObject = pm.getObjectById(idInstance);		
		}
		catch (JDOObjectNotFoundException e) {
			logger.warn("objected does not exist yet: " + objectId);
		}
		catch (Exception e) {
			logger.warn("error accessing object: " + objectId,e);
		}
		finally {
    		if (closePersistenceManager && !pm.isClosed())
    			pm.close();
    	}
		
		return returnObject;
	}
    
    public static List<Bill> getAmendments (Bill bill)
    {

    	ArrayList<Bill> results = null;
    	
    	PersistenceManager pm = getPersistenceManager();
    	Transaction tx = null;
    	QueryResult qr = null;
    	
    	tx = pm.currentTransaction();
    	
    	try
    	{
	    	tx.begin();
    	
	    	String baseSenateId = bill.getSenateBillNo();
	    	
	    	if (baseSenateId.matches(".*[A-Z]"))
	    		baseSenateId = baseSenateId.substring(0,baseSenateId.length()-1);
	    	
	    	StringBuffer query = new StringBuffer();
	    	query.append("senateBillNo.matches('");
	    	query.append(baseSenateId);
	    	query.append("[A-Z]')");
	    	
	    	qr = queryBills (query.toString(),0,25,SENATEBILLNO_DESCENDING);
	    	
	    	results = new ArrayList<Bill> ((Collection<Bill>)qr.getResult());
	    	
	    	tx.commit();
			    	
    	}
    	catch (Exception e)
    	{
    		
    	}
    	finally
    	{
    		if (tx != null && tx.isActive())
    			tx.rollback();
    		if (!pm.isClosed())
    			pm.close();
    		
    	}
    	
    	return results;
    
    }
    
    public static ArrayList<String> getCommittees() {
    	PersistenceManager pm = PMF.getPersistenceManager();
    	Transaction trans = pm.currentTransaction();
    	ArrayList<String> result = null;
    	try {
    		trans.begin();
    		
	    	Query query = pm.newQuery("SELECT currentCommittee FROM Bill GROUP BY currentCommittee");
	    	query.declareImports("import gov.nysenate.openleg.model.Bill");
	        query.setOrdering(COMMITTEE_ASCENDING);
	        
	        result = new ArrayList<String>((Collection<String>)query.execute());

	        trans.commit();
    	}
    	catch (Exception e) {
    		logger.warn(e);
    		
    	 }
    	 finally {
    		if (trans.isActive())
     			trans.rollback();
         	if (!pm.isClosed())
         		pm.close();
         }

    	 return result;
    }
    
    public static ArrayList<BillEvent> getBillEvents (int start, int max) {
    	ArrayList<BillEvent> allBills = null;
    	PersistenceManager pm = PMF.getPersistenceManager();
    	Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
	        
	        Query query = pm.newQuery(BillEvent.class);
	        query.setOrdering(SORT_EVENTDATE_DESCENDING);
	        //query.addExtension("org.jpox.query.fetchSize", "20");
	        query.setRange(start,max);
	        
	        allBills = new ArrayList<BillEvent>((Collection<BillEvent>)query.execute());
	      
	        query.closeAll();
	        tx.commit();
    	}
    	catch (Exception e) {
    		logger.warn(e);
    		
    	}
    	finally {
    		if (tx.isActive())
    			tx.rollback();
         	if (!pm.isClosed())
         		pm.close();
        }
        return allBills;
    }
    
    
    public static ArrayList<BillEvent> getBillEventsBySponsor (String sponsor, int max)
    {
    	ArrayList<BillEvent> returnResults = new ArrayList<BillEvent>();
    	
	    PersistenceManager pm = getPersistenceManager();
	    Transaction tx = pm.currentTransaction();
	    
	    int increment = 200;
	    
	    long start = 0;
	    long end = increment;
	    
	    try
	    {
		    tx.begin();
		    
		    while (returnResults.size() < max)
		    {
		      
		
		        try
		        {
		        	  
		    		Extent<?> e=pm.getExtent(BillEvent.class,true);
		        	Query query = pm.newQuery(e);
		        	
			      //  query.setClass(BillEvent.class);
		//	        String filterText = "sponsor.matches('(?i).*" + sponsor + ".*')";
			       // query.setFilter(filterText);
		
			        query.setOrdering("eventDate descending");
			        
			        if (start != -1)
			        	query.setRange(start, end);
			        
			        Collection<?> result = ((Collection<?>)query.execute());
		        	result = pm.detachCopyAll(result);
		        	
		        	returnResults.addAll((Collection<BillEvent>)result);
		        	
		        	query.closeAll();
			        
		        }
		        catch (Exception e)
		        {
		        	//tx.rollback();
		        	logger.info("error getting billevents: " + e);
		        }
		        
		        start += increment;
		        end = start + increment;
	        
	    	}
		    
		    tx.commit();
		    
	    }
	    catch (Exception e)
	    {
	    	logger.info("error getting events for sponsor", e);
	    	
	    	
	    	
	    }
	    finally
	    {
	    	if (tx.isActive())
	    		tx.rollback();
			if (!pm.isClosed())
	      	      pm.close();
	    }

        return returnResults;
    }
    
    public static ArrayList<BillEvent> searchBillEvent (Date startDate, Date endDate, String eventText, long start, long end, String legTypeFilter)
    {
		ArrayList<BillEvent> returnResults = null;
		
		PersistenceManager pm = PMF.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
	
		tx.begin();
		
		try
		{
			
			returnResults = searchBillEvent (startDate, endDate, eventText, start, end, legTypeFilter);
			
			 tx.commit();
		        
        }
        catch (Exception e)
        {
        	
        
        	logger.info("error getting billevent: " + e);
        }
        finally
	    {
        	if (tx.isActive())
        		tx.rollback();
			if (!pm.isClosed())
	      	      pm.close();
	    }

        return returnResults;

    }
	
	public static ArrayList<BillEvent> searchBillEvent (PersistenceManager pm, Date startDate, Date endDate, String eventText, long start, long end, String legTypeFilter)
    {
        
    	ArrayList<BillEvent> returnResults = new ArrayList<BillEvent>();
    
    	

        try
        {
        	
        	if (startDate != null)
        	{
		        Query query = pm.newQuery(getPersistenceManager().getExtent(BillEvent.class,true));
		        
		        String filterText = "(eventDate >= startDate && eventDate <= endDate) && eventText.matches(event_query_text)";
		        
		        if (legTypeFilter != null)
		        {
		        	filterText += " && billEventId.matches('(?i)" + legTypeFilter + ".*')";
		        }
		        
		        query.setFilter(filterText);
		        
		        query.declareImports("import java.util.Date");
		        query.declareParameters("Date startDate,Date endDate,String event_query_text");
		        query.setOrdering("eventDate descending");

		        if (start != -1)
		        	query.setRange(start, end);
	
		        String queryText = "(?i).*" + eventText + ".*";

		        query.compile();
		       
		        
		        logger.info("searchBillEvent query: " + query.toString());
		        
		        
		        // 3. perform query
		        // 4. now iterate over the result to print each
		        // product and finish tx
		        Collection<?> result = (Collection<?>)query.execute(startDate,endDate,queryText);
		        result = pm.detachCopyAll(result);
		        returnResults = new ArrayList<BillEvent>((Collection<BillEvent>)result);
	        
		        query.closeAll();
        	}
        	else
        	{
        		Extent<?> e=pm.getExtent(BillEvent.class,true);
		        Query query = pm.newQuery(e);
		        
		        String filterText = "eventText.matches('(?i).*" + eventText + ".*')";

		        if (legTypeFilter != null)
		        {
		        	filterText += " && billEventId.matches('(?i)" + legTypeFilter + ".*')";
		        }
		        
		        query.setFilter(filterText);
		        
		        
		        query.setOrdering("eventDate descending");
		        if (start != -1)
		        	query.setRange(start, end);
		        //query.declareParameters("String event_query_text");
		        
		        Collection<?>  result = (Collection<?>)query.execute(eventText);
		        result = pm.detachCopyAll(result);
		        returnResults = new ArrayList<BillEvent>((Collection<BillEvent>)result);
		        
		        
		        query.closeAll();
		        
		        
        	}
        }
        catch (Exception e) {
        	logger.info("error getting billevents: " + e);
        }
        finally {}
        
        return returnResults;
    }
    
	
	public static QueryResult queryBills (String key, String value, long start, long end)
    {
    	return queryBills (key + ".matches('(?i).*" + value + ".*')",start,end,null);
    }
	
    /*
	
	
	public static ArrayList<Transcript> searchTranscripts (Date startDate, Date endDate, String eventText, long start, long end)
    {
        
    	ArrayList<Transcript> returnResults = new ArrayList<Transcript>();
	    
    	PersistenceManager pm = getPersistenceManager();
    	Transaction tx = pm.currentTransaction();


        try
        {
        	tx.begin();
        	
        	if (startDate != null)
        	{
		        Query query = pm.newQuery(getPersistenceManager().getExtent(Transcript.class,true));
		        
		        String filterText = "(timeStamp >= startDate && timeStamp <= endDate) && transcriptText.matches(event_query_text)";
		        
		        
		        query.setFilter(filterText);
		        
		        query.declareImports("import java.util.Date");
		        query.declareParameters("Date startDate,Date endDate,String event_query_text");
		        query.setOrdering("timeStamp descending");

		        if (start != -1)
		        	query.setRange(start, end);
	
		        String queryText = "(?i).*" + eventText + ".*";

		        query.compile();
		       
		        
		        logger.info("searchTranscript query: " + query.toString());
		        
		        
		        Collection<Transcript> result = (Collection<Transcript>)query.execute(startDate,endDate,queryText);
		        
		        result = pm.detachCopyAll(result);
		        
		        // 3. perform query
		        // 4. now iterate over the result to print each
		        // product and finish tx
		        returnResults = new ArrayList<Transcript>(result);
		        
		        query.closeAll();
	        
        	}
        	else
        	{
		        String queryText = "transcriptText.matches(key0)";

        		Extent ex = getPersistenceManager().getExtent(Transcript.class,true);
		        Query query = pm.newQuery(ex,queryText);
		        
		        query.declareParameters("String key0");
		        
		        query.setOrdering("timeStamp descending");
		        if (start != -1)
		        	query.setRange(start, end);
		        
		        String keyText = "(?i).*" + eventText + ".*";
		        
		        query.compile();

		        Collection<Transcript> result = (Collection<Transcript>)query.execute(keyText);
		        result = pm.detachCopyAll(result);
		        returnResults = new ArrayList<Transcript>(result);
		        
		        query.closeAll();
        	}
        	
        	tx.commit();
        
	     
        }
        catch (Exception e)
        {
        	
        	if (tx.isActive())
        		tx.rollback();
        	
        	logger.info("searchTranscripts() exception: " + e);
        	e.printStackTrace();
        }
        finally
        {
        	if (!pm.isClosed())
     	      pm.close();
        }
        
        return returnResults;
    }
	
	
    
    private static ArrayList<Bill> removeAmendedDups (ArrayList<Bill> results)
    {
    	 ArrayList<Bill> cleanResults = new ArrayList<Bill>();
         
         Bill bill = null;
         
         Iterator<Bill> it = results.iterator();
         
         while (it.hasNext())
         {
         	bill = it.next();
 	        Bill billAmd = PMF.getLatestAmendment(bill);
 	        
 	        if (billAmd != null)
 	        {
 	        	if (bill.getSenateBillNo().equals(billAmd.getSenateBillNo()))
 	        		cleanResults.add(bill);
 	        	
 	        	
 	        }
 	        else
 	        	cleanResults.add(bill);
 	        
         }
         
         return cleanResults;
	        	
    }   
    
    public static Bill getLatestAmendment (Bill bill)
    {
    	//logger.info("getLatestAmendment: source=" + bill.getSenateBillNo());
    	Bill result = null;
    	
    
    	
    	if (bill.getLatestAmendment()== null)
    	{
	    	
	    	 // 3. perform query
	    	ArrayList<Bill> results = getAmendments (bill);
	
	    	if (results.size()>0)
	    	{
	    		result = results.get(0);
	    		
	    		logger.info("getLatestAmendment: found=" + result.getSenateBillNo());
	    	}
	    
	    	bill.setLatestAmendment(result);
	    	
	    
	    	
    	}
    	else
    		result = bill.getLatestAmendment();
    		
    	return result;
    }
    
    public static ArrayList<Person> getPersons ()
    {
    	PersistenceManager pm = PMF.getPersistenceManager();
      
        // 4. mark object as persistent
        //pm.makePersistent(billMap);
        Query query = pm.newQuery(Person.class);
        query.setOrdering(SORT_FULLNAME_ASCENDING);
        // 3. perform query
        
        ArrayList<Person> allBills = new ArrayList<Person>((Collection<Person>)query.execute());
      
        return allBills;
    }
    
    public static ArrayList<Vote> getVotes (long start, long end) {
    	ArrayList<Vote> result = null;
    	PersistenceManager pm = PMF.getPersistenceManager();
    	Transaction tx = pm.currentTransaction();
        try {
	        tx.begin();
	        
 	        Query query = pm.newQuery(pm.getExtent(Vote.class,true));
 	        query.setOrdering("voteDate descending");
	        pm.getFetchPlan().setMaxFetchDepth(-1);
	        
 	        if (start != -1)
 	        	query.setRange(start,end);
 	        
 	        Collection<Vote> cResult = ( Collection<Vote>)query.execute();
 	        cResult = pm.detachCopyAll(cResult);
 	        
 	        result = new ArrayList<Vote>(cResult);
 	       
 	        query.closeAll();
 	        tx.commit();
        }
        catch (Exception e) {
        	logger.info("error occured retrieving vote",e);
        	if (tx.isActive())
        		tx.rollback();
        }
        finally {
        	if (!pm.isClosed())
        		pm.close();
        }

        return result;
    }
    
    public static void fixNoSponsor ()
    {
    	Bill bill = null;

    	String queryString = "sponsor == null";

    	long start = 0;
    	long end = 100;

    	PersistenceManager pm = PMF.getPersistenceManager();

    	Transaction trans = pm.currentTransaction();
    	
    	try
    	{
	    	if (!trans.isActive())
	    		trans.begin();
	    		
	    	Collection<Bill> alBills = null;
	    	QueryResult qr = PMF.queryBills(queryString,start,end);
	    	alBills = qr.getResult();
	
	    	while (alBills.size()>0)
	    	{
	    		
	    		Iterator<Bill> it = alBills.iterator();
	    		
	    		while (it.hasNext())
	    		{
	    		
	    		
	    			bill = it.next();
	    			
	    			if (bill.getSponsor() == null ||  bill.getSponsor().getFullname().length() == 0) {
	    				if (bill.getFulltext() != null) {
	    					String sponsorKey = "Introduced  by  Sen.";
	    					
	    					int sponsorKeyIdx = bill.getFulltext().indexOf(sponsorKey);
	    					
	    					if (sponsorKeyIdx != -1) {
	    						sponsorKeyIdx += sponsorKey.length(); 
	    						int sponsorKeyEndIdx = bill.getFulltext().indexOf("--", sponsorKeyIdx);
	    						
	    						String sponsorName = bill.getFulltext().substring(sponsorKeyIdx, sponsorKeyEndIdx);
	    						
	    						logger.info("extracted sponsor(" + sponsorName + ") for bill: " + bill.getSenateBillNo());
	    					//	Person pSponsor = PMF.getPerson(sponsorName);
	    						//bill.setSponsor(pSponsor);
	    					}		
	    				}
	    			}	
	    		}
	    		
	    		start+=100;
	    		end+=100;	
	    		
	    		alBills = PMF.queryBills(queryString,start,end).getResult();	
	    	}
	    	trans.commit();	
    	}
    	catch (Exception e) {
    		trans.rollback();
    	}
    	finally {
    		if (!pm.isClosed())
    			pm.close();
    	}
    }
    */
}