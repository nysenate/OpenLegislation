package gov.nysenate.openleg.search;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;

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
			
			response = search(
					((otype != null) ? "otype:" + otype : "") +
					((oid != null) ? (
							(otype!=null) ? " AND oid:" : "")+ oid : ""),
					format,start, numberOfResults, sortField, reverseSort);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return response;
    	
    }
	
	public SenateResponse search(String searchText, String format, int start, int max, String sortField, boolean reverseSort) throws ParseException, IOException {
		
		SenateResponse response = new SenateResponse();

    	ScoreDoc[] hits = null;
    	IndexSearcher searcher = openIndex();
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        Query query = new QueryParser(Version.LUCENE_CURRENT, "osearch", analyzer).parse(searchText);
    	TopScoreDocCollector collector = TopScoreDocCollector.create(start+max, false);
    	
		try
    	{
        	//Do this search no matter what so we can get the "total hits" for the response object
			//The sorted result search can't give us this information (I don't think)
        	searcher.search(query, collector);
        	response.addMetadataByKey("totalresults", collector.getTotalHits());
        	logger.info(response.getMetadataByKey("totalresults") + " total matching documents (" + query.toString() + ")");
	      
        	if (sortField != null) {
    			//If they want sorted results, do a new search with sorting enabled
        		Sort sort = new Sort(new SortField(sortField, SortField.STRING, reverseSort));
        		hits = searcher.search(query, null, start + max, sort).scoreDocs;
        	}
        	else {
        		hits = collector.topDocs().scoreDocs;
        	}
        	
        	//Build the response by adding results of the correct format
        	String data = "o"+format.toLowerCase()+"_new"; 
        	for (int i = start; (i < hits.length && i < start + max); i++) {
        		Document doc = searcher.doc(hits[i].doc);
        		response.addResult(new Result(doc.get("otype"),doc.get(data)));
        	}
    	}
    	catch (Exception e) {
    		logger.warn("Search Exception: " + query.toString(),e);
    	}
    	
    	return response;
	}
}
