package gov.nysenate.openleg.service.agenda;

import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.service.base.SobiProcessor;
import gov.nysenate.openleg.service.sobi.AbstractSobiProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.util.logging.resources.logging;

@Service
public class AgendaVoteProcessor extends AbstractSobiProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaVoteProcessor.class);

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.AGENDA_VOTE;
    }

    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing Agenda Vote...");
    }
}
