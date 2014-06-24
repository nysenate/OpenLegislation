package gov.nysenate.openleg.processors.sobi;

import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.log4j.Logger;

public class CalendarProcessor extends SOBIProcessor
{
    private static final Logger logger = Logger.getLogger(CalendarProcessor.class);

    @Override
    public void process(SOBIFragment sobiFragment) {
        logger.info("Processing calendar!");
    }
}
