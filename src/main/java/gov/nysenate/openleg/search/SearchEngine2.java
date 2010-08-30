package gov.nysenate.openleg.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.ParseException;

import gov.nysenate.openleg.lucene.LuceneResult;
import gov.nysenate.openleg.util.ApiConverter2;

public class SearchEngine2 extends SearchEngine {

	public static void main(String[] args) throws Exception {
		SearchEngine2 engine = new SearchEngine2();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		String line = null;
		System.out.print("openleg search > ");
		while (!(line = reader.readLine()).equals("quit"))
		{
			if (line.startsWith("index "))
				engine.indexSenateData(line.substring(line.indexOf(" ")+1), new ApiConverter2());
			else if (line.startsWith("optimize"))
				engine.optimizeIndex();
			else if (line.startsWith("delete"))
			{
				StringTokenizer cmd = new StringTokenizer(line.substring(line.indexOf(" ")+1)," ");
				String type = cmd.nextToken();
				String id = cmd.nextToken();
				engine.deleteSenateObjectById(type, id);
			}
			else if (line.startsWith("create"))
				engine.createIndex();
			else
				engine.search(line, "xml", 1, 10, null, false);
			
			System.out.print("openleg search > ");
		}
		System.out.println("Exiting Search Engine");
	}

	public SearchEngine2() {
//		indexDir = "/usr/local/openleg/lucene";
		indexDir = "C:\\n-lucene\\";
//		indexDir = "/Users/jaredwilliams/Documents/workspace/openleg/lucene2";
		
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
		
    	String data = "o"+format.toLowerCase()+"";
    	
    	LuceneResult result = search(searchText,start,max,sortField,reverseSort);
    	
    	SenateResponse response = new SenateResponse();
    	response.addMetadataByKey("totalresults", result.total );
    	
    	for (Document doc : result.results) {
    		response.addResult(new Result(doc.get("otype"),doc.get(data), doc.get("oid")));
    	}
    	
    	return response;
	}
}
