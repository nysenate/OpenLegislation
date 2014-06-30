package gov.nysenate.openleg.processors.entity;

import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.processors.sobi.SOBIProcessor;
import org.apache.log4j.Logger;

public class CommitteeProcessor extends SOBIProcessor
{
    private static final Logger logger = Logger.getLogger(CommitteeProcessor.class);

    @Override
    public void process(SOBIFragment sobiFragment) {
        logger.info("Called committee processor");

    }
}
