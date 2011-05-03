package gov.nysenate.openleg.qa.test;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.qa.test.ReportedBillManager.BillType;
import gov.nysenate.openleg.qa.test.ReportedBillManager.ReportFieldType;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.queryParser.ParseException;

public class ReportBuilder {
	long newestMod = 0L;
	final static double MS_IN_DAY = 86400000.0;
	final static int MAX_RESULTS = 500;

	public HashMap<String, ReportedBill> getBillReportSet(String year)
			throws ParseException, IOException {
		// add ReportBills to map, keeping track of missing fields
		// intentionally leaving memos out for the time being
		HashMap<String, ReportedBill> billReportMap = new HashMap<String, ReportedBill>();
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
			HashMap<String, ReportedBill> billReportMap) throws ParseException,
			IOException {

		ArrayList<Result> resultList = getResultList(field, year);

		for (Result result : resultList) {
			Bill bill = (Bill) ApiHelper.getMapper().readValue(
					formatJson(result.getData()), Bill.class);

			ReportedBill reportBill = null;
			if ((reportBill = billReportMap.get(bill.getSenateBillNo())) != null) {
				reportBill.addProblematicField(field, ReportFieldType.MISSING);
				
			} else {
				reportBill = new ReportedBill(bill.getSenateBillNo(), result.getLastModified(),
						BillType.PROBLEM_BILL, field, ReportFieldType.MISSING);
				billReportMap.put(bill.getSenateBillNo(), reportBill);
			}
		}
	}

	public ArrayList<Result> getResultList(String field, String year)
			throws ParseException, IOException {

		SenateResponse sr = SearchEngine.getInstance().search(
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
}
