package gov.nysenate.openleg.services;

import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.util.Storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public abstract class ServiceBase {
    protected HashMap<String, Class<? extends BaseObject>> classMap;
    protected Logger logger;

    public ServiceBase() {
        logger = Logger.getLogger(this.getClass());
        classMap = new HashMap<String, Class<? extends BaseObject>>();
        classMap.put("bill", Bill.class);
        classMap.put("agenda", Agenda.class);
        classMap.put("calendar", Calendar.class);
        classMap.put("meeting", Meeting.class);
        classMap.put("transcript", Transcript.class);
    }

    public abstract boolean process(List<Entry<String, Change>> changeLog, Storage storage) throws IOException;

}
