package gov.nysenate.openleg.processor.agenda;

import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.SobiProcessor;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.util.DateHelper;
import gov.nysenate.openleg.util.OutputHelper;
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
import java.util.Date;

@Service
public class AgendaProcessor extends AbstractDataProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaProcessor.class);

    @Autowired
    private XmlHelper xml;

    /** {@inheritDoc} */
    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.AGENDA;
    }

    /** {@inheritDoc} */
    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing Agenda...");
        Date modifiedDate = sobiFragment.getPublishedDateTime();
        try {
            Document doc = xml.parse(sobiFragment.getText());
            Node xmlAgenda = xml.getNode("SENATEDATA/senagenda", doc);
            Integer agendaNo = xml.getInteger("@no", xmlAgenda);
            Integer year = xml.getInteger("@year", xmlAgenda);
            AgendaId agendaId = new AgendaId(agendaNo, year);
            Agenda agenda = getOrCreateAgenda(agendaId, modifiedDate);
            agenda.setModifiedDate(modifiedDate);
            String action = xml.getString("@action", xmlAgenda);
            // Remove the Agenda if the action = 'remove'
            if (action.equalsIgnoreCase("remove")) {
                logger.info("Removing {}", agendaId);
                agendaDataService.deleteAgenda(agendaId);
            }
            // Otherwise update/insert any associated addenda.
            else if (action.equalsIgnoreCase("replace")) {
                NodeList xmlAddendums = xml.getNodeList("addendum", xmlAgenda);

                // The initial state as well as updates to an Agenda are sent via addenda which
                // have an id associated with them.
                for (int i = 0; i < xmlAddendums.getLength(); i++) {
                    Node xmlAddendum = xmlAddendums.item(i);
                    String addendumId = xml.getString("@id", xmlAddendum);
                    logger.info("Updating Addendum {} for {}", addendumId, agenda);
                    Date weekOf = DateHelper.getLrsDate(xml.getString("weekof/text()", xmlAddendum));
                    Date pubDateTime = DateHelper.getLrsDateTime(xml.getString("pubdate/text()", xmlAddendum) +
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
                        String notes = xml.getString("notes/text()", xmlCommittee);

                        // The meeting date/time may not be entirely accurate because often the data is expressed
                        // through the notes field, especially for multiple ad-hoc meetings during the end of session.
                        Date meetDateTime = DateHelper.getLrsDateTime(xml.getString("meetdate/text()", xmlCommittee) +
                            xml.getString("meettime/text()", xmlCommittee));

                        // Construct the committee info model using the parsed data.
                        AgendaInfoCommittee infoCommittee =
                            new AgendaInfoCommittee(committeeId, chair, location, notes, meetDateTime);
                        // A committee will have a collection of bills that are up for consideration.
                        NodeList xmlBills = xml.getNodeList("bills/bill", xmlCommittee);
                        for (int k = 0; k < xmlBills.getLength(); k++) {
                            Node xmlBill = xmlBills.item(k);
                            String printNo = xml.getString("@no", xmlBill);
                            String message = xml.getString("message/text()", xmlBill);
                            BillId billId = new BillId(printNo, DateHelper.resolveSession(year));
                            AgendaInfoCommitteeItem item = new AgendaInfoCommitteeItem(billId, message);
                            infoCommittee.addCommitteeItem(item);
                        }
                        addendum.putCommittee(infoCommittee);
                    }
                    agenda.putAgendaInfoAddendum(addendum);
                }
                logger.info("Saving {}", agenda);
                agendaDataService.saveAgenda(agenda, sobiFragment);
                logger.info("AGENDA {}", OutputHelper.toJson(agenda));
            }
        }
        catch (IOException | SAXException | XPathExpressionException ex) {
            logger.error("Failed to parse agenda fragment {}", sobiFragment.getFragmentId(), ex);
        }
    }
}
