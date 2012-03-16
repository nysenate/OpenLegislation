package gov.nysenate.openleg.qa.model;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.NameConventions;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.DesignDocument;

public class CouchInstance {
    private static CouchInstance couchInstance = null;

    public static CouchInstance getInstance(String databaseName, boolean createIfNotExist, HttpClient httpClient) {
        if(couchInstance == null) {
            couchInstance = new CouchInstance(databaseName, createIfNotExist, httpClient);
        }
        return couchInstance;
    }

    private CouchDbInstance couchDbInstance = null;
    private CouchDbConnector couchDbConnector = null;

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
