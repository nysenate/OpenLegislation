package gov.nysenate.openleg.util.serialize;

import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.ISenateSerializer;

public class JsonSerializer implements ISenateSerializer {

    @Override
    public String getData(BaseObject o) {
        return JsonConverter.getJson(o).toString();
    }

    @Override
    public String getType() {
        return "ojson";
    }

}
