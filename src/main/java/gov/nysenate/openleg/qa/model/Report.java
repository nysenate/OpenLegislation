package gov.nysenate.openleg.qa.model;


import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Report {
	long startDate;
	long endDate;
	
	TreeSet<BillReport> reportedBills;
	TreeMap<String, Integer> senatorBills;
	TreeMap<String, Integer> committeeBills;
	
	List<TypeReport> typeReports;
	
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

	public TreeSet<BillReport> getReportedBills() {
		return reportedBills;
	}

	public TreeMap<String, Integer> getSenatorBills() {
		return senatorBills;
	}

	public TreeMap<String, Integer> getCommitteeBills() {
		return committeeBills;
	}

	public List<TypeReport> getTypeReports() {
		return typeReports;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public void setReportedBills(TreeSet<BillReport> reportedBills) {
		this.reportedBills = reportedBills;
	}

	public void setSenatorBills(TreeMap<String, Integer> senatorBills) {
		this.senatorBills = senatorBills;
	}

	public void setCommitteeBills(TreeMap<String, Integer> committeeBills) {
		this.committeeBills = committeeBills;
	}

	public void setTypeReports(List<TypeReport> typeReports) {
		this.typeReports = typeReports;
	}
}
