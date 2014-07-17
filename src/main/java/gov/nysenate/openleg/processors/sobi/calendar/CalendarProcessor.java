package gov.nysenate.openleg.processors.sobi.calendar;

import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.processors.sobi.SobiProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarProcessor extends SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(CalendarProcessor.class);

    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing calendar!");

    }
}
