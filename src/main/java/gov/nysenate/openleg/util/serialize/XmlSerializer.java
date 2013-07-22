package gov.nysenate.openleg.util.serialize;

import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.ISenateSerializer;
import gov.nysenate.openleg.xstream.XStreamBuilder;

public class XmlSerializer implements ISenateSerializer {

    @Override
    public String getData(BaseObject o) {
        return XStreamBuilder.xml(o);
    }

    @Override
    public String getType() {
        return "oxml";
    }
}
