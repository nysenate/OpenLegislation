package gov.nysenate.openleg.services;

import gov.nysenate.openleg.model.BaseLegContent;
import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.util.Storage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public abstract class ServiceBase {
    protected HashMap<String, Class<? extends BaseLegContent>> classMap;
    protected Logger logger;

    public ServiceBase() {
        logger = Logger.getLogger(this.getClass());
        classMap = new HashMap<String, Class<? extends BaseLegContent>>();
        classMap.put("bill", Bill.class);
        classMap.put("calendar", Calendar.class);
        classMap.put("transcript", Transcript.class);
    }

    public abstract boolean process(List<Entry<String, Change>> changeLog, Storage storage) throws IOException;

}
