package gov.nysenate.openleg.lucene;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.util.ResultIterator;
import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.openleg.util.serialize.JsonSerializer;
import gov.nysenate.openleg.util.serialize.XmlSerializer;
import gov.nysenate.util.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Encapsulates basic Lucene configuration and index manipulation.
 *
 * @author GraylinKim
 *
 */
public class Lucene
{
    private static final Logger logger = Logger.getLogger(Lucene.class);

    /**
     * The version of Lucene for compatibility purposes. We can't upgrade the version right now
     * because the StandardAnalyzer now removes hyphens from quoted terms while searching. This
     * breaks document lookup by oid
     */
	protected static final Version VERSION = Version.LUCENE_30;

	/**
     * The directory the Lucene database is stored in.
     */
    protected File indexDir = null;

	/**
	 * Utility class to safely share IndexSearcher instances across multiple threads,
	 * while periodically reopening. This class ensures each searcher is closed only
	 * once all threads have finished using it.
	 */
	protected SearcherManager searcherManager = null;

    /**
     * A reference to the configuration used by the indexWriter
     */
    protected IndexWriterConfig indexWriterConfig = null;

	/**
	 * A reference to the open IndexWriter for this database
	 */
	protected IndexWriter indexWriter = null;

	/**
	 * A reference to the analyzer used when adding documents and parsing queries
	 */
	protected Analyzer analyzer = null;

	/**
	 * A list of serializers to appy to each object when converting to a document.
	 * TODO: Make these thread safe...
	 */
	protected Collection<LuceneSerializer> serializers = Arrays.asList(new XmlSerializer(), new JsonSerializer());

    /**
     * Constructs a new Lucene connection from the given configuration file using
     * parameters within the given dot separated prefix. If a lucene database
     * does not yet exist in the directory then a new one is created.
     *
     * @param config - The Config class to load from
     * @param prefix - The dot separated prefix to the configuration parameters
     * @throws IOException
     */
	public Lucene(Config config, String prefix) throws IOException
	{
	    this(new File(config.getValue(prefix+".directory")));
	}

	/**
	 * Creates a new Lucene connection to the given directory. If a lucene database
	 * does not yet exist in the directory then a new one is created.
	 *
	 * @param indexDir -  The directory for the lucene database.
	 */
	public Lucene(File indexDir) throws IOException
	{
	    this.indexDir = indexDir;
	    this.analyzer = new StandardAnalyzer(VERSION);
	    this.indexWriterConfig = new IndexWriterConfig(Version.LUCENE_33, this.analyzer);
	    this.indexWriter = new IndexWriter(FSDirectory.open(indexDir), indexWriterConfig);
        this.searcherManager = new SearcherManager(FSDirectory.open(indexDir), new SearcherFactory());
	}


	/**
	 * Performs a sorted search on the Lucene database with the given parameters.
	 *
	 * @param queryString - The search query
	 * @param skipCount - The offset of the results, e.g. to get results 101-120, use 100
	 * @param retrieveCount - The number of results to fetch, e.g. to get results 101-120 use 20
	 * @param sortFieldName - The document field to sort on. The field should not be tokenized. Use null to sort by relevance.
	 * @param reversed - true to reverse the order of results
	 * @return LuceneResult
	 * @throws IOException
	 */
    public LuceneResult search(String queryString, int skipCount, int retrieveCount, String sortFieldName, boolean reversed) throws IOException
    {
        // Some last minute hot fixes on the incoming query
        queryString = queryString.replaceAll("otype:resolution", "(otype:bill AND oid:(R* OR E* OR J* OR K* OR L*))");

        searcherManager.maybeRefresh();
        IndexSearcher searcher = searcherManager.acquire();

        try {
            Query query = new QueryParser(VERSION, "osearch", analyzer).parse(queryString);

            // Sort by relevance unless they say otherwise
            Sort sort;
            if (sortFieldName == null) {
                sort = new Sort(new SortField(null, SortField.SCORE, reversed));
            }
            else {
                sort = new Sort(new SortField(sortFieldName, SortField.STRING_VAL, reversed));
            }

            // Time our searches so bottle necks can be identified
            long startTime = System.nanoTime();
            TopDocs topDocs = searcher.search(query, skipCount + retrieveCount, sort);
            double duration = (System.nanoTime()-startTime)/1000000.0;
            logger.info(String.format("[%.2f ms] %,d hits for query %s; sorted by %s", duration, topDocs.totalHits, query, sort));

            // Only fetch the documents for this "page" for our results. Also,
            // don't fetch fields until we need them, often we won't. This saves
            // a ton of memory and time with big full, memo, ojson, and oxml fields
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            FieldSelector lazyFieldSelector = new LazyFieldSelector();
            ArrayList<Document> results = new ArrayList<Document>();
            for (int i=skipCount; (i < scoreDocs.length && i < skipCount+retrieveCount); i++) {
                results.add(searcher.doc(scoreDocs[i].doc, lazyFieldSelector));
            }

            return new LuceneResult(results,topDocs.totalHits);
        }
        catch (ParseException e) {
            logger.warn("Unable to parse query: "+queryString,e);
        }
        finally {
            // If this doesn't get released we'll be leaking GIGANTIC amounts of memory
            searcherManager.release(searcher);
        }

        return null;
    }

