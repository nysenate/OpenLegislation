package gov.nysenate.openleg.processors.sobi.calendar;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.entity.Person;
import gov.nysenate.openleg.model.sobi.SobiBlock;
import gov.nysenate.openleg.processors.sobi.bill.SobiBillProcessor;
import gov.nysenate.openleg.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Deprecated
public class SencalendarProcessor
{
    public Calendar getOrCreateCalendar(Integer calno, Integer sessYr, Integer year, Storage storage, Date date)
    {
        Calendar calendar = null; //FIXME new Calendar(calno, sessYr, year);
        calendar.setPublishDate(date);

        String key = storage.key(calendar);
        Calendar oldCalendar = (Calendar)storage.get(key, Calendar.class);
        if (oldCalendar != null) {
            calendar = oldCalendar;
        }
        return calendar;
    }

    public void processSencalendar(File file, Storage storage) throws SAXException, IOException, XPathExpressionException
    {
        // TODO: We need a better default here
        Date modifiedDate = DateHelper.getFileDate(file.getName());
        ChangeLogger.setContext(file, modifiedDate);

        XmlHelper xml = Application.getXmlHelper();
        Document doc = xml.parse(file);
        Node xmlCalendar = xml.getNode("SENATEDATA/sencalendar", doc);
        Integer calendarNo = xml.getInteger("@no", xmlCalendar);
        Integer sessYr = xml.getInteger("@sessyr", xmlCalendar);
        Integer year = xml.getInteger("@year", xmlCalendar);
        Calendar calendar = getOrCreateCalendar(calendarNo, sessYr, year, storage, modifiedDate);
        calendar.setModifiedDate(modifiedDate);
        //calendar.addDataSource(file.getName());

        // Actions apply to supplemental and not the whole calendar
        String action = xml.getString("@action", xmlCalendar);

        NodeList xmlSupplementals = xml.getNodeList("supplemental", xmlCalendar);
        for (int i=0; i < xmlSupplementals.getLength(); i++) {
            Node xmlSupplemental = xmlSupplementals.item(i);
            String id = xml.getString("@id", xmlSupplemental);
            if (action.equalsIgnoreCase("remove")) {
                calendar.removeSupplemental(id);
                // ChangeLogger.delete( ??? );
            }
            else {
                // Replace this supplemental
                Date calDate = DateHelper.getDate(xml.getString("caldate/text()", xmlSupplemental));
                Date releaseDateTime = DateHelper.getDateTime(xml.getString("releasedate/text()", xmlSupplemental)+xml.getString("releasetime/text()", xmlSupplemental));

          //      CalendarSupplemental supplemental = new CalendarSupplemental(id, calDate, releaseDateTime);
                NodeList xmlSections = xml.getNodeList("sections/section", xmlCalendar);
                for (int j=0; j < xmlSections.getLength(); j++) {
                    Node xmlSection = xmlSections.item(j);
                    String name = xml.getString("@name", xmlSection);
                    String type = xml.getString("@type", xmlSection);
                    Integer cd = xml.getInteger("@cd", xmlSection);
          // FIXME          CalendarSupplementalSection section = new CalendarSupplementalSection(name, type, cd);

                    NodeList xmlCalNos = xml.getNodeList("calnos/calno", xmlSection);
                    for (int k=0; k < xmlCalNos.getLength(); k++) {
                        Node xmlCalNo = xmlCalNos.item(k);
                        Integer no = xml.getInteger("@no", xmlCalNo);
                        String billno = xml.getString("bill/@no", xmlCalNo);
                        String billAmendment = billno.matches("[A-Z]$") ? billno.substring(billno.length()-1) : "";
                        String billSponsor = xml.getString("sponsor/text()", xmlCalNo);
                        Bill bill = this.getOrCreateBill(storage, billno, billAmendment, year, billSponsor, modifiedDate);
                        Boolean billHigh = xml.getString("bill/@high", xmlCalNo).equals("true");
                        String subBillNo = xml.getString("subbill/@no", xmlCalNo);
                        String subBillAmendment = subBillNo.matches("[A-Z]$") ? subBillNo.substring(subBillNo.length()-1) : "";
                        String subBillSponsor = xml.getString("subsponsor/text()", xmlCalNo);
                        Bill subBill = this.getOrCreateBill(storage, subBillNo, subBillAmendment, year, subBillSponsor, modifiedDate);
                // FIXME        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(no, bill, billAmendment, billHigh, subBill, subBillAmendment);
                        // FIXME        section.addEntry(entry);
                    }

                    // FIXME supplemental.putSection(section);
                }

          //      calendar.putSupplemental(supplemental);
            }
        }

        storage.set(calendar);
    }

