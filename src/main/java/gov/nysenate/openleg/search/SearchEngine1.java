package gov.nysenate.openleg.search;

import gov.nysenate.openleg.util.BillCleaner;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class SearchEngine1 extends SearchEngine {
	
	public static void main(String[] args) throws Exception {
		SearchEngine.run(new SearchEngine1(), args);
	}
	
	public SearchEngine1() {
		indexDir = "/usr/local/openleg/lucene";
		//indexDir = "C:\\n2-lucene\\";
		
		logger = Logger.getLogger(SearchEngine1.class);
	}
	
	public String get(String codeType, String otype, String oid, String sortField, int start, int numberOfResults, boolean reverseSort) {
    	
		try {
			
			SearchResultSet srs = search(
					((otype != null) ? "otype:" + otype : "") +
					((oid != null) ? (
							(otype!=null) ? " AND oid:" : "")+ oid : ""),
					start, numberOfResults, sortField, reverseSort);
			
			ArrayList<SearchResult> lst = srs.getResults();
			
			if(!lst.isEmpty()) {
				return (codeType.equals("xml") ? lst.iterator().next().xml: lst.iterator().next().json);
			}
						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
    }

    public SearchResultSet search(String searchText, int start, int max, String sortField, boolean sortOrder) throws IOException, ParseException
	{
    	if(!searchText.contains("oid") || !searchText.contains("otype")) {
    		if(searchText.matches(BillCleaner.BILL_SEARCH_REGEXP)) {
    			
    			searchText = BillCleaner.billFormat(searchText);
    			searchText = "oid:" + searchText + "~";
    		}
    	}
    	
    	IndexSearcher searcher = openIndex();

        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        Query query = new QueryParser(Version.LUCENE_CURRENT, "osearch", analyzer).parse(searchText);
        
        return doPagingSearch (searcher, query, start, max, sortField, sortOrder);
    }
}
