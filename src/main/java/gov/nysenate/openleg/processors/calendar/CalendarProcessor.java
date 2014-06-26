package gov.nysenate.openleg.processors.calendar;

import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.processors.sobi.SOBIProcessor;
import org.apache.log4j.Logger;

public class CalendarProcessor extends SOBIProcessor
{
    private static final Logger logger = Logger.getLogger(CalendarProcessor.class);

    @Override
    public void process(SOBIFragment sobiFragment) {
        logger.info("Processing calendar!");
    }
}
