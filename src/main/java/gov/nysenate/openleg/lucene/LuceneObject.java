package gov.nysenate.openleg.lucene;

import java.util.HashMap;

import org.apache.lucene.document.Field;

public class LuceneObject implements ILuceneObject {
	private boolean searchable = true;
	
	@Override
	public boolean getLuceneSearchable() {
		return searchable;
	}

	@Override
	public HashMap<String, Field> luceneFields() {
		return null;
	}

	@Override
	public String luceneOid() {
		return null;
	}

	@Override
	public String luceneOsearch() {
		return null;
	}

	@Override
	public String luceneOtype() {
		return null;
	}

	@Override
	public String luceneSummary() {
		return null;
	}

	@Override
	public String luceneTitle() {
		return null;
	}

	@Override
	public void setLuceneSearchable(boolean searchable) {
		this.searchable = searchable;
	}

}
