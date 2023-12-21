package gov.nysenate.openleg.processors.agenda;

import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.common.util.XmlHelper;
import gov.nysenate.openleg.legislation.agenda.*;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.processors.AbstractLegDataProcessor;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.bill.SourceType;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class XmlSenAgenProcessor extends AbstractLegDataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(XmlSenAgenProcessor.class);

    @Autowired private XmlHelper xml;

    /** {@inheritDoc} */
    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.AGENDA;
    }

    /** {@inheritDoc} */
    @Override
    public void process(LegDataFragment legDataFragment) {
        logger.info("Processing Agenda...");
        LocalDateTime modifiedDate = legDataFragment.getPublishedDateTime();
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        try {
            Node root = getXmlRoot(legDataFragment.getText().replaceAll("\u001a",""));
            Node xmlAgenda = xml.getNode("senagenda", root);
            Integer agendaNo = xml.getInteger("@no", xmlAgenda);
            Integer year = xml.getInteger("@year", xmlAgenda);
            AgendaId agendaId = new AgendaId(agendaNo, year);
            String action = xml.getString("@action", xmlAgenda);
            // Remove the Agenda if the action = 'remove'
            if (action.equalsIgnoreCase("remove")) {
                logger.info("Removing {}", agendaId);
                agendaDataService.deleteAgenda(agendaId);
            }
            // Otherwise update/insert any associated addenda.
            else if (action.equalsIgnoreCase("replace")) {
                Agenda agenda = getOrCreateAgenda(agendaId, legDataFragment);
                agenda.setModifiedDateTime(modifiedDate);
                NodeList xmlAddendums = xml.getNodeList("addendum", xmlAgenda);

                // The initial state as well as updates to an Agenda are sent via addenda which
                // have an id associated with them.
                for (int i = 0; i < xmlAddendums.getLength(); i++) {
                    Node xmlAddendum = xmlAddendums.item(i);
                    String addendumId = xml.getString("@id", xmlAddendum);
                    logger.info("Updating Addendum {} for {}", addendumId, agenda);
                    LocalDate weekOf = DateUtils.getLrsLocalDate(xml.getString("weekof/text()", xmlAddendum));
                    LocalDateTime pubDateTime = DateUtils.getLrsDateTime(xml.getString("pubdate/text()", xmlAddendum) +
                            xml.getString("pubtime/text()", xmlAddendum));
                    AgendaInfoAddendum addendum = new AgendaInfoAddendum(agendaId, addendumId, weekOf, pubDateTime);
                    // Each addendum will contain a section for each committee that has an update.
                    NodeList xmlCommittees = xml.getNodeList("committees/committee", xmlAddendum);
                    for (int j = 0; j < xmlCommittees.getLength(); j++) {
                        Node xmlCommittee = xmlCommittees.item(j);
                        String name = xml.getString("name/text()", xmlCommittee);
                        // We only get agendas for senate committees. This may or may not change in the future.
                        CommitteeId committeeId = new CommitteeId(Chamber.SENATE, name);
                        String chair = xml.getString("chair/text()", xmlCommittee);
                        String location = xml.getString("location/text()", xmlCommittee);

                        // The notes are very important because they will contain any vital information such
                        // as if the meeting is off the floor (ad-hoc) or if there was a change to the meeting time.
                        String notes = xml.getString("notes/text()", xmlCommittee)
                                .replaceAll("╣","§")
                                .replaceAll(" +"," ");

                        // Format specific replacements
                        if (legDataFragment.getParentLegDataFile().getSourceType() == SourceType.XML) {
                            notes = notes.replaceAll("\\|", "\n");
                        } else {
                            notes = notes.replaceAll("\\\\n", "\n");
                        }


                        // The meeting date/time may not be entirely accurate because often the data is expressed
                        // through the notes field, especially for multiple ad-hoc meetings during the end of session.
                        LocalDateTime meetDateTime = DateUtils.getLrsDateTime(
                                xml.getString("meetdate/text()", xmlCommittee) + xml.getString("meettime/text()", xmlCommittee));

                        // Construct the committee info model using the parsed data.
                        AgendaInfoCommittee infoCommittee =
                            new AgendaInfoCommittee(committeeId, agendaId, Version.of(addendumId), chair, location, notes, meetDateTime);
                        // A committee will have a collection of bills that are up for consideration.
                        NodeList xmlBills = xml.getNodeList("bills/bill", xmlCommittee);
                        for (int k = 0; k < xmlBills.getLength(); k++) {
                            Node xmlBill = xmlBills.item(k);
                            String printNo = xml.getString("@no", xmlBill);
                            String message = xml.getString("message/text()", xmlBill);
                            BillId billId = new BillId(printNo, DateUtils.resolveSession(year));
                            AgendaInfoCommitteeItem item = new AgendaInfoCommitteeItem(billId, message);
                            infoCommittee.addCommitteeItem(item);
                        }
                        addendum.putCommittee(infoCommittee);
                    }
                    agenda.putAgendaInfoAddendum(addendum);
                }
            }
        }
        catch (IOException | SAXException | XPathExpressionException ex) {
            logger.error("Failed to parse agenda fragment {}", legDataFragment.getFragmentId(), ex);
            unit.addException("Failed to parse Agenda: " + ex.getMessage());
        }
        // Notify the data processor that an agenda fragment has finished processing
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