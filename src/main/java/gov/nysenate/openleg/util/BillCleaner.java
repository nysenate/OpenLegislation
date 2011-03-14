package gov.nysenate.openleg.util;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryParser.ParseException;


public class BillCleaner implements OpenLegConstants {
	
	public final static String BILL_BAD_REGEXP = "[a-zA-Z][\\W]?0?\\d{2,}+[\\W]?[a-zA-Z]?";
	
	public final static String BILL_SEARCH_REGEXP = "[a-zA-Z][\\W]*0?\\d{2,}+[\\W]?[a-zA-Z]?";
	
	public final static String BILL_REGEXP = "[a-zA-Z][1-9]\\d{1,}+[a-zA-Z]?";
	
	/*
	 * on search this attempts to format a bill id based on
	 * what version the bill is at and returns the 'desired'
	 * result.  this mimics lrs functionality
	 * 
	 * if s1234, s1234a and s1234b exist:
	 * 
	 * s1234a -> S1234A-2011
	 * s1234- -> S1234-2011
	 * s1234  -> S1234B-2011
	 * 
	 */
	public static String getDesiredBillNumber(String billNumber) {
		String temp = billNumber;
		if(billNumber == null || (billNumber = fixBillNumber(billNumber)) == null) {
			if(temp != null && temp.split("-").length == 2) {
				return temp;
			}
			return null;
		}
				
		char c = billNumber.charAt(billNumber.length()-1);
		
		
		if (c == '-'){
			return billNumber + SessionYear.getSessionYear();
		}
		else if(!Character.isDigit(c)) {
			try {
				if(SearchEngine2.getInstance().search("oid:" + billNumber + "-" + SessionYear.getSessionYear(), "json", 0, 1, null, false).getResults().size() == 1) {
					return billNumber + "-" + SessionYear.getSessionYear();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return getNewestAmendment(billNumber);
		}
		else {
			return getNewestAmendment(billNumber);
		}
	}
	
	public static String fixBillNumber(String billNumber) {
		if(billNumber.matches(BILL_SEARCH_REGEXP)) {
			billNumber = billNumber.replaceAll("([ _\\.]|(?!\\-$)\\-)", "")
				.replaceAll("(^\\w)(0*)(.*?$)","$1$3")
				.toUpperCase();

			return billNumber;
		}
		return null;
	}
	
	private static String getNewestAmendment(String billNumber) {
		ArrayList<Result> results = getRelatedBills(billNumber);
		billNumber = billNumber + "-" + SessionYear.getSessionYear();
		
		if(results.isEmpty())
			return billNumber;
		
		ArrayList<String> billNumbers = new ArrayList<String>();				
		for(Result result:results) {
			billNumbers.add(result.getOid());
		}
		
		Collections.sort(billNumbers);
				
		return billNumbers.get(billNumbers.size()-1);
	}
	
	private static ArrayList<Result> getRelatedBills(String billNumber) {
		if(!Character.isDigit(billNumber.charAt(billNumber.length()-1))) {
			billNumber = billNumber.substring(0, billNumber.length()-1);
		}
		
		String query = "otype:bill AND oid:((" 
					+ billNumber + "-" + SessionYear.getSessionYear() 
		                + " OR [" + billNumber + "A-" + SessionYear.getSessionYear()  
		                   + " TO " + billNumber + "Z-" + SessionYear.getSessionYear() 
		                + "]) AND " + billNumber + "*-" + SessionYear.getSessionYear()  + ")";
		
		try {
			return SearchEngine2.getInstance().search(query, "json", 0, 100, null, false).getResults();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<Result>();
	}
	
	public static String formatBillEvent(String bill, String event, String appPath) {
		event = event.toUpperCase();
		if(event.contains("AMENDED") || event.contains("PRINT NUMBER")) {
			Pattern p = Pattern.compile("^(.*?)(\\d{2,5}\\w?)(.*?)$");
			Matcher m = p.matcher(event);
			
			if(m.find()) {
				return m.group(1) 
					+ "<a href=\"" 
						+ appPath 
						+ "/bill/" 
						+ bill.substring(0, 1) + m.group(2) + "-" + bill.split("-")[1] 
					+ "\">" 
					+ m.group(2) 
					+ "</a>" + m.group(3);
			}
		}
		else if(event.contains("SUBSTITUTED")) {
			Pattern p = Pattern.compile("^(.*?)(\\w\\d{2,5}\\w?)(.*?)$");
			Matcher m = p.matcher(event);
			
			if(m.find()) {
				return m.group(1) 
					+ "<a href=\"" 
						+ appPath 
						+ "/bill/" 
						+ m.group(2) + "-" + bill.split("-")[1] 
					+ "\">" 
					+ m.group(2) 
					+ "</a>" + m.group(3);
			}
		}
		return event;
	}
	
	public static ArrayList<BillEvent> sortBillEvents(List<SearchResult> results) {
		TreeSet<BillEvent> set = new TreeSet<BillEvent>(new BillEvent.ByEventDate());
		
		for(SearchResult result:results) {
			if(result.getObject() instanceof BillEvent) {
				set.add((BillEvent)result.getObject());
			}	
		}
		
		return new ArrayList<BillEvent>(set);
	}
	
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
