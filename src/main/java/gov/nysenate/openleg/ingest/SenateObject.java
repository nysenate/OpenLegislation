package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.lucene.LuceneObject;

import java.util.HashMap;

import org.apache.lucene.document.Field;

public class SenateObject extends LuceneObject implements ISenateObject {

	@Override
	public int getYear() {
		return 0;
	}

	@Override
	public void merge(ISenateObject obj) {
		return;
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
}
