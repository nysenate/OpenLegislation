package gov.nysenate.openleg.qa.test;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.qa.LBDConnect;
import gov.nysenate.openleg.qa.test.ReportedBillManager.BillType;
import gov.nysenate.openleg.qa.test.ReportedBillManager.ProblemBillAction;
import gov.nysenate.openleg.qa.test.ReportedBillManager.ReportFieldType;
import gov.nysenate.openleg.search.SearchEngine;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportManager extends CouchSupport {
	public final int LIMIT;
	public final long TIME = System.currentTimeMillis();
	
	public final Pattern pagePattern = Pattern.compile("^\\s+(?:" +
			"\\w\\.\\s\\d+(?:--\\w)?\\s+(\\d+)(?:\\s+\\w\\.\\s\\d+(?:--\\w)?)?|" +
			"(\\d+)(\\s+(?:\\d+(?:\\-)?){3})?)$", Pattern.MULTILINE);
	
	private LBDConnect lbdConnect = null;
	
	public ReportManager() {
		this(25);
	}
	
	public ReportManager(int limit) {
		super();
		LIMIT = limit;
	}
	
	public LBDConnect getLbdConnect() {
		if(lbdConnect == null)
			lbdConnect = LBDConnect.getInstance();
		
		return lbdConnect;
	}
	
	public void executeReport() {
		TreeSet<ReportedBill> set = getReportedBillsForReport();
		ArrayList<ReportedBill> ret = new ArrayList<ReportedBill>();
		
		for(ReportedBill rb:set) {
			
			Bill indexBill = SearchEngine.getInstance().getBill(rb.getOid());
/*			
 * 			Leave commented until aproval
			
			Bill lbdcBill = getLbdConnect().getBillFromLbdc(rb.getOid());
			
			if(lbdcBill == null) {
				rb.setProblemBillAction(ProblemBillAction.DELETE);
			}
			else {
				if(rb.getBillType().equals(BillType.PROBLEM_BILL)) {
					checkField("full", indexBill.getFulltext(), lbdcBill.getFulltext(), rb);
					checkField("memo", indexBill.getMemo(), lbdcBill.getMemo(), rb);
					checkField("sponsor", indexBill.getSponsor(), lbdcBill.getSponsor(), rb);
					checkField("summary", indexBill.getSummary(), lbdcBill.getSummary(), rb);
					checkField("title", indexBill.getTitle(), lbdcBill.getTitle(), rb);
					checkField("actions", indexBill.getBillEvents(), lbdcBill.getBillEvents(), rb);
				}
				
				int lbdcPageNumber = getPageNumber(lbdcBill.getFulltext());
				int indexPageNumber = getPageNumber(indexBill.getFulltext());
				
				if(lbdcPageNumber != indexPageNumber) {
					rb.addProblematicField("text", ReportFieldType.BROKEN);
				}
				if(lbdcBill.getBillEvents().size() != indexBill.getBillEvents().size()) {
					rb.addProblematicField("actions", ReportFieldType.BROKEN);
				}
*/
				//if fields are missing/broken set to problem bill
				if(rb.getProblemFields() != null && !rb.getProblemFields().isEmpty()) {
					rb.setBillType(BillType.PROBLEM_BILL);
					rb.setProblemBillAction(ProblemBillAction.CHECK);
				}
				else {
					rb.setBillType(BillType.STANDARD_BILL);
					rb.setProblemBillAction(null);
					rb.removeReportedData();
				}
/*
 * 			Leave commented until aproval
			}
*/
			
			rb.setProcessDate(TIME);
			rb.setPushToReport(null);
			rb.setActiveForReport(null);
			rb.setHideFromReport(null);
			
			ret.add(rb);
		}
		
		Report report = new Report(TIME, new ArrayList<ReportedBill>(set));
		rbr.persistMixedCollection(set);
		rr.add(report);
	}
	
	private void checkField(String string, Object one, Object two, ReportedBill rb) {
		if(one == null && two != null) {
			rb.addProblematicField(string, ReportFieldType.MISSING);
		}
		else {
			rb.removeProblematicField(string);
		}
	}

	private int getPageNumber(String text) {
		if(text == null)
			return -1;
		
		Matcher m = pagePattern.matcher(text);
		
		MatchResult result = null;
		while(m.find()) result = m.toMatchResult();
		
		if(result != null) {
			//with non capturing groups when a match is found it will either be in group 1 or 2
			return new Integer(result.group(1) == null ? result.group(2) : result.group(1));
		}
		
		return -1;
	}
	
	public TreeSet<ReportedBill> getReportedBillsForReport() {
		return rbr.getReportedBillsForReport(LIMIT);
	}
}