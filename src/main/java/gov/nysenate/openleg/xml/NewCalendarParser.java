package gov.nysenate.openleg.xml;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.Section;
import gov.nysenate.openleg.model.calendar.Sequence;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.util.JsonSerializer;
import gov.nysenate.openleg.util.XmlSerializer;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class NewCalendarParser implements OpenLegConstants {

	private static Logger logger = Logger.getLogger(CalendarParser.class);

	private Object removeObject = null;
	private String removeObjectId = null;
	
	private SearchEngine2 engine = null;
	private PersistenceManager pm = null;
	
	private void setRemoveObject (Object removeObject, String removeObjectId) {
		this.removeObject = removeObject;
		this.removeObjectId = removeObjectId;
	}
	
	public static void main (String[] args) throws Exception {
		String f = "/Users/jaredwilliams/Desktop/sobi/SOBI.D110118.T193709.TXT-calendar-1.xml";

		NewCalendarParser cp = new NewCalendarParser();

		File inFile = new File(f/*args[0]*/);
		
		if (inFile.isDirectory()) {
			File[] files = inFile.listFiles();
			
			for (int i = 0; i < files.length; i++)
			{
				cp.doParsing(files[i].getAbsolutePath());
			}
		}
		else {
			cp.doParsing(f/*args[0]*/);
		}
	}
	
	public void doParsing(String filePath) throws Exception {
		engine = SearchEngine2.getInstance();
		pm = PMF.getPersistenceManager();
		
		XMLSENATEDATA senateData = parseStream(new FileReader(new File(filePath)));
		ArrayList<LuceneObject> objectsToUpdate = new ArrayList<LuceneObject>();
		
		for(Object obj:senateData.getSencalendarOrSencalendaractive()) {
			
	        Calendar calendar = null;
	        String action = null;
			Supplemental supplemental = null;
			Transaction tx = pm.currentTransaction();
			tx.begin();

			if (obj instanceof XMLSencalendar) {
				XMLSencalendar xmlCalendar = (XMLSencalendar)obj;
				
				action = xmlCalendar.getAction();
				
				calendar = getCalendar(Calendar.TYPE_FLOOR,xmlCalendar.getNo(),xmlCalendar.getYear(),xmlCalendar.getSessyr());
				
				supplemental = parseSupplemental(calendar,xmlCalendar.getSupplemental());
				
				supplemental.setCalendar(calendar);
				
				calendar.addSupplemental(supplemental);
																
				objectsToUpdate.add(calendar);
			}
			else if (obj instanceof XMLSencalendaractive) {
				XMLSencalendaractive xmlActiveList = (XMLSencalendaractive)obj;
				
				action = xmlActiveList.getAction();
				
				calendar = getCalendar(Calendar.TYPE_ACTIVE,xmlActiveList.getNo(),xmlActiveList.getYear(),xmlActiveList.getSessyr());
				
				supplemental = parseSupplemental(calendar,xmlActiveList.getSupplemental());
				
				supplemental.setCalendar(calendar);
				
				calendar.addSupplemental(supplemental);

				objectsToUpdate.add(calendar);
				
			}
				        
			if (action.equals("remove") && removeObject != null) {
				logger.info("REMOVING: " + removeObject.getClass() + "=" + removeObjectId);
				PMF.removePersistedObject(pm, removeObject.getClass(), removeObjectId);

			}
			
			engine.indexSenateObjects(new ArrayList<LuceneObject>(Arrays.asList(calendar)), new LuceneSerializer[]{new XmlSerializer(), new JsonSerializer()});
			
			tx.commit();
		}
		

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
		calendar = (Calendar)PMF.getDetachedObject(Calendar.class,"id",calendarId.toString(), null);
		
		if (calendar == null) {
			calendar = new Calendar();
			calendar.setId(calendarId.toString());
			calendar.setNo(Integer.parseInt(no));
			calendar.setSessionYear(Integer.parseInt(sessYr));
			calendar.setYear(Integer.parseInt(year));
			calendar.setType(type);
			
			pm.makePersistent(calendar);
		}
		
		return calendar;
	}
	
	
	public Supplemental parseSupplemental (Calendar calendar, XMLSupplemental xmlSupp) {
		String suppId = calendar.getId() + "-supp-" + xmlSupp.getId();
		
		Supplemental supplemental = (Supplemental)PMF.getDetachedObject(Supplemental.class, "id", suppId, null);
		
		if (supplemental == null) {
			supplemental = new Supplemental();
			supplemental.setId(suppId);
			supplemental.setSupplementalId(xmlSupp.getId());
			supplemental.setCalendar(calendar);

			pm.makePersistent(supplemental);
		}
		
		//TODO what is the purpose of this?
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
			supplemental.setSequence(sequence);
			
			//TODO again what does this do?
			setRemoveObject(sequence, sequence.getId());
		}
		
		return supplemental;
	}
	
	public Section parseSection (Supplemental supplemental, XMLSection xmlSection) {
		String sectionId = supplemental.getId() + "-sect-" + xmlSection.getName();
		
		Section section = (Section)PMF.getDetachedObject(Section.class, "id", sectionId, null);
	
		if (section == null) {
			section = new Section();
			section.setId(sectionId);
			section.setCd(xmlSection.getCd());
			section.setName(xmlSection.getName());
			section.setType(xmlSection.getType());
			section.setSupplemental(supplemental);
			
			pm.makePersistent(section);
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
		String sequenceId = supplemental.getId() + "-seq-" + xmlSequence.getNo();
		
		Sequence sequence = (Sequence)PMF.getDetachedObject(Sequence.class, "id", sequenceId, null);
		
		if (sequence == null) {
			sequence = new Sequence();
			sequence.setId(sequenceId);
			sequence.setNo(xmlSequence.getNo());
			
			pm.makePersistent(sequence);
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
			sequence.setNotes(xmlSequence.getNotes());
		
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
		
		CalendarEntry calEntry = (CalendarEntry)PMF.getDetachedObject(CalendarEntry.class, "id", calEntId, null);
		
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
			pm.makePersistent(calEntry);
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
	
	private Bill getBill (String billId, int year, String sponsorName)
	{
		
		String billType = billId.substring(0,1);
		int billNumber = -1;
			
		char lastVal = billId.substring(billId.length()-1,billId.length()).toCharArray()[0];
		
		String senateBillNo = null;
		
		if (Character.isLetter(lastVal))
		{
			billNumber = Integer.parseInt(billId.substring(1,billId.length()-1));
			
			senateBillNo = billType + billNumber + lastVal;
		}
		else
		{
			billNumber = Integer.parseInt(billId.substring(1));
			
			senateBillNo = billType + billNumber;

		}
		
		
		//new bill numbers include year
		if(year != 2009) {
			senateBillNo += "-" + year;
		}
		
		Bill bill = PMF.getBill(pm, senateBillNo, year);
		
		if (bill == null)
		{
			bill = new Bill();
			bill.setSenateBillNo(senateBillNo);
		}

		
		if (sponsorName != null)
		{
			Person sponsor = PMF.getPerson(pm, sponsorName);
			
			bill.setSponsor(sponsor);
		}
				
		return bill;
	}
	
	public XMLSENATEDATA parseStream (Reader reader) throws Exception
	{
		String packageName = "gov.nysenate.openleg.xml.calendar";
	    JAXBContext jc = JAXBContext.newInstance( packageName );
	    Unmarshaller u = jc.createUnmarshaller();
	    XMLSENATEDATA sd = (XMLSENATEDATA)u.unmarshal( reader );
	   
	    return sd;
	}
	
	@SuppressWarnings("unchecked")
	public void printAllXMLCalendars () throws Exception
	{
		
		System.out.println("<?xml version= '1.0' encoding='UTF-8'?>");

		PersistenceManager pm = PMF.getPersistenceManager();
        
		Query query = pm.newQuery(Calendar.class);
		Iterator<Calendar> result = ((Collection<Calendar>) query.execute()).iterator();
        
		Calendar pCalendar = null;
		
		while (result.hasNext())
		{
			pCalendar = result.next();
			printXMLCalendar (pCalendar);
		}
	}
	
	public static void printXMLCalendar (Calendar cal) throws JAXBException
	{
		 JAXBContext context = JAXBContext.newInstance(Calendar.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(cal, System.out);
	}
}
