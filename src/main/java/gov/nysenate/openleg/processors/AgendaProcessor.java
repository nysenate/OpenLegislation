package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.ChangeLogger;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.xml.committee.XMLAddendum;
import gov.nysenate.openleg.xml.committee.XMLBill;
import gov.nysenate.openleg.xml.committee.XMLCommittee;
import gov.nysenate.openleg.xml.committee.XMLMember;
import gov.nysenate.openleg.xml.committee.XMLSENATEDATA;
import gov.nysenate.openleg.xml.committee.XMLSenagenda;
import gov.nysenate.openleg.xml.committee.XMLSenagendavote;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class AgendaProcessor implements OpenLegConstants {

    private final Logger logger;
    private final GregorianCalendar calendar;
    public static SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    public AgendaProcessor() {
        logger = Logger.getLogger(this.getClass());
        calendar = new GregorianCalendar();
    }

    public void process(File file, Storage storage) throws IOException, JAXBException {
        String packageName = "gov.nysenate.openleg.xml.committee";
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        XMLSENATEDATA senateData = (XMLSENATEDATA)u.unmarshal(new FileReader(file));

        // TODO: We need a better default here
        Date modifiedDate = null;
        try {
            modifiedDate = sobiDateFormat.parse(file.getName());
        } catch (ParseException e) {
            logger.error("Error parsing file date.", e);
        }

        for(Object next : senateData.getSenagendaOrSenagendavote()) {
            if (next instanceof XMLSenagenda) {
                Agenda agenda = handleXMLSenagenda(storage,(XMLSenagenda)next, modifiedDate);

                if (agenda != null) {
                    agenda.addSobiReference(file.getName());
                    agenda.setModified(modifiedDate.getTime());
                    String key = agenda.getYear()+"/agenda/"+agenda.getId();
                    storage.set(key, agenda);
                    ChangeLogger.record(key, storage, modifiedDate);

                    for (Addendum addendum : agenda.getAddendums()) {
                        for (Meeting meeting : addendum.getMeetings()) {
                            calendar.setTime(meeting.getMeetingDateTime());
                            key = calendar.get(GregorianCalendar.YEAR)+"/meeting/"+meeting.getId();
                            logger.info(key);

                            // TODO: We don't actually know if the meeting was modified or not
                            // This might be a false positive change
                            meeting.setModified(addendum.getPublicationDateTime().getTime());
                            storage.set(key, meeting);
                            ChangeLogger.record(key, storage, modifiedDate);
                        }
                    }
                }

            } else if (next instanceof XMLSenagendavote) {
                Agenda agenda = handleXMLSenagendavote(storage, (XMLSenagendavote)next, modifiedDate);

                if (agenda != null) {
                    agenda.addSobiReference(file.getName());
                    agenda.setModified(modifiedDate.getTime());
                    String key = agenda.getYear()+"/agenda/"+agenda.getId();
                    storage.set(key, agenda);
                    ChangeLogger.record(key, storage, modifiedDate);

                    for (Addendum addendum : agenda.getAddendums()) {
                        for (Meeting meeting : addendum.getMeetings()) {
                            calendar.setTime(meeting.getMeetingDateTime());
                            key = calendar.get(GregorianCalendar.YEAR)+"/meeting/"+meeting.getId();
                            logger.info(key);
                            storage.set(key, meeting);
                            ChangeLogger.record(key, storage, modifiedDate);
                        }
                    }
                }

            } else {
                // TODO: log error here; maybe not. This counts strings as "text" nodes I think
            }
        }
    }

    public Bill handleXMLBill(Storage storage, Meeting meeting, XMLBill xmlBill, int sessionYear, Date date) {
        Bill bill = getBill(storage, xmlBill.getNo(), sessionYear, xmlBill.getSponsor().getContent());

        if (xmlBill.getTitle() != null && bill.getActClause().isEmpty()) {
            bill.setActClause(xmlBill.getTitle().getContent());
        }

        if (xmlBill.getVotes() != null) {
            int ayeCount = -1;
            int nayCount = -1;
            Date voteDate = meeting.getMeetingDateTime();
            Vote vote = new Vote(bill, voteDate, ayeCount, nayCount);

            // remove the old vote
            // TODO: will this ever actually work with aye/nay counts at -1?
            //    I suppose is will now that I'm using sequence numbers instead
            bill.removeVote(vote);

            vote.setVoteType(Vote.VOTE_TYPE_COMMITTEE);
            vote.setDescription(meeting.getCommitteeName());
            for( XMLMember member : xmlBill.getVotes().getMember()) {
                Person person = new Person(member.getName().getContent());
                String voteType = member.getVote().getContent().toLowerCase();

                logger.debug("adding vote: " + bill.getSenateBillNo() + " - " + voteType + " - " + person.getFullname());

                if (voteType.startsWith("abstain"))
                    vote.addAbstain(person);
                else if (voteType.startsWith("aye w/r"))
                    vote.addAyeWR(person);
                else if (voteType.startsWith("aye"))
                    vote.addAye(person);
                else if (voteType.startsWith("excused"))
                    vote.addExcused(person);
                else if (voteType.startsWith("nay"))
                    vote.addNay(person);
                //else if (voteType.startsWith("absent"))
                //    vote.Absent(person);
            }

            // Add the new vote, effectively replacing an older copy
            bill.addVote(vote);

            // Make sure the bill gets updated on disc
            String key = String.valueOf(bill.getYear())+"/bill/"+bill.getSenateBillNo();
            storage.set(key, bill);
            ChangeLogger.record(key, storage, date);
        }

        return bill;
    }

    public Agenda handleXMLSenagendavote(Storage storage, XMLSenagendavote xmlAgendaVote, Date date) throws IOException {
        // TODO: It doesn't look like we parse any action here. Should we?

        // Sometimes these come up blank on bad feeds or something
        // TODO: Look into this with better documentation
        if (xmlAgendaVote.getYear().isEmpty())
            return null;

        logger.info("COMMITTEE AGENDA VOTE RECORD " + xmlAgendaVote.getNo());
        String agendaId = "commagenda-" + xmlAgendaVote.getNo() + '-' + xmlAgendaVote.getSessyr() + '-' + xmlAgendaVote.getYear();

        // Load the old agenda vote or create a new one
        String key = xmlAgendaVote.getYear()+"/agenda/"+agendaId;
        Agenda agendaVote = (Agenda)storage.get(key, Agenda.class);
        if (agendaVote == null) {
            logger.info("CREATING NEW AGENDA: " + agendaId);
            agendaVote = new Agenda();
            agendaVote.setId(agendaId);
            agendaVote.setNumber(Integer.parseInt(xmlAgendaVote.getNo()));

            if (xmlAgendaVote.getYear() != null && xmlAgendaVote.getYear().length() > 0) {
                agendaVote.setYear(Integer.parseInt(xmlAgendaVote.getYear()));
            }

            if (xmlAgendaVote.getSessyr() != null && xmlAgendaVote.getSessyr().length() > 0) {
                agendaVote.setSessionYear(Integer.parseInt(xmlAgendaVote.getSessyr()));
            }

        } else {
            logger.info("FOUND EXISTING AGENDA: " + agendaId);
        }

        // Add all the addendums to the agenda
        List<Addendum> listAddendums = agendaVote.getAddendums();
        for(Object next : xmlAgendaVote.getContent()) {

            if (next instanceof XMLAddendum) {
                XMLAddendum xmlAddendum = (XMLAddendum) next;
                // String keyId = "a-" + agendaVote.getNumber() + '-' + agendaVote.getSessionYear() + '-' + xmlAddendum.getId();
                String keyId = xmlAddendum.getId() + "-" + agendaVote.getNumber() + '-' + agendaVote.getSessionYear() + '-' + agendaVote.getYear();
                Addendum addendum = parseAddendum(storage, keyId, xmlAddendum, agendaVote, true, date);
                addendum.setAgenda(agendaVote);

                // Don't repeat yourself
                if (!listAddendums.contains(addendum)) {
                    listAddendums.add(addendum);
                }
            } else {
                // Don't log the text elements containing whitespace
                if (!(next instanceof String) || !((String) next).trim().isEmpty()) {
                    logger.error("Got AgendaVote content object anonamoly " + next.getClass()+ ": "+next);
                }
            }
        }

        return agendaVote;
    }

    public Agenda handleXMLSenagenda(Storage storage, XMLSenagenda xmlAgenda, Date date) throws IOException {
        // Sometimes these come up blank on bad feeds or something
        // TODO: Look into this with better documentation
        if (xmlAgenda.getYear().isEmpty())
            return null;

        logger.info("COMMITTEE AGENDA " + xmlAgenda.getNo() + " action=" + xmlAgenda.getAction());

        String agendaId = "commagenda-" + xmlAgenda.getNo() + '-' + xmlAgenda.getSessyr() + '-' + xmlAgenda.getYear();

        String key = xmlAgenda.getYear()+"/agenda/"+agendaId;
        Agenda agenda = (Agenda)storage.get(key, Agenda.class);

        String action = xmlAgenda.getAction();

        if (agenda != null && action.equalsIgnoreCase("remove")) {
            logger.info("removing agenda: " + agenda.getId());
            storage.del(key);
            ChangeLogger.delete(key, storage, date);

            for (Addendum addendum : agenda.getAddendums()) {
                for (Meeting meeting : addendum.getMeetings()) {
                    key = meeting.getYear()+"/meeting/"+meeting.getId();
                    storage.del(key);
                    ChangeLogger.delete(key, storage, date);
                }
            }

            return null;

        } else if (agenda == null) {
            logger.info("CREATING NEW AGENDA: " + agendaId);

            agenda = new Agenda();
            agenda.setId(agendaId);
            agenda.setNumber(Integer.parseInt(xmlAgenda.getNo()));

            if (xmlAgenda.getYear() != null && xmlAgenda.getYear().length() > 0) {
                agenda.setYear(Integer.parseInt(xmlAgenda.getYear()));
            }

            if (xmlAgenda.getSessyr() != null && xmlAgenda.getSessyr().length() > 0) {
                agenda.setSessionYear(Integer.parseInt(xmlAgenda.getSessyr()));
            }

        } else {
            logger.debug("FOUND EXISTING AGENDA: " + agenda.getId());
        }


        // Build a list of addendums on the current list.
        // TOOD: is this resent whole each time or not?
        List<Addendum> listAddendums = agenda.getAddendums();
        for(XMLAddendum xmlAddendum : xmlAgenda.getAddendum()) {
            String keyId = xmlAddendum.getId() + "-" + agenda.getNumber() + '-' + agenda.getSessionYear() + '-' + agenda.getYear();

            Addendum addendum = parseAddendum(storage, keyId, xmlAddendum, agenda, false, date);
            addendum.setAgenda(agenda);

            // Don't add duplicates!
            // TODO: What about addendums that are updated? Can that happen?
            if (!listAddendums.contains(addendum)) {
                listAddendums.add(addendum);
            }
        }

        return agenda;
    }

    public Addendum parseAddendum(Storage storage, String keyId, XMLAddendum xmlAddendum, Agenda agenda, boolean isVote, Date date) throws IOException {
        // TODO: Are addendums resent whole each time?
        // TODO: What are addendums?

        // Try to retrieve existing addendum
        Addendum addendum = new Addendum();
        addendum.setId(keyId);
        int index = agenda.getAddendums().indexOf(addendum);
        if (index == -1) {
            // Create a new one if it isn't found!
            addendum = new Addendum();
            addendum.setAddendumId(xmlAddendum.getId());
            addendum.setId(keyId);
            addendum.setAgenda(agenda);
            logger.debug("creating new addendum: " + addendum.getId());

        } else {
            addendum = agenda.getAddendums().get(index);
            addendum.setAgenda(agenda);
        }

        // Get the publication date if available
        if (xmlAddendum.getPubdate() != null) {
            try {
                Date pubDateTime = LRS_DATETIME_FORMAT.parse(xmlAddendum
                        .getPubdate().getContent()
                        + xmlAddendum.getPubtime().getContent());

                addendum.setPublicationDateTime(pubDateTime);
            } catch (ParseException pe) {
                logger.error("unable to parse addendum date/time format", pe);
            }
        }

        // Set weekOf if available.
        // TODO: What is the meaning of this?
        if (xmlAddendum.getWeekof() != null) {
            addendum.setWeekOf(xmlAddendum.getWeekof().getContent());
        }

        List<Meeting> listMeetings = addendum.getMeetings();
        for( XMLCommittee xmlCommMeeting : xmlAddendum.getCommittees().getCommittee()) {
            String action = xmlCommMeeting.getAction();

            String meetingId = "meeting-" + xmlCommMeeting.getName().getContent() + '-' + agenda.getNumber() + '-' + agenda.getSessionYear() + '-' + agenda.getYear();

            Meeting meeting = agenda.getCommitteeMeeting(meetingId);

            if (meeting != null && action != null) {
                if (action.matches("(remove|replace)")) {

                    // Pretty sure this is always false
                    if (!isVote) {
                        // Always remove the meeting
                        logger.info("removing meeting: " + meeting.getId());

                        // Delete the meeting and save the agenda
                        String key = meeting.getYear()+"/meeting/"+meeting.getId();
                        storage.del(key);
                        ChangeLogger.delete(key, storage, date);

                        agenda.removeCommitteeMeeting(meeting);
                        key = agenda.getYear()+"/agenda/"+agenda.getId();
                        storage.set(key, agenda);
                        ChangeLogger.record(key, storage, date);
                    }

                    if (action.equals("remove")) {
                        // If the action was remove, then skip the add meeting parts
                        continue;
                    }
                }


            } else if (meeting == null) {
                // In rare cases a meeting can be initially sent with a remove flag
                // in these cases we don't need to do anything.
                if (action != null && action.toLowerCase().equals("remove")) {
                    continue;

                } else {
                    meeting = new Meeting();
                    meeting.setId(meetingId);
                    meeting.setYear(agenda.getYear());
                    logger.info("CREATED NEW meeting: "+agenda.getYear()+"; " + meeting.getId());
                }
            }

            // Get the meeting date if possible
            if( xmlCommMeeting.getMeetdate() != null) {
                try {
                    Date meetDateTime = LRS_DATETIME_FORMAT.parse(xmlCommMeeting
                            .getMeetdate().getContent()
                            + xmlCommMeeting.getMeettime().getContent());

                    meeting.setMeetingDateTime(meetDateTime);
                } catch (ParseException e) {
                    logger.error("Could not parse meeting date", e);
                    continue;
                }
            }

            // Not entirely sure what is going on here
            // TODO: What is the relationship between meetings and addendums
            List<Addendum> addendums = meeting.getAddendums();
            if (!addendums.contains(addendum))
                addendums.add(addendum);

            // Add a bunch of meeting metadata
            if (xmlCommMeeting.getLocation() != null
                    && xmlCommMeeting.getLocation().getContent().length() > 0)
                meeting.setLocation(xmlCommMeeting.getLocation().getContent());

            if (xmlCommMeeting.getMeetday() != null
                    && xmlCommMeeting.getMeetday().getContent().length() > 0)
                meeting.setMeetday(xmlCommMeeting.getMeetday().getContent());

            if (xmlCommMeeting.getNotes() != null
                    && xmlCommMeeting.getNotes().getContent().length() > 0) {
                meeting.setNotes(xmlCommMeeting.getNotes().getContent());
            }

            if (xmlCommMeeting.getName() != null
                    && xmlCommMeeting.getName().getContent().length() > 0) {
                String commName = xmlCommMeeting.getName().getContent();
                String commChair = null;
                if (xmlCommMeeting.getChair() != null)
                    commChair = xmlCommMeeting.getChair().getContent();

                meeting.setCommitteeChair(commChair);
                meeting.setCommitteeName(commName);
            }

            if (!listMeetings.contains(meeting)) {
                listMeetings.add(meeting);
            }

            if (xmlCommMeeting.getBills() != null) {

                List<Bill> listBills = meeting.getBills();

                if (listBills == null) {
                    listBills = new ArrayList<Bill>();
                    meeting.setBills(listBills);
                }

                for(XMLBill xmlBill : xmlCommMeeting.getBills().getBill()) {
                    Bill bill = handleXMLBill(storage, meeting, xmlBill, addendum.getAgenda().getSessionYear(), date);

                    if (!listBills.contains(bill)) {
                        logger.debug("adding bill:" + bill.getSenateBillNo() + " to meeting:" + meeting.getId());
                        listBills.add(bill);
                    } else {
                        // TODO: It isn't doing any merging here?
                        logger.debug("bill:" + bill.getSenateBillNo() + " already added to meeting:" + meeting.getId() + ", merging.");
                    }
                }
            }
        }
        addendum.setMeetings(listMeetings);
        return addendum;
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

}
