package gov.nysenate.openleg.lucene;

import java.util.Collection;

import org.apache.lucene.document.Document;

public class LuceneResult
{
	public int total;
	public Collection<Document> results;
	public LuceneResult(Collection<Document> documents, int totalresults) {
		total = totalresults;
		results = documents;
	}
}
