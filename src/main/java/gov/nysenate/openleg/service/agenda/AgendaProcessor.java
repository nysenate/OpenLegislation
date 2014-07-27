package gov.nysenate.openleg.service.agenda;

import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.service.base.SobiProcessor;
import gov.nysenate.openleg.service.sobi.AbstractSobiProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AgendaProcessor extends AbstractSobiProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaProcessor.class);

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.AGENDA;
    }

    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing Agenda...");

    }
}
