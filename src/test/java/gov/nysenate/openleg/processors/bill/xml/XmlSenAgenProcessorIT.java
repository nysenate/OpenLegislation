package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.common.util.OutputUtils;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.agenda.*;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDao;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
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

    @Test
    public void processSenAgenda() {
        AgendaId agendaId = new AgendaId(20, 2008);
        String xmlPath = "processor/bill/senAgenda/2008-11-21-09.38.03.307472_SENAGEN_00020.XML";
        // Parse the XML file and store in the database.
        processXmlFile(xmlPath);
        Agenda actual = agendaDao.getAgenda(agendaId);
        Agenda expected = new Agenda(agendaId);

        var weekOf = LocalDate.of(2008, 6, 13);
        LocalDateTime pubDateTime = DateUtils.getLrsDateTime("2008-06-21T10.35.54Z");

        var agendaInfoAddendum = new AgendaInfoAddendum(agendaId, "", weekOf, pubDateTime);

        CommitteeId committeeId = new CommitteeId(Chamber.SENATE, "Rules");
        String notes = "\nThis meeting will be called off the floor.\n\nALL BILLS REPORT DIRECT TO THIRD READING\n";
        LocalDateTime meetDateTime = DateUtils.getLrsDateTime("2008-06-14T00.00.00Z");

        AgendaInfoCommittee agendaInfoCommittee = new AgendaInfoCommittee(committeeId, agendaId, Version.of(""), "John J. Flanagan", "332 CAP", notes, meetDateTime);

        BillId billid1 = new BillId("S01706A", 2007);
        AgendaInfoCommitteeItem agendaInfoCommitteeItem1 = new AgendaInfoCommitteeItem(billid1, "");

        BillId billid2 = new BillId("S01983A", 2007);
        AgendaInfoCommitteeItem agendaInfoCommitteeItem2 = new AgendaInfoCommitteeItem(billid2, "");

        agendaInfoCommittee.addCommitteeItem(agendaInfoCommitteeItem1);
        agendaInfoCommittee.addCommitteeItem(agendaInfoCommitteeItem2);
        agendaInfoAddendum.putCommittee(agendaInfoCommittee);
        expected.putAgendaInfoAddendum(agendaInfoAddendum);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(OutputUtils.toJson(expected.getAgendaInfoAddenda()), OutputUtils.toJson(actual.getAgendaInfoAddenda()));
        assertEquals(OutputUtils.toJson(expected.getAgendaVoteAddenda()), OutputUtils.toJson(actual.getAgendaVoteAddenda()));
        assertEquals(OutputUtils.toJson(expected.getCommitteeAgendaAddendumIds()), OutputUtils.toJson(actual.getCommitteeAgendaAddendumIds()));
        assertEquals(OutputUtils.toJson(expected.getCommittees()), OutputUtils.toJson(actual.getCommittees()));
    }
}
