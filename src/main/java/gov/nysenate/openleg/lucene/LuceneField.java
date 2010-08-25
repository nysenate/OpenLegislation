package gov.nysenate.openleg.lucene;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuceneField {
	String value();
	org.apache.lucene.document.Field.Store store();
	org.apache.lucene.document.Field.Index index();
}

