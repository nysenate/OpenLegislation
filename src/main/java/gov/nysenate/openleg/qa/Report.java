package gov.nysenate.openleg.qa;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Report {
	long startDate;
	long endDate;
	
	TreeSet<ReportBill> reportedBills;
	TreeMap<String, Integer> senatorBills;
	TreeMap<String, Integer> committeeBills;
	
	List<ReportType> typeReports;
	
	public Report(long startDate, long endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public long getStartDate() {
		return startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public TreeSet<ReportBill> getReportedBills() {
		return reportedBills;
	}

	public TreeMap<String, Integer> getSenatorBills() {
		return senatorBills;
	}

	public TreeMap<String, Integer> getCommitteeBills() {
		return committeeBills;
	}

	public List<ReportType> getTypeReports() {
		return typeReports;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public void setReportedBills(TreeSet<ReportBill> reportedBills) {
		this.reportedBills = reportedBills;
	}

	public void setSenatorBills(TreeMap<String, Integer> senatorBills) {
		this.senatorBills = senatorBills;
	}

	public void setCommitteeBills(TreeMap<String, Integer> committeeBills) {
		this.committeeBills = committeeBills;
	}

	public void setTypeReports(List<ReportType> typeReports) {
		this.typeReports = typeReports;
	}
}
