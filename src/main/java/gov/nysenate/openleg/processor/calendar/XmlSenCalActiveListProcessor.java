package gov.nysenate.openleg.processor.calendar;

import com.google.common.base.Strings;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.legdata.LegDataProcessor;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class XmlSenCalActiveListProcessor extends AbstractDataProcessor implements LegDataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(XmlSenCalActiveListProcessor.class);

    @Autowired protected XmlHelper xml;

    @PostConstruct
    public void init() {
        initBase();
    }

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.CALENDAR_ACTIVE;
    }

    @Override
    public void process(LegDataFragment legDataFragment) {
        logger.info("Processing Senate Calendar Active List... {}", legDataFragment.getFragmentId());
        LocalDateTime modifiedDate = legDataFragment.getPublishedDateTime();
        DataProcessUnit unit = createProcessUnit(legDataFragment);
        try {
            Node root = getXmlRoot(legDataFragment.getText());
            Node xmlCalendarActive = xml.getNode("sencalendaractive", root);
            Integer calendarNo = xml.getInteger("@no", xmlCalendarActive);
            Integer sessionYear = xml.getInteger("@sessyr", xmlCalendarActive);
            Integer year = xml.getInteger("@year", xmlCalendarActive);
            CalendarId calendarId = new CalendarId(calendarNo, year);
            final Calendar calendar = getOrCreateCalendar(calendarId, legDataFragment);
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

        checkIngestCache();
    }

    @Override
    public void postProcess() {
        flushCalendarUpdates();
    }

    @Override
    public void checkIngestCache() {
        if (!env.isLegDataBatchEnabled() || calendarIngestCache.exceedsCapacity()) {
            flushAllUpdates();
        }
    }
}