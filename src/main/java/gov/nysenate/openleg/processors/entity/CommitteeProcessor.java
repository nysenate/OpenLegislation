package gov.nysenate.openleg.processors.entity;

import gov.nysenate.openleg.dao.entity.CommitteeDao;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.processors.sobi.SOBIProcessor;
import gov.nysenate.openleg.service.entity.MemberNotFoundEx;
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
public class CommitteeProcessor extends SOBIProcessor
{
    private static final Logger logger = Logger.getLogger(CommitteeProcessor.class);

    private SimpleDateFormat meetTimeSDF = new SimpleDateFormat("hh:mm aa");

    @Autowired
    protected CommitteeDao committeeDao;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected XmlHelper xmlh;

    @Override
    public void process(SOBIFragment sobiFragment) {
        logger.info("Called committee processor");
        String xmlString = sobiFragment.getText();
        try {
            Document doc = xmlh.parse(xmlString);
            Node dataRoot = xmlh.getNode("SENATEDATA",doc);
            Node committeeRoot = xmlh.getNode("sencommmem", dataRoot);
            int sessionYear = Integer.parseInt(xmlh.getString("@sessyr", committeeRoot));
            int year = Integer.parseInt(xmlh.getString("@year", committeeRoot));
            Chamber chamber = Chamber.SENATE;
            logger.info("Processing " + chamber + "committees for s" + sessionYear + " y" + year + "\t" + sobiFragment.getPublishedDateTime());

            committeeRoot = xmlh.getNode("committees", committeeRoot);
            NodeList committeeNodes = committeeRoot.getChildNodes();
            for(int i=0;i<committeeNodes.getLength();i++){
                Node committeeNode = committeeNodes.item(i);
                if(committeeNode.getNodeName()=="committee"){
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
        } catch (Exception e) {
            logger.error(e);//.length()>120 ? e.getMessage().substring(0,120) : e.getMessage());
        }
    }

    private Committee processCommittee(Node committeeNode, Committee committee) throws XPathExpressionException, ParseException {
        committee.setName(xmlh.getString("name/text()", committeeNode));
        committee.setLocation(xmlh.getString("location/text()", committeeNode));
        committee.setMeetDay(xmlh.getString("meetday/text()", committeeNode));
        String meetTimeStr = xmlh.getString("meettime/text()", committeeNode);
        committee.setMeetTime(meetTimeStr.isEmpty() ? null : new Time(meetTimeSDF.parse(meetTimeStr).getTime()));
        committee.setMeetAltWeek(xmlh.getString("meetaltweek/text()", committeeNode).trim().equalsIgnoreCase("Yes"));
        committee.setMeetAltWeekText(xmlh.getString("meetaltweektext/text()", committeeNode));
        Node committeeMembership = xmlh.getNode("membership", committeeNode);
        committee.setMembers(processCommitteeMembers(committeeMembership, committee));
        return committee;
    }

    private List<CommitteeMember> processCommitteeMembers(Node committeeMembership, Committee committee) throws XPathExpressionException {
        List<CommitteeMember> committeeMembers = new ArrayList<CommitteeMember>();
        NodeList committeeMembersNodes = committeeMembership.getChildNodes();
        for(int i=0; i<committeeMembersNodes.getLength(); i++){
            Node memberNode = committeeMembersNodes.item(i);
            if(memberNode.getNodeName().equals("member")) {
                String shortName = xmlh.getString("name/text()", memberNode);
                Member sessionMember;
                try {
                    sessionMember = memberService.getMemberByLBDCName(shortName, committee.getSession(), committee.getChamber());
                } catch (MemberNotFoundEx memberNotFoundEx) {
                    logger.error("Could not identify committee member " + shortName + " " + committee.getSession() + " " + committee.getChamber());
                    continue;
                }
                CommitteeMember committeeMember = new CommitteeMember();
                committeeMember.setSequenceNo(Integer.parseInt(xmlh.getString("@seqno", memberNode)));
                committeeMember.setMember(sessionMember);
                committeeMember.setMajority(xmlh.getString("memberlist/text()", memberNode).trim().equalsIgnoreCase("Majority"));
                String title = xmlh.getString("title/text()", memberNode).trim();
                if (title.equalsIgnoreCase("Chairperson")) {
                    committeeMember.setTitle(CommitteeMemberTitle.CHAIR_PERSON);
                } else if (title.equalsIgnoreCase("Vice-Chair")) {
                    committeeMember.setTitle(CommitteeMemberTitle.VICE_CHAIR);
                } else {
                    committeeMember.setTitle(CommitteeMemberTitle.MEMBER);
                }
                committeeMembers.add(committeeMember);
            }
        }
        return committeeMembers;
    }
}
