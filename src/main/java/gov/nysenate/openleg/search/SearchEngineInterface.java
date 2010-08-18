package gov.nysenate.openleg.search;

import java.io.IOException;
import org.apache.lucene.queryParser.ParseException;

public interface SearchEngineInterface {
	public Object get(String codeType, String otype, String oid, String sortField, int start, int numberOfResults, boolean reverseSort);
	public Object search(String searchText, int start, int max, String sortField, boolean reverseSort) throws ParseException, IOException;
}