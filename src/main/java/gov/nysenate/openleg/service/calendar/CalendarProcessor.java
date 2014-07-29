package gov.nysenate.openleg.service.calendar;

import com.google.common.base.Strings;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
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
public class CalendarProcessor extends AbstractSobiProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(CalendarProcessor.class);

    @Autowired
    protected XmlHelper xml;

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.CALENDAR;
    }

    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing Senate Calendar... {}", sobiFragment.getFragmentId());
        Date modifiedDate = sobiFragment.getPublishedDateTime();
        try {
            Document doc = xml.parse(sobiFragment.getText());
            Node xmlCalendar = xml.getNode("SENATEDATA/sencalendar", doc);
            Integer calendarNo = xml.getInteger("@no", xmlCalendar);
            Integer sessionYear = xml.getInteger("@sessyr", xmlCalendar);
            Integer year = xml.getInteger("@year", xmlCalendar);
            CalendarId calendarId = new CalendarId(calendarNo, year);
            Calendar calendar = getOrCreateCalendar(calendarId, modifiedDate);
            calendar.setModifiedDate(modifiedDate);

            // Actions apply to supplemental and not the whole calendar
            String action = xml.getString("@action", xmlCalendar);

            NodeList xmlSupplementals = xml.getNodeList("supplemental", xmlCalendar);
            for (int i = 0; i < xmlSupplementals.getLength(); i++) {
                Node xmlSupplemental = xmlSupplementals.item(i);
                String supId = xml.getString("@id", xmlSupplemental);
                if (action.equalsIgnoreCase("remove")) {
                    calendar.removeSupplemental(supId);
                }
                else {
                    // Replace this supplemental
                    Date calDate = DateHelper.getLrsDate(xml.getString("caldate/text()", xmlSupplemental));
                    Date releaseDateTime = DateHelper.getLrsDateTime(xml.getString("releasedate/text()", xmlSupplemental)
                            + xml.getString("releasetime/text()", xmlSupplemental));

                    CalendarSupplemental supplemental = new CalendarSupplemental(calendarId, supId, calDate, releaseDateTime);
                    NodeList xmlSections = xml.getNodeList("sections/section", xmlSupplemental);
                    for (int j = 0; j < xmlSections.getLength(); j++) {
                        Node xmlSection = xmlSections.item(j);
                        Integer cd = xml.getInteger("@cd", xmlSection);
                        CalendarSectionType sectionType = CalendarSectionType.valueOfCode(cd);

                        NodeList xmlCalNos = xml.getNodeList("calnos/calno", xmlSection);
                        for (int k = 0; k < xmlCalNos.getLength(); k++) {
                            Node xmlCalNo = xmlCalNos.item(k);
                            Integer no = xml.getInteger("@no", xmlCalNo);
                            String billPrintNo = xml.getString("bill/@no", xmlCalNo);
                            BillId billId = new BillId(billPrintNo, sessionYear);
                            boolean billHigh = xml.getString("bill/@high", xmlCalNo).equals("true");
                            String subBillPrintNo = xml.getString("subbill/@no", xmlCalNo);
                            BillId subBillId = (!Strings.isNullOrEmpty(subBillPrintNo))
                                                ? new BillId(subBillPrintNo, sessionYear) : null;
                            CalendarSupplementalEntry entry =
                                new CalendarSupplementalEntry(no, sectionType, billId, subBillId, billHigh);
                            supplemental.addEntry(entry);
                        }
                    }
                    calendar.putSupplemental(supplemental);
                }
            }
            logger.debug("Saving {}", calendar);
            calendarDataService.saveCalendar(calendar, sobiFragment);
        }
        catch (IOException | SAXException | XPathExpressionException ex) {
            logger.error("Failed to parse calendar sobi {}", sobiFragment.getFragmentId(), ex);
        }
    }
}