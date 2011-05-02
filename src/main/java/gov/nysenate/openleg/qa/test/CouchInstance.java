package gov.nysenate.openleg.qa.test;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.NameConventions;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.DesignDocument;

public class CouchInstance {
	private static CouchInstance couchInstance = null;
	
	public static CouchInstance getInstance() {
		if(couchInstance == null) {
			couchInstance = new CouchInstance();
		}
		return couchInstance;
	}
	
	public static CouchInstance getInstance(String databaseName) {
		if(couchInstance == null) {
			couchInstance = new CouchInstance(databaseName);
		}
		return couchInstance;
	}
	
	public static CouchInstance getInstance(String databaseName, boolean createIfNotExist) {
		if(couchInstance == null) {
			couchInstance = new CouchInstance(databaseName, createIfNotExist);
		}
		return couchInstance;
	}
	
	public static CouchInstance getInstance(String databaseName, boolean createIfNotExist, HttpClient httpClient) {
		if(couchInstance == null) {
			couchInstance = new CouchInstance(databaseName, createIfNotExist, httpClient);
		}
		return couchInstance;
	}
	
	private static final boolean CREATE_IF_NOT_EXIST = true;
	private static final String DATABASE_NAME = "test";
	
	private CouchDbInstance couchDbInstance = null;
	private CouchDbConnector couchDbConnector = null;
	
	private CouchInstance() {
		this(DATABASE_NAME);
	}
	
	private CouchInstance(String databaseName) {
		this(databaseName, CREATE_IF_NOT_EXIST);
	}
	
	private CouchInstance(String databaseName, boolean createIfNotExist) {
		this(databaseName, createIfNotExist, new StdHttpClient.Builder().build());
	}
	
	private CouchInstance(String databaseName, boolean createIfNotExist, HttpClient httpClient) {
		couchDbInstance = new StdCouchDbInstance(httpClient);
		couchDbConnector = couchDbInstance.createConnector(databaseName, createIfNotExist);
	}

	public CouchDbInstance getDbInstance() {
		return couchDbInstance;
	}

	public CouchDbConnector getConnector() {
		return couchDbConnector;
	}
	
	public static DesignDocument getDesignDocumentForClass(Class<?> clazz, CouchInstance instance) {
		String stdDesignDocumentId = NameConventions.designDocName(clazz);
		DesignDocument dd = instance.getConnector().get(DesignDocument.class, stdDesignDocumentId);
		return dd;
	}
}
