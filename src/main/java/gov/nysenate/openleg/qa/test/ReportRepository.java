package gov.nysenate.openleg.qa.test;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;

@View(name="all", map = "function(doc) { if (doc.reportTime && doc.reportedBills) { emit(doc.reportTime, doc) } }")
public class ReportRepository extends CouchDbRepositorySupport<Report> {
	public static final Class<Report> clazz = Report.class;
	
	protected ReportRepository(CouchDbConnector db) {
		super(clazz, db);
	}

}
