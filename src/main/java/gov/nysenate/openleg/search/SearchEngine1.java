package gov.nysenate.openleg.search;

import gov.nysenate.openleg.util.BillCleaner;

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
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopScoreDocCollector;
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
    
    public SearchResultSet doPagingSearch(Searcher searcher, Query query, int start, int numberOfResults, String sortField, boolean reverseSort ) throws IOException
    {
    	 SearchResultSet srs = null;
    	 
    	try {
	      // Collect enough docs to show 5 pages
	      TopScoreDocCollector collector = TopScoreDocCollector.create(numberOfResults, false);
	      searcher.search(query, collector);
	      ScoreDoc[] hits = collector.topDocs().scoreDocs;
	      
	      int numTotalHits = collector.getTotalHits();
	      
	      logger.info(numTotalHits + " total matching documents (" + query.toString() + ")");
	      
	      collector = TopScoreDocCollector.create(numTotalHits, false);
	      
	      Sort sort = null;
	      
	      if (sortField != null) {
	    	  try {
	    		  sort = new Sort(new SortField(sortField, SortField.STRING, reverseSort));
		    	  Filter filter = null;
		    	  
		    	  hits = searcher.search(query, filter, start + numberOfResults, sort).scoreDocs;
	    	  }
	    	  catch (Exception e) {
	    		  e.printStackTrace();
	    	  }
	      }
	      else {
	    	  searcher.search(query, collector);
	    	  hits = collector.topDocs().scoreDocs;
	      }
	    
	
	      srs = new SearchResultSet();
	      
	      srs.totalHitCount = numTotalHits;
	      srs.results = new ArrayList<SearchResult>();
	      	      	           
	      for (int i = start;(i < hits.length && i < start + numberOfResults); i++) {
	    	  
	    	  Document doc = searcher.doc(hits[i].doc);
	
	    	  SearchResult sr = new SearchResult();
	    	  sr.type = doc.get("otype");
	    	  sr.id = doc.get("oid");
	    	  sr.title = doc.get("title");
	    	  sr.title_sortby = doc.get("title_sortby");
	    	  sr.summary = doc.get("summary");
	    	  
	    	  sr.xml = doc.get("oxml");
	    	  sr.json = doc.get("ojson");
	    	  
	    	  sr.score = hits[i].score;
	    	  sr.fields = new HashMap<String,String>(); 
	    	 
	    	  if (doc.get("modified")!=null)
	    		  sr.lastModified = new Date(Long.parseLong(doc.get("modified")));
	    	  
	    	  Iterator<Fieldable> itFields = doc.getFields().iterator();
	    	  while (itFields.hasNext()) {
	    		  Field field = (Field)itFields.next();
	    		  sr.fields.put(field.name(), field.stringValue());
	    	  }
	    	  
	    	  srs.results.add(sr);
	      }
	      
    	}
    	catch (Exception e) {
    		logger.warn("Search Exception: " + query.toString(),e);
    	}
    	
	    return srs;
    }
}
