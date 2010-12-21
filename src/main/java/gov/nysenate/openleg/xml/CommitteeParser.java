package gov.nysenate.openleg.xml;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Committee;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.model.committee.Addendum;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.util.JsonSerializer;
import gov.nysenate.openleg.util.XmlSerializer;
import gov.nysenate.openleg.xml.committee.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
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

public class CommitteeParser implements OpenLegConstants
{

	private static Logger logger = Logger.getLogger(CommitteeParser.class);

	private SearchEngine2 engine = null;
	
	private PersistenceManager pm = null;
	private Transaction trans = null;
	ArrayList<LuceneObject> objectsToUpdate = new ArrayList<LuceneObject>();

	
	public static void main (String[] args) throws Exception
	{
		
		
		File file = new File (args[0]);
		
		if (file.isDirectory())
		{
			String[] files = file.list();
			
			for (int i = 0; i < files.length; i++)
			{
				file = new File(args[0] + File.separatorChar + files[i]);
				
				if (file.isFile())
					loadFile(file,false);
			}
		
		}
		else
			loadFile(file,true);
		
	}
	
	public static void loadFile (File file, boolean doExit)
	{

		try
		{
			CommitteeParser cp = new CommitteeParser();

			boolean success = cp.doParse(file.getAbsolutePath());
			
			if (doExit)
			{
				if (success)
				{
					
					System.exit(0);
				}
				else
				{
					//return err code 1 to indicate failure
					System.exit(1);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("unable to parse committe data",e);
			//return err code 1 to indicate failure
			System.exit(1);
		}
	}
	public void printAgenda (String id) throws Exception
	{
		
		Iterator<?> it = (Iterator<?>)PMF.getDetachedObjects(Agenda.class, "id", id, "number ascending",1,100).iterator();
		
		while (it.hasNext())
		{
			printXML (Agenda.class, it.next());
			
			System.out.println("******************************************");
		
		}
	}
	
	public void printMeetings (String commName) throws Exception
	{
		
		Iterator<?> it = (Iterator<?>)PMF.getDetachedObjects(Meeting.class, "committeeName", commName, "meetingDateTime descending", 1, 100).iterator();
		
		while (it.hasNext())
		{
			Meeting meeting = (Meeting)it.next();
			printXML (Meeting.class, meeting);
			
			System.out.println("******************************************");
		
		}
	}
	
	public void printAllXMLAgendas () throws Exception
	{
		
		System.out.println("<?xml version= '1.0' encoding='UTF-8'?>");

		PersistenceManager pm = PMF.getPersistenceManager();
        
		Query query = pm.newQuery(Agenda.class);
		Iterator<?> result = ((Collection<?>) query.execute()).iterator();
        
		Agenda agenda = null;
		
		while (result.hasNext())
		{
			agenda = (Agenda)result.next();
			printXML (Agenda.class, agenda);
			
		}
		
	}
	
	public static void printXML (Class<?> classInst, Object obj) throws JAXBException
	{
		 JAXBContext context = JAXBContext.newInstance(classInst);

		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    m.marshal(obj, System.out);
	}
	
	
	public boolean doParse (String filePath) throws ParseException, JAXBException, FileNotFoundException, IOException
	{
		XMLSENATEDATA senateData = parseStream(new FileReader(new File(filePath)));
		
		Iterator<Object> it = senateData.getSenagendaOrSenagendavote().iterator();
		Object next = null;
		

		engine = new SearchEngine2();
		
		while (it.hasNext())
		{
		
			next = it.next();
			
			pm = PMF.getPersistenceManager();

			trans = pm.currentTransaction();
			
	        try
	        {
        		
	        	
	        	if (trans.isActive())
	        		trans.rollback();
	        	
	        		trans.begin();
	        	
				if (next instanceof XMLSenagenda)
				{
					
					XMLSenagenda cAgenda = (XMLSenagenda)next;
					Agenda agenda = handleXMLSenagenda (pm, cAgenda);
					
					if (agenda != null)
						PMF.makePersistent(pm,agenda);
					
					objectsToUpdate.add(agenda);
					
				}
				else if (next instanceof XMLSenagendavote)
				{
					XMLSenagendavote xmlAgendaVote = (XMLSenagendavote)next;
					Agenda agenda = handleXMLSenagendavote (pm, xmlAgendaVote);
					
					if (agenda != null)
						PMF.makePersistent(pm,agenda);
					
					objectsToUpdate.add(agenda);

				}
				
				engine.indexSenateObjects(objectsToUpdate, new LuceneSerializer[]{new XmlSerializer(), new JsonSerializer()});
				
				trans.commit();
		        
	        }
	        catch (Exception e)
	        {
	        	logger.warn("EXITING: ERROR PROCESSING: " + filePath + "; " + e.getLocalizedMessage());
	        	
	        	if (trans.isActive())
	        		trans.rollback();
	        	
	        	pm.close();
	        	return false;
	        
	        }
	       
	        pm.close();
	      
	        engine.optimize();
	        
		}
		
		  return true;
		
	}
	
	
	public Bill handleXMLBill (PersistenceManager pm, Meeting meeting, XMLBill xmlBill, int sessionYear)
	{
		
		Bill bill = getBill(pm, xmlBill.getNo(), sessionYear, xmlBill.getSponsor().getContent());
		
		if (xmlBill.getMessage()!=null)
		{
		//	System.out.println("message=" + bill.getMessage());
		}
		
		if (xmlBill.getSponsor()!=null)
		{
			//System.out.println("sponsor=" + xmlBill.getSponsor().getContent());
			if (bill.getSponsor() == null)
			{
				bill.setSponsor(PMF.getPerson(pm, xmlBill.getSponsor().getContent()));
			}
		}
		
		if (xmlBill.getTitle()!=null)
		{
			//System.out.println("title=" + xmlBill.getTitle().getContent());
			if (bill.getSummary()==null)
			{
				bill.setSummary(xmlBill.getTitle().getContent());
			}
		}
		
		
		
		if (xmlBill.getVotes()!=null)
		{
			Iterator<XMLMember> itMemberVotes = xmlBill.getVotes().getMember().iterator();
			XMLMember member = null;
			
			int ayeCount = -1;
			int nayCount = -1;
			
			Date voteDate = meeting.getMeetingDateTime();
			
			//clear out the previous vote
			
			PMF.removePersistedObject(pm, Vote.class, Vote.buildId(bill, voteDate, ayeCount, nayCount));
			
			Vote vote = new Vote(bill, voteDate, ayeCount, nayCount);
			
			vote.setVoteType(Vote.VOTE_TYPE_COMMITTEE);
			vote.setDescription(meeting.getCommitteeName());
			
			
			while (itMemberVotes.hasNext())
			{
				member = itMemberVotes.next();
				Person person = PMF.getPerson(pm, member.getName().getContent());
				
				String voteType = member.getVote().getContent().toLowerCase();
				
				logger.info("adding vote: " + bill.getSenateBillNo() + " - " + voteType + " - " + person.getFullname());
				
				if (voteType.startsWith("abstain"))
				{
					vote.addAbstain(person);
				}
				else if (voteType.startsWith("aye w/r"))
				{
					vote.addAyeWR(person);
				}
				else if (voteType.startsWith("aye"))
				{
					vote.addAye(person);
				}
				else if (voteType.startsWith("excused"))
				{
					vote.addExcused(person);
				}
				else if (voteType.startsWith("nay"))
				{
					vote.addNay(person);
				}
				
	
			}
	
			objectsToUpdate.add(vote);
			
			bill.addVote(vote);
			
			objectsToUpdate.add(bill);
			
			List<Vote> listVotes = meeting.getVotes();
			if (listVotes == null)
			{
				listVotes = new ArrayList<Vote>();
				listVotes.add(vote);
				meeting.setVotes(listVotes);
			}
		}
		
		return bill;
	}
	
	public Agenda handleXMLSenagendavote (PersistenceManager pm, XMLSenagendavote xmlAgendaVote) throws ParseException, SQLException
	{
		Agenda agendaVote = null;
		
		logger.info ("COMMITTEE AGENDA VOTE RECORD " + xmlAgendaVote.getNo());
		
		String agendaId = "commagenda-" + xmlAgendaVote.getNo() + '-' + xmlAgendaVote.getSessyr() + '-' + xmlAgendaVote.getYear();
		
		//figure out what is being removed or replaced
		agendaVote = (Agenda)PMF.getPersistedObject(pm, Agenda.class,agendaId, false);
		
		if (agendaVote == null)
		{
			//check if there is an one using the pervious id format
			String tmpAgendaId = "commagenda-" + xmlAgendaVote.getNo() + '-' + xmlAgendaVote.getSessyr();// + '-' + xmlAgendaVote.getYear();
			agendaVote = (Agenda)PMF.getPersistedObject(pm, Agenda.class,tmpAgendaId, false);
		}
		
		if (agendaVote == null)
		{
			logger.info("CREATING NEW AGENDA: " + agendaId);
			
			agendaVote = new Agenda();
			agendaVote.setId(agendaId);
			agendaVote.setNumber(Integer.parseInt(xmlAgendaVote.getNo()));
			
			if (xmlAgendaVote.getYear() != null && xmlAgendaVote.getYear().length() > 0)
				agendaVote.setYear(Integer.parseInt(xmlAgendaVote.getYear()));
			
			if (xmlAgendaVote.getSessyr() != null && xmlAgendaVote.getSessyr().length() > 0)
				agendaVote.setSessionYear(Integer.parseInt(xmlAgendaVote.getSessyr()));

			agendaVote = pm.makePersistent(agendaVote);
			
		}
		else
		{
			logger.info("FOUND EXISTING AGENDA: " + agendaId);
		}
		
			
		List<Addendum> listAddendums = agendaVote.getAddendums();
		
		if (listAddendums == null)
		{
			listAddendums = new ArrayList<Addendum>();
			agendaVote.setAddendums(listAddendums);
		}
		
		Iterator<Object> itAddendum = xmlAgendaVote.getContent().iterator();
		
		Addendum addendum = null;
		Object next = null;
		
		while (itAddendum.hasNext())
		{
			next = itAddendum.next();
			
			if (next instanceof XMLAddendum)
			{
				XMLAddendum xmlAddendum = (XMLAddendum)next;
				
				String keyId = "a-" + agendaVote.getNumber() + '-' + agendaVote.getSessionYear() + '-' + xmlAddendum.getId();
				addendum = parseAddendum(pm, keyId, xmlAddendum, agendaVote, true);
				addendum.setAgenda(agendaVote);
				
				if (!listAddendums.contains(addendum))
				{
					listAddendums.add(addendum);
				}
			}
			else
			{
				logger.warn("Got AgendaVote content object anonamoly: " + next.toString());
			}
			
		}
		
		
		return agendaVote;
	}
	
	public Agenda handleXMLSenagenda (PersistenceManager pm, XMLSenagenda xmlAgenda) throws ParseException, SQLException
	{
	
		logger.info ("COMMITTEE AGENDA " + xmlAgenda.getNo() + " action=" + xmlAgenda.getAction());
	
		String agendaId = "commagenda-" + xmlAgenda.getNo() + '-' + xmlAgenda.getSessyr() + '-' + xmlAgenda.getYear();
		
		//figure out what is being removed or replaced
		Agenda agenda = (Agenda)PMF.getPersistedObject(pm, Agenda.class,agendaId, false);
		
		if (agenda == null)
		{
			//check if there is an one using the previous id format
			String tmpAgendaId = "commagenda-" + xmlAgenda.getNo() + '-' + xmlAgenda.getSessyr();// + '-' + xmlAgendaVote.getYear();
			agenda = (Agenda)PMF.getPersistedObject(pm, Agenda.class,tmpAgendaId, false);
		}
		
		String action = xmlAgenda.getAction();
		
		if (agenda != null && action.equalsIgnoreCase("remove"))
		{
			
			List<Addendum> listAdd = agenda.getAddendums();
			
			Iterator<Addendum> itAdd = listAdd.iterator();
			
			while (itAdd.hasNext())
			{
				List<Meeting> lstMeetings = itAdd.next().getMeetings();
				
				Iterator<Meeting> itMeeting = lstMeetings.iterator();
				
				while (itMeeting.hasNext())
				{
					Meeting meeting = itMeeting.next();
					
					PMF.removePersistedObject(pm, Meeting.class, meeting.getId());
				}
			}
			
			PMF.removePersistedObject(pm, Agenda.class, agenda.getId());
			logger.info("removing agenda: " + agenda.getId());
			
			try {
				engine.deleteSenateObject(agenda);
			} catch (Exception e) {
				
				logger.error("error deleting Agenda from search index: " + agenda.getId(),e);
			}
			

			return null;
		}
		
		if (agenda == null)
		{
			logger.info("CREATING NEW AGENDA: " + agendaId);
			
			agenda = new Agenda();
			agenda.setId(agendaId);
			agenda.setNumber(Integer.parseInt(xmlAgenda.getNo()));
			
			if (xmlAgenda.getYear() != null && xmlAgenda.getYear().length() > 0)
				agenda.setYear(Integer.parseInt(xmlAgenda.getYear()));
			
			if (xmlAgenda.getSessyr() != null && xmlAgenda.getSessyr().length() > 0)
				agenda.setSessionYear(Integer.parseInt(xmlAgenda.getSessyr()));

			agenda = pm.makePersistent(agenda);
		}
		else
		{
			logger.info("FOUND EXISTING AGENDA: " + agenda.getId());
		}
		
		
		List<Addendum> listAddendums = agenda.getAddendums();
		
		if (listAddendums == null)
		{
			listAddendums = new ArrayList<Addendum>();
			agenda.setAddendums(listAddendums);
		}
		
		Iterator<XMLAddendum> itAddendum = xmlAgenda.getAddendum().iterator();
		
		Addendum addendum = null;
		
		while (itAddendum.hasNext())
		{	
			XMLAddendum xmlAddendum = (XMLAddendum)itAddendum.next();
			String keyId = "a-" + agenda.getNumber() + '-' + agenda.getSessionYear() + '-' + agenda.getYear();
			
			addendum = parseAddendum(pm, keyId, xmlAddendum, agenda, false);		
			addendum.setAgenda(agenda);
		
			if (!listAddendums.contains(addendum))
				listAddendums.add(addendum);
			
		}
		
		return agenda;
	}
	
	public Addendum parseAddendum (PersistenceManager pm, String keyId, XMLAddendum xmlAddendum, Agenda agenda, boolean isVote) throws ParseException, SQLException
	{
		
		Addendum addendum = (Addendum)PMF.getPersistedObject(pm, Addendum.class,keyId, false);
		
		if (addendum == null)
		{
			addendum = new Addendum();
			addendum.setAddendumId(xmlAddendum.getId());
			addendum.setId(keyId);
			
			addendum = pm.makePersistent(addendum);
			
			logger.info("creating new addendum: " + addendum.getId());
		}
		else
		{
			logger.info("found existing addendum: " + addendum.getId());
		}
		
		if (xmlAddendum.getPubdate()!=null)
		{
			try
			{
				Date pubDateTime = LRS_DATETIME_FORMAT.parse(xmlAddendum.getPubdate().getContent() + xmlAddendum.getPubtime().getContent());
			
				addendum.setPublicationDateTime(pubDateTime);
			}
			catch (ParseException pe)
			{
				logger.warn("unable to parse addendum date/time format",pe);
			}
		}
		
		if (xmlAddendum.getWeekof()!=null)
			addendum.setWeekOf(xmlAddendum.getWeekof().getContent());
		
		List<Meeting> listMeetings = addendum.getMeetings();
		
		if (listMeetings == null)
		{
			listMeetings = new ArrayList<Meeting>();
			addendum.setMeetings(listMeetings);
		}
		
		Iterator<XMLCommittee> itComm = xmlAddendum.getCommittees().getCommittee().iterator();
		while (itComm.hasNext())
		{
			XMLCommittee xmlCommMeeting = itComm.next();

			String action = xmlCommMeeting.getAction();
			
			Meeting meeting = null;
		

			String meetingId = "meeting-" + xmlCommMeeting.getName().getContent() + '-' + agenda.getNumber() + '-' + agenda.getSessionYear() + '-' + agenda.getYear();
			//+ '-' + meetDateTime.getTime();
				
			//	keyId + '-' + xmlCommMeeting.getName().getContent();
			
			meeting = (Meeting)PMF.getPersistedObject(pm, Meeting.class, meetingId, false);

			if (meeting != null && action != null && action.equals("remove"))
			{
				if (isVote)
				{
					//meeting.getAttendees();
					
					meeting.getBills();
					
				}
				else
				{
					PMF.removePersistedObject(pm, Meeting.class, meeting.getId());
					logger.info("removing meeting: " + meeting.getId());
				}
				
				continue;
			}
			
			
			if (meeting == null)
			{
				meeting = new Meeting();			
				meeting.setId(meetingId);
				
				meeting = (Meeting)PMF.makePersistent(pm,meeting);
				logger.info("CREATED NEW meeting: " + meeting.getId());

			}
			
			Date meetDateTime = LRS_DATETIME_FORMAT.parse(xmlCommMeeting.getMeetdate().getContent() + xmlCommMeeting.getMeettime().getContent());

			
			meeting.setMeetingDateTime(meetDateTime);
			
			List<Addendum> addendums = meeting.getAddendums();
			
			if (addendums == null)
			{
				addendums = new ArrayList<Addendum>();
				meeting.setAddendums(addendums);
			}
			
			if (!addendums.contains(addendum))
				addendums.add(addendum);
			
				
			if (xmlCommMeeting.getLocation()!=null && xmlCommMeeting.getLocation().getContent().length() > 0)
				meeting.setLocation(xmlCommMeeting.getLocation().getContent());
			
			if (xmlCommMeeting.getMeetday()!=null && xmlCommMeeting.getMeetday().getContent().length() > 0)
				meeting.setMeetday(xmlCommMeeting.getMeetday().getContent());
			
			if (xmlCommMeeting.getNotes()!=null && xmlCommMeeting.getNotes().getContent().length() > 0)
				meeting.setNotes(xmlCommMeeting.getNotes().getContent());
			
			if (xmlCommMeeting.getName()!=null && xmlCommMeeting.getName().getContent().length() > 0)
			{
				String commName = xmlCommMeeting.getName().getContent();
				String commChair = null;
				if (xmlCommMeeting.getChair()!=null)
					commChair = xmlCommMeeting.getChair().getContent();
				
				Committee committee = PMF.getCommittee(pm, commName);
				
				meeting.setCommitteeChair(commChair);
				meeting.setCommitteeName(commName);
				meeting.setCommittee(committee);
			}
			
			if (!listMeetings.contains(meeting))
			{
				listMeetings.add(meeting);
			
			}
			
			
			if (xmlCommMeeting.getBills()!=null)
			{

				List<Bill> listBills = meeting.getBills();
				
				if (listBills == null)
				{
					listBills = new ArrayList<Bill>();
					meeting.setBills(listBills);
				}
				
				Iterator<XMLBill> itBills = xmlCommMeeting.getBills().getBill().iterator();
				Bill bill = null;
				
				while (itBills.hasNext())
				{
					XMLBill xmlBill = itBills.next();
					
					bill = handleXMLBill (pm, meeting, xmlBill, addendum.getAgenda().getSessionYear());
					
					if (!listBills.contains(bill))
					{
						logger.info("adding bill:" + bill.getSenateBillNo() + " to meeting:" + meeting.getId());
						listBills.add(bill);
					}
					else
					{
						logger.info("bill:" + bill.getSenateBillNo() + " already added to meeting:" + meeting.getId());
					}
				}
			}			

		}
			

		return addendum;
	}
	
	//
	/*
	if (xmlCommMeeting.getAttendancelist() != null)
	{
		Iterator<XMLMember> itAttendance = xmlCommMeeting.getAttendancelist().getMember().iterator();
		XMLMember xmlMember = null;
		
		//as of 3/10/2010 - Per Stengel, we are no longer tracking attendence at meetings as it apparently isn't accurate data
		
		List<Attendance> listAttendees = meeting.getAttendees();
		
		if (listAttendees == null)
		{
			listAttendees = new ArrayList<Attendance>();
		
			meeting.setAttendees(listAttendees);
		}
		
		while (itAttendance.hasNext())
		{
			xmlMember = itAttendance.next();
			
			Attendance attendanceRecord = null;
			
			String aRecordId = meeting.getId() + '-' + xmlMember.getName().getContent();
			
			attendanceRecord = (Attendance)PMF.getPersistedObject(pm, Attendance.class, aRecordId, false);
			
			if (attendanceRecord == null)
			{
			
				attendanceRecord = new Attendance ();
				attendanceRecord.setId(aRecordId);
				attendanceRecord.setName(xmlMember.getName().getContent());
				
				Person person = PMF.getPerson(pm, xmlMember.getName().getContent());
				attendanceRecord.setMember(person);
	
				attendanceRecord.setParty(xmlMember.getParty().getContent());
				attendanceRecord.setRank(Integer.parseInt(xmlMember.getRank().getContent()));
	
				attendanceRecord.setMeeting(meeting);
			}

			attendanceRecord.setAttendance(xmlMember.getAttendance().getContent());

			
			if (listAttendees.contains(attendanceRecord))
			{
	//			logger.warn("list already has this record: " + attendanceRecord.getId());
		// do nothing
			}
			else
			{
				listAttendees.add(attendanceRecord);
			}
			
		}
	}
	*/
	
	public CommitteeParser ()
	{
		
		
		
	}
	
	public XMLSENATEDATA parseStream (Reader reader) throws ParseException, JAXBException
	{
		String packageName = "gov.nysenate.openleg.xml.committee";
	    JAXBContext jc = JAXBContext.newInstance( packageName );
	    
	    Unmarshaller u = jc.createUnmarshaller();
	    XMLSENATEDATA sd = (XMLSENATEDATA)u.unmarshal( reader );
	    
	    new Date();

	    return sd;
	}
	
	private static Bill getBill (PersistenceManager pm, String billId, int sessionYear, String sponsorName)
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
		
		Bill bill = PMF.getBill(pm, senateBillNo, sessionYear);
		
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
}
