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
    
    public void deleteDocuments(String otype, String oid) throws IOException {
    	Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(getDirectory(), analyzer, false, MaxFieldLength.UNLIMITED);
        
        try {
        	String qString ="otype:"+otype + ((oid!=null) ? " AND oid:"+oid : "");
            Query query = new QueryParser(Version.LUCENE_CURRENT, "otype", indexWriter.getAnalyzer()).parse(qString);
            indexWriter.deleteDocuments(query);
	    }
        catch (Exception e) {
			logger.warn("error deleting document to index: " + otype + "=" + oid, e);
        }
        
        indexWriter.close();
    }

    public LuceneResult search(String searchText, int start, int max, String sortField, boolean reverseSort) throws IOException{
    	
    	try {
    		ScoreDoc[] sdocs = null;
    		IndexSearcher searcher = openIndex();
			ArrayList<Document> results = new ArrayList<Document>();
		    Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
		    Query query = new QueryParser(Version.LUCENE_CURRENT, "osearch", analyzer).parse(searchText);
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
        
    public boolean addDocument(LuceneObject obj, LuceneSerializer serializer,IndexWriter indexWriter) throws InstantiationException,IllegalAccessException,IOException
    {
    	Document doc = new DocumentBuilder().buildDocument(obj, serializer);
    	
    	if(doc ==  null) {
    		return false;
    	}
    	
    	logger.info("indexing document: " + doc.getField("otype").stringValue() + "=" + doc.getField("oid").stringValue());
    	Term term = new Term("oid",doc.getField("oid").stringValue());
    	indexWriter.updateDocument(term, doc);
    	
    	return true;
    }
}
