package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.HashMap;

public class SenateResponse {

    HashMap<String, Object> metadata;

    ArrayList<Result> results;

    public SenateResponse() {
        this.metadata = new HashMap<String,Object>();
        this.results = new ArrayList<Result>();
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    public ArrayList<Result> getResults() {
        return this.results;
    }

    public void addResult(Result result) {
        this.results.add(result);
    }

    public HashMap<String, Object> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(HashMap<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addMetadataByKey(String key, Object value) {
        this.metadata.put(key, value);
    }

    public Object getMetadataByKey(String key) {
        return this.metadata.get(key);
    }
}
