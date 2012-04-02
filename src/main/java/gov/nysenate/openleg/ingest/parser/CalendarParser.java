package gov.nysenate.openleg.ingest.parser;

import gov.nysenate.openleg.ingest.JsonDao;
import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.xml.calendar.XMLCalno;
import gov.nysenate.openleg.xml.calendar.XMLSENATEDATA;
import gov.nysenate.openleg.xml.calendar.XMLSection;
import gov.nysenate.openleg.xml.calendar.XMLSencalendar;
import gov.nysenate.openleg.xml.calendar.XMLSencalendaractive;
import gov.nysenate.openleg.xml.calendar.XMLSequence;
import gov.nysenate.openleg.xml.calendar.XMLSupplemental;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class CalendarParser extends SenateParser<Calendar> implements OpenLegConstants {

    private Object removeObject = null;
    private String removeObjectId = null;

    public CalendarParser() {
        this(null, null);
    }

    public CalendarParser(JsonDao jsonDao, SearchEngine searchEngine) {
        super(CalendarParser.class, jsonDao, searchEngine);
    }

    @Override
    public void parse(File file) {
        try {
            doParsing(file);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public void doParsing(File file) throws Exception {
        XMLSENATEDATA senateData = parseStream(new FileReader(file));
        ArrayList<ILuceneObject> objectsToUpdate = new ArrayList<ILuceneObject>();

        for(Object obj:senateData.getSencalendarOrSencalendaractive()) {

            Calendar calendar = null;
            String action = null;
            Supplemental supplemental = null;


            if (obj instanceof XMLSencalendar) {
                XMLSencalendar xmlCalendar = (XMLSencalendar)obj;

                action = xmlCalendar.getAction();

                calendar = getCalendar(Calendar.TYPE_FLOOR,xmlCalendar.getNo(),xmlCalendar.getYear(),xmlCalendar.getSessyr());

                supplemental = parseSupplemental(calendar,xmlCalendar.getSupplemental());

                if(supplemental.getSequences() != null
                        || (supplemental.getSections() != null && !supplemental.getSections().isEmpty())) {
                    supplemental.setCalendar(calendar);

                    calendar.addSupplemental(supplemental);

                    objectsToUpdate.add(calendar);
                }
            }
            else if (obj instanceof XMLSencalendaractive) {
                XMLSencalendaractive xmlActiveList = (XMLSencalendaractive)obj;

                action = xmlActiveList.getAction();

                calendar = getCalendar(Calendar.TYPE_ACTIVE,xmlActiveList.getNo(),xmlActiveList.getYear(),xmlActiveList.getSessyr());

                supplemental = parseSupplemental(calendar,xmlActiveList.getSupplemental());

                if(supplemental.getSequences() != null
                        || (supplemental.getSections() != null && !supplemental.getSections().isEmpty())) {
                    supplemental.setCalendar(calendar);

                    calendar.addSupplemental(supplemental);

                    objectsToUpdate.add(calendar);

                }
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
                }
                if(this.canWrite(SenateParser.JSON)) {
                    jsonDao.write(calendar);
                }
            }
            if(calendar != null) {
                newSenateObjects.add(calendar);
            }
        }
        removeObject = null;
    }

    public Calendar getCalendar (String type, String no, String year, String sessYr) {
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

        if(canWrite(SenateParser.JSON)) {
            calendar = jsonDao.load(calendarId.toString(), year, "calendar", Calendar.class);
        }

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


    public Supplemental parseSupplemental (Calendar calendar, XMLSupplemental xmlSupp) {
        String suppId = calendar.getId() + "-supp-" + xmlSupp.getId();

        Supplemental supplemental = new Supplemental();
        supplemental.setId(suppId);

        int index = -1;
        if(calendar != null && calendar.getSupplementals() != null &&
                (index = calendar.getSupplementals().indexOf(supplemental)) != -1) {
            supplemental = calendar.getSupplementals().get(index);
        }
        else {
            supplemental.setSupplementalId(xmlSupp.getId());
        }
        supplemental.setCalendar(calendar);

        setRemoveObject(supplemental, supplemental.getId());

        if (xmlSupp.getCaldate()!=null)	{
            try {
                Date calDate = OpenLegConstants.LRS_DATE_ONLY_FORMAT.parse(xmlSupp.getCaldate().getContent());
                supplemental.setCalendarDate(calDate);
            }
            catch (ParseException e) {
                logger.error("Unable to parse calDate for supplement=" + xmlSupp.getId(),e);
            }
        }

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

            if (supplemental.getSections() == null)
                supplemental.setSections(new ArrayList<Section>());
            List<Section> sections = supplemental.getSections();

            for(XMLSection xmlSection:xmlSupp.getSections().getSection()) {
                section = parseSection(supplemental, xmlSection);

                try	{
                    if(!sections.contains(section))
                        sections.add(section);
                }
                catch (Exception e)	{
                    logger.warn("error adding section: " + section.getId() + ": " + e.getLocalizedMessage());
                }
            }
            supplemental.setSections(sections);
        }

        XMLSequence xmlSequence = xmlSupp.getSequence();

        if (xmlSequence != null) {
            Sequence sequence = parseSequence (supplemental, xmlSequence);
            supplemental.addSequence(sequence);

            setRemoveObject(sequence, sequence.getId());
        }

        return supplemental;
    }

    public Section parseSection (Supplemental supplemental, XMLSection xmlSection) {
        String sectionId = supplemental.getId() + "-sect-" + xmlSection.getName();

        Section section = null;

        if (section == null) {
            section = new Section();
            section.setId(sectionId);
            section.setCd(xmlSection.getCd());
            section.setName(xmlSection.getName());
            section.setType(xmlSection.getType());
            section.setSupplemental(supplemental);

        }

        CalendarEntry cEntry = null;

        if (section.getCalendarEntries() == null)
            section.setCalendarEntries(new ArrayList<CalendarEntry>());
        List<CalendarEntry> calendarEntries = section.getCalendarEntries();

        for(XMLCalno xmlCalno:xmlSection.getCalnos().getCalno()) {
            try	{
                cEntry = parseCalno(section.getId(), xmlCalno, supplemental.getCalendar().getSessionYear());
                cEntry.setSection(section);

                if (!calendarEntries.contains(cEntry))
                    calendarEntries.add(cEntry);
            }
            catch (Exception e) {
                if (cEntry != null)
                    logger.warn("Error adding CalenderEntry: " + cEntry.getId() + ": " + e.getLocalizedMessage());
                else {
                    logger.warn("Error adding CalenderEntry: " + e.getLocalizedMessage());
                }
            }
        }
        section.setCalendarEntries(calendarEntries);
        return section;
    }



    public Sequence parseSequence (Supplemental supplemental, XMLSequence xmlSequence)	{
        if(xmlSequence.getNo().equals("")) {
            xmlSequence.setNo("1");
        }

        String sequenceId = supplemental.getId() + "-seq-" + xmlSequence.getNo();

        Sequence sequence = null;

        if (sequence == null) {
            sequence = new Sequence();
            sequence.setId(sequenceId);
            sequence.setNo(xmlSequence.getNo());

        }

        if (xmlSequence.getActcaldate()!=null) {

            try {
                Date actCalDate = LRS_DATE_ONLY_FORMAT.parse(xmlSequence.getActcaldate().getContent());
                sequence.setActCalDate(actCalDate);
            }
            catch (ParseException e) {
                logger.error("unable to parse sequence actCalDate",e);
            }
        }

        if (xmlSequence.getReleasedate()!=null && xmlSequence.getReleasetime()!=null) {
            try {
                Date relDateTime = LRS_DATETIME_FORMAT.parse(xmlSequence.getReleasedate().getContent() + xmlSequence.getReleasetime().getContent());
                sequence.setReleaseDateTime(relDateTime);
            }
            catch (ParseException e) {
                logger.error("unable to parse sequence release date/time format",e);
            }
        }

        if (xmlSequence.getNotes()!=null)
            sequence.setNotes(xmlSequence.getNotes().replaceAll("\n", ""));

        if (xmlSequence.getCalnos()!=null) {
            CalendarEntry cEntry = null;

            if (sequence.getCalendarEntries() == null)
                sequence.setCalendarEntries(new ArrayList<CalendarEntry>());
            List<CalendarEntry> calendarEntries = sequence.getCalendarEntries();

            for(XMLCalno xmlCalno:xmlSequence.getCalnos().getCalno()) {
                cEntry = parseCalno(sequence.getId(),xmlCalno, supplemental.getCalendar().getSessionYear());
                cEntry.setSequence(sequence);

                if (!calendarEntries.contains(cEntry))
                    calendarEntries.add(cEntry);
            }
            sequence.setCalendarEntries(calendarEntries);
        }
        return sequence;
    }


    public CalendarEntry parseCalno (String parentId, XMLCalno xmlCalNo, int sessionYear)
    {
        String calEntId = parentId + '-' + xmlCalNo.getNo();

        CalendarEntry calEntry = null;

        //		CalendarEntry calEntry = (CalendarEntry)PMF.getDetachedObject(CalendarEntry.class, "id", calEntId, null);

        if (calEntry == null)
        {
            calEntry = new CalendarEntry();
            calEntry.setId(calEntId);
            try
            {
                //we do this to remove all the crazy leading 0's
                int calNo = Integer.parseInt(xmlCalNo.getNo());
                calEntry.setNo(calNo+"");
            }
            catch (Exception e)
            {
                //coudln't parse into an int! just store the string instead
                calEntry.setNo(xmlCalNo.getNo());
            }
        }

        if (xmlCalNo.getMotiondate()!=null)
        {
            try {
                Date motionDate = LRS_DATE_ONLY_FORMAT.parse(xmlCalNo.getMotiondate().getContent());

                calEntry.setMotionDate(motionDate);
            } catch (ParseException e) {
                logger.error("unable to parse calentry " + xmlCalNo.getNo() + " motiondate");
            }

        }

        if (xmlCalNo.getBill() != null)
        {

            String high = xmlCalNo.getBill().getHigh();

            calEntry.setBillHigh(high);

            String billId = xmlCalNo.getBill().getNo();

            if (billId.length() > 0)
            {
                String sponsor = null;
                if (xmlCalNo.getSponsor()!=null)
                    sponsor = xmlCalNo.getSponsor().getContent();

                calEntry.setBill(getBill(billId, sessionYear, sponsor));
            }
        }

        if (xmlCalNo.getSubbill()!=null)
        {

            String billId = xmlCalNo.getSubbill().getNo();

            if (billId.length() > 0)
            {
                String sponsor = null;
                if (xmlCalNo.getSubsponsor()!=null)
                    sponsor = xmlCalNo.getSubsponsor().getContent();

                calEntry.setSubBill(getBill(billId, sessionYear, sponsor));
            }
        }

        return calEntry;
    }

    private Bill getBill (String billId, int year, String sponsorName) {

        String billType = billId.substring(0,1);
        int billNumber = -1;

        char lastVal = billId.substring(billId.length()-1,billId.length()).toCharArray()[0];

        String senateBillNo = null;

        if (Character.isLetter(lastVal)) {
            billNumber = Integer.parseInt(billId.substring(1,billId.length()-1));

            senateBillNo = billType + billNumber + lastVal;
        }
        else {
            billNumber = Integer.parseInt(billId.substring(1));

            senateBillNo = billType + billNumber;

        }

        senateBillNo += "-" + year;

        Bill bill = null;

        if(canWrite(JSON)) {
            bill = jsonDao.load(senateBillNo, year +"", "bill", Bill.class);
        }

        if (bill == null) {
            bill = new Bill();
            bill.setSenateBillNo(senateBillNo);
            bill.setYear(year);

            Person sponsor = new Person(sponsorName);
            bill.setSponsor(sponsor);

            if(canWrite(SenateParser.JSON)) {
                jsonDao.write(bill);
            }
        }

        bill.setFulltext("");
        bill.setMemo("");
        bill.setActions(null);

        return bill;
    }

    private void setRemoveObject (Object removeObject, String removeObjectId) {
        this.removeObject = removeObject;
        this.removeObjectId = removeObjectId;
    }

    public XMLSENATEDATA parseStream (Reader reader) throws Exception
    {
        String packageName = "gov.nysenate.openleg.xml.calendar";
        JAXBContext jc = JAXBContext.newInstance( packageName );
        Unmarshaller u = jc.createUnmarshaller();
        XMLSENATEDATA sd = (XMLSENATEDATA)u.unmarshal( reader );

        return sd;
    }

    public static void printXMLCalendar (Calendar cal) throws JAXBException
    {
        JAXBContext context = JAXBContext.newInstance(Calendar.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(cal, System.out);
    }

}
