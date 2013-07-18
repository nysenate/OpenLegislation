package gov.nysenate.openleg.util.serialize;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.ILuceneSerializer;
import gov.nysenate.openleg.xstream.XStreamBuilder;

public class XmlSerializer implements ILuceneSerializer {

    @Override
    public String getData(ILuceneObject o) {
        return XStreamBuilder.xml(o);
    }

    @Override
    public String getType() {
        return "oxml";
    }
}