    public void processSencalendarActive(File file, Storage storage) throws SAXException, IOException, XPathExpressionException
    {
        // TODO: We need a better default here
        Date modifiedDate = DateHelper.getFileDate(file.getName());
        ChangeLogger.setContext(file, modifiedDate);

        XmlHelper xml = Application.getXmlHelper();
        Document doc = xml.parse(file);
        Node xmlCalendarActive = xml.getNode("SENATEDATA/sencalendaractive", doc);
        Integer calendarNo = xml.getInteger("@no", xmlCalendarActive);
        Integer sessYr = xml.getInteger("@sessyr", xmlCalendarActive);
        Integer year = xml.getInteger("@year", xmlCalendarActive);
        Calendar calendar = getOrCreateCalendar(calendarNo, sessYr, year, storage, modifiedDate);
        calendar.setModifiedDate(modifiedDate);
        //calendar.addDataSource(file.getName());

        // Actions apply to supplemental and not the whole calendar
        String action = xml.getString("@action", xmlCalendarActive);

        NodeList xmlSequences = xml.getNodeList("supplemental/sequence", xmlCalendarActive);
        for (int j=0; j < xmlSequences.getLength(); j++) {
            Node xmlSequence = xmlSequences.item(j);
            Integer id = xml.getInteger("@id", xmlSequence);
            if (action.equalsIgnoreCase("remove")) {
                // Remove this supplemental
                calendar.removeActiveList(id);
            }
            else {
                Date calDate = DateHelper.getDate(xml.getString("actcaldate/text()", xmlSequence));
                Date releaseDateTime = DateHelper.getDate(xml.getString("releasedate/text()", xmlSequence)+xml.getString("releasetime/text()", xmlSequence));
                String notes = xml.getString("notes/text()", xmlSequence);

              //  CalendarActiveList activeList = new CalendarActiveList(id, notes, calDate, releaseDateTime);
                NodeList xmlCalNos = xml.getNodeList("calnos/calno", xmlSequence);
                for (int k=0; k < xmlSequences.getLength(); k++) {
                    Node xmlCalNo = xmlCalNos.item(k);
                    Integer calno = xml.getInteger("@no", xmlCalNo);
                    String billno = xml.getString("bill/@no", xmlCalNo);
                    String billAmendment = billno.matches("[A-Z]$") ? billno.substring(billno.length()-1) : "";
                    Bill bill = this.getOrCreateBill(storage, billno, billAmendment, year, "", modifiedDate);
                  //  CalendarActiveListEntry entry = new CalendarActiveListEntry(calno, bill, billAmendment);
                   // activeList.addEntry(entry);
                }

          //      calendar.putActiveList(activeList);
            }
        }

        storage.set(calendar);
    }

    private Bill getOrCreateBill(Storage storage, String billId, String billAmendment, int year, String sponsorName, Date modifiedDate) {
        // This is a crappy situation, all bills on calendars should already exist but sometimes they won't.
        // This almost exclusively because we are missing sobi files. It shouldn't happen in production but
        // does frequently in development.
        SobiBillProcessor processor = new SobiBillProcessor();
        SobiBlock mockBlock = new SobiBlock(year+billId+(billId.matches("[A-Z]$") ? "" : " ")+1+"     ");
        Bill bill = null; /**FIXME processor.getOrCreateBaseBill(mockBlock, modifiedDate, storage); */

        // Active lists don't have sponsor listed, so check first!
        if (sponsorName != null) {
            String[] sponsors = sponsorName.trim().split(",");
            /** TODO: FIXME bill.setSponsor(new Person(sponsors[0].trim())); */

            // Other sponsors are removed when a calendar/agenda is resent without
            // The other sponsor included in the sponsors list.
            ArrayList<Person> otherSponsors = new ArrayList<Person>();
            for (int i = 1; i < sponsors.length; i++) {
                otherSponsors.add(new Person(sponsors[i].trim()));
            }

//            if (!bill.getAdditionalSponsors().equals(otherSponsors)) {
//                bill.setAdditionalSponsors(otherSponsors);
//                /** FIXME processor.saveBill(bill, billAmendment, storage); */
//            }
        }

        // It must be published if it is on the calendar
        BillAmendment amendment = bill.getAmendment(billAmendment);
        if (!amendment.isPublished()) {
            amendment.setPublishDate(modifiedDate);
            /** FIXME processor.saveBill(bill, billAmendment, storage); */
        }

        return bill;
    }
}
