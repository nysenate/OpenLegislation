package gov.nysenate.openleg.lucene;

import java.io.IOException;

public interface LuceneSearcher {

	LuceneResult search(String searchText, int start, int max, String sortField, boolean reverseSort) throws IOException;
}
