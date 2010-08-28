package gov.nysenate.openleg.search;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;

import gov.nysenate.openleg.lucene.LuceneResult;

public class SearchEngine2 extends SearchEngine {

	public static void main(String[] args) throws Exception {
		SearchEngine.run(new SearchEngine2(), args);
	}
	
	public SearchEngine2() {
		indexDir = "/usr/local/openleg/lucene";
		//indexDir = "C:\\n2-lucene\\";
		
		logger = Logger.getLogger(SearchEngine2.class);
	}
	
	public SenateResponse get(String format, String otype, String oid, String sortField, int start, int numberOfResults, boolean reverseSort) {
		
    	SenateResponse response = null;
    			    	
		try {
			
			String query = null;
			if (otype != null && oid !=null)
				query = "otype:"+otype+" AND oid:"+oid;
			else if (otype !=null && oid == null)
				query = "otype:"+otype;
			else if (otype ==null && oid != null)
				query = "oid:"+oid;
			else
				logger.error("Get Request had neither otype nor oid specified");
			
			if (query != null)
				response = search( query, format, start, numberOfResults, sortField, reverseSort);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return response;
    	
    }
	
	public SenateResponse search(String searchText, String format, int start, int max, String sortField, boolean reverseSort) throws ParseException, IOException {
		
    	String data = "o"+format.toLowerCase()+"_new";
    	
    	LuceneResult result = search(searchText,start,max,sortField,reverseSort);
    	
    	SenateResponse response = new SenateResponse();
    	response.addMetadataByKey("totalresults", result.total );
    	
    	for (Document doc : result.results) {
    		response.addResult(new Result(doc.get("otype"),doc.get(data)));
    	}
    	
    	return response;
	}
}
