package gov.nysenate.openleg.processors.sobi.calendar;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarActiveListEntry;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.processors.sobi.SobiProcessor;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.DateHelper;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Date;

public class SobiActiveListProcessor extends SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(SobiActiveListProcessor.class);

    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing Senate Calendar...");
        Date modifiedDate = sobiFragment.getPublishedDateTime();
        XmlHelper xml = Application.getXmlHelper();
        try {
            Document doc = xml.parse(sobiFragment.getText());
            Node xmlCalendarActive = null;
            xmlCalendarActive = xml.getNode("SENATEDATA/sencalendaractive", doc);
            Integer calendarNo = xml.getInteger("@no", xmlCalendarActive);
            Integer sessionYear = xml.getInteger("@sessyr", xmlCalendarActive);
            Integer year = xml.getInteger("@year", xmlCalendarActive);
            CalendarId calendarId = new CalendarId(calendarNo, year);
            Calendar calendar = getOrCreateCalendar(calendarId, modifiedDate);
            calendar.setModifiedDate(modifiedDate);

            // Actions apply to supplemental and not the whole calendar
            String action = xml.getString("@action", xmlCalendarActive);

            NodeList xmlSequences = xml.getNodeList("supplemental/sequence", xmlCalendarActive);
            for (int j = 0; j < xmlSequences.getLength(); j++) {
                Node xmlSequence = xmlSequences.item(j);
                Integer id = xml.getInteger("@id", xmlSequence);
                if (action.equalsIgnoreCase("remove")) {
                    // Remove this supplemental
                    calendar.removeActiveList(id);
                }
                else {
                    Date calDate = DateHelper.getDate(xml.getString("actcaldate/text()", xmlSequence));
                    Date releaseDateTime = DateHelper.getDate(xml.getString("releasedate/text()", xmlSequence)
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