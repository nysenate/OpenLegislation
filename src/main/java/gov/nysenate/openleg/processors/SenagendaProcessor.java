package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.entity.Person;
import gov.nysenate.openleg.model.sobi.SobiBlock;
import gov.nysenate.openleg.processors.sobi.bill.BillProcessor;
import gov.nysenate.openleg.util.*;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class SenagendaProcessor
{
    private final Logger logger = Logger.getLogger(SenagendaProcessor.class);

    public static enum VoteAction { FIRST_READING, THIRD_READING, REFERRED_TO_COMMITTEE, DEFEATED, RESTORED_TO_THIRD, SPECIAL}
    public static Map<String, VoteAction> VOTE_ACTION_MAP = new TreeMap<>();
    static {
        VOTE_ACTION_MAP.put("F", VoteAction.FIRST_READING);
        VOTE_ACTION_MAP.put("3", VoteAction.THIRD_READING);
        VOTE_ACTION_MAP.put("RC", VoteAction.REFERRED_TO_COMMITTEE);
        VOTE_ACTION_MAP.put("D", VoteAction.DEFEATED);
        VOTE_ACTION_MAP.put("R3", VoteAction.RESTORED_TO_THIRD);
        VOTE_ACTION_MAP.put("S", VoteAction.SPECIAL);
    }

    public void processSenagenda(File file, Storage storage) throws XPathExpressionException, SAXException, IOException
    {
        // TODO: We need a better default here
        Date modifiedDate = DateHelper.getFileDate(file.getName());
        ChangeLogger.setContext(file, modifiedDate);
        XmlHelper xml = Application.getXmlHelper();

        // Parse the document and construct our base agenda.
        Document doc = xml.parse(file);
        Node xmlAgenda = xml.getNode("SENATEDATA/senagenda", doc);
        Integer agendaNo = xml.getInteger("@no", xmlAgenda);
        Integer sessYr = xml.getInteger("@sessyr", xmlAgenda);
        Integer year = xml.getInteger("@year", xmlAgenda);
        Agenda agenda = new Agenda(agendaNo, sessYr, year);
        agenda.setPublishDate(modifiedDate);

        // action="remove" removes the whole agenda and all its addendum.
        // action="replace" replaces the whole agenda and all its addendum and inserts new ones.
        // As such, if we have an old agenda, clean out the old addendum and meeting information.
        String key = storage.key(agenda);
        Agenda oldAgenda = (Agenda)storage.get(key, Agenda.class);
        if (oldAgenda != null) {
            agenda = oldAgenda;
        }
        agenda.setModifiedDate(modifiedDate);
        //agenda.addDataSource(file.getName());

        String action = xml.getString("@action", xmlAgenda);
        if (action.equalsIgnoreCase("remove")) {
            logger.info("Removing agenda: " + agenda.getOid());
            storage.del(key);
            ChangeLogger.delete(key, storage);
        }
        else if (action.equalsIgnoreCase("replace")) {
            logger.info("Replacing senagenda addendums: "+agenda.getOid());
            NodeList xmlAddendums = xml.getNodeList("addendum", xmlAgenda);
            // Because we are replacing them in full, we create a new map here
            Map<String, AgendaInfoAddendum> addendums = new TreeMap<String, AgendaInfoAddendum>();
            for (int i=0; i < xmlAddendums.getLength(); i++) {
                Node xmlAddendum = xmlAddendums.item(i);
                String id = xml.getString("@id", xmlAddendum);
                Date weekOf = DateHelper.getDate(xml.getString("weekof/text()", xmlAddendum));
                Date pubDateTime = DateHelper.getDateTime(xml.getString("pubdate/text()", xmlAddendum)+xml.getString("pubtime/text()", xmlAddendum));
                AgendaInfoAddendum addendum = new AgendaInfoAddendum(id, weekOf, pubDateTime);

                NodeList xmlCommittees = xml.getNodeList("committees/committee", xmlAddendum);
                for (int j=0; j < xmlCommittees.getLength(); j++) {
                    Node xmlCommittee = xmlCommittees.item(j);
                    String name = xml.getString("name/text()", xmlCommittee);
                    String chair = xml.getString("chair/text()", xmlCommittee);
                    String location = xml.getString("location/text()", xmlCommittee);
                    String meetDay = xml.getString("meetday/text()", xmlCommittee);
                    String notes = xml.getString("notes/text()", xmlCommittee);
                    Date meetDateTime = DateHelper.getDateTime(xml.getString("meetdate/text()", xmlCommittee)+xml.getString("meettime/text()", xmlCommittee));
                    AgendaInfoCommittee committee = new AgendaInfoCommittee(name, chair, location, notes, meetDay, meetDateTime);

                    NodeList xmlBills = xml.getNodeList("bills/bill", xmlCommittee);
                    for (int k=0; k < xmlBills.getLength(); k++) {
                        Node xmlBill = xmlBills.item(k);
                        String billno = xml.getString("@no", xmlBill);
                        String sponsor = xml.getString("sponsor/text()", xmlBill);
                        String message = xml.getString("message/text()", xmlBill);
                        String title = xml.getString("title/text()", xmlBill);
                        String billAmendment = billno.matches("[A-Z]$") ? billno.substring(billno.length()-1) : "";
                        Bill bill = getOrCreateBill(storage, billno, billAmendment, sessYr, sponsor, modifiedDate);
                        AgendaInfoCommitteeItem item = new AgendaInfoCommitteeItem(bill, billAmendment, message, title);
                        committee.putItem(item);
                    }
                    addendum.putCommittee(committee);
                }
                addendums.put(id, addendum);
            }
            // This will override the existing set of addendums.
            agenda.setAgendaInfoAddendum(addendums);

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
        Date modifiedDate = DateHelper.getFileDate(file.getName());
        ChangeLogger.setContext(file, modifiedDate);

        XmlHelper xml = Application.getXmlHelper();
        Document doc = xml.parse(file);
        Node xmlAgendgaVote = xml.getNode("SENATEDATA/senagendavote", doc);
        Integer agendaNo = xml.getInteger("@no", xmlAgendgaVote);
        Integer sessYr = xml.getInteger("@sessyr", xmlAgendgaVote);
        Integer year = xml.getInteger("@year", xmlAgendgaVote);
        Agenda agenda = new Agenda(agendaNo, sessYr, year);

        // Use the old agenda if we have it
        String key = storage.key(agenda);
        Agenda oldAgenda = (Agenda)storage.get(key, Agenda.class);
        if (oldAgenda != null) {
            agenda = oldAgenda;
        }
        agenda.setModifiedDate(modifiedDate);
        //agenda.addDataSource(file.getName());

        NodeList xmlAddendums = xml.getNodeList("addendum", xmlAgendgaVote);
        for (int i=0; i < xmlAddendums.getLength(); i++) {
            Node xmlAddendum = xmlAddendums.item(i);
            String addendumId = xml.getString("@id", xmlAddendum);

            // Use the existing vote addendum if available, else create a new one
            AgendaVoteAddendum addendum = agenda.getAgendaVoteAddendum(addendumId);
            if (addendum == null) {
                addendum = new AgendaVoteAddendum(addendumId, year, sessYr);
                agenda.putAgendaVoteAddendum(addendum);
            }

            NodeList xmlCommittees = xml.getNodeList("committees/committee", xmlAddendum);
            for (int j=0; j < xmlCommittees.getLength(); j++) {
                Node xmlCommittee = xmlCommittees.item(j);
                String action = xml.getString("@action", xmlCommittee);
                String name = xml.getString("name/text()", xmlCommittee);
                String chair = xml.getString("chair/text()", xmlCommittee);
                Date meetDateTime = DateHelper.getDateTime(xml.getString("meetdate/text()", xmlCommittee)+xml.getString("meettime/text()", xmlCommittee));

                // If the action is remove, then discard the committee and move on
                if (action.equals("remove")) {
                    addendum.removeCommittee(name);
                    continue;
                }

                // Otherwise, the committee is completely replaced
                AgendaVoteCommittee committee = new AgendaVoteCommittee(name, chair, meetDateTime);
                committee.setModifiedDate(modifiedDate);
                NodeList xmlMembers = xml.getNodeList("attendancelist/member", xmlCommittee);
                for (int k=0; k < xmlMembers.getLength(); k++) {
                    Node xmlMember = xmlMembers.item(k);
                    String memberName = xml.getString("name/text()", xmlMember);
                    String rank = xml.getString("rank/text()", xmlMember);
                    String party = xml.getString("party/text()", xmlMember);
                    String attendance = xml.getString("attendance", xmlMember);
                    AgendaVoteCommitteeAttendance member = new AgendaVoteCommitteeAttendance(memberName, rank, party, attendance);
                    committee.addAttendance(member);
                }

                NodeList xmlBills = xml.getNodeList("bills/bill", xmlCommittee);
                for (int k=0; k < xmlBills.getLength(); k++) {
                    Node xmlBill = xmlBills.item(k);
                    String billno = xml.getString("@no", xmlBill);
                    String sponsor = xml.getString("sponsor/text()", xmlBill);
                    String billAmendment = billno.matches("[A-Z]$") ? billno.substring(billno.length()-1) : "";
                    Bill bill = getOrCreateBill(storage, billno, billAmendment, sessYr, sponsor, modifiedDate);

                    String billActionId = xml.getString("action/text()", xmlBill);
                    VoteAction billAction = VOTE_ACTION_MAP.get(billActionId);
                    String referCommittee = xml.getString("referCommittee/text()", xmlBill);
                    String withAmd = xml.getString("withamd/text()", xmlBill);
                    AgendaVoteCommitteeItem item = new AgendaVoteCommitteeItem(bill, billAmendment, billAction, referCommittee, withAmd.equalsIgnoreCase("Y"));

                    NodeList xmlVotes = xml.getNodeList("votes/member", xmlBill);
                    for (int v=0; v < xmlVotes.getLength(); v++) {
                        Node xmlVote = xmlVotes.item(v);
                        String voterName = xml.getString("name/text()", xmlVote);
                        String voterRank = xml.getString("rank/text()", xmlVote);
                        String voterVote = xml.getString("vote/text()", xmlVote);
                        String voterParty = xml.getString("party/text()", xmlVote);
                        AgendaVoteCommitteeVote vote = new AgendaVoteCommitteeVote(voterName, voterRank, voterParty, voterVote);
                        item.addVote(vote);
                    }

                    committee.putItem(item);
                }
                addendum.putCommittee(committee);
            }
        }
    }

    private Bill getOrCreateBill(Storage storage, String printNo, String billAmendment, int year, String sponsorName, Date modifiedDate) {
        String[] sponsors = {""};
        if (sponsorName != null) {
            sponsors = sponsorName.trim().split(",");
        }

        // All bills on calendars should already exist but sometimes, particularly during development/testing
        // they won't. Instead of breaking the processing, create a new bill using the bill processor.
        BillProcessor processor = new BillProcessor();
        SobiBlock mockBlock = new SobiBlock(year+printNo+(printNo.matches("[A-Z]$") ? "" : " ")+1+"     ");
        Bill bill = null; /** FIXME processor.getOrCreateBaseBill(mockBlock, modifiedDate, storage); */
        /** FIXME: bill.setSponsor(new Person(sponsors[0].trim())); */

        // It must be published if it is on the agenda
        BillAmendment amendment = bill.getAmendment(billAmendment);
        if (!amendment.isPublished()) {
            amendment.setPublishDate(modifiedDate);
            /** FIXME processor.saveBill(bill, billAmendment, storage); */
        }

        // Other sponsors are removed when a calendar/agenda is resent without
        // The other sponsor included in the sponsors list.
        ArrayList<Person> otherSponsors = new ArrayList<Person>();
        for (int i = 1; i < sponsors.length; i++) {
            otherSponsors.add(new Person(sponsors[i].trim()));
        }

//        if (!bill.getAdditionalSponsors().equals(otherSponsors)) {
//            bill.setAdditionalSponsors(otherSponsors);
            /** FIXME new BillProcessor(Application.getEnvironment()).saveBill(bill, Bill.BASE_VERSION, storage); */
//        }

        return bill;
    }
}
