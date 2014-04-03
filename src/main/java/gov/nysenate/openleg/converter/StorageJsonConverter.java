package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.AgendaInfoAddendum;
import gov.nysenate.openleg.model.AgendaInfoCommittee;
import gov.nysenate.openleg.model.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.model.AgendaVoteAddendum;
import gov.nysenate.openleg.model.AgendaVoteCommittee;
import gov.nysenate.openleg.model.AgendaVoteCommitteeAttendance;
import gov.nysenate.openleg.model.AgendaVoteCommitteeItem;
import gov.nysenate.openleg.model.AgendaVoteCommitteeVote;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.BillAction;
import gov.nysenate.openleg.model.BillAmendment;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarActiveList;
import gov.nysenate.openleg.model.CalendarActiveListEntry;
import gov.nysenate.openleg.model.CalendarSupplemental;
import gov.nysenate.openleg.model.CalendarSupplementalSection;
import gov.nysenate.openleg.model.CalendarSupplementalSectionEntry;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.processors.SenagendaProcessor.VoteAction;
import gov.nysenate.openleg.util.Storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

@SuppressWarnings("unchecked")
public class StorageJsonConverter
{
    protected final Logger logger;
    protected final Storage storage;
    protected final String encoding = "UTF-8";
    protected final JsonFactory jsonFactory;
    protected final ObjectMapper objectMapper;
    protected final PrettyPrinter prettyPrinter;

    private final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public StorageJsonConverter(Storage storage)
    {
        this.storage = storage;
        this.logger  = Logger.getLogger(this.getClass());

        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(Feature.INDENT_OUTPUT);
        this.jsonFactory = this.objectMapper.getJsonFactory();
        this.prettyPrinter = new DefaultPrettyPrinter();
    }

