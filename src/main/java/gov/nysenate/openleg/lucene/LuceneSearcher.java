package gov.nysenate.openleg.lucene;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

public interface LuceneSearcher {
	void closeSearcher() throws IOException;
	IndexSearcher openSearcher() throws IOException;
	LuceneResult search(String searchText, int start, int max, String sortField, boolean reverseSort) throws IOException;
}
