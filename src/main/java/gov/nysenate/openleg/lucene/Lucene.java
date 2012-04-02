package gov.nysenate.openleg.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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

public class Lucene implements LuceneIndexer, LuceneSearcher {

	private static final Version VERSION = Version.LUCENE_30;

	public SearcherManager searcherManager = null;

	protected IndexWriter indexWriter = null;
	protected IndexSearcher indexSearcher = null;

	protected Logger logger;
	protected String indexDir;

	public Lucene(String indexDir) {
		this.indexDir = indexDir;
		this.logger = Logger.getLogger(this.getClass());

		try {
			createIndex();
			searcherManager = new SearcherManager(getDirectory());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/////////////////////////////////
	// Implementing LuceneIndexer
	//

    @Override
    public synchronized void createIndex() throws IOException{
        File index = new File(indexDir);
        if (!index.exists()) {
            FileUtils.forceMkdir(index);
        }

        if (0 == index.listFiles().length) {
	        IndexWriter indexWriter = new IndexWriter(getDirectory(), getConfig());
	        indexWriter.optimize();
	        indexWriter.close();
        }
    }

	@Override
    public synchronized IndexWriter openWriter() throws IOException {
		if (indexWriter == null) {
			indexWriter = new IndexWriter(getDirectory(), getConfig());
		}
		return indexWriter;
	}

	@Override
	public synchronized void commit() throws CorruptIndexException, IOException {
		openWriter().commit();
	}

    @Override
    public boolean addDocument(ILuceneObject obj, LuceneSerializer[] serializer,IndexWriter indexWriter) throws IOException
    {
    	if(obj == null)
    		return false;

    	Document doc = new DocumentBuilder().buildDocument(obj, serializer);

    	if(doc ==  null)
    		return false;

    	logger.info("indexing document: " + doc.getFieldable("otype").stringValue() + "=" + doc.getFieldable("oid").stringValue());

    	Query query;
		try {
			query = new QueryParser(Version.LUCENE_30, "oid", indexWriter.getAnalyzer()).parse("oid:" + doc.getFieldable("oid").stringValue());
	        indexWriter.deleteDocuments(query);
	    	indexWriter.addDocument(doc);
		} catch (ParseException e) {
			logger.warn("error adding document to index: " + doc.getFieldable("otype").stringValue() + "=" + doc.getFieldable("oid").stringValue(), e);
			e.printStackTrace();
		}
    	return true;
    }

    public void deleteDocumentsByQuery(String qString, IndexWriter indexWriter) throws IOException {
    	try {
    		Query query = new QueryParser(VERSION, "otype", indexWriter.getAnalyzer()).parse(qString);
    		indexWriter.deleteDocuments(query);
    	}
    	catch (Exception e) {
    		logger.warn("error deleting document to index: " + qString);
    	}
    }

    @Override
    public void deleteDocuments(String otype, String oid) throws IOException {
    	openWriter();
        deleteDocuments(otype, oid, indexWriter);
        closeWriter();
    }

    public void deleteDocuments(String otype, String oid, IndexWriter writer) throws IOException {
        try {
            String qString ="otype:"+otype + ((oid!=null) ? " AND oid:"+oid : "");
            logger.error(qString);
            Query query = new QueryParser(VERSION, "otype", writer.getAnalyzer()).parse(qString);
            writer.deleteDocuments(query);
        }
        catch (Exception e) {
            logger.warn("error deleting document to index: " + otype + "=" + oid, e);
        }
    }

    @Override
    public synchronized void optimize() throws IOException {
    	openWriter().optimize();
    }

    @Override
    public synchronized void closeWriter() throws IOException {
    	if (indexWriter != null) {

    		indexWriter.close();
			indexWriter = null;
    	}
    }

    /////////////////////////////////
    // Implementing Lucene Searcher
    //

	@Override
    public synchronized IndexSearcher openSearcher() throws IOException {

		if (indexSearcher == null) {
			logger.info("opening search index: " + getDirectory().toString());
			indexSearcher = new IndexSearcher(getDirectory(), true);
		}

		return indexSearcher;
	}

    @Override
    public LuceneResult search(String searchText, int start, int max, String sortField, boolean reverseSort) throws IOException{

    	IndexSearcher searcher = searcherManager.get();

    	if(!searcher.getIndexReader().isCurrent()) {
    		try {
				searcherManager.maybeReopen();
			} catch (InterruptedException e) {
				logger.error(e);
			}
    	}

    	try {
    		ScoreDoc[] sdocs = null;
			ArrayList<Document> results = new ArrayList<Document>();
		    Query query = new QueryParser(VERSION, "osearch", getAnalyzer()).parse(searchText);
			TopScoreDocCollector collector = TopScoreDocCollector.create(start+max, false);

			try {
		    	//Do this search no matter what so we can get the "total hits" for the response object
			    //The sorted result search can't give us this information (I don't think)
		    	searcher.search(query, collector);
		    	logger.info(collector.getTotalHits() + " total matching documents (" + query.toString() + ")");

		    	if (sortField != null) {
				    //If they want sorted results, do a new search with sorting enabled

		    		Sort sort = new Sort(new SortField(sortField, SortField.STRING_VAL, reverseSort));
		    		sdocs = searcher.search(query, null, start + max, sort).scoreDocs;
		    	}
		    	else {
		    		sdocs = collector.topDocs().scoreDocs;
		    	}

		    	for (int i=start; (i < sdocs.length && i < start+max); i++) {

		    		/*
		    		 * certain bills have pretty massive json, xml and bill text fields that
		    		 * can cause heap issues.  lazy loading is our best bet for now
		    		 */
		    		results.add(searcher.doc(sdocs[i].doc, new FieldSelector() {
						private static final long serialVersionUID = -5944405015166445368L;
						@Override
                        public FieldSelectorResult accept(String field) {
		    				return FieldSelectorResult.LAZY_LOAD;
		    			}
		    		}));
		    	}

		    	return new LuceneResult(results,collector.getTotalHits());

			} catch (Exception e) {
				e.printStackTrace();
				logger.warn("Search Exception: " + query.toString(),e);
			}
    	} catch (ParseException e) {
    		logger.warn("Parse Exception: " + searchText,e);
    	}
    	finally {
    		searcherManager.release(searcher);
    	}

		return null;
    }

    @Override
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

    public IndexWriterConfig getConfig() {
		return new IndexWriterConfig(Version.LUCENE_33, getAnalyzer());
    }

	public Directory getDirectory() throws IOException {
		return FSDirectory.open(new File(indexDir));
	}

	public Analyzer getAnalyzer() {
		return new StandardAnalyzer(VERSION);
	}

	public IndexWriter newIndexWriter() throws IOException {
	    // Use with caution
	    return new IndexWriter(getDirectory(), getConfig());
	}
}
