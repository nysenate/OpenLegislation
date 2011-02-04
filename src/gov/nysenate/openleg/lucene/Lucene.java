package gov.nysenate.openleg.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Lucene implements LuceneIndexer,LuceneSearcher{

	protected IndexWriter indexWriter = null;
	protected IndexSearcher indexSearcher = null;
	
	protected Logger logger;
	protected String indexDir;
	
	public Lucene(String indexDir) {
		this.indexDir = indexDir;
		this.logger = Logger.getLogger(Lucene.class);
	}
	
	/////////////////////////////////
	// Implementing LuceneIndexer
	//
	
    public void createIndex() throws IOException{
        if (0 == new File(indexDir).listFiles().length) {
	        IndexWriter indexWriter = new IndexWriter(getDirectory(), getAnalyzer(), true, MaxFieldLength.LIMITED);
	        indexWriter.optimize();
	        indexWriter.close();
        }
    }
    
	public IndexWriter openWriter() throws IOException {
		if (indexWriter == null) {
			indexWriter = new IndexWriter(getDirectory(), getAnalyzer(), false, MaxFieldLength.UNLIMITED);
		}
		return indexWriter;
	}
    
    public boolean addDocument(LuceneObject obj, LuceneSerializer[] serializer,IndexWriter indexWriter) throws InstantiationException,IllegalAccessException,IOException
    {
    	Document doc = new DocumentBuilder().buildDocument(obj, serializer);
    	
    	if(doc ==  null) {
    		return false;
    	}
    	
    	logger.info("indexing document: " + doc.getField("otype").stringValue() + "=" + doc.getField("oid").stringValue());
    	/*Term term = new Term("oid",doc.getField("oid").stringValue());*/
    	
    	Query query;
		try {
			query = new QueryParser(Version.LUCENE_CURRENT, "oid", indexWriter.getAnalyzer()).parse("oid:" + doc.getField("oid").stringValue());
	        indexWriter.deleteDocuments(query);    	
	    	indexWriter.addDocument(doc);


		} catch (ParseException e) {
			logger.warn("error adding document to index: " + doc.getField("otype").stringValue() + "=" + doc.getField("oid").stringValue(), e);
		}

    	
    	/*indexWriter.updateDocument(term, doc);*/
    	
    	
    	return true;
    }
    
    public void deleteDocuments(String otype, String oid) throws IOException {
    	openWriter();
        try {
        	String qString ="otype:"+otype + ((oid!=null) ? " AND oid:"+oid : "");
            Query query = new QueryParser(Version.LUCENE_CURRENT, "otype", indexWriter.getAnalyzer()).parse(qString);
            indexWriter.deleteDocuments(query);
	    }
        catch (Exception e) {
			logger.warn("error deleting document to index: " + otype + "=" + oid, e);
        }
        closeWriter();
    }
    
    public void optimize() throws IOException {
    	openWriter().optimize();
    }
    
    public synchronized void closeWriter() throws IOException {
    	if (indexWriter != null) {
    		indexWriter.close();
			indexWriter = null;
    	}
    }
    
    /////////////////////////////////
    // Implementing Lucene Searcher
    //
    
	public synchronized IndexSearcher openSearcher() throws IOException {
		
		if (indexSearcher == null) {
			logger.info("opening search index: " + getDirectory().toString());
			indexSearcher = new IndexSearcher(getDirectory(), true);
		}
		
		return indexSearcher;
	}
	
    public LuceneResult search(String searchText, int start, int max, String sortField, boolean reverseSort) throws IOException{
    	
    	try {
    		ScoreDoc[] sdocs = null;
    		IndexSearcher searcher = openSearcher();
			ArrayList<Document> results = new ArrayList<Document>();
		    Query query = new QueryParser(Version.LUCENE_CURRENT, "osearch", getAnalyzer()).parse(searchText);
			TopScoreDocCollector collector = TopScoreDocCollector.create(start+max, false);
			
			try {
		    	//Do this search no matter what so we can get the "total hits" for the response object
			    //The sorted result search can't give us this information (I don't think)
		    	searcher.search(query, collector);
		    	logger.info(collector.getTotalHits() + " total matching documents (" + query.toString() + ")");
		      
		    	if (sortField != null) {
				    //If they want sorted results, do a new search with sorting enabled
		    		Sort sort = new Sort(new SortField(sortField, SortField.STRING, reverseSort));
		    		sdocs = searcher.search(query, null, start + max, sort).scoreDocs;
		    	}
		    	else {
		    		sdocs = collector.topDocs().scoreDocs;
		    	}
		    	
		    	for (int i=start; (i < sdocs.length && i < start+max); i++) {
		    		
		    		results.add(searcher.doc(sdocs[i].doc));
		    	}
		    	
		    	return new LuceneResult(results,collector.getTotalHits());
		    	
			} catch (Exception e) {
				logger.warn("Search Exception: " + query.toString(),e);
			}
    	} catch (ParseException e) {
    		logger.warn("Parse Exception: " + searchText,e);
    	}
		
		return null;
    }
    
    public synchronized void closeSearcher() throws IOException {
    	if (indexSearcher != null) {
    		logger.info("closing search index");
			indexSearcher.close();
			indexSearcher = null;
    	}
    }
    
	/////////////////////
	//Utility methods
	//
	
	public Directory getDirectory() throws IOException {
		return FSDirectory.open(new File(indexDir));
	}
	
	public Analyzer getAnalyzer() {
		return new StandardAnalyzer(Version.LUCENE_CURRENT);
	}
}
