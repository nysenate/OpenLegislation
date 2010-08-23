package gov.nysenate.openleg.lucene;

import java.util.Collection;
import org.apache.lucene.document.Field;

public interface LuceneObjectConverter {
	String lucene_oid();
	String lucene_osearch();
	String lucene_otype();
	
	Collection<Field> convert(Object obj);
}
