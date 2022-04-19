package gov.nysenate.openleg.processors.agenda;

import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.agenda.*;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.processors.AbstractLegDataProcessor;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class XmlSenAgenVoteProcessor extends AbstractLegDataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(XmlSenAgenVoteProcessor.class);

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.AGENDA_VOTE;
    }

    @Override
    public void process(LegDataFragment legDataFragment) {
        LocalDateTime modifiedDate = legDataFragment.getPublishedDateTime();
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        try {
            Node root = getXmlRoot(legDataFragment.getText());
            Node xmlAgendaVote = xmlHelper.getNode("senagendavote", root);
            Integer agendaNo = xmlHelper.getInteger("@no", xmlAgendaVote);
            SessionYear session = new SessionYear(xmlHelper.getInteger("@sessyr", xmlAgendaVote));
            Integer year = xmlHelper.getInteger("@year", xmlAgendaVote);
            AgendaId agendaId = new AgendaId(agendaNo, year);
            Agenda agenda = getOrCreateAgenda(agendaId, legDataFragment);
            agenda.setModifiedDateTime(modifiedDate);

            logger.info("Processing Votes for {} - {}", agendaId, legDataFragment);

            NodeList xmlAddenda = xmlHelper.getNodeList("addendum", xmlAgendaVote);
            for (int i = 0; i < xmlAddenda.getLength(); i++) {
                Node xmlAddendum = xmlAddenda.item(i);
                String addendumId = xmlHelper.getString("@id", xmlAddendum);

                logger.info("\tProcessing Vote Addendum {}", (addendumId.isEmpty()) ? "''" : addendumId);

                // Use the existing vote addendum if available, else create a new one
                AgendaVoteAddendum addendum = agenda.getAgendaVoteAddendum(addendumId);
                if (addendum == null) {
                    addendum = new AgendaVoteAddendum(agendaId, addendumId, modifiedDate);
                    agenda.putAgendaVoteAddendum(addendum);
                }
                addendum.setModifiedDateTime(modifiedDate);

                NodeList xmlCommittees = xmlHelper.getNodeList("committees/committee", xmlAddendum);
                for (int j = 0; j < xmlCommittees.getLength(); j++) {
                    Node xmlCommittee = xmlCommittees.item(j);
                    String action = xmlHelper.getString("@action", xmlCommittee);
                    String name = xmlHelper.getString("name/text()", xmlCommittee);
                    // We only get agendas for senate committees. This may or may not change in the future.
                    CommitteeId committeeId = new CommitteeId(Chamber.SENATE, name);
                    // If the action is remove, then discard the committee and move on
                    if (action.equals("remove")) {
                        addendum.removeCommittee(committeeId);
                        continue;
                    }
                    // Otherwise, the committee is completely replaced
                    String chair = xmlHelper.getString("chair/text()", xmlCommittee);
                    LocalDateTime meetDateTime = DateUtils.getLrsDateTime(
                            xmlHelper.getString("meetdate/text()", xmlCommittee) + xmlHelper.getString("meettime/text()", xmlCommittee));
                    AgendaVoteCommittee voteCommittee = new AgendaVoteCommittee(committeeId, chair, meetDateTime);

                    NodeList xmlMembers = xmlHelper.getNodeList("attendancelist/member", xmlCommittee);
                    for (int k = 0; k < xmlMembers.getLength(); k++) {
                        Node xmlMember = xmlMembers.item(k);
                        String memberName = xmlHelper.getString("name/text()", xmlMember);
                        SessionMember member = getMemberFromShortName(memberName, session, Chamber.SENATE);
                        Integer rank = xmlHelper.getInteger("rank/text()", xmlMember);
                        String party = xmlHelper.getString("party/text()", xmlMember);
                        String attendance = xmlHelper.getString("attendance", xmlMember);
                        AgendaVoteAttendance memberAttendance = new AgendaVoteAttendance(member, rank, party, attendance);
                        voteCommittee.addAttendance(memberAttendance);
                    }

                    NodeList xmlBills = xmlHelper.getNodeList("bills/bill", xmlCommittee);
                    for (int k = 0; k < xmlBills.getLength(); k++) {
                        Node xmlBill = xmlBills.item(k);
                        String printNo = xmlHelper.getString("@no", xmlBill);
                        BillId billId = new BillId(printNo, session);
                        String voteActionCode = xmlHelper.getString("action/text()", xmlBill);
                        AgendaVoteAction voteAction = AgendaVoteAction.valueOfCode(voteActionCode);
                        String referCommittee = xmlHelper.getString("refercomm/text()", xmlBill);
                        CommitteeId referCommitteeId = null;
                        if (!referCommittee.isEmpty()) {
                            referCommitteeId = new CommitteeId(Chamber.SENATE, referCommittee);
                        }
                        String withAmd = xmlHelper.getString("withamd/text()", xmlBill);
                        boolean withAmdBoolean = (withAmd != null && withAmd.equalsIgnoreCase("Y"));

                        // Create the committee bill vote.
                        BillVote vote = new BillVote(billId, meetDateTime.toLocalDate(), BillVoteType.COMMITTEE, 1, committeeId);
                        vote.setModifiedDateTime(modifiedDate);
                        vote.setPublishedDateTime(modifiedDate);

                        // Add the members and their vote to the BillVote.
                        NodeList xmlVotes = xmlHelper.getNodeList("votes/member", xmlBill);
                        for (int v = 0; v < xmlVotes.getLength(); v++) {
                            Node xmlVote = xmlVotes.item(v);
                            String voterName = xmlHelper.getString("name/text()", xmlVote);
                            SessionMember voterMember = getMemberFromShortName(voterName, session, Chamber.SENATE);
                            String voteCodeStr = xmlHelper.getString("vote/text()", xmlVote).replace(" ", "").replace("/", "");
                            BillVoteCode voteCode = BillVoteCode.getValue(voteCodeStr);
                            vote.addMemberVote(voteCode, voterMember);
                        }
                        // The AgendaVoteBill will contain the vote as well as additional vote metadata specific
                        // to committee votes.
                        AgendaVoteBill voteBill = new AgendaVoteBill(voteAction, referCommitteeId, withAmdBoolean, vote);
                        voteCommittee.addVoteBill(voteBill);

                        // Update the actual Bill with the vote information and persist it.
                        Bill bill = getOrCreateBaseBill(billId, legDataFragment);
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

        checkIngestCache();
    }

    @Override
    public void postProcess() {
        flushAllUpdates();
    }

    @Override
    public void checkIngestCache() {
        if (!env.isLegDataBatchEnabled() || agendaIngestCache.exceedsCapacity()) {
            flushAllUpdates();
        }
    }
}