package gov.nysenate.openleg.ingest.parser;

import gov.nysenate.openleg.ingest.JsonDao;
import gov.nysenate.openleg.model.ISenateObject;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.search.SearchEngine;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public abstract class SenateParser<T extends ISenateObject> {
    public static final String JSON = "json";
    public static final String LUCENE = "lucene";

    protected final Logger logger;

    protected ArrayList<SenateObject> newSenateObjects;
    protected ArrayList<SenateObject> deletedSenateObjects;

    protected JsonDao jsonDao;
    protected SearchEngine searchEngine;

    public SenateParser(Class<? extends SenateParser<T>> clazz) {
        this(clazz, null, null);
    }

    public SenateParser(Class<? extends SenateParser<T>> clazz, JsonDao jsonDao, SearchEngine searchEngine) {
        logger = Logger.getLogger(clazz);

        this.searchEngine = searchEngine;
        this.jsonDao = jsonDao;

        newSenateObjects = new ArrayList<SenateObject>();
        deletedSenateObjects = new ArrayList<SenateObject>();
    }

    public void addNewSenateObject(SenateObject senateObject) {
        newSenateObjects.add(senateObject);
    }

    public void addDeletedSenateObject(SenateObject senateObject) {
        deletedSenateObjects.add(senateObject);
    }

    public ArrayList<SenateObject> getNewSenateObjects() {
        return newSenateObjects;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<T> getNewSenateObjects(Class<T> clazz) {
        return (ArrayList<T>) newSenateObjects;
    }

    public ArrayList<SenateObject> getDeletedSenateObjects() {
        return deletedSenateObjects;
    }

    public void clear() {
        newSenateObjects.clear();
        deletedSenateObjects.clear();
    }

    public boolean canWrite(String type) {
        if(type != null) {
            if(type.equals(JSON)) {
                if(jsonDao != null) {
                    return true;
                }
            }
            else if (type.equals(LUCENE)) {
                if(searchEngine != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract void parse(File file);
}
