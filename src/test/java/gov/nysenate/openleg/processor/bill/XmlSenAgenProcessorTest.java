package gov.nysenate.openleg.processor.bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.dao.agenda.data.AgendaDao;
import gov.nysenate.openleg.model.agenda.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.agenda.XmlSenAgenProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


/**
 * Created by uros on 4/12/17.
 */
@Transactional
public class XmlSenAgenProcessorTest extends BaseXmlProcessorTest {

    ObjectMapper mapper = new ObjectMapper();
    @Autowired private AgendaDao agendaDao;
    @Autowired private XmlSenAgenProcessor xmlSenAgenProcessor;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return xmlSenAgenProcessor;
    }

    @Test
    public void processSenAgenda() throws JsonProcessingException {

        AgendaId agendaId = new AgendaId(20,2016);
        agendaDao.deleteAgenda(agendaId);

        String xmlPath ="processor/bill/senAgenda/2016-11-21-09.38.03.307472_SENAGEN_00020.XML";
        processXmlFile(xmlPath);

        Agenda actual = agendaDao.getAgenda(new AgendaId(20,2016));

        Agenda excepted = new Agenda(agendaId);

        LocalDate weekOf = LocalDate.of(2016,6,13);
        LocalDateTime pubDateTime = DateUtils.getLrsDateTime("2016-06-21T10.35.54Z");

        AgendaInfoAddendum agendaInfoAddendum = new AgendaInfoAddendum(agendaId,"",weekOf,pubDateTime);

        CommitteeId committeeId = new CommitteeId(Chamber.SENATE, "Rules");
        String notes = "\nThis meeting will be called off the floor.||ALL BILLS REPORT DIRECT TO THIRD READING\n";
        LocalDateTime meetDateTime = DateUtils.getLrsDateTime("2016-06-14T00.00.00Z");

        AgendaInfoCommittee agendaInfoCommittee = new AgendaInfoCommittee(committeeId,agendaId, Version.of(""),"John J. Flanagan","332 CAP",notes,meetDateTime);

        BillId billid = new BillId("S01706A",2015);
        AgendaInfoCommitteeItem agendaInfoCommitteeItem1 = new AgendaInfoCommitteeItem(billid,"");

        BillId billid1 = new BillId("S01983A", 2015);
        AgendaInfoCommitteeItem agendaInfoCommitteeItem2 = new AgendaInfoCommitteeItem(billid1,"");


        agendaInfoCommittee.addCommitteeItem(agendaInfoCommitteeItem1);
        agendaInfoCommittee.addCommitteeItem(agendaInfoCommitteeItem2);
        agendaInfoAddendum.putCommittee(agendaInfoCommittee);

        excepted.putAgendaInfoAddendum(agendaInfoAddendum);

        agendaInfoAddendum.putCommittee(agendaInfoCommittee);


        assertEquals(excepted.getId(),actual.getId());
        assertEquals(excepted.getAgendaVoteAddenda(),actual.getAgendaVoteAddenda());
        assertEquals(mapper.writeValueAsString(excepted.getAgendaInfoAddenda()),mapper.writeValueAsString(actual.getAgendaInfoAddenda()));
        assertEquals(mapper.writeValueAsString(excepted.getAgendaVoteAddenda()),mapper.writeValueAsString(actual.getAgendaVoteAddenda()));
        assertEquals(mapper.writeValueAsString(excepted.getCommitteeAgendaAddendumIds()),mapper.writeValueAsString(actual.getCommitteeAgendaAddendumIds()));
        assertEquals(mapper.writeValueAsString(excepted.getCommittees()),mapper.writeValueAsString(actual.getCommittees()));

    }
}
