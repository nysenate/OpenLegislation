package gov.nysenate.openleg.search;

import java.util.ArrayList;
import java.util.HashMap;

import com.thoughtworks.xstream.annotations.*;

@XStreamAlias("response")
public class SenateResponse {
	
	HashMap<String, Object> metadata;
	
	ArrayList<String> results;
	
	public SenateResponse() {
		this.metadata = new HashMap<String,Object>();
		this.results = new ArrayList<String>();
	}
	
	public void setResults(ArrayList<String> results) {
		this.results = results;
	}
	
	public ArrayList<String> getResults() {
		return this.results;
	}
	
	public void addResult(String result) {
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
