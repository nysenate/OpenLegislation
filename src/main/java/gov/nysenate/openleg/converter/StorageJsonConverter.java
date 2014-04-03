package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.model.BillAction;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.BillAmendment;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarSectionEntry;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
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
        node.put("no", calendar.getNumber());
        node.put("session", calendar.getSession());
        node.put("year", calendar.getYear());
//        node.put("type", calendar.getType());
//        node.put("supplementals", makeArrayNode(calendar.getSupplementals()));
        node.put("modified", makeNode(calendar.getModifiedDate()));
        node.put("published", makeNode(calendar.getPublishDate()));
        node.put("dataSources", makeArrayNode(calendar.getDataSources()));
        write(node, out);
    }

    public void write(Agenda agenda, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        write(node, out);
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
//            else if (cls == Supplemental.class) {
//                list.add(makeSupplemental(iter.next()));
//            }
//            else if (cls == Sequence.class) {
//                list.add(makeSequence(iter.next()));
//            }
//            else if (cls == Section.class) {
//                list.add(makeSection(iter.next()));
//            }
            else if (cls == CalendarSectionEntry.class) {
                list.add(makeCalendarEntry(iter.next()));
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

    public CalendarSectionEntry makeCalendarEntry(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            CalendarSectionEntry entry = new CalendarSectionEntry();
            entry.setNumber(node.get("no").asInt());
            entry.setBill(makeBill(node.get("bill")));
            entry.setSubBill(makeBill(node.get("subBill")));
            entry.setBillHigh(node.get("billHigh").asBoolean());
            return entry;
        }
    }

//    public Sequence makeSequence(JsonNode node)
//    {
//        if (node.isNull()) {
//            return null;
//        }
//        else {
//            Sequence sequence = new Sequence();
//            sequence.setNo(node.get("no").asText());
//            sequence.setId(node.get("id").asText());
//            sequence.setActCalDate(makeDate(node.get("actCalDate")));
//            sequence.setReleaseDateTime(makeDate(node.get("releaseDateTime")));
//            sequence.setNotes(node.get("notes").asText());
//            sequence.setCalendarEntries((List<CalendarSectionEntry>)makeList(CalendarSectionEntry.class, node.get("calendarEntries")));
//            return sequence;
//        }
//    }
//
//    public Section makeSection(JsonNode node)
//    {
//        if (node.isNull()) {
//            return null;
//        }
//        else {
//            Section section = new Section();
//            section.setId(node.get("id").asText());
//            section.setName(node.get("name").asText());
//            section.setType(node.get("type").asText());
//            section.setCd(node.get("cd").asText());
//            section.setCalendarEntries((List<CalendarSectionEntry>)makeList(CalendarSectionEntry.class, node.get("calendarEntries")));
//            return section;
//        }
//    }
//
//    public Supplemental makeSupplemental(JsonNode node)
//    {
//        if (node.isNull()) {
//            return null;
//        }
//        else {
//            Supplemental supplemental = new Supplemental();
//            supplemental.setId(node.get("id").asText());
//            supplemental.setSupplementalId(node.get("supplementalId").asText());
//            supplemental.setCalendarDate(makeDate(node.get("calendarDate")));
//            supplemental.setReleaseDateTime(makeDate(node.get("releaseDateTime")));
//
//            supplemental.setSections((List<Section>)makeList(Section.class, node.get("sections")));
//            for(Section section : supplemental.getSections()) {
//                section.setSupplemental(supplemental);
//            }
//
//            supplemental.setSequences((List<Sequence>)makeList(Sequence.class, node.get("sequences")));
//            for(Sequence sequence : supplemental.getSequences()) {
//                sequence.setSupplemental(supplemental);
//            }
//
//            return supplemental;
//        }
//    }

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
//                else if (Supplemental.class.isInstance(item)) {
//                    arrayNode.add(makeNode((Supplemental)item));
//                }
//                else if (Sequence.class.isInstance(item)) {
//                    arrayNode.add(makeNode((Sequence)item));
//                }
//                else if (Section.class.isInstance(item)) {
//                    arrayNode.add(makeNode((Section)item));
//                }
                else if (CalendarSectionEntry.class.isInstance(item)) {
                    arrayNode.add(makeNode((CalendarSectionEntry)item));
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

    public JsonNode makeNode(CalendarSectionEntry entry)
    {
        if (entry != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("no", entry.getNumber());
            node.put("bill", makeNode(entry.getBill()));
            node.put("subBill", makeNode(entry.getSubBill()));
            node.put("billHigh", entry.getBillHigh());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

//    public JsonNode makeNode(Section section)
//    {
//        if (section != null) {
//            ObjectNode node = objectMapper.createObjectNode();
//            node.put("id", section.getId());
//            node.put("name", section.getName());
//            node.put("type", section.getType());
//            node.put("cd", section.getCd());
//            node.put("calendarEntries", makeArrayNode(section.getCalendarEntries()));
//            return node;
//        }
//        else {
//            return objectMapper.createObjectNode().nullNode();
//        }
//    }
//
//    public JsonNode makeNode(Sequence sequence)
//    {
//        if (sequence != null) {
//            ObjectNode node = objectMapper.createObjectNode();
//            node.put("no", sequence.getNo());
//            node.put("id", sequence.getId());
//            node.put("notes", sequence.getNotes());
//            node.put("actCalDate", makeNode(sequence.getActCalDate()));
//            node.put("releaseDateTime", makeNode(sequence.getReleaseDateTime()));
//            node.put("calendarEntries", makeArrayNode(sequence.getCalendarEntries()));
//            return node;
//        }
//        else {
//            return objectMapper.createObjectNode().nullNode();
//        }
//    }
//
//    public JsonNode makeNode(Supplemental supplemental)
//    {
//        if (supplemental != null) {
//            ObjectNode node = objectMapper.createObjectNode();
//            node.put("id", supplemental.getId());
//            node.put("supplementalId", supplemental.getSupplementalId());
//            node.put("calendarDate", makeNode(supplemental.getCalendarDate()));
//            node.put("releaseDateTime", makeNode(supplemental.getReleaseDateTime()));
//            node.put("sections", makeArrayNode(supplemental.getSections()));
//            node.put("sequences", makeArrayNode(supplemental.getSequences()));
//            return node;
//        }
//        else {
//            return objectMapper.createObjectNode().nullNode();
//        }
//    }

    public JsonNode makeNode(Person person) {
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
            node.put("year", vote.getYear());
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
        return objectMapper.readValue(reader, Transcript.class);
//        JsonNode node = objectMapper.readTree(reader);
//        Transcript transcript = new Transcript();
//        transcript.setId(node.get("id").asText());
//        transcript.setTimeStamp(makeDate(node.get("timeStamp")));
//        transcript.setLocation(node.get("location").asText());
//        transcript.setType(node.get("type").asText());
//        transcript.setTranscriptText(node.get("transcriptText").asText());
//        transcript.setTranscriptTextProcessed(node.get("transcriptTextProcessed").asText());
//        transcript.setRelatedBills((List<Bill>)makeList(Bill.class, node.get("relatedBills")));
//
//        transcript.setYear(node.get("year").asInt());
//        transcript.setSession(node.get("session").asInt());
//        transcript.setModifiedDate(makeDate(node.get("modified")));
//        transcript.setPublishDate(makeDate(node.get("published")));
//        transcript.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
//        return transcript;
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
        bill.setUniBill(node.get("uniBill").asBoolean());
        for (Object obj : makeList(BillAmendment.class, node.get("amendments"))) {
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
        return objectMapper.readValue(reader, Calendar.class);
//        JsonNode node = objectMapper.readTree(reader);
//        Calendar calendar = new Calendar(
//            node.get("no").asInt(),
//            node.get("session").asInt(),
//            node.get("year").asInt()
//            node.get("type").asText()
//        );
//        calendar.setSupplementals((List<Supplemental>)makeList(Supplemental.class, node.get("supplementals")));
//        for(Supplemental supplemental : calendar.getSupplementals()) {
//            supplemental.setCalendar(calendar);
//        }
//
//        calendar.setModifiedDate(makeDate(node.get("modified")));
//        calendar.setPublishDate(makeDate(node.get("published")));
//        calendar.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
//        return calendar;
    }

    public Agenda readAgenda(Reader reader) throws JsonProcessingException, IOException
    {
        return objectMapper.readValue(reader, Agenda.class);
//        JsonNode node = objectMapper.readTree(reader);
//        Agenda agenda = new Agenda();
//        return agenda;
    }

    private void write(ObjectNode node, OutputStream out) throws IOException
    {
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
    }
}