    /**
     * Builds a lucene document from the given object using the given serializers. Updates
     * the document in the index if it already exists, otherwise it inserts the new document
     * into the index.
     *
     * @param obj - The object to be indexed.
     * @param serializer - A list of serializers used to make serializations to store in the document
     * @return true on success
     * @throws IOException
     */
    public boolean updateDocument(ILuceneObject obj) throws IOException
    {
        if (obj != null) {
            Document doc = new DocumentBuilder().buildDocument(obj, serializers);
            if (doc != null) {
                logger.info("indexing document: " + doc.getFieldable("otype").stringValue() + "=" + doc.getFieldable("oid").stringValue());
                String oid = doc.getFieldable("oid").stringValue();
                indexWriter.updateDocument(new Term("oid",oid), doc);
                return true;
            }
        }

        return false;
    }

    /**
     * Deletes all documents from the index that match the given query.
     *
     * @param queryString - The query to use when deleting documents
     * @throws IOException
     * @throws ParseException
     */
    public void deleteDocumentsByQuery(String queryString) throws IOException, ParseException
    {
		Query query = new QueryParser(VERSION, "osearch", indexWriter.getAnalyzer()).parse(queryString);
		indexWriter.deleteDocuments(query);
    }

    /**
     * Deletes a document based on its unique lucene oid.
     *
     * @param oid - The oid of the document to be deleted.
     * @throws IOException
     */
    public void deleteDocumentById(String oid) throws IOException
    {
        indexWriter.deleteDocuments(new Term("oid", oid));
    }

    /**
     * Commits all uncommitted document changes to the index.
     *
     * @throws CorruptIndexException
     * @throws IOException
     */
    public void commit() throws CorruptIndexException, IOException
    {
        this.indexWriter.commit();
    }

    /**
     * Closes all lucene resources. This Lucene instance can no longer be used
     * once it is closed.
     *
     * @throws IOException
     */
    public synchronized void close() throws IOException
    {
        if (analyzer != null) {
            analyzer.close();
            analyzer = null;
        }

        if (indexWriter != null) {
            indexWriter.close();
            indexWriter = null;
        }

        if (searcherManager != null) {
            searcherManager.close();
            searcherManager = null;
        }
    }



    public SenateResponse search(String queryText, String format, int skipCount, int retrieveCount, String sortFieldName, boolean reversed) throws ParseException, IOException
    {
        SenateResponse response = new SenateResponse();

        LuceneResult result = search(queryText,skipCount,retrieveCount,sortFieldName,reversed);

        if (result != null) {
            response.addMetadataByKey("totalresults", result.total );

            for (Document doc : result.results) {
                String lastModified = doc.get("modified");
                if (lastModified == null || lastModified.length() == 0)
                    lastModified = new Date().getTime()+"";

                HashMap<String,String> fields = new HashMap<String,String>();
                for(Fieldable field : doc.getFields()) {
                    fields.put(field.name(), doc.get(field.name()));
                }

                response.addResult(new Result(
                        doc.get("otype"),
                        doc.get("o"+format.toLowerCase()),
                        doc.get("oid"),
                        Long.parseLong(lastModified),
                        Boolean.parseBoolean(doc.get("active")),
                        fields));
            }
        }
        else {
            response.addMetadataByKey("totalresults", 0 );
        }

        return response;
    }

    public SenateObject getSenateObject(String oid, String type) {
        ArrayList<? extends SenateObject> senateObjects = getSenateObjects("otype:"+type+" AND oid:\""+oid+"\"");
        if (!senateObjects.isEmpty()) {
            return senateObjects.get(0);
        }
        else {
            return null;
        }
    }

    public <T extends SenateObject> ArrayList<T> getSenateObjects(String query) {
        ArrayList<T> senateObjects = new ArrayList<T>();

        ResultIterator longSearch = new ResultIterator(query);
        for(Result result:longSearch) {
            senateObjects.add((T)result.getObject());
        }

        return senateObjects;
    }

    public Bill getNewestAmendment(String oid) {
        oid = Bill.formatBillNo(oid);
        String[] billParts = oid.split("-");

        ArrayList<Bill> bills = getRelatedBills(billParts[0], billParts[1]);
        if (!bills.isEmpty()) {
            Collections.sort(bills);
            return bills.get(bills.size()-1);
        }
        else {
            return null;
        }
    }

    private ArrayList<Bill> getRelatedBills(String billNumber, String year) {
        int length = billNumber.length();
        if(!Character.isDigit(billNumber.charAt(length-1))) {
            billNumber = billNumber.substring(0, length-1);
        }

        String query = TextFormatter.append("otype:bill AND oid:((",
                billNumber, "-", year,
                " OR [", billNumber, "A-", year,
                " TO ", billNumber, "Z-", year,
                "]) AND ", billNumber, "*-", year, ")");

        return getSenateObjects(query);
    }
}
