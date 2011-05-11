package gov.nysenate.openleg.qa.test;

import gov.nysenate.openleg.qa.test.ReportedBillManager.BillType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;

@View(name="all", map = "function(doc) { if (doc.oid && doc.modified && doc.billType) { emit(doc.oid, doc) } }")
public class ReportedBillRepository extends
		CouchDbRepositorySupport<ReportedBill> {
	
	public static final Class<ReportedBill> clazz = ReportedBill.class;
	
	protected ReportedBillRepository(CouchDbConnector db) {
		super(clazz, db);
		initStandardDesignDocument();
	}

	@GenerateView
	public List<ReportedBill> findByOid(String oid) {
		return queryView("by_oid", oid);
	}
	
	@View(name="by_push_to_report", map = "function(doc) { if (doc.oid && doc.modified && doc.billType) { emit(doc.pushToReport, doc) } }")
	public List<ReportedBill> findByPushToReport(boolean pushToReport) {
		return db.queryView(createQuery("by_push_to_report").includeDocs(true).key(pushToReport), clazz);
	}
	
	@View(name="by_modified", map = "function(doc) { if (doc.oid && doc.modified && doc.billType) { emit(doc.modified, doc) } }")
	public List<ReportedBill> findByModified(boolean descending, int limit) {
		return db.queryView(createQuery("by_modified").includeDocs(true).descending(descending).limit(limit), clazz);
	}

	@View(name="by_bill_type", map = "function(doc) { if (doc.oid && doc.modified && doc.billType) { emit(doc.billType, doc) } }")
	public List<ReportedBill> findByBillType(BillType billType) {
		return db.queryView(createQuery("by_bill_type").includeDocs(true).key(billType.toString()), clazz);
	}
	
	@View(name="standard_list_for_report", map="function(doc) {" +
				" if (doc.oid && doc.modified && doc.billType && doc.billType == 'STANDARD_BILL' && !doc.processDate && doc.activeForReport) " +
				"{ emit(doc.modified, doc) } }")
	public List<ReportedBill> findStandardBillsForReport(int limit) {
		return db.queryView(
				createQuery("standard_list_for_report")
					.includeDocs(true)
					.descending(false)
					.limit(limit), clazz);
	}
	
	/*
	 * get list of ReportedBills are known to be problematic and need to be processed
	 */
	@View(name="problem_list_for_report", map = "function(doc) {" +
				" if (doc.oid && doc.modified && doc.billType " +
						"&& doc.billType == 'PROBLEM_BILL' && !doc.processDate && doc.activeForReport " +
						"&& (!doc.hideFromReport || doc.hideFromReport == 'true')) " +
					"{ emit(doc.rank, doc) } }")
	public List<ReportedBill> findProblemBillsForReport(int limit) {
		return db.queryView(
				createQuery("problem_list_for_report")
					.includeDocs(true)
					.descending(true)
					.limit(limit), clazz);
	}
	
	public TreeSet<ReportedBill> getReportedBillsForReport(int limit) {
		TreeSet<ReportedBill> reportedBills = new TreeSet<ReportedBill>(new ReportedBill.ByHeat());
		reportedBills.addAll(findByPushToReport(true));
		
		int size = reportedBills.size();
		if(size < limit) {
			reportedBills.addAll(findProblemBillsForReport(limit-size));
		}
		
		size = reportedBills.size();
		if(size < limit) {
			reportedBills.addAll(findStandardBillsForReport(limit - size));
		}
		
		List<ReportedBill> returnList = new ArrayList<ReportedBill>();
		returnList.addAll(reportedBills);
		
		return reportedBills;
	}
	
	public void persistMixedCollection(Collection<ReportedBill> reportedBills) {
		for(ReportedBill b:reportedBills) {
			List<ReportedBill> temp = findByOid(b.getOid());
			if(temp == null || temp.isEmpty()) {
				db.create(b);
			}
			else {
				b.setRevision(temp.get(0).getRevision());
				db.update(b);
			}			
		}
	}
}
