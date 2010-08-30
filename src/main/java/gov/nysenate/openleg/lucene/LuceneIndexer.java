package gov.nysenate.openleg.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;

public interface LuceneIndexer {
	
	boolean addDocument(LuceneObject o, LuceneSerializer serializer, IndexWriter writer)  throws InstantiationException,IllegalAccessException,IOException;
	void deleteDocuments(String otype, String oid)  throws IOException;
}
