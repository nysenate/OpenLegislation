package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.agenda.*;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDao;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillVote;
import gov.nysenate.openleg.legislation.bill.BillVoteType;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;


/**
 * Created by uros on 4/12/17.
 *
 */
@Category(IntegrationTest.class)
public class XmlSenAgenVoteProcessorIT extends BaseXmlProcessorTest {

    @Autowired private AgendaDao agendaDao;
    @Autowired private MemberService memberService;


    @Test
    public void processSenAgendaVote() {
        AgendaId agendaId = new AgendaId(5, 2017);
        agendaDao.deleteAgenda(agendaId);
        String xmlPath = "processor/bill/senAgendaVote/2017-02-06-16.54.35.038848_SENAGENV_RULES.XML";

        LegDataFragment legDataFragment = generateXmlSobiFragment(xmlPath);
        processFragment(legDataFragment);

        Agenda agenda = agendaDao.getAgenda(agendaId);
        AgendaVoteAddendum actual = agenda.getAgendaVoteAddendum("C");

        AgendaVoteAddendum expected = new AgendaVoteAddendum();
        expected.setAgendaId(agendaId);
        expected.setId("C");
        expected.setPublishedDateTime(legDataFragment.getPublishedDateTime());
        expected.setModifiedDateTime(legDataFragment.getPublishedDateTime());

        final var committeeId = new CommitteeId(Chamber.SENATE, "Rules");
        final var billId1 = new BillId("S2956A",2017);
        final var billId2 = new BillId("S3505",2017);
        String chair = "John J. Flanagan";
        LocalDateTime meetDataTime = DateUtils.getLrsDateTime("2017-02-06T00.00.00Z");
        AgendaVoteCommittee voteCommittee = new AgendaVoteCommittee(committeeId, chair, meetDataTime);
        SessionYear sessionYear = new SessionYear(2017);
        SessionMember member = memberService.getSessionMemberByShortName("FLANAGAN", sessionYear, Chamber.SENATE);
        AgendaVoteAttendance memberAttendance = new AgendaVoteAttendance(member,1,"R","Present");
        SessionMember member1 = memberService.getSessionMemberByShortName("DEFRANCISCO", sessionYear, Chamber.SENATE);
        AgendaVoteAttendance memberAttendance1 = new AgendaVoteAttendance(member1,2,"R","Present");
        voteCommittee.addAttendance(memberAttendance);
        voteCommittee.addAttendance(memberAttendance1);
        expected.putCommittee(voteCommittee);

        var voteDay = LocalDate.of(2017, 2, 6);
        Map<BillId, AgendaVoteBill> voteBillMap = new TreeMap<>();
        AgendaVoteBill agendaVoteBill1 = new AgendaVoteBill(AgendaVoteAction.THIRD_READING,
                committeeId, false, new BillVote(billId1, voteDay, BillVoteType.COMMITTEE));
        AgendaVoteBill agendaVoteBill2 = new AgendaVoteBill(AgendaVoteAction.THIRD_READING,
                committeeId, false, new BillVote(billId2, voteDay, BillVoteType.COMMITTEE));
        voteBillMap.put(billId1, agendaVoteBill1);
        voteBillMap.put(billId2, agendaVoteBill2);
        voteCommittee.setVotedBills(voteBillMap);
        Map<CommitteeId, AgendaVoteCommittee> committeeIdAgendaVoteCommitteeHashMap = new HashMap<>();
        committeeIdAgendaVoteCommitteeHashMap.put(committeeId, voteCommittee);
        expected.setCommitteeVoteMap(committeeIdAgendaVoteCommitteeHashMap);

        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getModifiedDateTime(), actual.getModifiedDateTime());
        assertEquals(expected.getPublishedDateTime(), actual.getPublishedDateTime());
        assertEquals(expected.getSession(), actual.getSession());
        assertEquals(expected.getYear(), actual.getYear());

        /**
         * Compare AgendaVoteCommittee
         */

        AgendaVoteCommittee expectedAgendaVoteCommittee = getAgendaVoteCommittee(expected, committeeId);
        AgendaVoteCommittee actualAgendaVoteCommittee = getAgendaVoteCommittee(actual, committeeId);

        assertEquals(expectedAgendaVoteCommittee.getAttendance(), actualAgendaVoteCommittee.getAttendance());
        assertEquals(expectedAgendaVoteCommittee.getChair(), actualAgendaVoteCommittee.getChair());
        assertEquals(expectedAgendaVoteCommittee.getMeetingDateTime(), actualAgendaVoteCommittee.getMeetingDateTime());
        assertEquals(expectedAgendaVoteCommittee.getCommitteeId(), actualAgendaVoteCommittee.getCommitteeId());
    }

    private static AgendaVoteCommittee getAgendaVoteCommittee(AgendaVoteAddendum addendum, CommitteeId id) {
        return addendum.getCommitteeVoteMap().get(id);
    }
}
