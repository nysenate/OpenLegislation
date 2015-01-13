package gov.nysenate.openleg.processor.rules;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.processor.base.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RulesProcessService implements ProcessService
{
    private static final Logger logger = LoggerFactory.getLogger(RulesProcessService.class);

    @Autowired private Environment env;
    @Autowired private EventBus eventBus;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public int collate() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int ingest() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public String getCollateType() {
        return null;
    }
}
