package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.MeetingAttendance;
import gov.nysenate.openleg.model.MeetingItem;
import gov.nysenate.openleg.model.MeetingVote;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.SOBIBlock;
import gov.nysenate.openleg.model.Senagenda;
import gov.nysenate.openleg.model.SenagendaInfoAddendum;
import gov.nysenate.openleg.model.SenagendaInfoCommittee;
import gov.nysenate.openleg.model.SenagendaVoteAddendum;
import gov.nysenate.openleg.model.SenagendaVoteCommittee;
import gov.nysenate.openleg.util.ChangeLogger;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SenagendaProcessor
{
    public static enum VoteAction { FIRST_READING, THIRD_READING, REFERRED_TO_COMMITTEE, DEFEATED, RESTORED_TO_THIRD, SPECIAL}
    public static Map<String, VoteAction> VOTE_ACTION_MAP = new TreeMap<String, VoteAction>();
    static {
        VOTE_ACTION_MAP.put("F", VoteAction.FIRST_READING);
        VOTE_ACTION_MAP.put("3", VoteAction.THIRD_READING);
        VOTE_ACTION_MAP.put("RC", VoteAction.REFERRED_TO_COMMITTEE);
        VOTE_ACTION_MAP.put("D", VoteAction.DEFEATED);
        VOTE_ACTION_MAP.put("R3", VoteAction.RESTORED_TO_THIRD);
        VOTE_ACTION_MAP.put("S", VoteAction.SPECIAL);
    }

    private final Logger logger = Logger.getLogger(SenagendaProcessor.class);
    private Date modifiedDate;
    public static SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    private final DocumentBuilder dBuilder;
    private final XPath xpath;

    public SenagendaProcessor() throws ParserConfigurationException
    {
        dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xpath = XPathFactory.newInstance().newXPath();
    }

    public void processSenagenda(File file, Storage storage) throws XPathExpressionException, SAXException, IOException, ParseException
    {
        // TODO: We need a better default here
        modifiedDate = null;
        try {
            modifiedDate = sobiDateFormat.parse(file.getName());
        } catch (ParseException e) {
            logger.error("Error parsing file date.", e);
        }
        ChangeLogger.setContext(file, modifiedDate);

        // Parse the document and construct our base agenda.
        Document doc = dBuilder.parse(file);
        Node xmlAgenda = (Node)xpath.evaluate("SENATEDATA/senagenda", doc, XPathConstants.NODE);
        Integer agendaNo = (Integer)xpath.evaluate("@no", xmlAgenda, XPathConstants.NUMBER);
        Integer sessYr = (Integer)xpath.evaluate("@sessYr", xmlAgenda, XPathConstants.NUMBER);
        Integer year = (Integer)xpath.evaluate("@year", xmlAgenda, XPathConstants.NUMBER);
        Senagenda agenda = new Senagenda(agendaNo, sessYr, year);
        agenda.setPublishDate(modifiedDate);

        // action="remove" removes the whole agenda and all its addendum.
        // action="replace" replaces the whole agenda and all its addendum and inserts new ones.
        // As such, if we have an old agenda, clean out the old addendum and meeting information.
        String key = storage.key(agenda);
        Senagenda oldAgenda = (Senagenda)storage.get(key, Senagenda.class);
        if (oldAgenda != null) {
            agenda = oldAgenda;
        }
        agenda.setModifiedDate(modifiedDate);
        agenda.addDataSource(file.getName());

        String action = xpath.evaluate("@action", xmlAgenda);
        if (action.equalsIgnoreCase("remove")) {
            logger.info("Removing agenda: " + agenda.getOid());
            storage.del(key);
            ChangeLogger.delete(key, storage);
        }
        else if (action.equalsIgnoreCase("replace")) {
            logger.info("Replacing senagendaAddendums: "+agenda.getOid());
            NodeList xmlAddendums = (NodeList)xpath.evaluate("addendum", xmlAgenda, XPathConstants.NODESET);
            Map<String, SenagendaInfoAddendum> addendums = new TreeMap<String, SenagendaInfoAddendum>();
            for (int i=0; i < xmlAddendums.getLength(); i++) {
                Node xmlAddendum = xmlAddendums.item(i);
                String id = xpath.evaluate("@id", xmlAddendum);
                String weekOf = xpath.evaluate("weekof/text()", xmlAddendum);
                String pubDate = xpath.evaluate("pubdate/text()", xmlAddendum);
                String pubTime = xpath.evaluate("pubtime/text()", xmlAddendum);
                Date pubDateTime = OpenLegConstants.LRS_DATETIME_FORMAT.parse(pubDate+pubTime);
                SenagendaInfoAddendum addendum = new SenagendaInfoAddendum(id, weekOf, pubDateTime);

                Map<String, SenagendaInfoCommittee> committees = new TreeMap<String, SenagendaInfoCommittee>();
                NodeList xmlCommittees = (NodeList)xpath.evaluate("committees/committee", xmlAddendum, XPathConstants.NODESET);
                for (int j=0; j < xmlCommittees.getLength(); j++) {
                    Node xmlCommittee = xmlCommittees.item(j);
                    String name = xpath.evaluate("name/text()", xmlCommittee);
                    String chair = xpath.evaluate("chair/text()", xmlCommittee);
                    String location = xpath.evaluate("location/text()", xmlCommittee);
                    String meetDay = xpath.evaluate("meetday/text()", xmlCommittee);
                    String meetDate = xpath.evaluate("meetdate/text()", xmlCommittee);
                    String meetTime = xpath.evaluate("meettime/text()", xmlCommittee);
                    String notes = xpath.evaluate("notes/text()", xmlCommittee);
                    Date meetDateTime = OpenLegConstants.LRS_DATETIME_FORMAT.parse(meetDate+meetTime);
                    SenagendaInfoCommittee committee = new SenagendaInfoCommittee(name, chair, location, notes, meetDay, meetDateTime);

                    HashMap<String, Bill> bills = new HashMap<String, Bill>();
                    NodeList xmlBills = (NodeList)xpath.evaluate("bills/bill", xmlCommittee, XPathConstants.NODESET);
                    for (int k=0; k < xmlBills.getLength(); k++) {
                        Node xmlBill = xmlBills.item(k);
                        String billno = xpath.evaluate("@no", xmlBill);
                        String sponsor = xpath.evaluate("sponsor/text()", xmlBill);
                        // String message = xpath.evaluate("message/text()", xmlBill);
                        // String title = xpath.evaluate("title/text()", xmlBill);
                        Bill bill = getOrCreateBill(storage, billno, sessYr, sponsor);
                        bills.put(billno, bill);
                    }
                    committee.bills = bills;
                    committees.put(name, committee);
                }
                addendum.committees = committees;
                addendums.put(id, addendum);
            }
            agenda.setSenagendaAddendum(addendums);

            // Record and persist these changes
            ChangeLogger.record(key, storage);
            storage.set(agenda);
        }
        else {
            logger.error("Unknown senagenda action: "+action);
        }
    }

    public void processSenagendaVote(File file, Storage storage) throws SAXException, IOException, XPathExpressionException, ParseException
    {
        // TODO: We need a better default here
        modifiedDate = null;
        try {
            modifiedDate = sobiDateFormat.parse(file.getName());
        } catch (ParseException e) {
            logger.error("Error parsing file date.", e);
        }
        ChangeLogger.setContext(file, modifiedDate);

        Document doc = dBuilder.parse(file);
        Node xmlAgendgaVote = (Node)xpath.evaluate("SENATEDATA/senagendavote", doc, XPathConstants.NODE);
        Integer agendaNo = (Integer)xpath.evaluate("@no", xmlAgendgaVote, XPathConstants.NUMBER);
        Integer sessYr = (Integer)xpath.evaluate("@sessYr", xmlAgendgaVote, XPathConstants.NUMBER);
        Integer year = (Integer)xpath.evaluate("@year", xmlAgendgaVote, XPathConstants.NUMBER);
        Senagenda agenda = new Senagenda(agendaNo, sessYr, year);

        // Use the old agenda if we have it
        String key = storage.key(agenda);
        Senagenda oldAgenda = (Senagenda)storage.get(key, Senagenda.class);
        if (oldAgenda != null) {
            agenda = oldAgenda;
        }
        agenda.setModifiedDate(modifiedDate);
        agenda.addDataSource(file.getName());

        Map<String, SenagendaVoteAddendum> addendums = agenda.getSenagendaVoteAddendum();
        NodeList xmlAddendums = (NodeList)xpath.evaluate("addendum", xmlAgendgaVote, XPathConstants.NODESET);
        for (int i=0; i < xmlAddendums.getLength(); i++) {
            Node xmlAddendum = xmlAddendums.item(i);
            String addendumId = xpath.evaluate("@id", xmlAddendum);

            // Use the existing vote addendum if available, else create a new one
            SenagendaVoteAddendum addendum = addendums.get(addendumId);
            if (addendum == null) {
                addendum = new SenagendaVoteAddendum(addendumId);
            }

            NodeList xmlCommittees = (NodeList)xpath.evaluate("committees/committee", xmlAddendum, XPathConstants.NODESET);
            for (int j=0; j < xmlCommittees.getLength(); j++) {
                Node xmlCommittee = xmlCommittees.item(j);
                String action = xpath.evaluate("@action", xmlCommittee);
                String name = xpath.evaluate("name/text()", xmlCommittee);
                String chair = xpath.evaluate("chair/text()", xmlCommittee);
                String meetdate = xpath.evaluate("meetdate/text()", xmlCommittee);
                String meettime = xpath.evaluate("meettime/text()", xmlCommittee);
                Date meetDateTime = OpenLegConstants.LRS_DATETIME_FORMAT.parse(meetdate + meettime);

                // If the action is remove, then discard the committee and move on
                if (action.equals("remove")) {
                    addendum.committees.remove(name);
                    continue;
                }

                // Otherwise, the committee is completely replaced
                SenagendaVoteCommittee committee = new SenagendaVoteCommittee(name, chair, meetDateTime);
                committee.modifiedDate = modifiedDate;
                NodeList xmlMembers = (NodeList)xpath.evaluate("attendancelist/member", xmlCommittee, XPathConstants.NODESET);
                for (int k=0; k < xmlMembers.getLength(); k++) {
                    Node xmlMember = xmlMembers.item(k);
                    String memberName = xpath.evaluate("name/text()", xmlMember);
                    String rank = xpath.evaluate("rank/text()", xmlMember);
                    String party = xpath.evaluate("party/text()", xmlMember);
                    String attendance = xpath.evaluate("attendance", xmlMember);
                    MeetingAttendance member = new MeetingAttendance(memberName, rank, party, attendance);
                    committee.attendance.add(member);
                }

                NodeList xmlBills = (NodeList)xpath.evaluate("bills/bill", xmlCommittee, XPathConstants.NODESET);
                for (int k=0; k < xmlBills.getLength(); k++) {
                    Node xmlBill = xmlBills.item(k);
                    String billno = xpath.evaluate("@no", xmlBill);
                    String sponsor = xpath.evaluate("sponsor/text()", xmlBill);
                    Bill bill = getOrCreateBill(storage, billno, sessYr, sponsor);

                    String billActionId = xpath.evaluate("action/text()", xmlBill);
                    VoteAction billAction = VoteAction.valueOf(billActionId);
                    String referCommittee = xpath.evaluate("referCommittee/text()", xmlBill);
                    String withAmd = xpath.evaluate("withamd/text()", xmlBill);
                    MeetingItem item = new MeetingItem(bill, billAction, referCommittee, withAmd.equalsIgnoreCase("Y"));

                    NodeList xmlVotes = (NodeList)xpath.evaluate("votes/member", xmlBill, XPathConstants.NODESET);
                    for (int v=0; v < xmlVotes.getLength(); v++) {
                        Node xmlVote = xmlVotes.item(v);
                        String voterName = xpath.evaluate("name/text()", xmlVote);
                        String voterRank = xpath.evaluate("rank/text()", xmlVote);
                        String voterVote = xpath.evaluate("vote/text()", xmlVote);
                        String voterParty = xpath.evaluate("party/text()", xmlVote);
                        MeetingVote vote = new MeetingVote(voterName, voterRank, voterParty, voterVote);
                        item.getVotes().add(vote);
                    }

                    committee.items.put(billno, item);
                }
                addendum.committees.put(name, committee);
            }
        }
    }

    private Bill getOrCreateBill(Storage storage, String printNo, int year, String sponsorName) {
        String[] sponsors = {""};
        if (sponsorName != null) {
            sponsors = sponsorName.trim().split(",");
        }

        // All bills on calendars should already exist but sometimes, particularly during development/testing
        // they won't. Instead of breaking the processing, create a new bill using the bill processor.
        BillProcessor processor = new BillProcessor();
        SOBIBlock mockBlock = new SOBIBlock(year+printNo+(printNo.matches("[A-Z]$") ? "" : " ")+1+"     ");
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
