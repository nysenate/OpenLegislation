package gov.nysenate.openleg.processors.sobi;

import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.log4j.Logger;

public class CommitteeProcessor extends SOBIProcessor
{
    private static final Logger logger = Logger.getLogger(CommitteeProcessor.class);

    @Override
    public void process(SOBIFragment sobiFragment) {
        logger.info("Called committee processor");
    }
}
