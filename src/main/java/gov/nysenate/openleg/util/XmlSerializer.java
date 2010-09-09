package gov.nysenate.openleg.util;

import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.xstream.XStreamBuilder;

public class XmlSerializer implements LuceneSerializer {

	@Override
	public String getData(LuceneObject o) {
		return XStreamBuilder.xml(o);
	}

	@Override
	public String getType() {
		return "oxml";
	}
}
