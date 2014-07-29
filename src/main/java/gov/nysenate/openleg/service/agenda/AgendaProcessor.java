package gov.nysenate.openleg.service.agenda;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.service.base.SobiProcessor;
import gov.nysenate.openleg.service.sobi.AbstractSobiProcessor;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Date;

@Service
public class AgendaProcessor extends AbstractSobiProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaProcessor.class);

    @Autowired
    XmlHelper xml;

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.AGENDA;
    }

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

            String action = xml.getString("@action", xmlAgenda);
            if (action.equalsIgnoreCase("remove")) {
                logger.info("Removing agenda {}", agendaId);
                agendaDataService.deleteAgenda(agendaId);
            }

        }
        catch (IOException | SAXException | XPathExpressionException ex) {
            logger.error("Failed to ", ex);
        }
    }
}
