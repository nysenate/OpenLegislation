package gov.nysenate.openleg.processor.calendar;

import com.google.common.base.Strings;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ActiveListProcessor extends AbstractDataProcessor implements SobiProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(ActiveListProcessor.class);

    @Autowired protected XmlHelper xml;

    @PostConstruct
    public void init() {
        initBase();
    }

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.CALENDAR_ACTIVE;
    }

    @Override
    public void process(SobiFragment sobiFragment) {
        logger.info("Processing Senate Calendar Active List... {}", sobiFragment.getFragmentId());
        LocalDateTime modifiedDate = sobiFragment.getPublishedDateTime();
        DataProcessUnit unit = createProcessUnit(sobiFragment);
        try {
            Document doc = xml.parse(sobiFragment.getText());
            Node xmlCalendarActive = xml.getNode("SENATEDATA/sencalendaractive", doc);
            Integer calendarNo = xml.getInteger("@no", xmlCalendarActive);
            Integer sessionYear = xml.getInteger("@sessyr", xmlCalendarActive);
            Integer year = xml.getInteger("@year", xmlCalendarActive);
            CalendarId calendarId = new CalendarId(calendarNo, year);
            final Calendar calendar = getOrCreateCalendar(calendarId, sobiFragment);
            calendar.setModifiedDateTime(modifiedDate);

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
                    LocalDate calDate = DateUtils.getLrsLocalDate(xml.getString("actcaldate/text()", xmlSequence));
                    LocalDateTime releaseDateTime = DateUtils.getLrsDateTime(
                            xml.getString("releasedate/text()", xmlSequence) + xml.getString("releasetime/text()", xmlSequence));
                    String notes = xml.getString("notes/text()", xmlSequence);
                    CalendarActiveList activeList = new CalendarActiveList(calendarId, id, notes, calDate, releaseDateTime);
                    activeList.setModifiedDateTime(modifiedDate);
                    activeList.setPublishedDateTime(modifiedDate);

                    NodeList xmlCalNos = xml.getNodeList("calnos/calno", xmlSequence);
                    for (int k = 0; k < xmlCalNos.getLength(); k++) {
                        Node xmlCalNo = xmlCalNos.item(k);
                        Integer calNo = xml.getInteger("@no", xmlCalNo);
                        String billPrintNo = xml.getString("bill/@no", xmlCalNo);
                        if (!Strings.isNullOrEmpty(billPrintNo)) {
                            BillId billId = new BillId(billPrintNo, sessionYear);
                            CalendarEntry entry = new CalendarEntry(calNo, billId);
                            activeList.addEntry(entry);
                        }
                    }
                    calendar.putActiveList(activeList);
                }
            }
        }
        catch (IOException | SAXException | XPathExpressionException ex) {
            logger.error("Failed to parse active list sobi", ex);
            unit.addException("Failed to parse active list: " + ex.getMessage());
        }
        // Notify the data processor that a calendar active list fragment has finished processing
        postDataUnitEvent(unit);

        if (!env.isSobiBatchEnabled() || calendarIngestCache.exceedsCapacity()) {
            flushCalendarUpdates();
        }
    }

    @Override
    public void postProcess() {
        flushCalendarUpdates();
    }
}