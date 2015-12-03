package gov.nysenate.openleg.processor.agenda;

import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class AgendaVoteProcessor extends AbstractDataProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaVoteProcessor.class);

    @Autowired private XmlHelper xml;

    @PostConstruct
    public void init() {
        initBase();
    }

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.AGENDA_VOTE;
    }

    @Override
    public void process(SobiFragment sobiFragment) {
        LocalDateTime modifiedDate = sobiFragment.getPublishedDateTime();
        DataProcessUnit unit = createProcessUnit(sobiFragment);
        try {
            Document doc = xml.parse(sobiFragment.getText());
            Node xmlAgendaVote = xml.getNode("SENATEDATA/senagendavote", doc);
            Integer agendaNo = xml.getInteger("@no", xmlAgendaVote);
            SessionYear session = new SessionYear(xml.getInteger("@sessyr", xmlAgendaVote));
            Integer year = xml.getInteger("@year", xmlAgendaVote);
            AgendaId agendaId = new AgendaId(agendaNo, year);
            Agenda agenda = getOrCreateAgenda(agendaId, sobiFragment);
            agenda.setModifiedDateTime(modifiedDate);

            logger.info("Processing Votes for {} - {}", agendaId, sobiFragment);

            NodeList xmlAddenda = xml.getNodeList("addendum", xmlAgendaVote);
            for (int i = 0; i < xmlAddenda.getLength(); i++) {
                Node xmlAddendum = xmlAddenda.item(i);
                String addendumId = xml.getString("@id", xmlAddendum);

                logger.info("\tProcessing Vote Addendum {}", (addendumId.isEmpty()) ? "''" : addendumId);

                // Use the existing vote addendum if available, else create a new one
                AgendaVoteAddendum addendum = agenda.getAgendaVoteAddendum(addendumId);
                if (addendum == null) {
                    addendum = new AgendaVoteAddendum(agendaId, addendumId, modifiedDate);
                    agenda.putAgendaVoteAddendum(addendum);
                }
                addendum.setModifiedDateTime(modifiedDate);

                NodeList xmlCommittees = xml.getNodeList("committees/committee", xmlAddendum);
                for (int j = 0; j < xmlCommittees.getLength(); j++) {
                    Node xmlCommittee = xmlCommittees.item(j);
                    String action = xml.getString("@action", xmlCommittee);
                    String name = xml.getString("name/text()", xmlCommittee);
                    // We only get agendas for senate committees. This may or may not change in the future.
                    CommitteeId committeeId = new CommitteeId(Chamber.SENATE, name);
                    // If the action is remove, then discard the committee and move on
                    if (action.equals("remove")) {
                        addendum.removeCommittee(committeeId);
                        continue;
                    }
                    // Otherwise, the committee is completely replaced
                    String chair = xml.getString("chair/text()", xmlCommittee);
                    LocalDateTime meetDateTime = DateUtils.getLrsDateTime(
                            xml.getString("meetdate/text()", xmlCommittee) + xml.getString("meettime/text()", xmlCommittee));
                    AgendaVoteCommittee voteCommittee = new AgendaVoteCommittee(committeeId, chair, meetDateTime);

                    NodeList xmlMembers = xml.getNodeList("attendancelist/member", xmlCommittee);
                    for (int k = 0; k < xmlMembers.getLength(); k++) {
                        Node xmlMember = xmlMembers.item(k);
                        String memberName = xml.getString("name/text()", xmlMember);
                        SessionMember member = getMemberFromShortName(memberName, session, Chamber.SENATE);
                        Integer rank = xml.getInteger("rank/text()", xmlMember);
                        String party = xml.getString("party/text()", xmlMember);
                        String attendance = xml.getString("attendance", xmlMember);
                        AgendaVoteAttendance memberAttendance = new AgendaVoteAttendance(member, rank, party, attendance);
                        voteCommittee.addAttendance(memberAttendance);
                    }

                    NodeList xmlBills = xml.getNodeList("bills/bill", xmlCommittee);
                    for (int k = 0; k < xmlBills.getLength(); k++) {
                        Node xmlBill = xmlBills.item(k);
                        String printNo = xml.getString("@no", xmlBill);
                        BillId billId = new BillId(printNo, session);
                        String voteActionCode = xml.getString("action/text()", xmlBill);
                        AgendaVoteAction voteAction = AgendaVoteAction.valueOfCode(voteActionCode);
                        String referCommittee = xml.getString("refercomm/text()", xmlBill);
                        CommitteeId referCommitteeId = null;
                        if (!referCommittee.isEmpty()) {
                            referCommitteeId = new CommitteeId(Chamber.SENATE, referCommittee);
                        }
                        String withAmd = xml.getString("withamd/text()", xmlBill);
                        boolean withAmdBoolean = (withAmd != null && withAmd.equalsIgnoreCase("Y"));

                        // The AgendaVoteBill will contain the vote as well as additional vote metadata specific
                        // to committee votes.
                        AgendaVoteBill voteBill = new AgendaVoteBill(voteAction, referCommitteeId, withAmdBoolean);

                        // Create the committee bill vote.
                        BillVote vote = new BillVote(billId, meetDateTime.toLocalDate(), BillVoteType.COMMITTEE, 1, committeeId);
                        vote.setModifiedDateTime(modifiedDate);
                        vote.setPublishedDateTime(modifiedDate);

                        // Add the members and their vote to the BillVote.
                        NodeList xmlVotes = xml.getNodeList("votes/member", xmlBill);
                        for (int v = 0; v < xmlVotes.getLength(); v++) {
                            Node xmlVote = xmlVotes.item(v);
                            String voterName = xml.getString("name/text()", xmlVote);
                            SessionMember voterMember = getMemberFromShortName(voterName, session, Chamber.SENATE);
                            String voteCodeStr = xml.getString("vote/text()", xmlVote).replace(" ", "").replace("/", "");
                            BillVoteCode voteCode = BillVoteCode.getValue(voteCodeStr);
                            vote.addMemberVote(voteCode, voterMember);
                        }
                        voteBill.setBillVote(vote);
                        voteCommittee.addVoteBill(voteBill);

                        // Update the actual Bill with the vote information and persist it.
                        Bill bill = getOrCreateBaseBill(modifiedDate, billId, sobiFragment);
                        bill.getAmendment(billId.getVersion()).updateVote(vote);
                    }
                    addendum.putCommittee(voteCommittee);
                }
            }
        }
        catch (SAXException | XPathExpressionException | IOException | ParseError ex) {
            logger.error("Failed to parse Agenda Vote.", ex);
            unit.addException("Failed to parse Agenda Vote: " + ex.getMessage());
        }
        // Notify the data processor that an agenda vote fragment has finished processing
        postDataUnitEvent(unit);

        if (!env.isSobiBatchEnabled() || agendaIngestCache.exceedsCapacity()) {
            flushAllUpdates(); // Flush all the things
        }
    }

    @Override
    public void postProcess() {
        flushAllUpdates();
    }
}