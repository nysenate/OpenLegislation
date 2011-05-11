package gov.nysenate.openleg.qa.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.ektorp.BulkDeleteDocument;
import org.ektorp.http.StdHttpClient;

import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.SessionYear;

public class ReportedBillManager extends CouchSupport {
	private Logger logger = Logger.getLogger(ReportedBillManager.class);
	
	public static final long FOUR_DAYS_MS = 345600000L;
	public static final long TWO_DAYS_MS = 172800000L;
	public static final long ONE_DAY_MS = 86400000L;
	public static final long CURRENT_TIME = System.currentTimeMillis();

	public static void main(String[] args) throws ParseException, IOException {
		System.setProperty("org.ektorp.support.AutoUpdateViewOnChange", "true");
		
		CouchInstance instance = CouchInstance.getInstance("test", true, new StdHttpClient.Builder().build());
//		instance.getDbInstance().deleteDatabase("test");
		
		
//		ReportManager rm = new ReportManager(5);
//		rm.executeReport();
		
//		ReportedBillManager rbm = new ReportedBillManager();
//		rbm.refreshReportedBills();
//		rbm.refreshMissingData();
//		rbm.refreshReportedBills();
//		rbm.refreshMissingData();
				
//		ReportedBillRepository rbr = new ReportedBillRepository(instance.getConnector());
		
//		List<ReportedBill> bills = rbr.findByOid("S3914-2011");
//		for(ReportedBill bill: bills) {
//			bill.setHideFromReport(true);
//			rbr.update(bill);
//		}
		
		
//		List<ReportedBill> bills = rbr.findProblemBillsForReport(100);
//		
//		for(ReportedBill bill:bills) {
//			System.out.println(bill.getOid() + " : " + bill.getBillType() + " : " + bill.getRank());
//		}
		
		ReportedBillManager rbm = new ReportedBillManager();
		
		ReportedBillRepository rbr = new ReportedBillRepository(instance.getConnector());
		
		Collection<ReportedBill> col = rbr.findProblemBillsForReport(1000);
		for(ReportedBill bill:col) {
			System.out.println(bill.getOid() + " : " + bill.getProblemFields());
		}
	}
	
	public ReportedBillManager() {
		super();
	}
	
	public void refreshReportedBills() throws ParseException, IOException {
		List<Object> toBeUpdated = new ArrayList<Object>();

		List<ReportedBill> couchBillList = rbr.getAll();
		HashMap<String, ReportedBill> indexBillMap = getReportedBillMapFromIndex();

		for (ReportedBill couchBill : couchBillList) {
			ReportedBill indexBill = null;
			if ((indexBill = indexBillMap.get(couchBill.getOid())) == null) {
				// document no longer in index, set to be removed
				toBeUpdated.add(BulkDeleteDocument.of(couchBill));
				continue;
			}
			
			if (!couchBill.getModified().equals(indexBill.getModified())) {
				couchBill.setModified(indexBill.getModified());
				
				/*
				 * when a bill is updated it should probably be
				 * flagged for reporting again
				 */
				couchBill.setHideFromReport(null);
			}
			
			if(couchBill.getActiveForReport() == null || !couchBill.getActiveForReport()) {
				couchBill.setActiveForReport(isActiveForReport(couchBill));
			}
			
			toBeUpdated.add(couchBill);

			// at the end of this operation the only bills remaining
			// in the map will be new to the index since this
			// process was last executed
			indexBillMap.remove(couchBill.getOid());
		}

		// add new bills from index
		toBeUpdated.addAll(indexBillMap.values());

		// execute bulk update
		instance.getConnector().executeBulk(toBeUpdated);
	}

	public void refreshMissingData() throws ParseException, IOException {
		List<ReportedBill> problemBillList = rbr.findByBillType(BillType.PROBLEM_BILL);
		
		logger.info("found " + problemBillList.size() + " existing problem bills in database");

		ReportBuilder reportBuilder = new ReportBuilder();
		HashMap<String, ReportedBill> reportedBillMap = 
				reportBuilder.getBillReportSet(SessionYear.getSessionYear() + "");
		
		logger.info("found " + reportedBillMap.size() + " problem bills in index");
		
		for(ReportedBill reportedBill:problemBillList)
		{
			//if bill was in missing report but no longer, clear dead values
			//else merge values with past version
			if(reportedBillMap.get(reportedBill.getOid()) == null)
			{
				logger.info("bill " + reportedBill.getOid() + " no longer in problem bill report");
				instance.getConnector().addToBulkBuffer(
						removeReportedBillMissingData(reportedBill));
			}
			else {
				logger.info("merging existing problem bill " + reportedBill.getOid() + " with new");
				mergeReportedBillMissingData(
						reportedBill, reportedBillMap.get(reportedBill.getOid()));
			}
		}
		
		logger.info("ranking and persisting problem bills");
		rbr.persistMixedCollection(billsWithRank(reportedBillMap.values()));
		
		logger.info("updating entries no longer in problem bill report");
		instance.getConnector().flushBulkBuffer();
		instance.getConnector().clearBulkBuffer();		
	}
	
