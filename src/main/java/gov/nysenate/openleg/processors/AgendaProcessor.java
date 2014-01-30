package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.SOBIBlock;
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
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class AgendaProcessor implements OpenLegConstants {

    private final Logger logger;
    private Date modifiedDate;
    public static SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    public AgendaProcessor() {
        logger = Logger.getLogger(this.getClass());
    }

    public void process(File file, Storage storage) throws IOException, JAXBException {
        String packageName = "gov.nysenate.openleg.xml.committee";
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        XMLSENATEDATA senateData = (XMLSENATEDATA)u.unmarshal(new FileReader(file));

        // TODO: We need a better default here
        modifiedDate = null;
        try {
            modifiedDate = sobiDateFormat.parse(file.getName());
        } catch (ParseException e) {
            logger.error("Error parsing file date.", e);
        }

        ChangeLogger.setContext(file, modifiedDate);
        for(Object next : senateData.getSenagendaOrSenagendavote()) {
            if (next instanceof XMLSenagenda) {
                Agenda agenda = handleXMLSenagenda(storage,(XMLSenagenda)next);

                if (agenda != null) {
                    agenda.addDataSource(file.getName());
                    agenda.setModifiedDate(modifiedDate);
                    if (agenda.getPublishDate() == null) {
                        agenda.setPublishDate(modifiedDate);
                    }
                    storage.set(agenda);
                    ChangeLogger.record(storage.key(agenda), storage);

                    for (Addendum addendum : agenda.getAddendums()) {
                        for (Meeting meeting : addendum.getMeetings()) {
                            // TODO: We don't actually know if the meeting was modified or not
                            // This might be a false positive change
                            meeting.setModifiedDate(addendum.getPublishDate());
                            storage.set(meeting);
                            ChangeLogger.record(storage.key(meeting), storage);
                        }
                    }
                }

            } else if (next instanceof XMLSenagendavote) {
                Agenda agenda = handleXMLSenagendavote(storage, (XMLSenagendavote)next);

                if (agenda != null) {
                    agenda.addDataSource(file.getName());
                    agenda.setModifiedDate(modifiedDate);
                    if (agenda.getPublishDate() == null) {
                        agenda.setPublishDate(modifiedDate);
                    }
                    storage.set(agenda);
                    ChangeLogger.record(storage.key(agenda), storage);

                    for (Addendum addendum : agenda.getAddendums()) {
                        for (Meeting meeting : addendum.getMeetings()) {
                            storage.set(meeting);
                            ChangeLogger.record(storage.key(meeting), storage);
                        }
                    }
                }

            } else {
                logger.warn("Unknown agenda type found: "+next);
            }
        }
    }

    public Bill handleXMLBill(Storage storage, Meeting meeting, XMLBill xmlBill, int sessionYear) {
        Bill bill = getBill(storage, xmlBill.getNo(), sessionYear, xmlBill.getSponsor().getContent());

        if (xmlBill.getTitle() != null && bill.getActClause().isEmpty()) {
            bill.setActClause(xmlBill.getTitle().getContent());
        }

        if (xmlBill.getVotes() != null) {
            Date voteDate = meeting.getMeetingDateTime();
            Vote vote = new Vote(bill, voteDate, Vote.VOTE_TYPE_COMMITTEE, "1");
            vote.setPublishDate(modifiedDate);
            vote.setModifiedDate(modifiedDate);

            // remove the old vote
            // TODO: will this ever actually work with aye/nay counts at -1?
            //    I suppose is will now that I'm using sequence numbers instead
            bill.removeVote(vote);

            vote.setVoteType(Vote.VOTE_TYPE_COMMITTEE);
            vote.setDescription(meeting.getCommitteeName());
            for( XMLMember member : xmlBill.getVotes().getMember()) {
                Person person = new Person(member.getName().getContent());
                String voteType = member.getVote().getContent().toLowerCase();

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
                else if (voteType.startsWith("absent"))
                    vote.addAbsent(person);
            }

            // Add the new vote, effectively replacing an older copy
            bill.updateVote(vote);

            // Make sure the bill gets updated on disc
            storage.set(bill);
            ChangeLogger.record(storage.key(bill), storage);
        }

        return bill;
    }

    public Agenda handleXMLSenagendavote(Storage storage, XMLSenagendavote xmlAgendaVote) throws IOException {
        // TODO: It doesn't look like we parse any action here. Should we?

        // Sometimes these come up blank on bad feeds or something
        // TODO: Look into this with better documentation
        if (xmlAgendaVote.getYear().isEmpty())
            return null;

        Agenda agendaVote = new Agenda(
            Integer.parseInt(xmlAgendaVote.getSessyr()),
            Integer.parseInt(xmlAgendaVote.getYear()),
            Integer.parseInt(xmlAgendaVote.getNo())
        );
        String key = storage.key(agendaVote);

        // Load the old agenda vote or create a new one
        if (storage.get(key, Agenda.class) != null) {
            agendaVote = (Agenda)storage.get(key, Agenda.class);
        }

        // Add all the addendums to the agenda
        List<Addendum> listAddendums = agendaVote.getAddendums();
        for(Object next : xmlAgendaVote.getContent()) {

            if (next instanceof XMLAddendum) {
                XMLAddendum xmlAddendum = (XMLAddendum) next;
                Addendum addendum = parseAddendum(storage, xmlAddendum, agendaVote, true);
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

    public Agenda handleXMLSenagenda(Storage storage, XMLSenagenda xmlAgenda) throws IOException {
        // Sometimes these come up blank on bad feeds or something
        // TODO: Look into this with better documentation
        if (xmlAgenda.getYear().isEmpty())
            return null;

        logger.info("COMMITTEE AGENDA " + xmlAgenda.getNo() + " action=" + xmlAgenda.getAction());
        Agenda agenda = new Agenda(
            Integer.parseInt(xmlAgenda.getSessyr()),
            Integer.parseInt(xmlAgenda.getYear()),
            Integer.parseInt(xmlAgenda.getNo())
        );
        String key = storage.key(agenda);

        String action = xmlAgenda.getAction();
        if (agenda != null && action.equalsIgnoreCase("remove")) {
            logger.info("removing agenda: " + agenda.getOid());
            storage.del(key);
            ChangeLogger.delete(key, storage);

            for (Addendum addendum : agenda.getAddendums()) {
                for (Meeting meeting : addendum.getMeetings()) {
                    key = storage.key(meeting);
                    storage.del(key);
                    ChangeLogger.delete(key, storage);
                }
            }

            return null;

        }
        else if (storage.get(key, Agenda.class) != null) {
            // Use an existing agenda if we can find one.
            agenda = (Agenda)storage.get(key, Agenda.class);
        }


        // Build a list of addendums on the current list.
        // TOOD: is this resent whole each time or not?
        List<Addendum> listAddendums = agenda.getAddendums();
        for(XMLAddendum xmlAddendum : xmlAgenda.getAddendum()) {
            Addendum addendum = parseAddendum(storage, xmlAddendum, agenda, false);
            addendum.setAgenda(agenda);

            // Don't add duplicates!
            // TODO: What about addendums that are updated? Can that happen?
            if (!listAddendums.contains(addendum)) {
                listAddendums.add(addendum);
            }
        }

        return agenda;
    }

    public Addendum parseAddendum(Storage storage, XMLAddendum xmlAddendum, Agenda agenda, boolean isVote) throws IOException {
        // TODO: Are addendums resent whole each time?

        // Get the publication date if available
        String addendumId = xmlAddendum.getId();

        String weekOf = "";
        if (xmlAddendum.getWeekof() != null) {
            weekOf = xmlAddendum.getWeekof().getContent();
        }

        Date publishDate = null;
        if (xmlAddendum.getPubdate() != null) {
            try {
                publishDate = LRS_DATETIME_FORMAT.parse(xmlAddendum.getPubdate().getContent() + xmlAddendum.getPubtime().getContent());
            } catch (ParseException pe) {
                logger.error("unable to parse addendum date/time format", pe);
            }
        }

        // Try to retrieve existing addendum and update it
        Addendum addendum = new Addendum(addendumId, weekOf, publishDate, agenda.getNumber(), agenda.getYear());
        addendum.setAgenda(agenda);
        for (Addendum oldAddendum : agenda.getAddendums()) {
            if (oldAddendum.getOid().equals(addendum.getOid())) {
                addendum = oldAddendum;
                addendum.setAgenda(agenda); // Nulled out during serialization.
                addendum.setWeekOf(weekOf);
                addendum.setPublishDate(publishDate);
                break;
            }
        }

        List<Meeting> listMeetings = addendum.getMeetings();
        for( XMLCommittee xmlCommMeeting : xmlAddendum.getCommittees().getCommittee()) {
            String action = xmlCommMeeting.getAction();
            String commName = xmlCommMeeting.getName().getContent();
            if (commName == null) {
                continue;
            }

            if (action != null && action.equals("remove")) {
                for (Meeting meeting : new ArrayList<Meeting>(listMeetings)) {
                    if (meeting.getCommitteeName().equals(commName)) {
                        logger.info("removing meeting: " + meeting.getOid());
                        // Delete the meeting and save the agenda
                        String key = storage.key(meeting);
                        storage.del(key);
                        ChangeLogger.delete(key, storage);
                        listMeetings.remove(meeting);
                        storage.set(agenda);
                        ChangeLogger.record(storage.key(agenda), storage);
                        break;
                    }
                }
                continue;
            }

            // If action isn't remove, we should have a date time.
            Date meetDateTime = null;
            try {
                meetDateTime = LRS_DATETIME_FORMAT.parse(xmlCommMeeting.getMeetdate().getContent() + xmlCommMeeting.getMeettime().getContent());
            } catch (ParseException e) {
                logger.error("Could not parse meeting date", e);
                continue;
            }

            Meeting meeting = new Meeting(commName, meetDateTime);
            meeting.setPublishDate(modifiedDate);
            String key = storage.key(meeting);
            Meeting oldMeeting = (Meeting)storage.get(key, Meeting.class);

            if (oldMeeting != null) {
                // If we have an old meeting, either use it or delete it if we are replacing.
                // This is wrong just like the rest of the meeting stuff. The replace is for
                // the addendum entry, you don't know how much to replace.
                if (action != null && action.equals("replace")) {
                    // If we are replacing votes it'll be handled in handleXmlBill
                    if (!isVote) {
                        logger.info("removing meeting: " + oldMeeting.getOid());
                        storage.del(key);
                        agenda.removeCommitteeMeeting(oldMeeting);
                    }
                }
                else {
                    // Use the old meeting of since it is not null or replaced.
                    meeting = oldMeeting;
                }
            }

            meeting.setModifiedDate(modifiedDate);

            // Add a bunch of meeting meta data
            if (xmlCommMeeting.getLocation() != null && xmlCommMeeting.getLocation().getContent().length() > 0) {
                meeting.setLocation(xmlCommMeeting.getLocation().getContent());
            }

            if (xmlCommMeeting.getMeetday() != null && xmlCommMeeting.getMeetday().getContent().length() > 0) {
                meeting.setMeetday(xmlCommMeeting.getMeetday().getContent());
            }

            if (xmlCommMeeting.getNotes() != null && xmlCommMeeting.getNotes().getContent().length() > 0) {
                // latin1 encoded
                String notes = xmlCommMeeting.getNotes().getContent();
                notes = new String(notes.getBytes("CP850"), "latin1");
                meeting.setNotes(notes);
            }

            if (xmlCommMeeting.getChair() != null && xmlCommMeeting.getChair().getContent().length() > 0) {
                meeting.setCommitteeChair(xmlCommMeeting.getChair().getContent());
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
                    Bill bill = handleXMLBill(storage, meeting, xmlBill, addendum.getAgenda().getSession());
                    if (!listBills.contains(bill)) {
                        logger.debug("adding bill:" + bill.getBillId() + " to meeting:" + meeting.getOid());
                        listBills.add(bill);
                    }
                    else {
                        // Since we already have a reference don't do anything. handleXMLBill will update the bill details
                    }
                }
            }
            storage.set(meeting);
            ChangeLogger.record(storage.key(meeting), storage);
        }
        addendum.setMeetings(listMeetings);
        return addendum;
    }

    private Bill getBill(Storage storage, String billId, int year, String sponsorName) {
        String[] sponsors = {""};
        if (sponsorName != null) {
            sponsors = sponsorName.trim().split(",");
        }

        BillProcessor processor = new BillProcessor();
        SOBIBlock mockBlock = new SOBIBlock(year+billId+(billId.matches("[A-Z]$") ? "" : " ")+1+"     ");

        // This is a crappy situation, all bills on calendars should already exist but sometimes they won't.
        // This almost exclusively because we are missing sobi files. It shouldn't happen in production but
        // does frequently in development.
        Bill bill = processor.getOrCreateBill(mockBlock, modifiedDate, storage);
        bill.setSponsor(new Person(sponsors[0].trim()));

        // It must be published if it is on the agenda
        if (!bill.isPublished()) {
            bill.setPublishDate(modifiedDate);
            bill.setActive(true);
            processor.saveBill(bill, storage);
        }

        // Other sponsors are removed when a calendar/agenda is resent without
        // The other sponsor included in the sponsors list.
        ArrayList<Person> otherSponsors = new ArrayList<Person>();
        for (int i = 1; i < sponsors.length; i++) {
            otherSponsors.add(new Person(sponsors[i].trim()));
        }

        if (!bill.getOtherSponsors().equals(otherSponsors)) {
            bill.setOtherSponsors(otherSponsors);
            new BillProcessor().saveBill(bill, storage);
        }

        return bill;
    }

}
