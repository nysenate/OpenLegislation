package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.dao.agenda.data.AgendaDao;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaInfoAddendum;
import java.time.LocalDate;
import java.time.LocalDateTime;

import gov.nysenate.openleg.model.agenda.AgendaVoteAddendum;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.agenda.AgendaProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;


/**
 * Created by uros on 4/12/17.
 */
@Transactional
public class AgendaProcessorTest extends BaseXmlProcessorTest {

    @Autowired private AgendaDao agendaDao;
    @Autowired private AgendaProcessor agendaProcessor;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return agendaProcessor;
    }

    @Test
    public void processSenAgenda()  {
        String xmlPath ="processor/bill/senAgenda/2016-11-21-09.38.03.307472_SENAGEN_00020.XML";
        processXmlFile(xmlPath);

        Agenda processedAgendaObject = agendaDao.getAgenda(new AgendaId(20,2016));

        Agenda agendaTest = new Agenda();
        AgendaId agendaId = new AgendaId(20,2016);
        LocalDate weekOf = LocalDate.of(2016,6,13);
        LocalDateTime time = LocalDateTime.of(2016,6,21,15,23);

        AgendaInfoAddendum agendaInfoAddendum = new AgendaInfoAddendum(agendaId,"",weekOf,time);
        AgendaVoteAddendum agendaVoteAddendum = new AgendaVoteAddendum(agendaId, "", time);

        agendaTest.setYear(2016);
        agendaTest.setId(agendaId);
        agendaTest.setPublishedDateTime();

        agendaTest.putAgendaInfoAddendum(agendaInfoAddendum);
        agendaTest.putAgendaVoteAddendum(agendaVoteAddendum);

        assertTrue(processedAgendaObject.equals(agendaTest));
    }
}
