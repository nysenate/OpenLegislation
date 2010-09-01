package gov.nysenate.openleg.lucene;

public interface LuceneSerializerType {
	String getType();
	String getData(LuceneObject o);
}
