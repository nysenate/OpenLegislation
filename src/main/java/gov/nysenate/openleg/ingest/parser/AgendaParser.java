package gov.nysenate.openleg.ingest.parser;

import gov.nysenate.openleg.ingest.JsonDao;
import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.xml.committee.XMLAddendum;
import gov.nysenate.openleg.xml.committee.XMLBill;
import gov.nysenate.openleg.xml.committee.XMLCommittee;
import gov.nysenate.openleg.xml.committee.XMLMember;
import gov.nysenate.openleg.xml.committee.XMLSENATEDATA;
import gov.nysenate.openleg.xml.committee.XMLSenagenda;
import gov.nysenate.openleg.xml.committee.XMLSenagendavote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class AgendaParser extends SenateParser<Agenda> implements OpenLegConstants {

    public AgendaParser() {
        this(null, null);
    }

    public AgendaParser(JsonDao jsonDao, SearchEngine searchEngine) {
        super(AgendaParser.class, jsonDao, searchEngine);
    }

    @Override
    public void parse(File file) {
        try {
            doParse(file);
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (ParseException e) {
            logger.error(e);
        } catch (JAXBException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void doParse(File file) throws ParseException, JAXBException,
    FileNotFoundException, IOException {

        XMLSENATEDATA senateData = parseStream(new FileReader(file));

        Iterator<Object> it = senateData.getSenagendaOrSenagendavote()
                .iterator();
        Object next = null;

        while (it.hasNext()) {
            next = it.next();

            try {
                if (next instanceof XMLSenagenda) {

                    XMLSenagenda cAgenda = (XMLSenagenda) next;
                    Agenda agenda = handleXMLSenagenda(cAgenda);

                    if (agenda != null) {
                        newSenateObjects.add(agenda);
                    }

                } else if (next instanceof XMLSenagendavote) {
                    XMLSenagendavote xmlAgendaVote = (XMLSenagendavote) next;
                    Agenda agenda = handleXMLSenagendavote(xmlAgendaVote);

                    if (agenda != null) {
                        newSenateObjects.add(agenda);
                    }

                }
            } catch (Exception e) {
                logger.warn("EXITING: ERROR PROCESSING: " + file.getAbsolutePath() + "; "
                        + e.getLocalizedMessage());
            }
        }
    }

    public Bill handleXMLBill(Meeting meeting, XMLBill xmlBill, int sessionYear) {
        Bill bill = getBill(xmlBill.getNo(), sessionYear, xmlBill.getSponsor()
                .getContent());

        if (xmlBill.getSponsor() != null) {
            if (bill.getSponsor() == null) {
                bill.setSponsor(new Person(xmlBill.getSponsor().getContent()));
            }
        }

        if (xmlBill.getTitle() != null) {
            if (bill.getSummary() == null) {
                bill.setSummary(xmlBill.getTitle().getContent());
            }
        }

        if (xmlBill.getVotes() != null) {
            Iterator<XMLMember> itMemberVotes = xmlBill.getVotes().getMember()
                    .iterator();
            XMLMember member = null;

            int ayeCount = -1;
            int nayCount = -1;

            Date voteDate = meeting.getMeetingDateTime();

            Vote vote = new Vote(bill, voteDate, ayeCount, nayCount);

            bill.removeVote(vote);

            vote.setVoteType(Vote.VOTE_TYPE_COMMITTEE);
            vote.setDescription(meeting.getCommitteeName());

            while (itMemberVotes.hasNext()) {
                member = itMemberVotes.next();
                Person person = new Person(member.getName().getContent());
                String voteType = member.getVote().getContent().toLowerCase();

                logger.info("adding vote: " + bill.getSenateBillNo() + " - "
                        + voteType + " - " + person.getFullname());

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
            }

            bill.addVote(vote);
            if (newSenateObjects.contains(bill)) {
                int index = newSenateObjects.indexOf(bill);
                ((Bill) newSenateObjects.get(index)).merge(bill);
            } else {
                newSenateObjects.add(bill);
            }
        }

        return bill;
    }

    public Agenda handleXMLSenagendavote(XMLSenagendavote xmlAgendaVote)
            throws ParseException, SQLException {

        Agenda agendaVote = null;
        String agendaId = "commagenda-" + xmlAgendaVote.getNo() + '-'
                + xmlAgendaVote.getSessyr() + '-' + xmlAgendaVote.getYear();

        if(canWrite(SenateParser.JSON)) {
            agendaVote = jsonDao.load(agendaId, xmlAgendaVote.getSessyr(), "agenda", Agenda.class);
        }

        logger.info("COMMITTEE AGENDA VOTE RECORD " + xmlAgendaVote.getNo());

        if (agendaVote == null) {
            logger.info("CREATING NEW AGENDA: " + agendaId);

            agendaVote = new Agenda();
            agendaVote.setId(agendaId);
            agendaVote.setNumber(Integer.parseInt(xmlAgendaVote.getNo()));

            if (xmlAgendaVote.getYear() != null
                    && xmlAgendaVote.getYear().length() > 0)
                agendaVote.setYear(Integer.parseInt(xmlAgendaVote.getYear()));

            if (xmlAgendaVote.getSessyr() != null
                    && xmlAgendaVote.getSessyr().length() > 0)
                agendaVote.setSessionYear(Integer.parseInt(xmlAgendaVote
                        .getSessyr()));
        } else {
            logger.info("FOUND EXISTING AGENDA: " + agendaId);
        }

        List<Addendum> listAddendums = agendaVote.getAddendums();

        if (listAddendums == null) {
            listAddendums = new ArrayList<Addendum>();
            agendaVote.setAddendums(listAddendums);
        }

        Iterator<Object> itAddendum = xmlAgendaVote.getContent().iterator();

        Addendum addendum = null;
        Object next = null;

        while (itAddendum.hasNext()) {
            next = itAddendum.next();

            if (next instanceof XMLAddendum) {
                XMLAddendum xmlAddendum = (XMLAddendum) next;
                // String keyId = "a-" + agendaVote.getNumber() + '-' +
                // agendaVote.getSessionYear() + '-' + xmlAddendum.getId();
                String keyId = xmlAddendum.getId() + "-"
                        + agendaVote.getNumber() + '-'
                        + agendaVote.getSessionYear() + '-'
                        + agendaVote.getYear();
                addendum = parseAddendum(keyId, xmlAddendum, agendaVote, true);
                addendum.setAgenda(agendaVote);

                if (!listAddendums.contains(addendum)) {
                    listAddendums.add(addendum);
                }
            } else {
                logger.warn("Got AgendaVote content object anonamoly: "
                        + next.getClass());
            }
        }

        return agendaVote;
    }

    public Agenda handleXMLSenagenda(XMLSenagenda xmlAgenda)
            throws ParseException, SQLException {

        logger.info("COMMITTEE AGENDA " + xmlAgenda.getNo() + " action="
                + xmlAgenda.getAction());

        String agendaId = "commagenda-" + xmlAgenda.getNo() + '-'
                + xmlAgenda.getSessyr() + '-' + xmlAgenda.getYear();

        Agenda agenda = null;

        if(canWrite(JSON)) {
            agenda = jsonDao.load(agendaId, xmlAgenda.getSessyr(), "agenda", Agenda.class);
        }

        String action = xmlAgenda.getAction();

        if (agenda != null && action.equalsIgnoreCase("remove")) {

            if(canWrite(JSON)) {
                jsonDao.delete(agenda);
            }
            if(canWrite(LUCENE)) {
                try {
                    searchEngine.deleteSenateObject(agenda);
                } catch (Exception e) {
                    logger.error(e);
                }
            }

            logger.info("removing agenda: " + agenda.getId());

            return null;
        }

        if (agenda == null) {
            logger.info("CREATING NEW AGENDA: " + agendaId);

            agenda = new Agenda();
            agenda.setId(agendaId);
            agenda.setNumber(Integer.parseInt(xmlAgenda.getNo()));

            if (xmlAgenda.getYear() != null && xmlAgenda.getYear().length() > 0)
                agenda.setYear(Integer.parseInt(xmlAgenda.getYear()));

            if (xmlAgenda.getSessyr() != null
                    && xmlAgenda.getSessyr().length() > 0)
                agenda.setSessionYear(Integer.parseInt(xmlAgenda.getSessyr()));
        } else {
            logger.info("FOUND EXISTING AGENDA: " + agenda.getId());
        }

        List<Addendum> listAddendums = agenda.getAddendums();

        if (listAddendums == null) {
            listAddendums = new ArrayList<Addendum>();
            agenda.setAddendums(listAddendums);
        }

        Iterator<XMLAddendum> itAddendum = xmlAgenda.getAddendum().iterator();
        Addendum addendum = null;

        while (itAddendum.hasNext()) {
            XMLAddendum xmlAddendum = itAddendum.next();
            String keyId = xmlAddendum.getId() + "-" + agenda.getNumber() + '-'
                    + agenda.getSessionYear() + '-' + agenda.getYear();

            addendum = parseAddendum(keyId, xmlAddendum, agenda, false);
            addendum.setAgenda(agenda);

            if (!listAddendums.contains(addendum)) {
                listAddendums.add(addendum);
            }
        }

        return agenda;
    }

    public Addendum parseAddendum(String keyId, XMLAddendum xmlAddendum,
            Agenda agenda, boolean isVote) throws ParseException, SQLException {

        Addendum addendum = new Addendum();
        addendum.setId(keyId);

        int index = agenda.getAddendums().indexOf(addendum);
        if (index == -1) {
            addendum = null;
        } else {
            addendum = agenda.getAddendums().get(index);
            addendum.setAgenda(agenda);
        }

        if (addendum == null) {
            addendum = new Addendum();
            addendum.setAddendumId(xmlAddendum.getId());
            addendum.setId(keyId);
            addendum.setAgenda(agenda);

            logger.info("creating new addendum: " + addendum.getId());
        }

        if (xmlAddendum.getPubdate() != null) {
            try {
                Date pubDateTime = LRS_DATETIME_FORMAT.parse(xmlAddendum
                        .getPubdate().getContent()
                        + xmlAddendum.getPubtime().getContent());

                addendum.setPublicationDateTime(pubDateTime);
            } catch (ParseException pe) {
                logger.warn("unable to parse addendum date/time format", pe);
            }
        }

        if (xmlAddendum.getWeekof() != null)
            addendum.setWeekOf(xmlAddendum.getWeekof().getContent());

        List<Meeting> listMeetings = addendum.getMeetings();

        if (listMeetings == null) {
            listMeetings = new ArrayList<Meeting>();
            addendum.setMeetings(listMeetings);
        }

        Iterator<XMLCommittee> itComm = xmlAddendum.getCommittees()
                .getCommittee().iterator();
        while (itComm.hasNext()) {
            XMLCommittee xmlCommMeeting = itComm.next();
            String action = xmlCommMeeting.getAction();

            Meeting meeting = null;
            String meetingId = "meeting-"
                    + xmlCommMeeting.getName().getContent() + '-'
                    + agenda.getNumber() + '-' + agenda.getSessionYear() + '-'
                    + agenda.getYear();

            meeting = agenda.getCommitteeMeeting(meetingId);

            if (meeting != null && action != null) {
                if (action.matches("(remove|replace)")) {
                    if (isVote) {

                    } else {
                        agenda.removeCommitteeMeeting(meeting);
                        logger.info("removing meeting: " + meeting.getId());

                        if(canWrite(LUCENE)) {
                            try {
                                searchEngine.deleteDocuments("meeting", meeting.luceneOid());
                            } catch (IOException e) {
                                logger.error(e);
                            }
                        }
                        if(canWrite(JSON)) {
                            jsonDao.write(agenda);
                        }
                    }
                    if (action.equals("remove"))
                        continue;
                }
            }

            if (meeting == null) {
                meeting = new Meeting();
                meeting.setId(meetingId);

                logger.info("CREATED NEW meeting: " + meeting.getId());

            }

            Date meetDateTime = LRS_DATETIME_FORMAT.parse(xmlCommMeeting
                    .getMeetdate().getContent()
                    + xmlCommMeeting.getMeettime().getContent());

            meeting.setMeetingDateTime(meetDateTime);

            List<Addendum> addendums = meeting.getAddendums();

            if (addendums == null) {
                addendums = new ArrayList<Addendum>();
                meeting.setAddendums(addendums);
            }

            if (!addendums.contains(addendum))
                addendums.add(addendum);

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

                Iterator<XMLBill> itBills = xmlCommMeeting.getBills().getBill()
                        .iterator();
                Bill bill = null;

                while (itBills.hasNext()) {
                    XMLBill xmlBill = itBills.next();

                    bill = handleXMLBill(meeting, xmlBill, addendum.getAgenda()
                            .getSessionYear());

                    if (!listBills.contains(bill)) {
                        logger.info("adding bill:" + bill.getSenateBillNo()
                                + " to meeting:" + meeting.getId());
                        listBills.add(bill);
                    } else {
                        logger.info("bill:" + bill.getSenateBillNo()
                                + " already added to meeting:"
                                + meeting.getId() + ", merging.");
                    }
                }
            }
        }

        return addendum;
    }

    public XMLSENATEDATA parseStream(Reader xmlReader) throws ParseException,
    JAXBException {
        String packageName = "gov.nysenate.openleg.xml.committee";
        JAXBContext jc = JAXBContext.newInstance(packageName);

        Unmarshaller u = jc.createUnmarshaller();
        XMLSENATEDATA sd = (XMLSENATEDATA) u.unmarshal(xmlReader);

        new Date();

        return sd;
    }

    private Bill getBill(String billId, int year, String sponsorName) {

        String billType = billId.substring(0, 1);
        int billNumber = -1;

        char lastVal = billId.substring(billId.length() - 1, billId.length())
                .toCharArray()[0];

        String senateBillNo = null;

        if (Character.isLetter(lastVal)) {
            billNumber = Integer.parseInt(billId.substring(1,
                    billId.length() - 1));

            senateBillNo = billType + billNumber + lastVal;
        } else {
            billNumber = Integer.parseInt(billId.substring(1));

            senateBillNo = billType + billNumber;

        }

        senateBillNo += "-" + year;

        Bill bill = null;

        if(canWrite(SenateParser.JSON)) {
            bill = jsonDao.load(senateBillNo, year +"", "bill", Bill.class);
        }

        /*
         * 
         * 
         */

        if (bill == null) {
            bill = new Bill();
            bill.setSenateBillNo(senateBillNo);
            bill.setYear(year);

            Person sponsor = new Person(sponsorName);
            bill.setSponsor(sponsor);

        }

        bill.setFulltext("");
        bill.setMemo("");
        bill.setActions(null);

        return bill;
    }

}
