package gov.nysenate.openleg.qa.test;

import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;

public class CouchSupport {
	private static final String DATABASE_NAME = "test";
	private static final boolean CREATE_IF_NOT_EXIST = true;
	
	protected CouchInstance instance = null;
	protected ReportedBillRepository rbr = null;
	protected ReportRepository rr = null;
	
	public CouchSupport() {
		this(DATABASE_NAME, CREATE_IF_NOT_EXIST);
	}
	
	public CouchSupport(String databaseName) {
		this(databaseName, CREATE_IF_NOT_EXIST);
	}
	
	public CouchSupport(String databaseName, boolean createIfNotExist) {
		this(databaseName, createIfNotExist, new StdHttpClient.Builder().build());
	}
	
	public CouchSupport(String databaseName, boolean createIfNotExist, HttpClient httpClient) {
		instance = CouchInstance.getInstance(databaseName, createIfNotExist, httpClient);
		rbr = new ReportedBillRepository(instance.getConnector());
		rr = new ReportRepository(instance.getConnector());
	}
}