    public String toString(Transcript value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(Bill value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(Calendar value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(Agenda value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public void write(Transcript value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
    }

    public void write(Bill value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
    }

    public void write(Calendar value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
    }

    public void write(Agenda value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
    }

    public void write(Transcript transcript, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", transcript.getId());
        node.put("timeStamp", makeNode(transcript.getTimeStamp()));
        node.put("location", transcript.getLocation());
        node.put("type", transcript.getType());
        node.put("transcriptText", transcript.getTranscriptText());
        node.put("transcriptTextProcessed", transcript.getTranscriptTextProcessed());
        node.put("relatedBills", makeArrayNode(transcript.getRelatedBills()));

        node.put("year", transcript.getYear());
        node.put("session", transcript.getSession());
        node.put("modified", makeNode(transcript.getModifiedDate()));
        node.put("published", makeNode(transcript.getPublishDate()));
        node.put("dataSources", makeArrayNode(transcript.getDataSources()));
        this.write(node, out);
    }

    public void write(Bill bill, OutputStream out) throws IOException
    {
        logger.debug("Writing Bill: "+bill.getBillId());
        ObjectNode node = objectMapper.createObjectNode();
        node.put("billId", bill.getBillId());
        node.put("title", bill.getTitle());
        node.put("lawSection", bill.getLawSection());
        node.put("summary", bill.getSummary());
        node.put("activeVersion", bill.getActiveVersion());
        node.put("activeHistory", makeArrayNode(bill.getActiveHistory()));
        node.put("previousVersions", makeArrayNode(bill.getPreviousVersions()));
        node.put("sponsor", makeNode(bill.getSponsor()));
        node.put("otherSponsors", makeArrayNode(bill.getOtherSponsors()));
        node.put("pastCommittees", makeArrayNode(bill.getPastCommittees()));
        node.put("actions", makeArrayNode(bill.getActions()));
        node.put("uniBill", bill.isUniBill());
        node.put("amendments", makeArrayNode(bill.getAmendmentList()));

        node.put("year", bill.getYear());
        node.put("session", bill.getSession());
        node.put("modified", makeNode(bill.getModifiedDate()));
        node.put("dataSources", makeArrayNode(bill.getDataSources()));
        write(node, out);
    }

    public void write(Calendar calendar, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("number", calendar.getNumber());
        node.put("supplementals", makeArrayNode(calendar.getSupplementals().values()));
        node.put("activeLists", makeArrayNode(calendar.getActiveLists().values()));

        node.put("year", calendar.getYear());
        node.put("session", calendar.getSession());
        node.put("modified", makeNode(calendar.getModifiedDate()));
        node.put("dataSources", makeArrayNode(calendar.getDataSources()));
        write(node, out);
    }

    public void write(Agenda agenda, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("number", agenda.getNumber());
        node.put("agendaInfoAddendum", makeArrayNode(agenda.getAgendaInfoAddendum().values()));
        node.put("agendaVoteAddendum", makeArrayNode(agenda.getAgendaVoteAddendum().values()));

        node.put("year", agenda.getYear());
        node.put("session", agenda.getSession());
        node.put("modified", makeNode(agenda.getModifiedDate()));
        node.put("dataSources", makeArrayNode(agenda.getDataSources()));
        write(node, out);
    }

    private void write(ObjectNode node, OutputStream out) throws IOException
    {
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
    }

    public ArrayNode makeArrayNode(Collection<? extends Object> list)
    {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        if (list != null) {
            for (Object item : list) {
                if (Vote.class.isInstance(item)) {
                    arrayNode.add(makeNode((Vote)item));
                }
                else if (BillAmendment.class.isInstance(item)) {
                    arrayNode.add(makeNode((BillAmendment)item));
                }
                else if (Person.class.isInstance(item)) {
                    arrayNode.add(makeNode((Person)item));
                }
                else if (String.class.isInstance(item)) {
                    arrayNode.add((String)item);
                }
                else if (Bill.class.isInstance(item)) {
                    arrayNode.add(makeNode((Bill)item));
                }
                else if (BillAction.class.isInstance(item)) {
                    arrayNode.add(makeNode((BillAction)item));
                }
                else if (CalendarSupplemental.class.isInstance(item)) {
                    arrayNode.add(makeNode((CalendarSupplemental)item));
                }
                else if (CalendarSupplementalSection.class.isInstance(item)) {
                    arrayNode.add(makeNode((CalendarSupplementalSection)item));
                }
                else if (CalendarSupplementalSectionEntry.class.isInstance(item)) {
                    arrayNode.add(makeNode((CalendarSupplementalSectionEntry)item));
                }
                else if (CalendarActiveList.class.isInstance(item)) {
                    arrayNode.add(makeNode((CalendarActiveList)item));
                }
                else if (CalendarActiveListEntry.class.isInstance(item)) {
                    arrayNode.add(makeNode((CalendarActiveListEntry)item));
                }
                else if (AgendaInfoAddendum.class.isInstance(item)) {
                    arrayNode.add(makeNode((AgendaInfoAddendum)item));
                }
                else if (AgendaInfoCommittee.class.isInstance(item)) {
                    arrayNode.add(makeNode((AgendaInfoCommittee)item));
                }
                else if (AgendaInfoCommitteeItem.class.isInstance(item)) {
                    arrayNode.add(makeNode((AgendaInfoCommitteeItem)item));
                }
                else if (AgendaVoteAddendum.class.isInstance(item)) {
                    arrayNode.add(makeNode((AgendaVoteAddendum)item));
                }
                else if (AgendaVoteCommitteeAttendance.class.isInstance(item)) {
                    arrayNode.add(makeNode((AgendaVoteCommitteeAttendance)item));
                }
                else if (AgendaVoteCommitteeItem.class.isInstance(item)) {
                    arrayNode.add(makeNode((AgendaVoteCommitteeItem)item));
                }
                else if (AgendaVoteCommitteeVote.class.isInstance(item)) {
                    arrayNode.add(makeNode((AgendaVoteCommitteeVote)item));
                }
                else {
                    throw new RuntimeException("Invalid array node type: "+item.getClass());
                }
            }
        }
        return arrayNode;
    }

    public String makeNode(Bill bill)
    {
        if (bill != null) {
            return storage.key(bill);
        }
        else {
            return null;
        }
    }

    public JsonNode makeNode(BillAmendment amendment)
    {
        if (amendment != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("baseBillId", amendment.getBaseBillId());
            node.put("baseBillPrintNo", amendment.getBaseBillPrintNo());
            node.put("version", amendment.getVersion());
            node.put("sameAs", amendment.getSameAs());
            node.put("memo", amendment.getMemo());
            node.put("law", amendment.getLaw());
            node.put("actClause", amendment.getActClause());
            node.put("fulltext", amendment.getFulltext());
            node.put("currentCommittee", amendment.getCurrentCommittee());
            node.put("coSponsors", makeArrayNode(amendment.getCoSponsors()));
            node.put("multiSponsors", makeArrayNode(amendment.getMultiSponsors()));
            node.put("stricken", amendment.isStricken());
            node.put("votes", makeArrayNode(amendment.getVotes()));

            node.put("year", amendment.getYear());
            node.put("session", amendment.getSession());
            node.put("modified", makeNode(amendment.getModifiedDate()));
            node.put("published", makeNode(amendment.getPublishDate()));
            node.put("dataSources", makeArrayNode(amendment.getDataSources()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(BillAction action)
    {
        if (action != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", action.getOid());
            node.put("bill", (action.getBill() != null) ? action.getBill().getBillId() : "");
            node.put("date", action.getDate().getTime());
            node.put("modified", makeNode(action.getModifiedDate()));
            node.put("published", makeNode(action.getPublishDate()));
            node.put("dataSources", makeArrayNode(action.getDataSources()));
            node.put("text", action.getText());
            node.put("year", action.getYear());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Vote vote)
    {
        if (vote != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", vote.getOid());
            node.put("modified", makeNode(vote.getModifiedDate()));
            node.put("published", makeNode(vote.getPublishDate()));
            node.put("dataSources", makeArrayNode(vote.getDataSources()));
            node.put("date", makeNode(vote.getVoteDate()));
            node.put("voteType", vote.getVoteType());
            node.put("description", vote.getDescription());
            node.put("sequenceNumber", vote.getSequenceNumber());
            node.put("ayes", makeArrayNode(vote.getAyes()));
            node.put("ayeswr", makeArrayNode(vote.getAyeswr()));
            node.put("nays", makeArrayNode(vote.getNays()));
            node.put("abstains", makeArrayNode(vote.getAbstains()));
            node.put("absent", makeArrayNode(vote.getAbsent()));
            node.put("excused", makeArrayNode(vote.getExcused()));
            node.put("billId", vote.getBill().getBillId());
            node.put("billAmendment", vote.getBillAmendment());
            node.put("year", vote.getYear());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public String makeNode(Date date)
    {
        if (date != null) {
            return jsonDateFormat.format(date);
        }
        else {
            return null;
        }
    }

    public JsonNode makeNode(CalendarSupplemental supplemental)
    {
        if (supplemental != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", supplemental.getId());
            node.put("calDate", makeNode(supplemental.getCalDate()));
            node.put("releaseDateTime", makeNode(supplemental.getReleaseDateTime()));
            node.put("sections", makeArrayNode(supplemental.getSections().values()));

            node.put("year", supplemental.getYear());
            node.put("session", supplemental.getSession());
            node.put("modified", makeNode(supplemental.getModifiedDate()));
            node.put("published", makeNode(supplemental.getPublishDate()));
            node.put("dataSources", makeArrayNode(supplemental.getDataSources()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(CalendarSupplementalSection section)
    {
        if (section != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("name", section.getName());
            node.put("type", section.getType());
            node.put("cd", section.getCd());
            node.put("entries", makeArrayNode(section.getEntries()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(CalendarSupplementalSectionEntry entry)
    {
        if (entry != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("calendarNumber", entry.getCalendarNumber());
            node.put("bill", makeNode(entry.getBill()));
            node.put("billAmendment", entry.getBillAmendment());
            node.put("subBill", makeNode(entry.getSubBill()));
            node.put("subBillAmendment", entry.getSubBillAmendment());
            node.put("billHigh", entry.isBillHigh());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(CalendarActiveList activeList)
    {
        if (activeList != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", activeList.getId());
            node.put("notes", activeList.getNotes());
            node.put("calDate", makeNode(activeList.getCalDate()));
            node.put("releaseDateTime", makeNode(activeList.getReleaseDateTime()));
            node.put("entries", makeArrayNode(activeList.getEntries()));

            node.put("year", activeList.getYear());
            node.put("session", activeList.getSession());
            node.put("modified", makeNode(activeList.getModifiedDate()));
            node.put("published", makeNode(activeList.getPublishDate()));
            node.put("dataSources", makeArrayNode(activeList.getDataSources()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(CalendarActiveListEntry entry)
    {
        if (entry != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("calendarNumber", entry.getCalendarNumber());
            node.put("bill", makeNode(entry.getBill()));
            node.put("billAmendment", entry.getBillAmendment());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Person person)
    {
        if (person != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("position", person.getPosition());
            node.put("fullname", person.getFullname());
            node.put("id", person.getId());
            node.put("branch", person.getBranch());
            node.put("contactInfo", person.getContactInfo());
            node.put("guid", person.getGuid());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(AgendaInfoAddendum addendum)
    {
        if (addendum != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", addendum.getId());
            node.put("weekOf", makeNode(addendum.getWeekOf()));
            node.put("agendaNumber", addendum.getAgendaNumber());
            node.put("committees", makeArrayNode(addendum.getCommittees().values()));

            node.put("year", addendum.getYear());
            node.put("session", addendum.getSession());
            node.put("modified", makeNode(addendum.getModifiedDate()));
            node.put("published", makeNode(addendum.getPublishDate()));
            node.put("dataSources", makeArrayNode(addendum.getDataSources()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(AgendaInfoCommittee committee)
    {
        if (committee != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("name", committee.getName());
            node.put("chair", committee.getChair());
            node.put("location", committee.getLocation());
            node.put("meetDay", committee.getMeetDay());
            node.put("meetDate", makeNode(committee.getMeetDate()));
            node.put("notes", committee.getNotes());
            node.put("items", makeArrayNode(committee.getItems().values()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(AgendaInfoCommitteeItem item)
    {
        if (item != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("bill", makeNode(item.getBill()));
            node.put("billAmendment", item.getBillAmendment());
            node.put("message", item.getMessage());
            node.put("title", item.getTitle());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(AgendaVoteAddendum addendum)
    {
        if (addendum != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", addendum.getId());
            node.put("agendaNumber", addendum.getAgendaNumber());
            node.put("committees", makeArrayNode(addendum.getCommittees().values()));

            node.put("year", addendum.getYear());
            node.put("session", addendum.getSession());
            node.put("modified", makeNode(addendum.getModifiedDate()));
            node.put("published", makeNode(addendum.getPublishDate()));
            node.put("dataSources", makeArrayNode(addendum.getDataSources()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(AgendaVoteCommittee committee)
    {
        if (committee != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("name", committee.getName());
            node.put("chair", committee.getChair());
            node.put("meetDate", makeNode(committee.getMeetDate()));
            node.put("modified", makeNode(committee.getModifiedDate()));
            node.put("items", makeArrayNode(committee.getItems().values()));
            node.put("attendance", makeArrayNode(committee.getAttendance()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(AgendaVoteCommitteeAttendance attendance)
    {
        if (attendance != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("name", attendance.getName());
            node.put("rank", attendance.getRank());
            node.put("party", attendance.getParty());
            node.put("attendance", attendance.getAttendance());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(AgendaVoteCommitteeItem item)
    {
        if (item != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("bill", makeNode(item.getBill()));
            node.put("billAmendment", item.getBillAmendment());
            node.put("action", item.getAction().toString());
            node.put("referCommittee", item.getReferCommittee());
            node.put("withAmd", item.getWithAmd());
            node.put("votes", makeArrayNode(item.getVotes()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(AgendaVoteCommitteeVote vote)
    {
        if (vote != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("name", vote.getName());
            node.put("rank", vote.getRank());
            node.put("party", vote.getParty());
            node.put("vote",  vote.getVote());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public Transcript readTranscript(File storageFile) throws JsonProcessingException, IOException
    {
        return readTranscript(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Bill readBill(File storageFile) throws JsonProcessingException, IOException
    {
        return readBill(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Calendar readCalendar(File storageFile) throws JsonProcessingException, IOException
    {
        return readCalendar(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Agenda readAgenda(File storageFile) throws JsonProcessingException, IOException
    {
        return readAgenda(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Transcript readTranscript(String data) throws JsonProcessingException, IOException
    {
        return readTranscript(new StringReader(data));
    }

    public Bill readBill(String data) throws JsonProcessingException, IOException
    {
        return readBill(new StringReader(data));
    }

    public Calendar readCalendar(String data) throws JsonProcessingException, IOException
    {
        return readCalendar(new StringReader(data));
    }

    public Agenda readAgenda(String data) throws JsonProcessingException, IOException
    {
        return readAgenda(new StringReader(data));
    }

    public Transcript readTranscript(Reader reader) throws JsonProcessingException, IOException
    {
        JsonNode node = objectMapper.readTree(reader);
        Transcript transcript = new Transcript();
        transcript.setId(node.get("id").asText());
        transcript.setTimeStamp(makeDate(node.get("timeStamp")));
        transcript.setLocation(node.get("location").asText());
        transcript.setType(node.get("type").asText());
        transcript.setTranscriptText(node.get("transcriptText").asText());
        transcript.setTranscriptTextProcessed(node.get("transcriptTextProcessed").asText());
        transcript.setRelatedBills((List<Bill>)makeList(Bill.class, node.get("relatedBills")));

        transcript.setYear(node.get("year").asInt());
        transcript.setSession(node.get("session").asInt());
        transcript.setModifiedDate(makeDate(node.get("modified")));
        transcript.setPublishDate(makeDate(node.get("published")));
        transcript.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
        return transcript;
    }

    public Bill readBill(Reader reader) throws JsonProcessingException, IOException
    {
        JsonNode node = objectMapper.readTree(reader);
        Bill bill = new Bill();
        bill.setBillId(node.get("billId").asText());
        bill.setTitle(node.get("title").asText());
        bill.setLawSection(node.get("lawSection").asText());
        bill.setSummary(node.get("summary").asText());
        bill.setActiveVersion(node.get("activeVersion").asText());
        bill.setActiveHistory(new LinkedList<String>((List<String>)makeList(String.class, node.get("activeHistory"))));
        bill.setPreviousVersions((List<String>)makeList(String.class, node.get("previousVersions")));
        bill.setSponsor(makePerson(node.get("sponsor")));
        bill.setOtherSponsors((List<Person>)makeList(Person.class, node.get("otherSponsors")));
        bill.setPastCommittees((List<String>)makeList(String.class, node.get("pastCommittees")));
        bill.setActions((List<BillAction>)makeList(BillAction.class, node.get("actions")));
        for (BillAction action : bill.getActions()) {
            action.setBill(bill);
        }
        bill.setUniBill(node.get("uniBill").asBoolean());
        for (Object obj : makeList(BillAmendment.class, node.get("amendments"))) {
            BillAmendment amendment = (BillAmendment)obj;
            for (Vote vote : amendment.getVotes()) {
                vote.setBill(bill);
            }
            bill.addAmendment((BillAmendment)obj);
        }

        bill.setYear(node.get("year").asInt());
        bill.setSession(node.get("session").asInt());
        bill.setModifiedDate(makeDate(node.get("modified")));
        bill.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
        return bill;
    }

    public Calendar readCalendar(Reader reader) throws JsonProcessingException, IOException
    {
        JsonNode node = objectMapper.readTree(reader);
        Calendar calendar = new Calendar();
        calendar.setNumber(node.get("number").asInt());
        calendar.setYear(node.get("year").asInt());
        calendar.setSession(node.get("session").asInt());
        calendar.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));

        for (Object obj : makeList(CalendarSupplemental.class, node.get("supplementals"))) {
            calendar.putSupplemental((CalendarSupplemental)obj);
        }
        for (Object obj : makeList(CalendarActiveList.class, node.get("activeLists"))) {
            calendar.putActiveList((CalendarActiveList)obj);
        }
        return calendar;
    }

    public Agenda readAgenda(Reader reader) throws JsonProcessingException, IOException
    {
        JsonNode node = objectMapper.readTree(reader);
        Agenda agenda = new Agenda();
        agenda.setNumber(node.get("number").asInt());
        agenda.setYear(node.get("year").asInt());
        agenda.setSession(node.get("session").asInt());
        agenda.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));

        for (Object obj : makeList(AgendaInfoAddendum.class, node.get("agendaInfoAddendum"))) {
            agenda.putAgendaInfoAddendum((AgendaInfoAddendum)obj);
        }
        for (Object obj : makeList(AgendaVoteAddendum.class, node.get("agendaVoteAddendum"))) {
            agenda.putAgendaVoteAddendum((AgendaVoteAddendum)obj);
        }

        return agenda;
    }

    public List<? extends Object> makeList(Class<? extends Object> cls, JsonNode array) {
        List<Object> list = new ArrayList<Object>();
        Iterator<JsonNode> iter = array.getElements();
        while(iter.hasNext()) {
            if (cls == String.class) {
                list.add(iter.next().asText());
            }
            else if (cls == Bill.class) {
                list.add(makeBill(iter.next()));
            }
            else if (cls == BillAmendment.class) {
                list.add(makeBillAmendment(iter.next()));
            }
            else if (cls == Vote.class) {
                list.add(makeVote(iter.next()));
            }
            else if (cls == BillAction.class) {
                list.add(makeAction(iter.next()));
            }
            else if (cls == Person.class) {
                list.add(makePerson(iter.next()));
            }
            else if (cls == CalendarSupplemental.class) {
                list.add(makeCalendarSupplemental(iter.next()));
            }
            else if (cls == CalendarSupplementalSection.class) {
                list.add(makeCalendarSupplementalSection(iter.next()));
            }
            else if (cls == CalendarSupplementalSectionEntry.class) {
                list.add(makeCalendarSupplementalSectionEntry(iter.next()));
            }
            else if (cls == CalendarActiveList.class) {
                list.add(makeCalendarActiveList(iter.next()));
            }
            else if (cls == CalendarActiveListEntry.class) {
                list.add(makeCalendarActiveListEntry(iter.next()));
            }
            else if (cls == AgendaInfoAddendum.class) {
                list.add(makeAgendaInfoAddendum(iter.next()));
            }
            else if (cls == AgendaInfoCommittee.class) {
                list.add(makeAgendaInfoCommittee(iter.next()));
            }
            else if (cls == AgendaInfoCommitteeItem.class) {
                list.add(makeAgendaInfoCommitteeItem(iter.next()));
            }
            else if (cls == AgendaVoteAddendum.class) {
                list.add(makeAgendaVoteAddendum(iter.next()));
            }
            else if (cls == AgendaVoteCommittee.class) {
                list.add(makeAgendaVoteCommittee(iter.next()));
            }
            else if (cls == AgendaVoteCommitteeAttendance.class) {
                list.add(makeAgendaVoteCommitteeAttendance(iter.next()));
            }
            else if (cls == AgendaVoteCommitteeItem.class) {
                list.add(makeAgendaVoteCommitteeItem(iter.next()));
            }
            else if (cls == AgendaVoteCommitteeVote.class) {
                list.add(makeAgendaVoteCommitteeVote(iter.next()));
            }
            else {
                throw new RuntimeException("Invalid list item type: "+cls);
            }
        }
        // Remove all null entries from the list since null == deleted in storage
        while (list.remove(null)) {}

        return list;
    }

    public Set<? extends Object> makeSet(Class<? extends Object> cls, JsonNode array)
    {
        return new HashSet<Object>(makeList(cls, array));
    }

    public CalendarSupplemental makeCalendarSupplemental(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            CalendarSupplemental supplemental = new CalendarSupplemental();
            supplemental.setId(node.get("id").asText());
            supplemental.setCalDate(makeDate(node.get("calDate")));
            supplemental.setReleaseDateTime(makeDate(node.get("releaseDateTime")));
            for (Object obj : makeList(CalendarSupplementalSection.class, node.get("sections"))) {
                supplemental.putSection((CalendarSupplementalSection)obj);
            }

            supplemental.setYear(node.get("year").asInt());
            supplemental.setSession(node.get("session").asInt());
            supplemental.setModifiedDate(makeDate(node.get("modified")));
            supplemental.setPublishDate(makeDate(node.get("published")));
            supplemental.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return supplemental;
        }
    }

    public CalendarSupplementalSection makeCalendarSupplementalSection(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            CalendarSupplementalSection section = new CalendarSupplementalSection();
            section.setName(node.get("name").asText());
            section.setType(node.get("type").asText());
            section.setCd(node.get("cd").asInt());
            for (Object obj : makeList(CalendarSupplementalSectionEntry.class, node.get("entries"))) {
                section.addEntry((CalendarSupplementalSectionEntry)obj);
            }
            return section;

        }
    }

    public CalendarSupplementalSectionEntry makeCalendarSupplementalSectionEntry(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            CalendarSupplementalSectionEntry entry = new CalendarSupplementalSectionEntry();
            entry.setCalendarNumber(node.get("calendarNumber").asInt());
            entry.setBill(makeBill(node.get("bill")));
            entry.setBillAmendment(node.get("billAmendment").asText());
            entry.setSubBill(makeBill(node.get("subBill")));
            entry.setSubBillAmendment(node.get("subBillAmendment").asText());
            entry.setBillHigh(node.get("billHigh").asBoolean());
            return entry;
        }
    }

    public CalendarActiveList makeCalendarActiveList(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            CalendarActiveList activeList = new CalendarActiveList();
            activeList.setId(node.get("id").asInt());
            activeList.setNotes(node.get("notes").asText());
            activeList.setCalDate(makeDate(node.get("calDate")));
            activeList.setReleaseDateTime(makeDate(node.get("releaseDateTime")));
            for (Object obj : makeList(CalendarActiveListEntry.class, node.get("entries"))) {
                activeList.addEntry((CalendarActiveListEntry)obj);
            }

            activeList.setYear(node.get("year").asInt());
            activeList.setSession(node.get("session").asInt());
            activeList.setModifiedDate(makeDate(node.get("modified")));
            activeList.setPublishDate(makeDate(node.get("published")));
            activeList.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return activeList;
        }
    }

    public CalendarActiveListEntry makeCalendarActiveListEntry(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            CalendarActiveListEntry entry = new CalendarActiveListEntry();
            entry.setCalendarNumber(node.get("calendarNumber").asInt());
            entry.setBill(makeBill(node.get("bill")));
            entry.setBillAmendment(node.get("billAmendment").asText());
            return entry;
        }
    }

    public AgendaInfoAddendum makeAgendaInfoAddendum(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            AgendaInfoAddendum addendum = new AgendaInfoAddendum();
            addendum.setId(node.get("id").asText());
            addendum.setWeekOf(makeDate(node.get("weekOf")));
            addendum.setAgendaNumber(node.get("agendaNumber").asInt());
            for (Object obj : makeList(AgendaInfoCommittee.class, node.get("committees"))) {
                addendum.putCommittee((AgendaInfoCommittee)obj);
            }

            addendum.setYear(node.get("year").asInt());
            addendum.setSession(node.get("session").asInt());
            addendum.setModifiedDate(makeDate(node.get("modified")));
            addendum.setPublishDate(makeDate(node.get("published")));
            addendum.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return addendum;
        }
    }

    public AgendaInfoCommittee makeAgendaInfoCommittee(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            AgendaInfoCommittee committee = new AgendaInfoCommittee();
            committee.setName(node.get("name").asText());
            committee.setChair(node.get("chair").asText());
            committee.setLocation(node.get("location").asText());
            committee.setMeetDay(node.get("meetDay").asText());
            committee.setMeetDate(makeDate(node.get("meetDate")));
            committee.setNotes(node.get("notes").asText());
            for (Object obj : makeList(AgendaInfoCommitteeItem.class, node.get("items"))) {
                committee.putItem((AgendaInfoCommitteeItem)obj);
            }
            return committee;
        }
    }

    public AgendaInfoCommitteeItem makeAgendaInfoCommitteeItem(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            AgendaInfoCommitteeItem item = new AgendaInfoCommitteeItem();
            item.setBill(makeBill(node.get("bill")));
            item.setBillAmendment(node.get("billAmendment").asText());
            item.setMessage(node.get("message").asText());
            item.setTitle(node.get("title").asText());
            return item;
        }
    }

    public AgendaVoteAddendum makeAgendaVoteAddendum(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            AgendaVoteAddendum addendum = new AgendaVoteAddendum();
            addendum.setId(node.get("id").asText());
            addendum.setAgendaNumber(node.get("agendaNumber").asInt());
            for (Object obj : makeList(AgendaVoteCommittee.class, node.get("committees"))) {
                addendum.putCommittee((AgendaVoteCommittee)obj);
            }

            addendum.setYear(node.get("year").asInt());
            addendum.setSession(node.get("session").asInt());
            addendum.setModifiedDate(makeDate(node.get("modified")));
            addendum.setPublishDate(makeDate(node.get("published")));
            addendum.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return addendum;
        }
    }

    public AgendaVoteCommittee makeAgendaVoteCommittee(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            AgendaVoteCommittee committee = new AgendaVoteCommittee();
            committee.setName(node.get("name").asText());
            committee.setChair(node.get("chair").asText());
            committee.setMeetDate(makeDate(node.get("meetDate")));
            committee.setModifiedDate(makeDate(node.get("modified")));
            for (Object obj : makeList(AgendaVoteCommitteeItem.class, node.get("items"))) {
                committee.putItem((AgendaVoteCommitteeItem)obj);
            }
            for (Object obj : makeList(AgendaVoteCommitteeAttendance.class, node.get("attendance"))) {
                committee.addAttendance((AgendaVoteCommitteeAttendance)obj);
            }
            return committee;
        }
    }

    public AgendaVoteCommitteeAttendance makeAgendaVoteCommitteeAttendance(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            AgendaVoteCommitteeAttendance attendance = new AgendaVoteCommitteeAttendance();
            attendance.setName(node.get("name").asText());
            attendance.setRank(node.get("rank").asText());
            attendance.setParty(node.get("party").asText());
            attendance.setAttendance(node.get("attendance").asText());
            return attendance;
        }
    }

    public AgendaVoteCommitteeItem makeAgendaVoteCommitteeItem(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            AgendaVoteCommitteeItem item = new AgendaVoteCommitteeItem();
            item.setBill(makeBill(node.get("bill")));
            item.setBillAmendment(node.get("billAmendment").asText());
            item.setAction(VoteAction.valueOf(node.get("action").asText()));
            item.setReferCommittee(node.get("referCommittee").asText());
            item.setWithAmd(node.get("withAmd").asBoolean());
            for (Object obj : makeList(AgendaVoteCommitteeVote.class, node.get("votes"))) {
                item.addVote((AgendaVoteCommitteeVote)obj);
            }
            return item;
        }
    }

    public AgendaVoteCommitteeVote makeAgendaVoteCommitteeVote(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            AgendaVoteCommitteeVote vote = new AgendaVoteCommitteeVote();
            vote.setName(node.get("name").asText());
            vote.setRank(node.get("rank").asText());
            vote.setParty(node.get("party").asText());
            vote.setVote(node.get("vote").asText());
            return vote;
        }
    }

    public Bill makeBill(JsonNode node)
    {
        return node.isNull() ? null : (Bill)storage.get(node.asText(), Bill.class);
    }

    public BillAmendment makeBillAmendment(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            BillAmendment amendment = new BillAmendment();
            amendment.setBaseBillId(node.get("baseBillId").asText());
            amendment.setBaseBillPrintNo(node.get("baseBillPrintNo").asText());
            amendment.setVersion(node.get("version").asText());
            amendment.setSameAs(node.get("sameAs").asText());
            amendment.setMemo(node.get("memo").asText());
            amendment.setLaw(node.get("law").asText());
            amendment.setActClause(node.get("actClause").asText());
            amendment.setFulltext(node.get("fulltext").asText());
            amendment.setCurrentCommittee(node.get("currentCommittee").asText());
            amendment.setCoSponsors((List<Person>)makeList(Person.class, node.get("coSponsors")));
            amendment.setMultiSponsors((List<Person>)makeList(Person.class, node.get("multiSponsors")));
            amendment.setStricken(node.get("stricken").asBoolean());
            amendment.setVotes((List<Vote>)makeList(Vote.class, node.get("votes")));

            amendment.setYear(node.get("year").asInt());
            amendment.setSession(node.get("session").asInt());
            amendment.setModifiedDate(makeDate(node.get("modified")));
            amendment.setPublishDate(makeDate(node.get("published")));
            amendment.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return amendment;
        }
    }

    public Date makeDate(JsonNode node)
    {
        try {
            return node.isNull() ? null : jsonDateFormat.parse(node.asText());
        }
        catch (ParseException e) {
            logger.error("Invalid json date format: "+node.asText(), e);
            return null;
        }
    }

    public Person makePerson(JsonNode node) {
        if (node.isNull()) {
            return null;
        }
        else {
            logger.debug(node.toString());
            Person person = new Person();
            person.setId(node.get("id").asText());
            person.setFullname(node.get("fullname").asText());
            person.setPosition(node.get("position").asText());
            person.setBranch(node.get("branch").asText());
            person.setContactInfo(node.get("contactInfo").asText());
            person.setGuid(node.get("guid").asText());
            return person;
        }
    }

    public BillAction makeAction(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            BillAction action = new BillAction();
            action.setOid(node.get("id").asText());
            action.setDate(new Date(node.get("date").asLong()));
            action.setText(node.get("text").asText());
            action.setSession(node.get("year").asInt());
            action.setModifiedDate(makeDate(node.get("modified")));
            action.setPublishDate(makeDate(node.get("published")));
            action.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return action;
        }
    }

    public Vote makeVote(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            Vote vote = new Vote(
                node.get("billId").asText(),
                node.get("billAmendment").asText(),
                makeDate(node.get("date")),
                node.get("voteType").asInt(),
                node.get("sequenceNumber").asText()
            );
            vote.setAbsent((List<String>)makeList(String.class, node.get("absent")));
            vote.setAyes((List<String>)makeList(String.class, node.get("ayes")));
            vote.setAyeswr((List<String>)makeList(String.class, node.get("ayeswr")));
            vote.setNays((List<String>)makeList(String.class, node.get("nays")));
            vote.setExcused((List<String>)makeList(String.class, node.get("excused")));
            vote.setAbstains((List<String>)makeList(String.class, node.get("abstains")));
            vote.setDescription(node.get("description").asText());
            vote.setModifiedDate(makeDate(node.get("modified")));
            vote.setPublishDate(makeDate(node.get("published")));
            vote.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return vote;
        }
    }
}
