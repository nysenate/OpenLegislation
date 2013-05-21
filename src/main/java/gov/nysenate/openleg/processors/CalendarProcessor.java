package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.util.ChangeLogger;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.xml.calendar.XMLCalno;
import gov.nysenate.openleg.xml.calendar.XMLSENATEDATA;
import gov.nysenate.openleg.xml.calendar.XMLSection;
import gov.nysenate.openleg.xml.calendar.XMLSencalendar;
import gov.nysenate.openleg.xml.calendar.XMLSencalendaractive;
import gov.nysenate.openleg.xml.calendar.XMLSequence;
import gov.nysenate.openleg.xml.calendar.XMLSupplemental;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class CalendarProcessor implements OpenLegConstants {

    private final Logger logger;
    private Object removeObject = null;
    private String removeObjectId = null;

    public static SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    public CalendarProcessor() {
        logger = Logger.getLogger(this.getClass());
    }

    public void process(File file, Storage storage) throws IOException, JAXBException {
        // TODO: Definitely needs some revisiting. What can be removed, when/how?
        String packageName = "gov.nysenate.openleg.xml.calendar";
        JAXBContext jc = JAXBContext.newInstance( packageName );
        Unmarshaller u = jc.createUnmarshaller();
        XMLSENATEDATA senateData = (XMLSENATEDATA)u.unmarshal( new FileReader(file) );

        Date modifiedDate = null;
        try {
            modifiedDate = sobiDateFormat.parse(file.getName());
        } catch (ParseException e) {
            logger.error("Error parsing file date.", e);
        }

        ChangeLogger.setContext(file, modifiedDate);
        for(Object obj:senateData.getSencalendarOrSencalendaractive()) {

            Calendar calendar = null;
            String action = null;
            Supplemental supplemental = null;

            if (obj instanceof XMLSencalendar) {
                XMLSencalendar xmlCalendar = (XMLSencalendar)obj;

                action = xmlCalendar.getAction();

                calendar = getCalendar(storage, Calendar.TYPE_FLOOR,xmlCalendar.getNo(),xmlCalendar.getYear(),xmlCalendar.getSessyr());

                supplemental = parseSupplemental(storage, calendar,xmlCalendar.getSupplemental());

                if(supplemental.getSequences() != null
                        || (supplemental.getSections() != null && !supplemental.getSections().isEmpty())) {
                    supplemental.setCalendar(calendar);

                    calendar.addSupplemental(supplemental);
                }
            }
            else if (obj instanceof XMLSencalendaractive) {
                XMLSencalendaractive xmlActiveList = (XMLSencalendaractive)obj;

                action = xmlActiveList.getAction();

                calendar = getCalendar(storage, Calendar.TYPE_ACTIVE,xmlActiveList.getNo(),xmlActiveList.getYear(),xmlActiveList.getSessyr());

                supplemental = parseSupplemental(storage, calendar,xmlActiveList.getSupplemental());

                if(supplemental.getSequences() != null
                        || (supplemental.getSections() != null && !supplemental.getSections().isEmpty())) {
                    supplemental.setCalendar(calendar);

                    calendar.addSupplemental(supplemental);
                }
            } else {
                // TODO: log something here
                continue;
            }

            if (action.equals("remove") && removeObject != null) {

                logger.info("REMOVING: " + removeObject.getClass() + "=" + removeObjectId);

                if(removeObject instanceof Supplemental) {
                    if(calendar.getSupplementals() != null) {
                        int indexOf = -1;
                        if((indexOf = calendar.getSupplementals().indexOf(removeObject)) != -1) {
                            calendar.getSupplementals().remove(indexOf);
                        }
                    }
                }
                else if (removeObject instanceof Sequence && calendar.getSupplementals() != null){
                    int supSize = calendar.getSupplementals().size();

                    for(int i = 0; i < supSize; i++) {
                        Supplemental sup = calendar.getSupplementals().get(i);

                        int indexOf = -1;
                        if((indexOf = sup.getSequences().indexOf(removeObject)) != -1) {
                            calendar.getSupplementals().get(i).getSequences().remove(indexOf);
                            break;
                        }
                    }
                } else {
                    // TODO: log something here
                }
            }

            calendar.addSobiReference(file.getName());
            calendar.setModified(modifiedDate.getTime());
            String key = String.valueOf(calendar.getYear())+"/calendar/"+calendar.getId();
            storage.set(key, calendar);
            ChangeLogger.record(key, storage, modifiedDate);
            removeObject = null;
        }
    }

    public Calendar getCalendar (Storage storage, String type, String no, String year, String sessYr) {
        Calendar calendar = null;

        StringBuffer calendarId = new StringBuffer();
        calendarId.append("cal-");
        calendarId.append(type);
        calendarId.append('-');
        calendarId.append(no);
        calendarId.append('-');
        calendarId.append(sessYr);
        calendarId.append('-');
        calendarId.append(year);

        logger.info("getting calendar: " + calendarId.toString());

        String key = year+"/calendar/"+calendarId;
        calendar = (Calendar)storage.get(key, Calendar.class);

        if (calendar == null) {
            calendar = new Calendar();
            calendar.setId(calendarId.toString());
            calendar.setNo(Integer.parseInt(no));
            calendar.setSessionYear(Integer.parseInt(sessYr));
            calendar.setYear(Integer.parseInt(year));
            calendar.setType(type);
        }

        return calendar;
    }


    public Supplemental parseSupplemental (Storage storage, Calendar calendar, XMLSupplemental xmlSupp) {
        String suppId = calendar.getId() + "-supp-" + xmlSupp.getId();

        // Create a new supplemental or get the existing one from the calendar
        Supplemental supplemental = new Supplemental();
        supplemental.setId(suppId);
        int index = -1;
        if(calendar != null && calendar.getSupplementals() != null &&
                (index = calendar.getSupplementals().indexOf(supplemental)) != -1) {
            supplemental = calendar.getSupplementals().get(index);
        } else {
            supplemental.setSupplementalId(xmlSupp.getId());
        }

        // Set parent reference
        supplemental.setCalendar(calendar);

        // Set this as the current object in case of removal
        setRemoveObject(supplemental, supplemental.getId());

        // Set the supplemental calendar date from the parent calendar
        if (xmlSupp.getCaldate()!=null)	{
            try {
                Date calDate = OpenLegConstants.LRS_DATE_ONLY_FORMAT.parse(xmlSupp.getCaldate().getContent());
                supplemental.setCalendarDate(calDate);
            }
            catch (ParseException e) {
                logger.error("Unable to parse calDate for supplement=" + xmlSupp.getId(),e);
            }
        }

        // Set the supplemental release date-time
        if (xmlSupp.getReleasedate()!=null && xmlSupp.getReleasetime()!=null) {
            try {
                String dateString = xmlSupp.getReleasedate().getContent() + xmlSupp.getReleasetime().getContent();
                Date releaseDateTime = OpenLegConstants.LRS_DATETIME_FORMAT.parse(dateString);
                supplemental.setReleaseDateTime(releaseDateTime);

            } catch (ParseException e) {
                logger.error("Unable to parse relDate for supplement=" + xmlSupp.getId(),e);
            }
        }

        if (xmlSupp.getSections()!=null) {
            Section section = null;

            // Get the existing set of sections if available.
            if (supplemental.getSections() == null)
                supplemental.setSections(new ArrayList<Section>());
            List<Section> sections = supplemental.getSections();

            // Parse the various sections from the supplied XML
            // TODO: Since whole sections might be sent each time, are whole supplementals resent also?
            for(XMLSection xmlSection:xmlSupp.getSections().getSection()) {
                section = parseSection(storage, supplemental, xmlSection);

                // Only add new sections
                if(!sections.contains(section))
                    sections.add(section);
            }
            supplemental.setSections(sections);
        }

        // Handle sequences when available
        XMLSequence xmlSequence = xmlSupp.getSequence();
        if (xmlSequence != null) {
            Sequence sequence = parseSequence(storage, supplemental, xmlSequence);
            supplemental.addSequence(sequence);

            // TODO: new removal objects? how/why?
            setRemoveObject(sequence, sequence.getId());
        }


        return supplemental;
    }

    public Section parseSection (Storage storage, Supplemental supplemental, XMLSection xmlSection) {
        // TODO: Confirm: build a new section from scratch, whole section is resent every time?
        String sectionId = supplemental.getId() + "-sect-" + xmlSection.getName();

        // Create a new section
        Section section = new Section();
        section.setId(sectionId);
        section.setCd(xmlSection.getCd());
        section.setName(xmlSection.getName());
        section.setType(xmlSection.getType());
        section.setSupplemental(supplemental);

        // New section entry list!
        List<CalendarEntry> calendarEntries = section.getCalendarEntries();

        // Loop through the entries
        for(XMLCalno xmlCalno:xmlSection.getCalnos().getCalno()) {
            CalendarEntry cEntry = null;
            try	{
                // Add each entry with a parent reference
                cEntry = parseCalno(storage, section.getId(), xmlCalno, supplemental.getCalendar().getSessionYear());
                cEntry.setSection(section);

                // But don't add things twice
                if (!calendarEntries.contains(cEntry))
                    calendarEntries.add(cEntry);
            }
            catch (Exception e) {
                if (cEntry != null)
                    logger.warn("Error adding CalenderEntry: " + cEntry.getId(), e);
                else {
                    logger.warn("Error adding CalenderEntry: ", e);
                }
            }
        }

        // Save our entry list
        section.setCalendarEntries(calendarEntries);
        return section;
    }



    public Sequence parseSequence (Storage storage, Supplemental supplemental, XMLSequence xmlSequence)	{
        String sequenceId = supplemental.getId() + "-seq-" + xmlSequence.getNo();

        Sequence sequence = new Sequence();
        sequence.setId(sequenceId);
        sequence.setNo(xmlSequence.getNo());

        // Attempt to set the Actcal Date
        //  TODO: what is actcal date?
        if (xmlSequence.getActcaldate()!=null) {

            try {
                Date actCalDate = LRS_DATE_ONLY_FORMAT.parse(xmlSequence.getActcaldate().getContent());
                sequence.setActCalDate(actCalDate);
            }
            catch (ParseException e) {
                logger.error("unable to parse sequence actCalDate",e);
            }
        }

        // Set release date time if possible
        if (xmlSequence.getReleasedate()!=null && xmlSequence.getReleasetime()!=null) {
            try {
                Date relDateTime = LRS_DATETIME_FORMAT.parse(xmlSequence.getReleasedate().getContent() + xmlSequence.getReleasetime().getContent());
                sequence.setReleaseDateTime(relDateTime);
            }
            catch (ParseException e) {
                logger.error("unable to parse sequence release date/time format",e);
            }
        }

        // Notes?
        if (xmlSequence.getNotes()!=null)
            sequence.setNotes(xmlSequence.getNotes().replaceAll("\n", ""));

        // Sequence Entries, just like a section?
        List<CalendarEntry> calendarEntries = new ArrayList<CalendarEntry>();
        if (xmlSequence.getCalnos()!=null) {


            for(XMLCalno xmlCalno:xmlSequence.getCalnos().getCalno()) {
                CalendarEntry cEntry = parseCalno(storage, sequence.getId(),xmlCalno, supplemental.getCalendar().getSessionYear());
                cEntry.setSequence(sequence);

                if (!calendarEntries.contains(cEntry))
                    calendarEntries.add(cEntry);
            }
        }
        sequence.setCalendarEntries(calendarEntries);
        return sequence;
    }


    public CalendarEntry parseCalno (Storage storage, String parentId, XMLCalno xmlCalNo, int sessionYear)
    {
        String calEntId = parentId + '-' + xmlCalNo.getNo();

        CalendarEntry calEntry = new CalendarEntry();
        calEntry.setId(calEntId);

        // remove all the leading 0's
        calEntry.setNo(xmlCalNo.getNo().replaceAll("^0*", ""));

        // Set the motion date?
        // TODO: What is a motion date?
        if (xmlCalNo.getMotiondate()!=null)
        {
            try {
                Date motionDate = LRS_DATE_ONLY_FORMAT.parse(xmlCalNo.getMotiondate().getContent());
                calEntry.setMotionDate(motionDate);
            } catch (ParseException e) {
                logger.error("unable to parse calentry " + xmlCalNo.getNo() + " motiondate");
            }
        }

        // Get the bill for the entry, it may be marked has HIGH
        // TODO: What is high? Importance maybe?
        if (xmlCalNo.getBill() != null)
        {
            calEntry.setBillHigh(xmlCalNo.getBill().getHigh());

            // Get the bill from storage if possible, otherwise it makes a new one
            // TODO: should it be possible to not have the bill already?
            String billId = xmlCalNo.getBill().getNo();
            if (!billId.isEmpty()) {
                String sponsor = null;
                if (xmlCalNo.getSponsor()!=null)
                    sponsor = xmlCalNo.getSponsor().getContent();

                calEntry.setBill(getBill(storage, billId, sessionYear, sponsor));
            }
        }

        // Get the substituted bill from storage if possible, otherwise it makes a new one
        if (xmlCalNo.getSubbill()!=null)
        {
            String billId = xmlCalNo.getSubbill().getNo();
            if (!billId.isEmpty())
            {
                String sponsor = null;
                if (xmlCalNo.getSubsponsor()!=null)
                    sponsor = xmlCalNo.getSubsponsor().getContent();

                calEntry.setSubBill(getBill(storage, billId, sessionYear, sponsor));
            }
        }

        return calEntry;
    }

    private Bill getBill(Storage storage, String billId, int year, String sponsorName) {
        String senateBillNo = billId.replaceAll("(?<=[A-Z])0*", "")+"-"+year;
        String key = year+"/bill/"+senateBillNo;

        String[] sponsors = {""};
        if (sponsorName != null) {
            sponsors = sponsorName.trim().split(",");
        }

        Bill bill = (Bill)storage.get(key, Bill.class);
        if (bill == null) {
            bill = new Bill();
            bill.setYear(year);
            bill.setSenateBillNo(senateBillNo);
            bill.setSponsor(new Person(sponsors[0].trim()));
        }

        // Other sponsors are removed when a calendar/agenda is resent without
        // The other sponsor included in the sponsors list.
        ArrayList<Person> otherSponsors = new ArrayList<Person>();
        for (int i = 1; i < sponsors.length; i++) {
            otherSponsors.add(new Person(sponsors[i].trim()));
        }
        bill.setOtherSponsors(otherSponsors);
        new BillProcessor().saveBill(bill, storage, new Date());

        return bill;
    }

    private void setRemoveObject (Object removeObject, String removeObjectId) {
        this.removeObject = removeObject;
        this.removeObjectId = removeObjectId;
    }
}
