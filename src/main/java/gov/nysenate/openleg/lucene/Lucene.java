package gov.nysenate.openleg.lucene;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.util.ResultIterator;
import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.util.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
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
 *
 */
public class Lucene
{
    private static final Logger logger = Logger.getLogger(Lucene.class);

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
	    this(new File(config.getValue(prefix+".directory")),
	         Boolean.parseBoolean(config.getValue(prefix+".readOnly", "false")));
	}

	/**
	 * Creates a new Lucene connection to the given directory. If a lucene database
	 * does not yet exist in the directory then a new one is created.
	 *
	 * @param indexDir - The directory for the lucene database.
	 * @param readOnly - When true, an index writer is not created. Only one index
	 *                   writer may be open at a time across all system processes.
	 */
	public Lucene(File indexDir, boolean readOnly) throws IOException
	{
        this.indexDir = indexDir;
        this.analyzer = new OpenLegislationAnalyzer(Version.LUCENE_46);

	    if (!readOnly) {
            this.indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, this.analyzer);
            this.indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            this.indexWriter = new IndexWriter(FSDirectory.open(indexDir), indexWriterConfig);

            // The index needs to exist before creating the searcher manager so do a quick commit
            // of nothing in case the index doesn't exist already.
            this.indexWriter.commit();
        }

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
    protected LuceneResult _search(String queryString, int skipCount, int retrieveCount, String sortFieldName, boolean reversed) throws IOException
    {
        // detect when people are trying to pull up a specific bill
        if (queryString.matches("^[A-Z][0-9]{1,5}(-[0-9]+)?$")) {
            queryString = "oid:"+queryString;
        }
        else {
            // cheap implementation of otype:resolution
            queryString = queryString.replaceAll("otype:resolution", "(otype:bill AND oid:(R* OR E* OR J* OR K* OR L*))");
            logger.info(queryString);
            queryString = queryToLowerCase(queryString);
        }

        searcherManager.maybeRefresh();
        IndexSearcher searcher = searcherManager.acquire();

        try {
            Query query = new OpenLegislationQueryParser(analyzer).parse(queryString, "osearch");

            // Sort by relevance unless they say otherwise
            Sort sort;
            if (sortFieldName == null || sortFieldName.isEmpty()) {
                sort = new Sort(new SortField(null, SortField.Type.SCORE, reversed));
            }
            else {
                SortField.Type sortType;
                if (sortFieldName.equals("year")) {
                    sortType = SortField.Type.INT;
                }
                else if (sortFieldName.equals("when") || sortFieldName.equals("modified") || sortFieldName.equals("published")) {
                    sortType = SortField.Type.LONG;
                }
                else {
                    sortType = SortField.Type.STRING_VAL;
                }
                sort = new Sort(new SortField(sortFieldName, sortType, reversed));
            }

            // Time our searches so bottle necks can be identified
            long startTime = System.nanoTime();
            TopDocs topDocs = searcher.search(query, skipCount + retrieveCount, sort);
            double duration = (System.nanoTime()-startTime)/1000000.0;
            logger.info(String.format("[%.2f ms] %,d hits for query %s; sorted by %s", duration, topDocs.totalHits, query, sort));

            // Only fetch the documents for this "page" for our results.
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            ArrayList<Document> results = new ArrayList<Document>();
            for (int i=skipCount; (i < scoreDocs.length && i < skipCount+retrieveCount); i++) {
                results.add(searcher.doc(scoreDocs[i].doc));
            }

            return new LuceneResult(results,topDocs.totalHits);
        }
        catch (QueryNodeException e) {
            logger.warn("Unable to parse query: "+queryString,e);
        }
        finally {
            // If this doesn't get released we'll be leaking GIGANTIC amounts of memory
            searcherManager.release(searcher);
        }

        return null;
    }

    /**
     * Updates the document in Lucene using the document oid as a primary key. Inserts
     * a new document if an existing document is not found.
     *
     * @param doc - The document to be indexed.
     * @throws IOException
     */
    public void updateDocument(Document doc) throws IOException
    {
        logger.info("indexing document: " + doc.getField("otype").stringValue() + "=" + doc.getField("oid").stringValue());
        String oid = doc.getField("oid").stringValue();
        indexWriter.updateDocument(new Term("oid", oid.toLowerCase()), doc);
    }

    /**
     * Deletes all documents from the index that match the given query.
     *
     * @param queryString - The query to use when deleting documents
     * @throws IOException
     * @throws ParseException
     */
    public void deleteDocumentsByQuery(String queryString) throws IOException, QueryNodeException
    {
        queryString = queryToLowerCase(queryString);
		Query query = new OpenLegislationQueryParser(indexWriter.getAnalyzer()).parse(queryString, "osearch");
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
        indexWriter.deleteDocuments(new Term("oid", oid.toLowerCase()));
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



    public SenateResponse search(String queryText, int skipCount, int retrieveCount, String sortFieldName, boolean reversed) throws IOException
    {
        SenateResponse response = new SenateResponse();

        LuceneResult result = this._search(queryText,skipCount,retrieveCount,sortFieldName,reversed);

        if (result != null) {
            response.addMetadataByKey("totalresults", result.total );

            for (Document doc : result.results) {
                String lastModified = doc.get("modified");
                if (lastModified == null || lastModified.length() == 0)
                    lastModified = new Date().getTime()+"";

                HashMap<String,String> fields = new HashMap<String,String>();
                for(IndexableField field : doc.getFields()) {
                    fields.put(field.name(), doc.get(field.name()));
                }

                response.addResult(new Result(
                        doc.get("otype"),
                        doc.get("odata"),
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

    public IBaseObject getSenateObject(String oid, String type) {
        oid = oid.replace(" ", "-").replace(",", "");
        ResultIterator longSearch = new ResultIterator("otype:"+type+" AND oid:\""+oid+"\"", 1, 1, "oid", true);
        for(Result result:longSearch) {
            return result.getObject();
        }
        return null;
    }

    @SuppressWarnings("unchecked") // Doesn't seem to be a way to properly type check here
    public <T extends IBaseObject> ArrayList<T> getSenateObjects(String query) {
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

    /**
     * Searches should be case insensitive for fields and values
     * Lucene keywords must be capitalized though to be parsed correctly.
     *
     * @param query
     * @return
     */
    private String queryToLowerCase(String query)
    {
        String[] tokens = query.split(" ");
        query = "";
        for(String token : tokens) {
            if (token.equals("TO") || token.equals("AND") || token.equals("OR") || token.equals("NOT")) {
                query += token+" ";
            }
            else {
                query += token.toLowerCase()+" ";
            }
        }
        return query;
    }
}
