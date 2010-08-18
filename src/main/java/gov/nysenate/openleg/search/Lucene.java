package gov.nysenate.openleg.search;

import java.io.File;
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
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Lucene {

	protected IndexSearcher indexSearcher = null;
	
	protected String indexDir;
	
	protected Logger logger;
	
	public Directory getDirectory() throws IOException {
		return FSDirectory.open(new File(indexDir));
	}

    public boolean ifIndexExist(){
    	return (0 < new File(indexDir).listFiles().length);
    }
    
    public boolean createIndex() throws IOException{
        if (ifIndexExist() == false) {
	        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
	        IndexWriter indexWriter = new IndexWriter(getDirectory(), analyzer, true, MaxFieldLength.LIMITED);
	        
	        indexWriter.optimize();
	        indexWriter.close();
        }
        return true;
    }
    
    public boolean optimizeIndex() throws IOException{

        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(getDirectory(), analyzer, false, MaxFieldLength.LIMITED);
        
        indexWriter.optimize();
        indexWriter.close();
        return true;

    }
    
    public synchronized IndexSearcher openIndex() throws IOException {

    	if (indexSearcher == null)
    	{
    		boolean readOnly = true;
    		
    		Directory fsDirectory = FSDirectory.open(new File(indexDir));
    		indexSearcher = new IndexSearcher(fsDirectory, readOnly);
    	}
    	return indexSearcher;
    } 
    
    public synchronized void closeIndex() throws IOException {
    	if (indexSearcher != null) {
			indexSearcher.close();
			indexSearcher = null;
    	}
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

    public void deleteDocument(String otype, String oid) throws IOException
    {
    	Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(getDirectory(), analyzer, false, MaxFieldLength.UNLIMITED);
    	
		logger.info("deleting document: " + otype + "=" + oid);

        try {
            Query query = new QueryParser(Version.LUCENE_CURRENT, "oid", indexWriter.getAnalyzer()).parse("otype:" + otype + " AND oid:" + oid);
            indexWriter.deleteDocuments(query);
	    }
        catch (Exception e) {
			logger.warn("error deleting document to index: " + otype + "=" + oid, e);
        }
        
        indexWriter.close();
    }
    
    public void deleteAllDocumentByType(String otype) throws IOException
    {
    	Directory fsDirectory = FSDirectory.open(new File(indexDir));
        
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, analyzer, false, MaxFieldLength.UNLIMITED);
       
		logger.info("deleting all document: " + otype);

        try {
            Query query = new QueryParser(Version.LUCENE_CURRENT, "otype", indexWriter.getAnalyzer()).parse("otype:" + otype);
            indexWriter.deleteDocuments(query);
		} 
		catch (Exception e) {
			logger.warn("error deleting document to index: " + otype, e);
		}
    	indexWriter.close();
    }

}
