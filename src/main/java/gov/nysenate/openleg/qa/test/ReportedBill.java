package gov.nysenate.openleg.qa.test;

import gov.nysenate.openleg.qa.test.ReportedBillManager.BillType;
import gov.nysenate.openleg.qa.test.ReportedBillManager.ReportFieldType;
import gov.nysenate.openleg.qa.test.ReportedBillManager.ProblemBillAction;

import java.util.Comparator;
import java.util.HashMap;

import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.TypeDiscriminator;

@SuppressWarnings("serial")
public class ReportedBill extends CouchDbDocument {
	
	/* for all bills */
	@TypeDiscriminator
	String oid;
	Long modified;
	BillType billType;
	
	Boolean activeForReport;
	
	/* for bills that have been processed */
	Long processDate;
	
	/* for problematic bills */	
	Boolean pushToReport;
	Boolean hideFromReport;
	
	Double rank;
	HashMap<String, ReportFieldType> problemFields;
	
	ProblemBillAction problemBillAction;
	
	public ReportedBill() {
		
	}
	
	public ReportedBill(String oid, Long modified, BillType billType) {
		this.setId(oid);
		this.oid = oid;
		this.modified = modified;
		this.billType = billType;
	}
	
	public ReportedBill(String oid, Long modified, BillType billType, String fieldName, ReportFieldType reportFieldType) {
		this.setId(oid);
		this.oid = oid;
		this.modified = modified;
		this.billType = billType;
		this.addProblematicField(fieldName, reportFieldType);
	}
	
	public String getOid() {
		return oid;
	}

	public Long getModified() {
		return modified;
	}

	public BillType getBillType() {
		return billType;
	}

	public Boolean getActiveForReport() {
		return activeForReport;
	}

	public Long getProcessDate() {
		return processDate;
	}

	public Boolean getPushToReport() {
		return pushToReport;
	}

	public Boolean getHideFromReport() {
		return hideFromReport;
	}

	public Double getRank() {
		return rank;
	}

	public HashMap<String, ReportFieldType> getProblemFields() {
		return problemFields;
	}

	public ProblemBillAction getProblemBillAction() {
		return problemBillAction;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}

	public void setBillType(BillType billType) {
		this.billType = billType;
	}

	public void setActiveForReport(Boolean activeForReport) {
		this.activeForReport = activeForReport;
	}

	public void setProcessDate(Long processDate) {
		this.processDate = processDate;
	}

	public void setPushToReport(Boolean pushToReport) {
		this.pushToReport = pushToReport;
	}

	public void setHideFromReport(Boolean hideFromReport) {
		this.hideFromReport = hideFromReport;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	public void setProblemFields(HashMap<String, ReportFieldType> problemFields) {
		this.problemFields = problemFields;
	}

	public void setProblemBillAction(ProblemBillAction problemBillAction) {
		this.problemBillAction = problemBillAction;
	}

	public void addProblematicField(String fieldName, ReportFieldType reportFieldType) {
		if(this.problemFields == null) {
			this.problemFields = new HashMap<String, ReportFieldType>();
		}
		this.problemFields.put(fieldName, reportFieldType);
	}
	
	public void removeProblematicField(String fieldName) {
		if(this.problemFields == null)
			return;
		this.problemFields.remove(fieldName);
	}
	
	public void removeReportedData() {
		this.pushToReport = null;
		this.hideFromReport = null;
		this.rank = null;
		this.problemBillAction = null;
		this.problemFields = null;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ReportedBill))
			return false;
		
		if(((ReportedBill)obj).getOid().equals(this.getOid()))
			return true;
		
		return false;
	}
	
	public static class ByHeat implements Comparator<ReportedBill> {
		public int compare(ReportedBill one, ReportedBill two) {
			double oneRank = one.getRank() == null ? 0.0 : one.getRank();
			double twoRank = two.getRank() == null ? 0.0 : two.getRank();;
			int ret = ((Double)twoRank).compareTo((Double)oneRank);
			if(ret == 0) {
				ret = ((Long)one.getModified()).compareTo((Long)two.getModified());
				if(ret == 0) {
					ret = one.getOid().compareTo(two.getOid());
				}
			}
			return ret;
		}
	}
}
