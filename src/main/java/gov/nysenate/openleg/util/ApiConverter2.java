package gov.nysenate.openleg.util;

import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.xstream.XStreamBuilder;

public class ApiConverter2 implements LuceneSerializer {

	@Override
	public String toJson(Object o) {
		return JsonConverter.getJson(o).toString();
	}

	@Override
	public String toXml(Object o) {
		return XStreamBuilder.xml(o);
	}

}
