package gov.nysenate.openleg.processors.committee;

import gov.nysenate.openleg.common.util.XmlHelper;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.Committee;
import gov.nysenate.openleg.legislation.committee.CommitteeMember;
import gov.nysenate.openleg.legislation.committee.CommitteeMemberTitle;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.processors.AbstractLegDataProcessor;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlSenCommProcessor extends AbstractLegDataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(XmlSenCommProcessor.class);

    private static final DateTimeFormatter meetTimeSDF = DateTimeFormatter.ofPattern("hh:mm a");

    protected final MemberService memberService;

    @Autowired
    public XmlSenCommProcessor(MemberService memberService, XmlHelper xml) {
        this.memberService = memberService;
        this.xmlHelper = xml;
    }

    /** {@inheritDoc  */
    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.COMMITTEE;
    }

    /** {@inheritDoc  */
    @Override
    public void process(LegDataFragment legDataFragment) {
        logger.info("Called committee processor");
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        String xmlString = legDataFragment.getText();
        try {
            Node root = getXmlRoot(xmlString);
            Node committeeRoot = xmlHelper.getNode("sencommmem", root);
            SessionYear sessionYear = new SessionYear(Integer.parseInt(xmlHelper.getString("@sessyr", committeeRoot)));
            int year = Integer.parseInt(xmlHelper.getString("@year", committeeRoot));
            Chamber chamber = Chamber.SENATE;
            logger.info("Processing " + chamber + "committees for s" + sessionYear + " y" + year + "\t" +
                    legDataFragment.getPublishedDateTime());

            committeeRoot = xmlHelper.getNode("committees", committeeRoot);
            NodeList committeeNodes = committeeRoot.getChildNodes();
            for (int i = 0; i < committeeNodes.getLength(); i++) {
                Node committeeNode = committeeNodes.item(i);
                if (committeeNode.getNodeName().equals("committee")) {
                    Committee committee = new Committee();
                    committee.setSession(sessionYear);
                    committee.setPublishedDateTime(legDataFragment.getPublishedDateTime());
                    committee.setChamber(chamber);
                    processCommittee(committeeNode, committee);
                    committee.setModifiedDateTime(legDataFragment.getPublishedDateTime());
                    committeeDataService.saveCommittee(committee, legDataFragment);
                }
            }
        } catch (Exception e) {
            unit.addException("XML Sen Comm parsing error", e);
            RuntimeException rethrowEx;
            if (e instanceof RuntimeException) {
                rethrowEx = (RuntimeException) e;
            } else {
                rethrowEx = new ParseError("Error occurred while parsing committee data", e);
            }
            throw rethrowEx;
        } finally {
            postDataUnitEvent(unit);
            checkIngestCache();
        }
    }

    @Override
    public void postProcess() {}

    @Override
    public void checkIngestCache() {
        if (!env.isLegDataBatchEnabled()) {
            flushAllUpdates();
        }
    }

    /* --- Internal Methods --- */

    private Committee processCommittee(Node committeeNode, Committee committee) throws XPathExpressionException {
        committee.setName(xmlHelper.getString("name/text()", committeeNode));
        committee.setLocation(xmlHelper.getString("location/text()", committeeNode));

        String meetDay = xmlHelper.getString("meetday/text()", committeeNode);
        committee.setMeetDay(StringUtils.isNotEmpty(meetDay) ? DayOfWeek.valueOf(meetDay.toUpperCase()) : null);

        String meetTimeStr = xmlHelper.getString("meettime/text()", committeeNode);
        committee.setMeetTime(StringUtils.isNotEmpty(meetTimeStr) ? LocalTime.parse(meetTimeStr, meetTimeSDF) : null);

        committee.setMeetAltWeek(
                xmlHelper.getString("meetaltweek/text()", committeeNode)
                        .trim()
                        .equalsIgnoreCase("Yes"));
        committee.setMeetAltWeekText(xmlHelper.getString("meetaltweektext/text()", committeeNode));

        Node committeeMembership = xmlHelper.getNode("membership", committeeNode);
        committee.setMembers(processCommitteeMembers(committeeMembership, committee));
        return committee;
    }

    private List<CommitteeMember> processCommitteeMembers(Node committeeMembership, Committee committee)
            throws XPathExpressionException {
        List<CommitteeMember> committeeMembers = new ArrayList<CommitteeMember>();
        NodeList committeeMembersNodes = committeeMembership.getChildNodes();

        for (int i = 0; i < committeeMembersNodes.getLength(); i++) {

            Node memberNode = committeeMembersNodes.item(i);
            if (memberNode.getNodeName().equals("member")) {
                String shortName = xmlHelper.getString("name/text()", memberNode);
                SessionMember sessionMember = memberService.getSessionMemberByShortName(
                        shortName, committee.getSession(), committee.getChamber());

                CommitteeMember committeeMember = new CommitteeMember();
                committeeMember.setSequenceNo(Integer.parseInt(xmlHelper.getString("@seqno", memberNode)));
                committeeMember.setSessionMember(sessionMember);
                committeeMember.setMajority(
                        xmlHelper.getString("memberlist/text()", memberNode)
                                .trim()
                                .equalsIgnoreCase("Majority"));

                String title = xmlHelper.getString("title/text()", memberNode).trim();
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
