package gov.nysenate.openleg.processor.agenda.reference;

import gov.nysenate.openleg.dao.agenda.reference.AgendaAlertDao;
import gov.nysenate.openleg.model.agenda.reference.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.processor.base.ParseError;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgendaAlertProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AgendaAlertProcessor.class);

    @Autowired
    private AgendaAlertDao agendaAlertDao;

    public void processAgendaAlerts() throws ParseError, IOException {
        for (File alertFile : agendaAlertDao.getIncomingAgendaAlerts()) {
            logger.info("processing agenda alert {}", alertFile.getName());
            AgendaAlertParser.parseAgendaAlert(alertFile)
                    .forEach(agendaAlertDao::updateAgendaAlertInfoCommittee);
            logger.info("archiving agenda alert {}", alertFile.getName());
            agendaAlertDao.archiveAgendaAlert(alertFile);
        }
    }

}
