package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.SessionYear;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryParser.ParseException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

public class ReportBuilder {
	
	public static void main(String[] args) throws ParseException, IOException {
		ReportBuilder builder = new ReportBuilder();
		Report report = builder.run();
		
		JsonGenerator gen = ApiHelper.getMapper().getJsonFactory().createJsonGenerator(new PrintWriter(System.out));
		gen.setPrettyPrinter(new DefaultPrettyPrinter());
		ApiHelper.getMapper().writeValue(gen, report);
		
	}
	
	final int MAX_RESULTS = 100;
	final String SENATOR_URL = "http://open.nysenate.gov/legislation/senators";
	final String COMMITTEE_URL = "http://open.nysenate.gov/legislation/committees/";

	Report report;
	long start;
	long end;
	long time = new Date().getTime();
	long oldestMod = time;

	public ReportBuilder(long start, long end) {
		this.start = start;
		this.end = end;

		report = new Report(start, end);
	}

	public ReportBuilder() {
		//default interval is sunday 12:00:00 am to saturday 11:59:59 pm
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

		report = new Report(start, end);
	}

	public Report run() throws ParseException, IOException {
		return run(SessionYear.getSessionYear() + "");
	}

	public Report run(String year) throws ParseException, IOException {
		//add range counts with new, occurred and total
		ArrayList<ReportType> types = new ArrayList<ReportType>();
		types.add(simpleNumberResults("bill"));
		types.add(simpleNumberResults("calendar"));
		types.add(simpleNumberResults("meeting"));
		types.add(simpleNumberResults("transcript"));
		types.add(simpleNumberResults("vote"));
		report.setTypeReports(types);

		//add committee and senator bill totals
		report.setSenatorBills(this.getOpenlegSenators(year));
		report.setCommitteeBills(this.getOpenLegCommittees(year));
		
		//add ReportBills to map, keeping track of missing fields
		//intentionally leaving memos out for the time being
		HashMap<String, ReportBill> billReportMap = new HashMap<String, ReportBill>();
		addBillListToReport("full", year, billReportMap);
		addBillListToReport("sponsor", year, billReportMap);
		addBillListToReport("summary", year, billReportMap);
		addBillListToReport("title", year, billReportMap);
		addBillListToReport("actions", year, billReportMap);
		
		double MS_IN_DAY = 86400000.0;
		double MS_IN_HOUR = 3600000.0;
		
		//create and sort by heat		
		TreeSet<ReportBill> billReportSet = new TreeSet<ReportBill>(new ReportBill.ByHeat());
		for(String key:billReportMap.keySet()) {
			
			double mod = billReportMap.get(key).modified;
			int size = billReportMap.get(key).missingFields.size();
			
			double difference = Math.abs((oldestMod + 1) - mod) + 0.0;
			double offset = 0;
			if(difference > MS_IN_DAY) {
				offset = 1;
			}
			
			//an attempt to rank "heat" based on how many fields are missing and how long they've been missing
			double hoursDiff = (difference % MS_IN_DAY) / MS_IN_HOUR;
			hoursDiff = hoursDiff > 3 ? 4 : hoursDiff;
			
			double heat = 10 - (((hoursDiff)) / ((Math.pow(size*(2 + offset),1.5)) )* 10);

			billReportMap.get(key).setHeat(new BigDecimal(heat).setScale(1,BigDecimal.ROUND_UP).doubleValue());
			billReportSet.add(billReportMap.get(key));
		}
		
		report.setReportedBills(billReportSet);
		
		return report;
	}

	/**
	 * makes a query to lucene with  getResultList to find bills that don't have a valid parameter <field>
	 * @param field 
	 * @param year
	 * @param billReportMap
	 */
	public void addBillListToReport(String field,
			String year, HashMap<String, ReportBill> billReportMap) throws ParseException, IOException {

		ArrayList<Result> resultList = getResultList(field, year);
		
		for(Result result:resultList) {
			Bill bill =	(Bill)ApiHelper.getMapper().readValue(formatJson(result.getData()), Bill.class);
			
			ReportBill reportBill = null;
			if((reportBill = billReportMap.get(bill.getSenateBillNo())) != null) {
				reportBill.addMissingField(field);
				
				if(reportBill.modified < oldestMod)
					oldestMod = reportBill.modified;
			}
			else {
				reportBill = new ReportBill(result.getLastModified(), bill, field);
				billReportMap.put(bill.getSenateBillNo(), reportBill);
			}
		}
	}
	
