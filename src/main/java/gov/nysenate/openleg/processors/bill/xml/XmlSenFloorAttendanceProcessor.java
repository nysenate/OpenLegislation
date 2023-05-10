package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.attendance.SqlSenateVoteAttendanceDao;
import gov.nysenate.openleg.legislation.attendance.VoteId;
import gov.nysenate.openleg.legislation.bill.BillVoteType;
import gov.nysenate.openleg.legislation.attendance.SenateVoteAttendance;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.processors.AbstractLegDataProcessor;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class XmlSenFloorAttendanceProcessor extends AbstractLegDataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlSenFloorAttendanceProcessor.class);

    @Autowired private SqlSenateVoteAttendanceDao attendanceDao;

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.SENFLATD;
    }

    @Override
    public void process(LegDataFragment fragment) {
        logger.info("Processing {} ", fragment.getFragmentId());

        DataProcessUnit unit = createProcessUnit(fragment);
        try {
            LocalDateTime publishedDateTime = fragment.getPublishedDateTime();
            Document doc = xmlHelper.parse(fragment.getText());
            Node root = xmlHelper.getNode("senfloorattd", doc);
            String action = xmlHelper.getString("@action", root);

            if (action.equalsIgnoreCase("remove")) {
                // TODO no remote members.
            } else if (action.equalsIgnoreCase("replace")) {
                int sessyr = xmlHelper.getInteger("@sessyr", root);
                String dateOfVote = xmlHelper.getString("@dateofvote", root).trim();
                int dateSeqNo = xmlHelper.getInteger("@date_seqno", root);
                String remoteMembers = xmlHelper.getString("remote_members", root).trim();
                int year = xmlHelper.getInteger("@year", root);

                LocalDate voteDate = LocalDate.from(voteDateFormat.parse(dateOfVote));
                SessionYear sessionYear = SessionYear.of(sessyr);
                List<SessionMember> remoteSessionMembers = getSessionMember(
                        remoteMembers, sessionYear, Chamber.SENATE, fragment.getFragmentId());

                VoteId voteId = new VoteId(voteDate, dateSeqNo, BillVoteType.FLOOR);
                SenateVoteAttendance attendance = new SenateVoteAttendance(voteId, remoteSessionMembers);
                attendance.setSession(sessionYear);
                attendance.setYear(year);
                attendance.setPublishedDateTime(publishedDateTime);
                attendance.setModifiedDateTime(LocalDateTime.now());
                attendanceDao.saveAttendance(attendance, fragment.getFragmentId());
            }
            else {
                logger.warn("Unknown action found in xml {}", fragment.getFragmentId());
            }

        }
        catch (IOException | SAXException | XPathExpressionException | NullPointerException e) {
            unit.addException("XML Sen Fl Atd parsing error", e);
            throw new ParseError("Error While Parsing XmlSenFlAtdProcessor", e);
        }
        finally {
            postDataUnitEvent(unit);
            checkIngestCache();
        }
    }
}
