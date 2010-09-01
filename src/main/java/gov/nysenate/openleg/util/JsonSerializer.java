package gov.nysenate.openleg.util;

import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;

public class JsonSerializer implements LuceneSerializer {

	@Override
	public String getData(LuceneObject o) {
		return JsonConverter.getJson(o).toString();
	}

	@Override
	public String getType() {
		return "ojson";
	}

}
