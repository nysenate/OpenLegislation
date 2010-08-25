package gov.nysenate.openleg.util;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.model.Bill;

import java.util.Collection;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

public class BillCleaner implements OpenLegConstants {
	
	public final static String BILL_BAD_REGEXP = "[a-zA-Z][\\W]?0?\\d{2,}+[\\W]?[a-zA-Z]?";
	
	public final static String BILL_SEARCH_REGEXP = "[a-zA-Z][\\W]?0?\\d{2,}+[\\W]?[a-zA-Z]?";
	
	public final static String BILL_REGEXP = "[a-zA-Z][1-9]\\d{1,}+[a-zA-Z]?";
	
	public static String billFormat(String key) {		
		if(key.matches(BILL_BAD_REGEXP)){
			key = key.replaceAll("\\W", "");
			return removeZero(key);
		}
		return key;
	}
	
	private static String removeZero(String s) {				
		if(s.matches("[a-zA-Z][\\W]?0+\\d{2,}+[a-zA-Z]?")) {			
			return removeZero(s.replaceFirst("0",""));					
		}
		else {			
			return s;			
		}
	}
	
	public static String validBill(String uri) {
		Pattern p = Pattern.compile(BILL_BAD_REGEXP);
		Matcher m = p.matcher(uri);
		
		String bill = null;
		if(m.find()) {			
			bill = uri.substring(m.start(),m.end());			
			
			if(!bill.matches(BILL_REGEXP)) {
				bill = billFormat(bill);
								
				uri = uri.replaceAll(BILL_BAD_REGEXP, bill);
								
			}
		}
		return uri;
	}
	
	
	
	
	
	public static String formatSameAs(String sameAs, String billNo) {
		StringTokenizer st  = null;
		String newSameAs = null;
		SortedSet<String> set = new TreeSet<String>();
		
		if(sameAs == null)
			sameAs = "";
		if(billNo != null)
			set.add(billNo);
		
		sameAs = sameAs.replaceAll(" ,",",");
		sameAs = sameAs.replaceAll(",,", ",");
		st  = new StringTokenizer(sameAs, ",");
		
		while(st.hasMoreElements()) {
			String token = st.nextToken().trim();
			if(!token.equals("")) {
				set.add(token);
			}
		}
			
		
		for(String s:set) {
			if(newSameAs == null) {
				newSameAs = s;
			}
			else {
				newSameAs += ", " + s;
			}
		}
		
		return newSameAs;
	}
	
	public static void main(String[] args) {	
		
		int pageSize = 50;
		
		
		int start = 1;
		int end = start+pageSize;
		
		PersistenceManager pm = PMF.getPersistenceManager();
		Collection<Object> result = null;
		
		
		do {
			result = getDetachedObjects(Bill.class, null, start, end);
			start += pageSize;
			end = start+pageSize;
		}
		while (result.size() == pageSize);
	}
	
	public static Collection getDetachedObjects (Class someClass, String orderBy, int start, int end) 
    {
		Collection<Object> results = null;
		
	    PersistenceManager pm = PMF.getPersistenceManager();
        
        Transaction tx = pm.currentTransaction();
        
        try
        {
        	tx.begin();
        	
	        // 4. mark object as persistent
	        //pm.makePersistent(billMap);
	        Extent<Object> e=pm.getExtent(someClass,true);
	        pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
	        pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
	       
	        Query query = pm.newQuery(e);
	        
	        if (orderBy != null)
	        	query.setOrdering(orderBy);
	        
	        query.setRange(start, end);
	        // 3. perform query
	        results = (Collection<Object>)query.execute();
	        for(Object o:results) {
	        	Bill temp = (Bill)o;
	        	
	        	String old = temp.getSameAs();
	        	String newS = formatSameAs(temp.getSameAs(), null);
	        	
	        	if(old != null && !old.equals(newS)) {
	        		System.out.println(old + " : " + newS);
	        		Bill bill = PMF.getBill(pm, temp.getSenateBillNo());
	        		bill.setSameAs(newS);
	        	}
	        }
	        
	        results = pm.detachCopyAll(results);

	        query.closeAll();
	        
	        pm.flush();
	        
	        tx.commit();
        }
        catch (Exception e)
        {        	
        	e.printStackTrace();
    
        }
        finally
        {
        	if (tx.isActive())
        		tx.rollback();
        	pm.close();
        }

        return results;
    }
}
