package gov.nysenate.openleg.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
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
	
	public SenateResponse get(String codeType, String otype, String oid, String sortField, int start, int numberOfResults, boolean reverseSort) {
		
    	SenateResponse senResp = new SenateResponse();
    			    	
		try {
			
			SearchResultSet srs = search(
					((otype != null) ? "otype:" + otype : "") +
					((oid != null) ? (
							(otype!=null) ? " AND oid:" : "")+ oid : ""),
					start, numberOfResults, sortField, reverseSort);
			
			ArrayList<SearchResult> lst = srs.getResults();
			
			for(SearchResult sr:lst) {
				senResp.addResult((codeType.equals("xml") ? sr.xml : (codeType.equals("json") ? sr.json : "")));
			}
			
			senResp.addMetadataByKey("totalresults", srs.getTotalHitCount());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return senResp;
    	
    }
	
	public SearchResultSet search(String searchText, int start, int max, String sortField, boolean reverseSort) throws ParseException, IOException {
		
    	IndexSearcher searcher = openIndex();

        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        Query query = new QueryParser(Version.LUCENE_CURRENT, "osearch", analyzer).parse(searchText);
    	
    	
    	
        SearchResultSet srs = null;
        
        try
    	{
	      // Collect enough docs to show 5 pages
	      TopScoreDocCollector collector = TopScoreDocCollector.create(max, false);
	      searcher.search(query, collector);
	      ScoreDoc[] hits = null;
	      
	      int numTotalHits = collector.getTotalHits();
	      
	      logger.info(numTotalHits + " total matching documents (" + query.toString() + ")");
	      
	      collector = TopScoreDocCollector.create(numTotalHits, false);
	      
	      Sort sort = null;
	      
	      if (sortField != null)
	      {
	    	  sort = new Sort(new SortField(sortField, SortField.STRING, reverseSort));
	    	  Filter filter = null;
	    	  
	    	  hits = searcher.search(query, filter, start + max, sort).scoreDocs;
	      }
	      else
	      {
	    	  searcher.search(query, collector);
	    	  hits = collector.topDocs().scoreDocs;
	      }
	    	
	      srs = new SearchResultSet();
	      
	      srs.totalHitCount = numTotalHits;
	      srs.results = new ArrayList<SearchResult>();
	      	      	           
	      for (int i = start;(i < hits.length && i < start + max); i++)
	      {
	    	  Document doc = searcher.doc(hits[i].doc);
	
	    	  SearchResult sr = new SearchResult();
	    	  sr.xml = doc.get("oxml_new");
	    	  sr.json = doc.get("ojson_new");
	    	  sr.score = hits[i].score;
	    	  
	    	 
	    	  if (doc.get("modified")!=null)
	    		  sr.lastModified = new Date(Long.parseLong(doc.get("modified")));
	    	  
	    	  sr.fields = new HashMap<String,String>();
	    	  
	    	  Iterator<Fieldable> itFields = doc.getFields().iterator();
	    	  Field field = null;
	    	  
	    	  while (itFields.hasNext())
	    	  {
	    		  field = (Field)itFields.next();
	    		  sr.fields.put(field.name(), field.stringValue());
	    	  }
	    	  
	    	  srs.results.add(sr);
	      }
	      
    	}
    	catch (Exception e)
    	{
    		logger.warn("Search Exception: " + query.toString(),e);
    	}
    	
	
	     return srs;   
        
	}
}
