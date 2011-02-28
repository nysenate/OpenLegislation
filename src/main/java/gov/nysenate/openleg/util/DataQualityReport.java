package gov.nysenate.openleg.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryParser.ParseException;

import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SenateResponse;

public class DataQualityReport {
	final static int MAX_RESULTS = 500;
	final static String SENATOR_URL = "http://open.nysenate.gov/legislation/senators";
	final static String COMMITTEE_URL = "http://open.nysenate.gov/legislation/committees/";
	
	
	StringBuffer report = null;
	long start = 0L;
	long end = 0L;
	
	
	public static void main(String[] args) throws ParseException, IOException {
		DataQualityReport bqt = new DataQualityReport();
		System.out.println(bqt.buildReport("2011"));
	}
	
	public DataQualityReport() {
		report = new StringBuffer("");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		start = cal.getTimeInMillis();
		
		cal.set(Calendar.DAY_OF_WEEK, 6);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		end = cal.getTimeInMillis();
	}
	
	public String buildReport(String year) throws ParseException, IOException {
		report.append("OpenLeg report from " + new Date(start) + " to " + new Date(end) + "\n\n");
		
		report.append("\nBy the numbers (occurred last week / updated over the past week / total):\n");
		report.append("bills:        " 
				+ "N/A / " 
				+ getNumberResultsForType("bill", true) 
				+ " / " 
				+ getNumberResultsForType("bill", false) 
				+ "\n");
		report.append(simpleNumberResults("calendars:    ", "calendar"));
		report.append(simpleNumberResults("meetings:     ", "meeting"));
		report.append(simpleNumberResults("transcripts:  ", "transcript"));
		report.append(simpleNumberResults("votes:        " , "vote"));
		
		
		report.append("\n\n");
		
		report.append("Bill Data Report (listed bills are MISSING the given field):\n");
		addBillListToReport("text","full", year);
		addBillListToReport("memo","memo", year);
		addBillListToReport("sponsor","sponsor", year);
		addBillListToReport("summary","summary", year);
		addBillListToReport("title","title", year);
		addBillListToReport("actions","actions", year);
		
		report.append("\n\nBills by sponsor (total):\n");
		report.append(getOpenlegSenators("2011"));
		
		report.append("\n\nBills by committee (total):\n");
		report.append(getOpenLegCommittees("2011"));
		
		return report.toString();
	}
	
	public String simpleNumberResults(String head, String type)
			throws ParseException, IOException {
		
		return head
			+ getNumberResultsForWhen(type) 
			+ " / " 
			+ getNumberResultsForType(type, true) 
			+ " / " 
			+ getNumberResultsForType(type, false) 
			+ "\n";
	}
	
	public int getNumberResultsForWhen(String type)
			throws ParseException, IOException {
		
		String query = "otype:" + type;
		query += " AND when:[" + start + " TO " + end + "]";
		return getNumberResultsForQuery(query);
	}
	
	public int getNumberResultsForType(String type, boolean toggle)
			throws ParseException, IOException {
		
		String query = "otype:" + type;
		if(toggle) {
			query += " AND modified:[" + start + " TO " + end + "]";
		}
		return getNumberResultsForQuery(query);
	}
	
	public int getNumberResultsForQuery(String query) throws ParseException, IOException {
		SenateResponse sr = SearchEngine2.getInstance().search(
				query + " AND active:true", "json", 0, MAX_RESULTS, "sortindex", false);
		return (Integer) sr.getMetadataByKey("totalresults");
	}
	
	public ArrayList<String> getBillList(String field, String year)
			throws ParseException, IOException {
		
		ArrayList<String> list = new ArrayList<String>();
		SenateResponse sr = SearchEngine2.getInstance().search(
					"otype:bill AND NOT " 
					+ field 
					+ ":[A* TO Z*] AND oid:s* AND year:" 
					+ year, "json",
				0,
				MAX_RESULTS,
				"sortindex",
				false);
		
		for(Result result:sr.getResults()) {
			list.add(result.getOid());
		}
		
		return list;
	}
	
	public void addBillListToReport(String description, String field, String year)
			throws ParseException, IOException {
		
		ArrayList<String> idList = getBillList(field, year);
		report.append(description 
				+" (" 
				+ idList.size() 
				+ (idList.size() == MAX_RESULTS ? " of "
						+ getNumberResultsForQuery("otype:bill AND NOT " 
								+ field 
								+ ":[A* TO Z*] AND oid:s* AND year:" 
								+ year):"") 
				+ "):\n\t");
		
		for(int i = 0; i < idList.size(); i++) {
			report.append(idList.get(i));
			if((i+1) != idList.size()) {
				report.append(", ");
			}
			
			if(i != 0 && i%10 == 0) {
				report.append("\n\t");
			}
		}
		report.append("\n");
	}
	
	public String getOpenlegSenators(String year)
			throws MalformedURLException, IOException, ParseException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(
				SENATOR_URL).openStream()));
		Pattern pattern = Pattern.compile(
				"\"/legislation/search/\\?term\\=(sponsor.+?)\">(?!(Sponsored Bills|<img.+?>))(.+?)</a>");
		Matcher m = null;
		String in = null;
		
		StringBuffer list = new StringBuffer("");
		
		while((in = br.readLine()) != null) {
			m = pattern.matcher(in);
			if(m.find()) {
				list.append(
						m.group(3)
						+ ": "
						+ getNumberResultsForQuery(URLDecoder.decode(m.group(1), "utf-8") 
								+ " AND year:" + year)
						+ "\n");
			}
		}
		
		return list.toString();
	}
	
	public String getOpenLegCommittees(String year) 
				throws UnsupportedEncodingException, ParseException, IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(
				COMMITTEE_URL).openStream()));
		Pattern pattern = Pattern.compile(
				"<a href=\"/legislation/committee/(.+?)\">(.+?)</a>");
		Matcher m = null;
		String in = null;
		
		StringBuffer list = new StringBuffer("");
		
		while((in = br.readLine()) != null) {
			m = pattern.matcher(in);
			if(m.find()) {
				list.append(m.group(2) 
						+ ": " 
						+ getNumberResultsForQuery(
								"otype:bill AND oid:s* AND committee:" 
								+ m.group(1) 
								+ " AND year:" 
								+ year) 
						+ "\n");
			}
		}
				
		return list.toString();
	}
	
}
