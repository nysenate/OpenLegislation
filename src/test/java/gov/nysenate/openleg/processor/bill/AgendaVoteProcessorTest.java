package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.dao.agenda.data.AgendaDao;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.agenda.AgendaVoteProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.util.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;


/**
 * Created by uros on 4/12/17.
 */
@Transactional
public class AgendaVoteProcessorTest extends BaseXmlProcessorTest {

    @Autowired private AgendaDao agendaDao;
    @Autowired private AgendaVoteProcessor agendaVoteProcessor;
    @Autowired private MemberService memberService;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return agendaVoteProcessor;
    }

    @Test
    public void processSenAgendaVote() {
        AgendaId agendaId = new AgendaId(5, 2017);
        agendaDao.deleteAgenda(agendaId);

        String xmlPath = "processor/bill/senAgendaVote/2017-02-06-16.54.35.038848_SENAGENV_RULES.XML";

        SobiFragment sobiFragment = generateXmlSobiFragment(xmlPath);
        processFragment(sobiFragment);

        Agenda agenda = agendaDao.getAgenda(agendaId);
        AgendaVoteAddendum agendaVoteAddendum = agenda.getAgendaVoteAddendum("C");

        AgendaVoteAddendum agendaVoteTest = new AgendaVoteAddendum();
        agendaVoteTest.setAgendaId(agendaId);
        agendaVoteTest.setId("C");
        agendaVoteTest.setPublishedDateTime(sobiFragment.getPublishedDateTime());
        agendaVoteTest.setModifiedDateTime(sobiFragment.getPublishedDateTime());

        CommitteeId committeeId = new CommitteeId(Chamber.SENATE, "Rules");
        String chair = "John J. Flanagan";
        LocalDateTime meetDataTime = DateUtils.getLrsDateTime("2017-02-06T00.00.00Z");

        AgendaVoteCommittee voteCommittee = new AgendaVoteCommittee(committeeId, chair, meetDataTime);

        SessionYear sessionYear = new SessionYear(2017);
        SessionMember member = memberService.getMemberByShortNameEnsured("Flanagan",sessionYear,Chamber.SENATE);
        AgendaVoteAttendance memberAttendance = new AgendaVoteAttendance(member,1,"R","Present");

        SessionMember member1 = memberService.getMemberByShortNameEnsured("DeFrancisco",sessionYear,Chamber.SENATE);
        AgendaVoteAttendance memberAttendance1 = new AgendaVoteAttendance(member1,2,"R","Present");

        voteCommittee.addAttendance(memberAttendance);
        voteCommittee.addAttendance(memberAttendance1);

        agendaVoteTest.putCommittee(voteCommittee);


        assertTrue(agendaVoteAddendum.getAgendaId().equals(agendaVoteTest.getAgendaId()));
        assertTrue(agendaVoteAddendum.getId().equals(agendaVoteTest.getId()));
        assertTrue(agendaVoteAddendum.getModifiedDateTime().equals(agendaVoteTest.getModifiedDateTime()));
        assertTrue(agendaVoteAddendum.getPublishedDateTime().equals(agendaVoteTest.getPublishedDateTime()));
    }
}
