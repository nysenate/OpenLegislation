package gov.nysenate.openleg.processor.bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.dao.agenda.data.AgendaDao;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.agenda.XmlSenAgenProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;


/**
 * Created by uros on 4/12/17.
 */
@Category(IntegrationTest.class)
public class XmlSenAgenProcessorIT extends BaseXmlProcessorTest {

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

        Agenda expected = new Agenda(agendaId);

        LocalDate weekOf = LocalDate.of(2016,6,13);
        LocalDateTime pubDateTime = DateUtils.getLrsDateTime("2016-06-21T10.35.54Z");

        AgendaInfoAddendum agendaInfoAddendum = new AgendaInfoAddendum(agendaId,"",weekOf,pubDateTime);

        CommitteeId committeeId = new CommitteeId(Chamber.SENATE, "Rules");
        String notes = "\nThis meeting will be called off the floor.\n\nALL BILLS REPORT DIRECT TO THIRD READING\n";
        LocalDateTime meetDateTime = DateUtils.getLrsDateTime("2016-06-14T00.00.00Z");

        AgendaInfoCommittee agendaInfoCommittee = new AgendaInfoCommittee(committeeId,agendaId, Version.of(""),"John J. Flanagan","332 CAP",notes,meetDateTime);

        BillId billid = new BillId("S01706A",2015);
        AgendaInfoCommitteeItem agendaInfoCommitteeItem1 = new AgendaInfoCommitteeItem(billid,"");

        BillId billid1 = new BillId("S01983A", 2015);
        AgendaInfoCommitteeItem agendaInfoCommitteeItem2 = new AgendaInfoCommitteeItem(billid1,"");


        agendaInfoCommittee.addCommitteeItem(agendaInfoCommitteeItem1);
        agendaInfoCommittee.addCommitteeItem(agendaInfoCommitteeItem2);
        agendaInfoAddendum.putCommittee(agendaInfoCommittee);

        expected.putAgendaInfoAddendum(agendaInfoAddendum);

        agendaInfoAddendum.putCommittee(agendaInfoCommittee);


        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaVoteAddenda(), actual.getAgendaVoteAddenda());
        assertEquals(OutputUtils.toJson(expected.getAgendaInfoAddenda()), OutputUtils.toJson(actual.getAgendaInfoAddenda()));
        assertEquals(OutputUtils.toJson(expected.getAgendaVoteAddenda()), OutputUtils.toJson(actual.getAgendaVoteAddenda()));
        assertEquals(OutputUtils.toJson(expected.getCommitteeAgendaAddendumIds()), OutputUtils.toJson(actual.getCommitteeAgendaAddendumIds()));
        assertEquals(OutputUtils.toJson(expected.getCommittees()), OutputUtils.toJson(actual.getCommittees()));

    }
}
