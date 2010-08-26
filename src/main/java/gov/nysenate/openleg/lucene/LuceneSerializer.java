package gov.nysenate.openleg.lucene;

public interface LuceneSerializer {
	String toXml(Object o);
	String toJson(Object o);
}
