package gov.nysenate.openleg.util;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.ingest.Ingest;
import gov.nysenate.openleg.model.ISenateObject;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * An easier, iterable way to move through multiple result sets
 *
 * @param <T>
 */
public class LongSearch<T extends ISenateObject> implements Iterator<T>, Iterable<T> {
	private static Logger logger = Logger.getLogger(LongSearch.class);
	
	private static final int SIZE = 500;
	private static final int PAGE = 0;
	private static final String FORMAT = "json";
	private static final String SORT_BY = "oid";
	private static final boolean REVERSE = false;
	
	//current position in senateResponse.getResuls()
	private int pos;
	//current size of senateResponse.getResults()
	private int size;

	//maximum number of objects to return per query
	public int max;
	//current page of query
	public int page;
	
	//true when search has been completed
	private boolean exhausted = false;
	
	public String format;
	public String sortBy;
	public boolean reverse;
	public String query;
	
	public SenateResponse senateResponse;
	
	public Class<T> clazz;
	
	private ObjectMapper mapper;
	private SearchEngine searchEngine;
	
	public LongSearch() {
		this(SIZE, PAGE, FORMAT, SORT_BY, REVERSE);
	}
	
	public LongSearch(int max, int page, String format, String sortBy, boolean reverse) {
		this.max = max;
		this.page = page;
		this.format = format;
		this.sortBy = sortBy;
		this.reverse = reverse;
		mapper = Ingest.getMapper();
		searchEngine = SearchEngine.getInstance();
	}
	
	public LongSearch<T> query(String query) {
		this.query = query;
		reset();
		return this;
	}
	
	public LongSearch<T> clazz(Class<T> clazz) {
		this.clazz = clazz;
		return this;
	}
	
	public boolean hasNext() {
		if(pos == size) {
			if(!exhausted) {
				doQuery();
			}
		}
		return pos < size;
	}
	
	public T next() {
		if(pos >= size) {
			throw new NoSuchElementException();
		}
		else {
			return result(senateResponse.getResults().get(pos++));
		}
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private void reset() {
		page = 0;
		pos = 0;
		size = 0;
		exhausted = false;
		senateResponse = null;
	}
	
	private void doQuery() {
		if(exhausted){
			return;
		}
		
		try {
			senateResponse = searchEngine.search(query, format, (max * page++), max, sortBy, reverse);
		} catch (ParseException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		
		if(senateResponse == null) {
			exhausted = true;
			return;
		}
		
		ArrayList<Result> results = senateResponse.getResults();
		
		if(results.size() != max)
			exhausted = true;
		
		size = results.size();
		pos = 0;
		
	}
	
	@SuppressWarnings("unchecked")
	private T result(Result result) {
		T object = null;
		
		try {
			object = (T) mapper.readValue(
					ApiHelper.unwrapJson(result.data), 
					(clazz == null ? ApiHelper.getApiType(result.getOtype()).clazz() : clazz));
			
			object.setLuceneModified(result.lastModified);
			object.setLuceneActive(result.active);
			
		} catch (JsonParseException e) {
			logger.error(e);
		} catch (JsonMappingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return object;
	}

	public Iterator<T> iterator() {
		return this;
	}
}
