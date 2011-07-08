package gov.nysenate.openleg.search;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.ingest.Ingest;
import gov.nysenate.openleg.model.ISenateObject;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * An easier, iterable way to move through multiple result sets
 *
 * @param <T>
 */
public class SenateObjectSearch<T extends ISenateObject> implements Iterator<T>, Iterable<T> {
	private static Logger logger = Logger.getLogger(SenateObjectSearch.class);
	
	private static final int SIZE = 500;
	private static final int PAGE = 0;
	private static final String FORMAT = "json";
	private static final String SORT_BY = "oid";
	private static final boolean REVERSE = false;
	
	ResultSearch resultSearch;
	
	public Class<T> clazz;
	
	private ObjectMapper mapper;
	
	public SenateObjectSearch() {
		this(SIZE, PAGE, FORMAT, SORT_BY, REVERSE);
	}
	
	public SenateObjectSearch(int max, int page, String format, String sortBy, boolean reverse) {
		resultSearch = new ResultSearch(max, page, format, sortBy, reverse);
		mapper = Ingest.getMapper();
	}
	
	public SenateObjectSearch<T> query(String query) {
		resultSearch.query = query;
		return this;
	}
	
	public SenateObjectSearch<T> clazz(Class<T> clazz) {
		this.clazz = clazz;
		return this;
	}
	
	public boolean hasNext() {
		return resultSearch.hasNext();
	}
	
	public T next() {
		return result(resultSearch.next());
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	private T result(Result result) {
		T object = null;
		
		try {
			object = (T) mapper.readValue(
					ApiHelper.unwrapJson(result.data), 
					(clazz == null ? ApiHelper.getApiType(result.getOtype()).clazz() : clazz));
			
			object.setModified(result.lastModified);
			object.setActive(result.active);
			
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
