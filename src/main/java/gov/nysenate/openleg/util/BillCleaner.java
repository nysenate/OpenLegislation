package gov.nysenate.openleg.util;

import gov.nysenate.openleg.OpenLegConstants;

import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BillCleaner implements OpenLegConstants {
	
	public final static String BILL_BAD_REGEXP = "[a-zA-Z][\\W]?0?\\d{2,}+[\\W]?[a-zA-Z]?";
	
	public final static String BILL_SEARCH_REGEXP = "[a-zA-Z][\\W]?0?\\d{2,}+[\\W]?[a-zA-Z]?";
	
	public final static String BILL_REGEXP = "[a-zA-Z][1-9]\\d{1,}+[a-zA-Z]?";
	
	public static String billFormat(String key) {		
		if(key.matches(BILL_BAD_REGEXP)){
			key = key.replaceAll("\\.", "");
			key = key.replaceAll("\\-", "");
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
	
	public static String formatV2Bill(String term) {
		
		String year = null;
		String bill = null;
		
		if(term.matches(BILL_BAD_REGEXP + "-\\d{4}")) {
			year = term.substring(term.length()-5,term.length());
			bill = term.substring(0,term.length()-5);
			
			if(bill.length() < 3) {
				bill = bill + year;
				year = "";
			}
		}
		else {
			bill = term;
			year = "";
		}
		
		return billFormat(bill) + year;
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

}
