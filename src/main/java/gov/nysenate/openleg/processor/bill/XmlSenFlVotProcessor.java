package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.legdata.LegDataProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class XmlSenFlVotProcessor extends AbstractDataProcessor implements LegDataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlSenFlVotProcessor.class);

    /** Date format found in SobiBlock[V] vote memo blocks. e.g. 02/05/2013 */
    protected static final DateTimeFormatter voteDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Autowired
    private XmlHelper xmlHelper;

    public XmlSenFlVotProcessor() {}

    @Override
    public void init() {
        initBase();
    }

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.SENFLVOTE;
    }

    @Override
    public void process(LegDataFragment legDataFragment) {
        logger.info("Processing SenFlVot...");
        logger.info("Processing " + legDataFragment.getFragmentId() + " (xml file).");
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        try {
            LocalDateTime date = legDataFragment.getPublishedDateTime();
            final Document doc = xmlHelper.parse(legDataFragment.getText());
            final Node senFloorVote = xmlHelper.getNode("senfloorvote", doc);
            //File Print number
            final Integer sessyr = xmlHelper.getInteger("@sessyr", senFloorVote);
            final Integer seqno = xmlHelper.getInteger("@date_seqno", senFloorVote);
            final String printNo = xmlHelper.getString("@no", senFloorVote).trim();
            BillId billId = new BillId(printNo, sessyr);
            String version = billId.getVersion().toString();
            final String action = xmlHelper.getString("@action", senFloorVote).trim();
            final String dateofvote = xmlHelper.getString("@dateofvote", senFloorVote).trim();

            Bill baseBill = getOrCreateBaseBill(billId, legDataFragment);
            BillAmendment billAmendment;
            if (!baseBill.hasAmendment( Version.of(version) )) {
                billAmendment = new BillAmendment(baseBill.getBaseBillId(), Version.of(version));
                baseBill.addAmendment(billAmendment);
            }
            else {
                billAmendment = baseBill.getAmendment(Version.of(version));
            }

            LocalDate voteDate;
            BillVote vote;
            try {
                voteDate = LocalDate.from(voteDateFormat.parse(dateofvote));
                vote = new BillVote(billId, voteDate, BillVoteType.FLOOR, seqno);
                vote.setModifiedDateTime(date);
                vote.setPublishedDateTime(date);

                if (action.equals("remove")) {
                    removeCase(billAmendment, vote);
                    return;
                }
            }
            catch (DateTimeParseException ex) {
                unit.addException("XML Sen Fl Vot parsing error", ex);
                throw new ParseError("voteDateFormat not matched: " + ex);
            }

            //loop through xml vote list
            NodeList memberVotes = doc.getElementsByTagName("member");
            for (int index = 0; index < memberVotes.getLength(); index++) {
                Node member = memberVotes.item(index);
                final String howMemberVoted = xmlHelper.getString("vote",member);
                final String shortName = xmlHelper.getString("name",member);

                BillVoteCode voteCode;
                try {
                    voteCode = BillVoteCode.getValue(howMemberVoted);
                }
                catch (IllegalArgumentException ex) {
                    unit.addException("XML Sen Fl Vot parsing error", ex);
                    throw new ParseError("No vote code mapping for " + howMemberVoted);
                }
                // Only senator votes are received. A valid member mapping is required.
                SessionMember voter = getMemberFromShortName(shortName, billId.getSession(), Chamber.SENATE);
                vote.addMemberVote(voteCode, voter);
            }
            billAmendment.updateVote(vote);

            baseBill.setModifiedDateTime(legDataFragment.getPublishedDateTime());
            billIngestCache.set(baseBill.getBaseBillId(), baseBill, legDataFragment);
        }

        catch (IOException | SAXException | XPathExpressionException | NullPointerException e) {
            unit.addException("XML Sen Fl Vot parsing error", e);
            throw new ParseError("Error While Parsing XmlSenFlVotProcessor", e);
        }

        finally {
            postDataUnitEvent(unit);
            checkIngestCache();
        }
    }

    @Override
    public void checkIngestCache() {
        if (!env.isLegDataBatchEnabled() || billIngestCache.exceedsCapacity()) {
            flushBillUpdates();
        }
    }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    private void removeCase(BillAmendment billAmendment, BillVote vote) {
        billAmendment.getVotesMap().remove(vote.getVoteId());
    }

}
