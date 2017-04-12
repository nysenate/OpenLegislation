package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.dao.agenda.data.AgendaDao;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.agenda.AgendaVoteProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by uros on 4/12/17.
 */
@Transactional
public class AgendaVoteProcessorTest extends BaseXmlProcessorTest {

    @Autowired private AgendaDao agendaDao;
    @Autowired private AgendaVoteProcessor agendaVoteProcessor;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return agendaVoteProcessor;
    }

    @Test
    public void processSenAgendaVote()  {
        String xmlPath ="processor/bill/senAgendaVote/2017-01-24-12.14.42.109650_SENAGENV_CODES.XML";
        processXmlFile(xmlPath);


    }
}
