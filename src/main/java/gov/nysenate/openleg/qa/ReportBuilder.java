package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.qa.model.ProblemBill;
import gov.nysenate.openleg.util.LongSearch;

import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.queryParser.ParseException;

public class ReportBuilder {
	final static double MS_IN_DAY = 86400000.0;
	final static int MAX_RESULTS = 500;
	
	LongSearch<Bill> longSearch;
	long newestMod ;
	
	public ReportBuilder() {
		longSearch = new LongSearch<Bill>();
		newestMod = 0L;
	}

	public HashMap<String, ProblemBill> getBillReportSet(String year)
			throws ParseException, IOException {
		// add ReportBills to map, keeping track of missing fields
		// intentionally leaving memos out for the time being
		HashMap<String, ProblemBill> billReportMap = new HashMap<String, ProblemBill>();
		//addBillListToReport("memo", year, billReportMap);
		addBillListToReport("full", year, billReportMap);
		addBillListToReport("sponsor", year, billReportMap);
		addBillListToReport("summary", year, billReportMap);
		addBillListToReport("title", year, billReportMap);
		addBillListToReport("actions", year, billReportMap);
		
		return billReportMap;
	}

	/**
	 * makes a query to lucene with getResultList to find bills that don't have
	 * a valid parameter <field>
	 * 
	 * @param field
	 * @param year
	 * @param billReportMap
	 */
	public void addBillListToReport(String field, String year,
			HashMap<String, ProblemBill> problemBillMap) throws ParseException,
			IOException {
		
		longSearch.query("otype:bill AND NOT " + field + ":[A* TO Z*] AND NOT " + field
						+ ":Z* AND oid:s* AND year:" + year);
		
		for(Bill bill:longSearch) {
			ProblemBill problemBill = null;
			if ((problemBill = problemBillMap.get(bill.getSenateBillNo())) != null) {
				problemBill.addMissingField(field);
				
			} else {
				problemBill = new ProblemBill(bill.getSenateBillNo(), bill.getModified());
				problemBill.addMissingField(field);
				problemBillMap.put(bill.getSenateBillNo(), problemBill);
			}
		}
	}

	public String formatJson(String jsonData) {
		jsonData = jsonData.substring(jsonData.indexOf(":") + 1);
		jsonData = jsonData.substring(0, jsonData.lastIndexOf("}"));
		return jsonData;
	}
}
