package gov.nysenate.openleg.lucenemodel;

import java.util.Collection;

import org.apache.lucene.document.Field;

import gov.nysenate.openleg.abstractmodel.AbstractSupplemental;
import gov.nysenate.openleg.lucene.LuceneObject;

public abstract class LuceneSupplemental extends AbstractSupplemental implements LuceneObject {

	@Override
	public Collection<Field> getLuceneFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public java.lang.reflect.Field[] lucene_fields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lucene_oid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lucene_osearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lucene_otype() {
		// TODO Auto-generated method stub
		return null;
	}

}
