package gov.nysenate.openleg.service.calendar;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarActiveListEntry;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.service.base.SobiProcessor;
import gov.nysenate.openleg.service.sobi.AbstractSobiProcessor;
import gov.nysenate.openleg.util.DateHelper;
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
public class ActiveListProcessor extends AbstractSobiProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(ActiveListProcessor.class);

    @Autowired
    protected XmlHelper xml;

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.CALENDAR_ACTIVE;
    }

    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing Senate Calendar Active List... {}", sobiFragment.getFragmentId());
        Date modifiedDate = sobiFragment.getPublishedDateTime();
        try {
            Document doc = xml.parse(sobiFragment.getText());
            Node xmlCalendarActive = xml.getNode("SENATEDATA/sencalendaractive", doc);
            Integer calendarNo = xml.getInteger("@no", xmlCalendarActive);
            Integer sessionYear = xml.getInteger("@sessyr", xmlCalendarActive);
            Integer year = xml.getInteger("@year", xmlCalendarActive);
            CalendarId calendarId = new CalendarId(calendarNo, year);
            Calendar calendar = getOrCreateCalendar(calendarId, modifiedDate);
            calendar.setModifiedDate(modifiedDate);

            String action = xml.getString("@action", xmlCalendarActive);
            // So far the only case we've seen is a single supplemental per active list.
            NodeList xmlSequences = xml.getNodeList("supplemental/sequence", xmlCalendarActive);
            for (int j = 0; j < xmlSequences.getLength(); j++) {
                Node xmlSequence = xmlSequences.item(j);
                Integer id = xml.getInteger("@no", xmlSequence);
                if (action.equalsIgnoreCase("remove")) {
                    // Remove this sequence
                    calendar.removeActiveList(id);
                }
                else {
                    Date calDate = DateHelper.getDate(xml.getString("actcaldate/text()", xmlSequence));
                    Date releaseDateTime = DateHelper.getDateTime(xml.getString("releasedate/text()", xmlSequence)
                        + xml.getString("releasetime/text()", xmlSequence));
                    String notes = xml.getString("notes/text()", xmlSequence);

                    CalendarActiveList activeList = new CalendarActiveList(calendarId, id, notes, calDate, releaseDateTime);
                    NodeList xmlCalNos = xml.getNodeList("calnos/calno", xmlSequence);
                    for (int k = 0; k < xmlSequences.getLength(); k++) {
                        Node xmlCalNo = xmlCalNos.item(k);
                        Integer calNo = xml.getInteger("@no", xmlCalNo);
                        String billPrintNo = xml.getString("bill/@no", xmlCalNo);
                        BillId billId = new BillId(billPrintNo, sessionYear);
                        CalendarActiveListEntry entry = new CalendarActiveListEntry(calNo, billId);
                        activeList.addEntry(entry);
                    }
                    calendar.putActiveList(activeList);
                }
            }
            logger.debug("Saving {}", calendar);
            saveCalendar(calendar, sobiFragment);
        }
        catch (IOException | SAXException | XPathExpressionException ex) {
            logger.error("Failed to parse active list sobi", ex);
        }
    }
}