	private ReportedBill mergeReportedBillMissingData(ReportedBill one, ReportedBill two) {
		if(one.getProblemFields() != null && !one.getProblemFields().isEmpty())
		{
			for(String problemFieldKey:one.getProblemFields().keySet())
			{
				if(!two.getProblemFields().containsKey(problemFieldKey))
				{
					two.getProblemFields().put(problemFieldKey, one.getProblemFields().get(problemFieldKey));
				}
			}
		}
		
		two.setActiveForReport(one.getActiveForReport());
		two.setHideFromReport(one.getHideFromReport());
		two.setModified(one.getModified());
		two.setProblemBillAction(one.getProblemBillAction());
		two.setProcessDate(one.getProcessDate());
		two.setPushToReport(one.getPushToReport());
		two.setRevision(one.getRevision());
		
		return two;
	}
	
	private ReportedBill removeReportedBillMissingData(ReportedBill reportedBill) {
		//bill not in missing field report, previously was
		if(reportedBill.getProblemFields() != null)
		{
			//remove missing fields, no longer applicable
			for(String problemFieldKey:reportedBill.getProblemFields().keySet())
			{
				if(reportedBill.getProblemFields().get(problemFieldKey).equals(ReportFieldType.MISSING))
				{
					reportedBill.getProblemFields().remove(problemFieldKey);
				}
			}
		}
		
		//if no problem fields exist the bill doesn't need any special flags
		if(reportedBill.getProblemFields() == null || reportedBill.getProblemFields().isEmpty())
		{
			reportedBill.removeReportedData();
		}
		
		return reportedBill;
	}

	public HashMap<String, ReportedBill> getReportedBillMapFromIndex()
			throws ParseException, IOException {
		SearchEngine se = SearchEngine.getInstance();

		int pageSize = 500;
		int pageIndex = 0;
		SenateResponse sr = null;

		HashMap<String, ReportedBill> reportedBills = new HashMap<String, ReportedBill>();

		do {
			sr = se.search("otype:bill AND year:2011 AND oid:S*", "json",
					pageIndex * pageSize, pageSize, null, false);

			for (int i = 0; i < sr.getResults().size(); i++) {
				Result result = sr.getResults().get(i);
				reportedBills.put(result.getOid(), new ReportedBill(result
						.getOid(), result.getLastModified(),
						BillType.STANDARD_BILL));
			}

			pageIndex++;
		} while (sr.getResults().size() == pageSize);

		return reportedBills;
	}
	
	
	public boolean isActiveForReport(ReportedBill reportedBill) {
		if(reportedBill.getActiveForReport() != null && reportedBill.getActiveForReport())
			return true;
		
		//if the bill has been processed before
		if(reportedBill.getProcessDate() != null)
		{
			//and it's process date is greater than four days ago
			if((CURRENT_TIME - reportedBill.getProcessDate() > FOUR_DAYS_MS))
			{
				//and it has been modified since that process date
				if(reportedBill.getModified() > reportedBill.getProcessDate())
				{
					//and it is a problem bill or it hasn't been modified for two days
					if(reportedBill.getBillType().equals(BillType.PROBLEM_BILL) 
							|| (CURRENT_TIME - reportedBill.getModified() > TWO_DAYS_MS))
					{
						return true;
					}
				}
			}
		}
		//the bill hasn't been processed before
		else {
			//and it's not active for report
			if(reportedBill.getActiveForReport() == null || !reportedBill.getActiveForReport()) {
				//and it hasn't been modified in two days
				if(CURRENT_TIME - reportedBill.getModified() > TWO_DAYS_MS) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Collection<ReportedBill> billsWithRank(Collection<ReportedBill> billReportList) {
		long newestMod = 0L;
		
		for (ReportedBill rb : billReportList) {
			if(rb.getModified() > newestMod)
				newestMod = rb.getModified();
		}
		
		// assign rank
		for (ReportedBill rb : billReportList) {
			rb.setRank(getRank(newestMod, rb.getModified(), rb.getProblemFields().size()));
			
			rb.setActiveForReport(isActiveForReport(rb));
		}

		return billReportList;
	}
	
	/**
	 * ranks 0-10 how problematic a bill is based on when it was last modified and
	 * how many fields it is missing
	 * 
	 * @param newestMod ranking a list of bills, newestMod is the more recent modification made
	 * @param mod the modification date of the bill being ranked
	 * @param size the amount of fields missing/problematic
	 * @return
	 */
	public double getRank(long newestMod, long mod, int size) {
		double difference = Math.abs((newestMod + 1) - mod) + 0.0;

		double daysDiff = difference / (ONE_DAY_MS + 0.0);
		if (daysDiff < 1.5)
			daysDiff = 1.25;
		if (daysDiff > 5)
			daysDiff = (daysDiff / 365) + 6;

		double heat = 10 - (((daysDiff))
				/ ((Math.pow(size * daysDiff, 1.5))) * 10);
		
		return new BigDecimal(heat).setScale(2, BigDecimal.ROUND_UP).doubleValue();
	}
	
	public enum ReportFieldType {
		MISSING, BROKEN;
	}
	public enum BillType {
		STANDARD_BILL, PROBLEM_BILL;
	}
	public enum ProblemBillAction {
		CHECK, REPORT, DELETE;
	}
}
