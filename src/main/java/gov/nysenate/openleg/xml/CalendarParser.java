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

public class CalendarParser implements OpenLegConstants {

	private static Logger logger = Logger.getLogger(CalendarParser.class);

	private Object removeObject = null;
	private String removeObjectId = null;
	
	private SearchEngine2 engine = null;
	
	
	private void setRemoveObject (Object removeObject, String removeObjectId)
	{
		this.removeObject = removeObject;
		this.removeObjectId = removeObjectId;
	}
	
	public static void main (String[] args) throws Exception
	{
		String f = "C:\\Documents and Settings\\Wililiams\\Desktop\\SOBI.D100621.T142028.TXT-calendar-1.xml";
		
		CalendarParser cp = new CalendarParser();

		File inFile = new File(f);
		
		if (inFile.isDirectory())
		{
			File[] files = inFile.listFiles();
			
			for (int i = 0; i < files.length; i++)
			{
				
			}
		}
		else
		{
			cp.doParsing(f);
		}
	}
	
	
	public void doParsing (String filePath) throws Exception
	{
		
		engine = SearchEngine2.getInstance();
		
		XMLSENATEDATA senateData = parseStream(new FileReader(new File(filePath)));
		
		Iterator<Object> it = senateData.getSencalendarOrSencalendaractive().iterator();
		
		Object nextItem = null;
		
		String action = null;
		
		Calendar calendar = null;
		Supplemental supplemental = null;

		ArrayList<LuceneObject> objectsToUpdate = new ArrayList<LuceneObject>();
		
		PersistenceManager pm = PMF.getPersistenceManager();
		         
		while (it.hasNext())
		{
			nextItem = it.next();

			Transaction currentTx = pm.currentTransaction();
	        currentTx.begin();

			if (nextItem instanceof XMLSencalendar)
			{
				XMLSencalendar xmlCalendar = (XMLSencalendar)nextItem;
				
				action = xmlCalendar.getAction();
				
				calendar = getCalendar(pm, Calendar.TYPE_FLOOR,xmlCalendar.getNo(),xmlCalendar.getYear(),xmlCalendar.getSessyr());
				
				supplemental = parseSupplemental(pm, calendar,xmlCalendar.getSupplemental());
				
				supplemental.setCalendar(calendar);
				
				objectsToUpdate.add(calendar);
			}
			else if (nextItem instanceof XMLSencalendaractive)
			{
				XMLSencalendaractive xmlActiveList = (XMLSencalendaractive)nextItem;
				action = xmlActiveList.getAction();
				
				calendar = getCalendar(pm, Calendar.TYPE_ACTIVE,xmlActiveList.getNo(),xmlActiveList.getYear(),xmlActiveList.getSessyr());
				
				supplemental = parseSupplemental(pm, calendar,xmlActiveList.getSupplemental());
				
				supplemental.setCalendar(calendar);

				objectsToUpdate.add(calendar);
			}
			
	        
			if (action.equals("remove") && removeObject != null)
			{
				logger.info("REMOVING: " + removeObject.getClass() + "=" + removeObjectId);
				PMF.removePersistedObject(pm, removeObject.getClass(), removeObjectId);

			}
			

			currentTx.commit();
			
		
		}
	

		Transaction currentTx = pm.currentTransaction();
		try
		{
	        currentTx.begin();
			engine.indexSenateObjects(objectsToUpdate, new LuceneSerializer[]{new XmlSerializer(), new JsonSerializer()});
			currentTx.commit();
		}
		catch (Exception e)
		{
			currentTx.rollback();
		}
		

        engine.optimize();
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
	
	public static Calendar getCalendar (PersistenceManager pm, String type, String no, String year, String sessYr)
	{
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
		calendar = (Calendar)PMF.getPersistedObject(pm, Calendar.class, calendarId.toString(), false);
		
		if (calendar == null)
		{
			calendar = new Calendar();
			calendar.setId(calendarId.toString());
			calendar.setNo(Integer.parseInt(no));
			calendar.setSessionYear(Integer.parseInt(sessYr));
			calendar.setYear(Integer.parseInt(year));
			calendar.setType(type);
			
			calendar = pm.makePersistent(calendar);
		}
		
		return calendar;
	}
	
	
	
	
	
	public static void printXMLCalendar (Calendar cal) throws JAXBException
	{
		 JAXBContext context = JAXBContext.newInstance(Calendar.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(cal, System.out);
	}
	
	
	
	/*
	 * <caldate>2009-11-09</caldate>
<releasedate>2009-11-10</releasedate>
<releasetime>T11.33.00Z</releasetime>
	 */
	
	
	public Supplemental parseSupplemental (PersistenceManager pm, Calendar calendar, XMLSupplemental xmlSupp)
	{
	
		String suppId = calendar.getId() + "-supp-" + xmlSupp.getId();
		
		Supplemental supplemental = (Supplemental)PMF.getPersistedObject(pm, Supplemental.class, suppId, false);
		
		if (supplemental == null)
		{
			supplemental = new Supplemental();
			supplemental.setId(suppId);
			supplemental.setSupplementalId(xmlSupp.getId());
			supplemental = pm.makePersistent(supplemental);
		}
		
		setRemoveObject(supplemental, supplemental.getId());
		
		if (xmlSupp.getCaldate()!=null)
		{
			try {
				Date calDate = OpenLegConstants.LRS_DATE_ONLY_FORMAT.parse(xmlSupp.getCaldate().getContent());
				supplemental.setCalendarDate(calDate);
			} catch (ParseException e) {
				logger.error("Unable to parse calDate for supplement=" + xmlSupp.getId(),e);
			}
		}
		
		if (xmlSupp.getReleasedate()!=null && xmlSupp.getReleasetime()!=null)
		{
			try {
				String dateString = xmlSupp.getReleasedate().getContent() + xmlSupp.getReleasetime().getContent();
				Date releaseDateTime = OpenLegConstants.LRS_DATETIME_FORMAT.parse(dateString);
				supplemental.setReleaseDateTime(releaseDateTime);
				
			} catch (ParseException e) {
				logger.error("Unable to parse relDate for supplement=" + xmlSupp.getId(),e);
			}
		}
		
		if (xmlSupp.getSections()!=null)
		{
			Iterator<XMLSection> itSections = xmlSupp.getSections().getSection().iterator();
			
			Section section = null;
			
			List<Section> sections = supplemental.getSections();
			if (sections == null)
			{
				sections = new ArrayList<Section>();
				supplemental.setSections(sections);
			}
			
			while (itSections.hasNext())
			{
				section = parseSection(pm, supplemental, itSections.next());
				
				try
				{
					if (!sections.contains(section))
						sections.add(section);
				}
				catch (Exception e)
				{
					logger.warn("error adding section: " + section.getId() + ": " + e.getLocalizedMessage());
				}
			}
		}
		
		
		XMLSequence xmlSequence = xmlSupp.getSequence();
		
		if (xmlSequence != null)
		{
			Sequence sequence = parseSequence (pm, supplemental, xmlSequence);
			supplemental.setSequence(sequence);
			
			setRemoveObject(sequence, sequence.getId());
		
		}
		
		return supplemental;
	}
	
	/*
	 * <sequence no="">
<actcaldate>2009-11-08</actcaldate>
<releasedate>2009-11-10</releasedate>
<releasetime>T11.34.54Z</releasetime>
<notes></notes>
<calnos>
<calno no="24">
<bill no="S01729" />
</calno>
</calnos>
</sequence>
	 */
	public Sequence parseSequence (PersistenceManager pm, Supplemental supplemental, XMLSequence xmlSequence)
	{
		
		String sequenceId = supplemental.getId() + "-seq-" + xmlSequence.getNo();
		
		Sequence sequence = (Sequence)PMF.getPersistedObject(pm, Sequence.class, sequenceId, false);
		
		if (sequence == null)
		{
			sequence = new Sequence();

			sequence.setId(sequenceId);

			sequence.setNo(xmlSequence.getNo());
		
			sequence = pm.makePersistent(sequence);

		}
		

		if (xmlSequence.getActcaldate()!=null)
		{
		
			try {
				Date actCalDate = LRS_DATE_ONLY_FORMAT.parse(xmlSequence.getActcaldate().getContent());
				sequence.setActCalDate(actCalDate);
			} catch (ParseException e) {
				logger.error("unable to parse sequence actCalDate",e);
			}
		
		}
		
		if (xmlSequence.getReleasedate()!=null && xmlSequence.getReleasetime()!=null)
		{
			try {
				Date relDateTime = LRS_DATETIME_FORMAT.parse(xmlSequence.getReleasedate().getContent() + xmlSequence.getReleasetime().getContent());
				sequence.setReleaseDateTime(relDateTime);
			} catch (ParseException e) {
				logger.error("unable to parse sequence release date/time format",e);
			}
		}
		
		if (xmlSequence.getNotes()!=null)
		{
			sequence.setNotes(xmlSequence.getNotes());
		}
		
		if (xmlSequence.getCalnos()!=null)
		{
			Iterator<XMLCalno> itCalnos = xmlSequence.getCalnos().getCalno().iterator();
			CalendarEntry cEntry = null;
			
			List<CalendarEntry> calendarEntries = sequence.getCalendarEntries();
			
			if (calendarEntries == null)
			{
				calendarEntries = new ArrayList<CalendarEntry>();
				sequence.setCalendarEntries(calendarEntries);
			}
			
			while (itCalnos.hasNext())
			{
				cEntry = parseCalno (pm, sequence.getId(),itCalnos.next(), supplemental.getCalendar().getSessionYear());
				cEntry.setSequence(sequence);
				
				if (!calendarEntries.contains(cEntry))
					calendarEntries.add(cEntry);
				
			}
		}
		
		return sequence;
	}
	
	public Section parseSection (PersistenceManager pm, Supplemental supplemental, XMLSection xmlSection)
	{
		String sectionId = supplemental.getId() + "-sect-" + xmlSection.getName();
		
		Section section = (Section)PMF.getPersistedObject(pm, Section.class, sectionId, false);
		
		
		if (section == null)
		{
			
			section = new Section();
			
			section.setId(sectionId);
			section.setCd(xmlSection.getCd());
			section.setName(xmlSection.getName());
			section.setType(xmlSection.getType());
			
			section = pm.makePersistent(section);
		}
		
		Iterator<XMLCalno> itCalnos = xmlSection.getCalnos().getCalno().iterator();
		CalendarEntry cEntry = null;
		
		List<CalendarEntry> calendarEntries = section.getCalendarEntries();
		
		if (calendarEntries == null)
		{
			calendarEntries = new ArrayList<CalendarEntry>();
			section.setCalendarEntries(calendarEntries);
		}
		
		while (itCalnos.hasNext())
		{
			try
			{
				cEntry = parseCalno (pm, section.getId(), itCalnos.next(), supplemental.getCalendar().getSessionYear());
				cEntry.setSection(section);
				
				if (!calendarEntries.contains(cEntry))
					calendarEntries.add(cEntry);
			}
			catch (Exception e)
			{
				if (cEntry != null)
					logger.warn("Error adding CalenderEntry: " + cEntry.getId() + ": " + e.getLocalizedMessage()); 
				else
				{
					logger.warn("Error adding CalenderEntry: " + e.getLocalizedMessage()); 
				}
			}
			
		}
		
		return section;
	}
	
	public CalendarEntry parseCalno (PersistenceManager pm, String parentId, XMLCalno xmlCalNo, int sessionYear)
	{
		String calEntId = parentId + '-' + xmlCalNo.getNo();
		
		CalendarEntry calEntry = (CalendarEntry)PMF.getPersistedObject(pm, CalendarEntry.class, calEntId, false);
		
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
			
			calEntry = pm.makePersistent(calEntry);
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
			
				calEntry.setBill(getBill(pm, billId, sessionYear, sponsor));
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
				
				calEntry.setSubBill(getBill(pm, billId, sessionYear, sponsor));
			}
		}
		
	
		
		return calEntry;
	}
	
	private static Bill getBill (PersistenceManager pm, String billId, int year, String sponsorName)
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
	
	public CalendarParser ()
	{
		
		
		
	}
	
	public XMLSENATEDATA parseStream (Reader reader) throws Exception
	{
		String packageName = "gov.nysenate.openleg.xml.calendar";
	    JAXBContext jc = JAXBContext.newInstance( packageName );
	    Unmarshaller u = jc.createUnmarshaller();
	    XMLSENATEDATA sd = (XMLSENATEDATA)u.unmarshal( reader );
	   
	    return sd;
	}
}
