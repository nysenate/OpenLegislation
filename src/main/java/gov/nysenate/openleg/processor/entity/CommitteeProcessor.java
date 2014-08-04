package gov.nysenate.openleg.processor.entity;

import gov.nysenate.openleg.dao.entity.CommitteeDao;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.SobiProcessor;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.MemberService;
import gov.nysenate.openleg.util.XmlHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommitteeProcessor extends AbstractDataProcessor implements SobiProcessor
{
    private static final Logger logger = Logger.getLogger(CommitteeProcessor.class);

    private SimpleDateFormat meetTimeSDF = new SimpleDateFormat("hh:mm aa");

    @Autowired
    protected CommitteeDao committeeDao;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected XmlHelper xml;

    /** {@inheritDoc  */
    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.COMMITTEE;
    }

    /** {@inheritDoc  */
    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Called committee processor");
        String xmlString = sobiFragment.getText();
        try {
            Document doc = xml.parse(xmlString);
            Node dataRoot = xml.getNode("SENATEDATA",doc);
            Node committeeRoot = xml.getNode("sencommmem", dataRoot);
            int sessionYear = Integer.parseInt(xml.getString("@sessyr", committeeRoot));
            int year = Integer.parseInt(xml.getString("@year", committeeRoot));
            Chamber chamber = Chamber.SENATE;
            logger.info("Processing " + chamber + "committees for s" + sessionYear + " y" + year + "\t" + sobiFragment.getPublishedDateTime());

            committeeRoot = xml.getNode("committees", committeeRoot);
            NodeList committeeNodes = committeeRoot.getChildNodes();
            for(int i = 0; i < committeeNodes.getLength() ; i++){
                Node committeeNode = committeeNodes.item(i);
                if (committeeNode.getNodeName().equals("committee")) {
                    try {
                        Committee committee = new Committee();
                        committee.setSession(sessionYear);
                        committee.setPublishDate(sobiFragment.getPublishedDateTime());
                        committee.setChamber(chamber);
                        processCommittee(committeeNode, committee);
                        committeeDao.updateCommittee(committee);
                    }
                    catch (Exception e){
                        logger.error(e);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error(e);
        }
    }

    /** --- Internal Methods --- */

    private Committee processCommittee(Node committeeNode, Committee committee) throws XPathExpressionException, ParseException {
        committee.setName(xml.getString("name/text()", committeeNode));
        committee.setLocation(xml.getString("location/text()", committeeNode));
        committee.setMeetDay(xml.getString("meetday/text()", committeeNode));
        String meetTimeStr = xml.getString("meettime/text()", committeeNode);
        committee.setMeetTime(meetTimeStr.isEmpty() ? null : new Time(meetTimeSDF.parse(meetTimeStr).getTime()));
        committee.setMeetAltWeek(xml.getString("meetaltweek/text()", committeeNode).trim().equalsIgnoreCase("Yes"));
        committee.setMeetAltWeekText(xml.getString("meetaltweektext/text()", committeeNode));
        Node committeeMembership = xml.getNode("membership", committeeNode);
        committee.setMembers(processCommitteeMembers(committeeMembership, committee));
        return committee;
    }

    private List<CommitteeMember> processCommitteeMembers(Node committeeMembership, Committee committee) throws XPathExpressionException {
        List<CommitteeMember> committeeMembers = new ArrayList<CommitteeMember>();
        NodeList committeeMembersNodes = committeeMembership.getChildNodes();
        for(int i = 0; i < committeeMembersNodes.getLength(); i++){
            Node memberNode = committeeMembersNodes.item(i);
            if (memberNode.getNodeName().equals("member")) {
                String shortName = xml.getString("name/text()", memberNode);
                Member sessionMember;
                try {
                    sessionMember = memberService.getMemberByLBDCName(shortName, committee.getSession(), committee.getChamber());
                }
                catch (MemberNotFoundEx memberNotFoundEx) {
                    logger.error("Could not identify committee member " + shortName + " " + committee.getSession() + " " + committee.getChamber());
                    continue;
                }
                CommitteeMember committeeMember = new CommitteeMember();
                committeeMember.setSequenceNo(Integer.parseInt(xml.getString("@seqno", memberNode)));
                committeeMember.setMember(sessionMember);
                committeeMember.setMajority(xml.getString("memberlist/text()", memberNode).trim().equalsIgnoreCase("Majority"));
                String title = xml.getString("title/text()", memberNode).trim();
                if (title.equalsIgnoreCase("Chairperson")) {
                    committeeMember.setTitle(CommitteeMemberTitle.CHAIR_PERSON);
                }
                else if (title.equalsIgnoreCase("Vice-Chair")) {
                    committeeMember.setTitle(CommitteeMemberTitle.VICE_CHAIR);
                }
                else {
                    committeeMember.setTitle(CommitteeMemberTitle.MEMBER);
                }
                committeeMembers.add(committeeMember);
            }
        }
        return committeeMembers;
    }
}
