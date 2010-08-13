package gov.nysenate.openleg.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillCleaner {
	
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
				System.out.println("no");
				bill = billFormat(bill);
				
				System.out.println(bill);
				
				uri = uri.replaceAll(BILL_BAD_REGEXP, bill);
								
			}
		}
		return uri;
	}
	
	
	
	
	
	public static String formatSameAs(String sameAs, String billNo) {
		sameAs = sameAs.replaceAll(" , ", "");
		if(!sameAs.contains(billNo)) {
			sameAs += ", " + billNo;
		}
		return sameAs;
	}
}
