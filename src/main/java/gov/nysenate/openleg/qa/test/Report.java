package gov.nysenate.openleg.qa.test;

import java.util.ArrayList;

import org.ektorp.support.CouchDbDocument;

@SuppressWarnings("serial")
public class Report extends CouchDbDocument {
	Long reportTime;
	ArrayList<ReportedBill> reportedBills;
	
	public Report() {
		
	}

	public Long getReportTime() {
		return reportTime;
	}

	public ArrayList<ReportedBill> getReportedBills() {
		return reportedBills;
	}

	public void setReportTime(Long reportTime) {
		this.reportTime = reportTime;
	}

	public void setReportedBills(ArrayList<ReportedBill> reportedBills) {
		this.reportedBills = reportedBills;
	}
	
	public void addReportedBill(ReportedBill reportedBill) {
		if(this.reportedBills == null) {
			this.reportedBills = new ArrayList<ReportedBill>();
		}
		this.reportedBills.add(reportedBill);
	}
}