	public ArrayList<Result> getResultList(String field, String year)
			throws ParseException, IOException {

		SenateResponse sr = SearchEngine2.getInstance().search(
				"otype:bill AND NOT " + field + ":[A* TO Z*] AND NOT " + field
						+ ":Z* AND oid:s* AND year:" + year, "json", 0,
				MAX_RESULTS, "sortindex", false);
		return sr.getResults();
	}
	
	public String formatJson(String jsonData) {
		jsonData = jsonData.substring(jsonData.indexOf(":") + 1);
		jsonData = jsonData.substring(0, jsonData.lastIndexOf("}"));
		return jsonData;
	}

	/**
	 * 
	 * @param type
	 * @return generated ReportType object with number of occurances, updates and the total
	 * 		   number of objects of @param type over the given time frame
	 */
	public ReportType simpleNumberResults(String type) throws ParseException,
			IOException {

		ReportType reportType = new ReportType();
		reportType.setType(type);
		reportType.setOccurred(getNumberResultsForWhen(type));
		reportType.setUpdated(getNumberResultsForType(type, true));
		reportType.setTotal(getNumberResultsForType(type, false));

		return reportType;
	}

	public int getNumberResultsForWhen(String type) throws ParseException,
			IOException {

		String query = "otype:" + type;
		query += " AND when:[" + start + " TO " + end + "]";
		return getNumberResultsForQuery(query);
	}

	public int getNumberResultsForType(String type, boolean toggle)
			throws ParseException, IOException {

		String query = "otype:" + type;
		if (toggle) {
			query += " AND modified:[" + start + " TO " + end + "]";
		}
		return getNumberResultsForQuery(query);
	}

	public int getNumberResultsForQuery(String query) throws ParseException,
			IOException {
		SenateResponse sr = SearchEngine2.getInstance().search(
				query + " AND active:true", "json", 0, MAX_RESULTS,
				"sortindex", false);
		return (Integer) sr.getMetadataByKey("totalresults");
	}
	

	/**
	 * reads openleg senator page to generate senator bill report
	 * @param year
	 * @return sorted TreeMap with the format <senator name, bills for senator determined by year>
	 */
	public TreeMap<String, Integer> getOpenlegSenators(String year)
			throws MalformedURLException, IOException, ParseException {

		TreeMap<String, Integer> retMap = new TreeMap<String, Integer>();

		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(
				SENATOR_URL).openStream()));
		Pattern pattern = Pattern
				.compile("\"/legislation/sponsor/(.+?)\\?filter=oid\\:s\\*\">(?!(Sponsored Bills|<img.+?>))(.+?)</a>");
		Matcher m = null;
		String in = null;

		while ((in = br.readLine()) != null) {
			m = pattern.matcher(in);
			if (m.find()) {
				retMap.put(m.group(1), getNumberResultsForQuery("otype:bill AND sponsor:\"" + URLDecoder
					.decode(m.group(1) + "\" AND year:" + year, "utf-8")));
			}
		}

		return retMap;
	}

	/**
	 * reads openleg committee page to generate committee bill report
	 * @param year
	 * @return sorted TreeMap with the format <committee name, bills for senator determined by year>
	 */
	public TreeMap<String, Integer> getOpenLegCommittees(String year)
			throws UnsupportedEncodingException, ParseException, IOException {

		TreeMap<String, Integer> retMap = new TreeMap<String, Integer>();

		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(
				COMMITTEE_URL).openStream()));
		Pattern pattern = Pattern
				.compile("<a href=\"/legislation/committee/(.+?)\">(.+?)</a>");
		Matcher m = null;
		String in = null;

		while ((in = br.readLine()) != null) {
			m = pattern.matcher(in);
			if (m.find()) {
				retMap.put(m.group(2),
					getNumberResultsForQuery("otype:bill AND oid:s* AND committee:\""
					+ m.group(1) + "\" AND year:" + year));
			}
		}

		return retMap;
	}

}
