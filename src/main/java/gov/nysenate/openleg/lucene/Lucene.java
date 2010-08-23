package gov.nysenate.openleg.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.sun.accessibility.internal.resources.accessibility;

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
    
    public void deleteAllDocumentByType(String otype) throws IOException
    {
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(getDirectory(), analyzer, false, MaxFieldLength.UNLIMITED);
       
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

    public void addDocument(Object obj, IndexWriter indexWriter) throws InstantiationException,IllegalAccessException,IOException
    {
        // This functionality should be somewhere else
    	//oid = oid.replace(" ","+"); //need to remove spaces from id's in order to have them properly work with Lucene

    	Document doc = new Document();
    	
    	Collection<Field> fields;
    	LuceneConverter a = obj.getClass().getAnnotation(LuceneConverter.class);
    	if (a!=null) {
    		LuceneObjectConverter c = (LuceneObjectConverter) a.value().newInstance();
    		doc.add(new Field("oid", c.lucene_oid(), Field.Store.YES,Field.Index.ANALYZED));
    		doc.add(new Field("osearch", c.lucene_osearch(), Field.Store.NO,Field.Index.ANALYZED));
    		doc.add(new Field("otype", c.lucene_otype(), Field.Store.YES,Field.Index.ANALYZED));
    		fields = c.convert(obj);
    	}
    	else {
    		LuceneObject lob = (LuceneObject)obj;
    		doc.add(new Field("oid", lob.lucene_oid(), Field.Store.YES,Field.Index.ANALYZED));
    		doc.add(new Field("osearch", lob.lucene_osearch(), Field.Store.NO,Field.Index.ANALYZED));
    		doc.add(new Field("otype", lob.lucene_otype(), Field.Store.YES,Field.Index.ANALYZED));
    		fields = lob.getLuceneFields();
    	}
    	
    	for (Field f : fields) {
    		doc.add(f);
    	}
    	
    	logger.info("indexing document: " + doc.getField("otype").stringValue() + "=" + doc.getField("oid").stringValue());
    	Term term = new Term("oid",doc.getField("oid").stringValue());
    	indexWriter.updateDocument(term, doc);
    }
}
