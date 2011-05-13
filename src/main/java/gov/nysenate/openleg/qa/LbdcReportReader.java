package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.qa.model.CouchInstance;
import gov.nysenate.openleg.qa.model.CouchSupport;
import gov.nysenate.openleg.qa.model.FieldName;
import gov.nysenate.openleg.qa.model.LbdcFile;
import gov.nysenate.openleg.qa.model.ProblemBill;
import gov.nysenate.openleg.qa.model.LbdcFile.AssociatedFields;
import gov.nysenate.openleg.util.SessionYear;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.ektorp.http.StdHttpClient;

public class LbdcReportReader extends CouchSupport {
	public static void main(String[] args) {
		System.setProperty("org.ektorp.support.AutoUpdateViewOnChange", "true");
		
		CouchInstance instance = CouchInstance.getInstance("test", true, new StdHttpClient.Builder().build());
		instance.getDbInstance().deleteDatabase("test");
		
		LbdcReportReader reader = new LbdcReportReader();
		reader.reportMissingData();
		reader.processFile("src/main/resources/senate3.html", ReportType.BILL_HTML);
		reader.processFile("src/main/resources/memos.txt", ReportType.MEMO);
		reader.processFile("src/main/resources/paging.txt", ReportType.PAGING);
	}
	
	public enum ReportType { 
		BILL_HTML, MEMO, PAGING
	}
	
	public LbdcReportReader() {
		
	}
	
	public void processFile(String fileName, ReportType reportType) {
		processFile(new File(fileName), reportType);
	}
	
	public void processFile(File file, ReportType reportType) {
		LbdcFile lbdcFile = null;
		switch(reportType) {
			case BILL_HTML:
				lbdcFile = new LbdcFileHtml(file);
				break;
			case MEMO:
				lbdcFile = new LbdcFileMemo(file);
				break;
			case PAGING:
				lbdcFile = new LbdcFilePaging(file);
		}
		
		AssociatedFields associatedFields = lbdcFile.getClass().getAnnotation(AssociatedFields.class);
		if(associatedFields != null) {
			removeProblemBillFields(associatedFields.value());
		}
		
		ArrayList<ProblemBill> problemBills = lbdcFile.getProblemBills();
		
		pbr.createOrUpdateProblemBills(problemBills, true);
		pbr.deleteNonProblemBills();
		pbr.rankProblemBills();
	}
	
	/*
	 * when running a new report we should delete all references
	 * to previous reports w/ the same fields
	 * TODO: this won't be the case if we receive fragmented files from LBDC
	 * 		 a temporary workaround would be to merge those files
	 */
	public void removeProblemBillFields(FieldName[] fieldNames) {
		List<ProblemBill> problemBills = pbr.getAll();
		
		for(ProblemBill problemBill:problemBills) {
			
			if(problemBill.getNonMatchingFields() != null) {
				for(FieldName fieldName:fieldNames) {
					problemBill.getNonMatchingFields().remove(fieldName.text());
				}
				
				if(problemBill.getNonMatchingFields().isEmpty()) {
					problemBill.setNonMatchingFields(null);
				}
			}
		}
		
		pbr.createOrUpdateProblemBills(problemBills, false);
	}
	
	public void reportMissingData() {
		try {
			refreshMissingData();
			pbr.deleteNonProblemBills();
			pbr.rankProblemBills();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void refreshMissingData() throws ParseException, IOException {
		List<ProblemBill> problemBillList = pbr.findByMissingFields();
		
		ReportBuilder reportBuilder = new ReportBuilder();
		HashMap<String, ProblemBill> reportedBillMap = 
				reportBuilder.getBillReportSet(SessionYear.getSessionYear() + "");
				
		for(ProblemBill problemBill:problemBillList)
		{
			//if bill was in missing report but no longer clear missingFields
			if(reportedBillMap.get(problemBill.getOid()) == null)
			{
				problemBill.setMissingFields(null);
				instance.getConnector().addToBulkBuffer(problemBill);
			}
		}
		
		pbr.createOrUpdateProblemBills(reportedBillMap.values(), true);
		
		instance.getConnector().flushBulkBuffer();
		instance.getConnector().clearBulkBuffer();		
	}
}
