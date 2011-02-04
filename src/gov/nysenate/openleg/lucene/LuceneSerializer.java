package gov.nysenate.openleg.lucene;

public interface LuceneSerializer {
	String getType();
	String getData(LuceneObject o);
}