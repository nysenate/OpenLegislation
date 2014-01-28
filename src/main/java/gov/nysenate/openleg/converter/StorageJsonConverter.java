package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
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

    public void write(Transcript value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
    }

    public void write(Bill value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
    }

    public void write(Agenda value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
    }

    public void write(Meeting value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
    }

    public void write(Calendar value, File storageFile) throws IOException
    {
        write(value, new FileOutputStream(storageFile));
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

    public String toString(Agenda value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(Meeting value) throws IOException
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

    public Transcript readTranscript(File storageFile) throws JsonProcessingException, IOException
    {
        return readTranscript(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Bill readBill(File storageFile) throws JsonProcessingException, IOException
    {
        return readBill(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Agenda readAgenda(File storageFile) throws JsonProcessingException, IOException
    {
        return readAgenda(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Meeting readMeeting(File storageFile) throws JsonProcessingException, IOException
    {
        return readMeeting(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Calendar readCalendar(File storageFile) throws JsonProcessingException, IOException
    {
        return readCalendar(new InputStreamReader(new FileInputStream(storageFile), this.encoding));
    }

    public Bill readBill(String data) throws JsonProcessingException, IOException
    {
        return readBill(new StringReader(data));
    }

    public Agenda readAgenda(String data) throws JsonProcessingException, IOException
    {
        return readAgenda(new StringReader(data));
    }

    public Meeting readMeeting(String data) throws JsonProcessingException, IOException
    {
        return readMeeting(new StringReader(data));
    }

    public Calendar readCalendar(String data) throws JsonProcessingException, IOException
    {
        return readCalendar(new StringReader(data));
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
        node.put("active", transcript.isActive());
        node.put("year", transcript.getYear());
        node.put("session", transcript.getSession());
        node.put("modified", makeNode(transcript.getModifiedDate()));
        node.put("published", makeNode(transcript.getPublishDate()));
        node.put("dataSources", makeArrayNode(transcript.getDataSources()));
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
    }

    public void write(Calendar calendar, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("no", calendar.getNo());
        node.put("session", calendar.getSession());
        node.put("year", calendar.getYear());
        node.put("type", calendar.getType());
        node.put("supplementals", makeArrayNode(calendar.getSupplementals()));
        node.put("active", calendar.isActive());
        node.put("modified", makeNode(calendar.getModifiedDate()));
        node.put("published", makeNode(calendar.getPublishDate()));
        node.put("dataSources", makeArrayNode(calendar.getDataSources()));
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
    }

    public Calendar readCalendar(Reader reader) throws JsonProcessingException, IOException
    {
        JsonNode node = objectMapper.readTree(reader);
        Calendar calendar = new Calendar(
            node.get("no").asInt(),
            node.get("session").asInt(),
            node.get("year").asInt(),
            node.get("type").asText()
        );
        calendar.setSupplementals((List<Supplemental>)makeList(Supplemental.class, node.get("supplementals")));
        for(Supplemental supplemental : calendar.getSupplementals()) {
            supplemental.setCalendar(calendar);
        }

        calendar.setActive(node.get("active").asBoolean());
        calendar.setModifiedDate(makeDate(node.get("modified")));
        calendar.setPublishDate(makeDate(node.get("published")));
        calendar.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
        return calendar;
    }

    public void write(Meeting meeting, OutputStream out) throws IOException
    {
        logger.debug("Writing Agenda: "+meeting.getOid());
        ObjectNode node = objectMapper.createObjectNode();
        node.put("meetingDateTime", makeNode(meeting.getMeetingDateTime()));
        node.put("meetday", meeting.getMeetday());
        node.put("location", meeting.getLocation());
        node.put("committeeName", meeting.getCommitteeName());
        node.put("committeeChair", meeting.getCommitteeChair());
        node.put("bills", makeArrayNode(meeting.getBills()));
        node.put("notes", meeting.getNotes());
        node.put("active", meeting.isActive());
        node.put("modified", makeNode(meeting.getModifiedDate()));
        node.put("published", makeNode(meeting.getPublishDate()));
        node.put("dataSources", makeArrayNode(meeting.getDataSources()));
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
    }

    public Meeting readMeeting(Reader reader) throws JsonProcessingException, IOException
    {
        JsonNode node = objectMapper.readTree(reader);
        Meeting meeting = new Meeting(node.get("committeeName").asText(), makeDate(node.get("meetingDateTime")));
        meeting.setMeetday(node.get("meetday").asText());
        meeting.setLocation(node.get("location").asText());
        meeting.setCommitteeChair(node.get("committeeChair").asText());
        meeting.setBills((List<Bill>)makeList(Bill.class, node.get("bills")));
        meeting.setNotes(node.get("notes").asText());
        meeting.setActive(node.get("active").asBoolean());
        meeting.setModifiedDate(makeDate(node.get("modified")));
        meeting.setPublishDate(makeDate(node.get("published")));
        meeting.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
        return meeting;
    }

    public void write(Agenda agenda, OutputStream out) throws IOException
    {
        logger.debug("Writing Agenda: "+agenda.getOid());
        ObjectNode node = objectMapper.createObjectNode();
        node.put("session", agenda.getSession());
        node.put("year", agenda.getYear());
        node.put("number", agenda.getNumber());
        node.put("addendums", makeArrayNode(agenda.getAddendums()));
        node.put("active", agenda.isActive());
        node.put("modified", makeNode(agenda.getModifiedDate()));
        node.put("published", makeNode(agenda.getPublishDate()));
        node.put("dataSources", makeArrayNode(agenda.getDataSources()));
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
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

        transcript.setActive(node.get("active").asBoolean());
        transcript.setYear(node.get("year").asInt());
        transcript.setSession(node.get("session").asInt());
        transcript.setModifiedDate(makeDate(node.get("modified")));
        transcript.setPublishDate(makeDate(node.get("published")));
        transcript.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
        return transcript;
    }

    public Agenda readAgenda(Reader reader) throws JsonProcessingException, IOException
    {
        JsonNode node = objectMapper.readTree(reader);
        Agenda agenda = new Agenda(node.get("session").asInt(), node.get("year").asInt(), node.get("number").asInt());
        agenda.setAddendums((List<Addendum>)makeList(Addendum.class, node.get("addendums")));
        for (Addendum addendum : agenda.getAddendums()) {
            addendum.setAgenda(agenda);
        }

        agenda.setActive(node.get("active").asBoolean());
        agenda.setModifiedDate(makeDate(node.get("modified")));
        agenda.setPublishDate(makeDate(node.get("published")));
        agenda.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
        return agenda;
    }

    public void write(Bill bill, OutputStream out) throws IOException
    {
        logger.debug("Writing Bill: "+bill.getBillId());
        ObjectNode node = objectMapper.createObjectNode();
        node.put("actClause", bill.getActClause());
        node.put("currentCommittee", bill.getCurrentCommittee());
        node.put("fulltext", bill.getFulltext());
        node.put("law", bill.getLaw());
        node.put("lawSection", bill.getLawSection());
        node.put("memo", bill.getMemo());
        node.put("active", bill.isActive());
        node.put("modified", makeNode(bill.getModifiedDate()));
        node.put("published", makeNode(bill.getPublishDate()));
        node.put("dataSources", makeArrayNode(bill.getDataSources()));
        node.put("sameAs", bill.getSameAs());
        node.put("otherSponsors", makeArrayNode(bill.getOtherSponsors()));
        node.put("multiSponsors", makeArrayNode(bill.getMultiSponsors()));
        node.put("coSponsors", makeArrayNode(bill.getCoSponsors()));
        node.put("actions", makeArrayNode(bill.getActions()));
        node.put("sponsor", makeNode(bill.getSponsor()));
        node.put("stricken", bill.isStricken());
        node.put("pastCommittees", makeArrayNode(bill.getPastCommittees()));
        node.put("previousVersions", makeArrayNode(bill.getPreviousVersions()));
        node.put("senateBillNo", bill.getBillId());
        node.put("summary", bill.getSummary());
        node.put("title", bill.getTitle());
        node.put("year", bill.getSession());
        node.put("uniBill", bill.isUniBill());
        node.put("amendments", makeArrayNode(bill.getAmendments()));
        node.put("votes", makeArrayNode(bill.getVotes()));
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
    }

    public Bill readBill(Reader reader) throws JsonProcessingException, IOException
    {
        JsonNode node = objectMapper.readTree(reader);
        Bill bill = new Bill(node.get("senateBillNo").asText(),node.get("year").asInt());
        bill.setActClause(node.get("actClause").asText());
        bill.setAmendments((List<String>)makeList(String.class, node.get("amendments")));
        bill.setCurrentCommittee(node.get("currentCommittee").asText());
        bill.setFulltext(node.get("fulltext").asText());
        bill.setLaw(node.get("law").asText());
        bill.setLawSection(node.get("lawSection").asText());
        bill.setMemo(node.get("memo").asText());
        bill.setPastCommittees((List<String>)makeList(String.class, node.get("pastCommittees")));
        bill.setPreviousVersions((List<String>)makeList(String.class, node.get("previousVersions")));
        bill.setSameAs(node.get("sameAs").asText());
        bill.setStricken(node.get("stricken").asBoolean());
        bill.setSummary(node.get("summary").asText());
        bill.setUniBill(node.get("uniBill").asBoolean());
        bill.setTitle(node.get("title").asText());

        bill.setActions((List<Action>)makeList(Action.class, node.get("actions")));
        for(Action action : bill.getActions()) {
            action.setBill(bill);
        }

        bill.setVotes((List<Vote>)makeList(Vote.class, node.get("votes")));
        for(Vote vote : bill.getVotes()) {
            vote.setBill(bill);
        }

        bill.setSponsor(makePerson(node.get("sponsor")));
        bill.setOtherSponsors((List<Person>)makeList(Person.class, node.get("otherSponsors")));
        bill.setCoSponsors((List<Person>)makeList(Person.class, node.get("coSponsors")));
        bill.setMultiSponsors((List<Person>)makeList(Person.class, node.get("multiSponsors")));

        bill.setActive(node.get("active").asBoolean());
        bill.setModifiedDate(makeDate(node.get("modified")));
        bill.setPublishDate(makeDate(node.get("published")));
        bill.setDataSources(new HashSet<String>((HashSet<String>)makeSet(String.class, node.get("dataSources"))));
        return bill;
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
            else if (cls == Vote.class) {
                list.add(makeVote(iter.next()));
            }
            else if (cls == Action.class) {
                list.add(makeAction(iter.next()));
            }
            else if (cls == Agenda.class) {
                list.add(makeAgenda(iter.next()));
            }
            else if (cls == Addendum.class) {
                list.add(makeAddendum(iter.next()));
            }
            else if (cls == Meeting.class) {
                list.add(makeMeeting(iter.next()));
            }
            else if (cls == Person.class) {
                list.add(makePerson(iter.next()));
            }
            else if (cls == Supplemental.class) {
                list.add(makeSupplemental(iter.next()));
            }
            else if (cls == Sequence.class) {
                list.add(makeSequence(iter.next()));
            }
            else if (cls == Section.class) {
                list.add(makeSection(iter.next()));
            }
            else if (cls == CalendarEntry.class) {
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

    public CalendarEntry makeCalendarEntry(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            CalendarEntry entry = new CalendarEntry();
            entry.setOid(node.get("oid").asText());
            entry.setNo(node.get("no").asText());
            entry.setBill(makeBill(node.get("bill")));
            entry.setSubBill(makeBill(node.get("subBill")));
            entry.setBillHigh(node.get("billHigh").asText());
            entry.setMotionDate(makeDate(node.get("motionDate")));
            return entry;
        }
    }

    public Sequence makeSequence(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            Sequence sequence = new Sequence();
            sequence.setNo(node.get("no").asText());
            sequence.setId(node.get("id").asText());
            sequence.setActCalDate(makeDate(node.get("actCalDate")));
            sequence.setReleaseDateTime(makeDate(node.get("releaseDateTime")));
            sequence.setNotes(node.get("notes").asText());
            sequence.setCalendarEntries((List<CalendarEntry>)makeList(CalendarEntry.class, node.get("calendarEntries")));
            for(CalendarEntry calendarEntry : sequence.getCalendarEntries()) {
                calendarEntry.setSequence(sequence);
            }
            return sequence;
        }
    }

    public Section makeSection(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            Section section = new Section();
            section.setId(node.get("id").asText());
            section.setName(node.get("name").asText());
            section.setType(node.get("type").asText());
            section.setCd(node.get("cd").asText());
            section.setCalendarEntries((List<CalendarEntry>)makeList(CalendarEntry.class, node.get("calendarEntries")));
            for(CalendarEntry calendarEntry : section.getCalendarEntries()) {
                calendarEntry.setSection(section);
            }
            return section;
        }
    }

    public Supplemental makeSupplemental(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            Supplemental supplemental = new Supplemental();
            supplemental.setId(node.get("id").asText());
            supplemental.setSupplementalId(node.get("supplementalId").asText());
            supplemental.setCalendarDate(makeDate(node.get("calendarDate")));
            supplemental.setReleaseDateTime(makeDate(node.get("releaseDateTime")));

            supplemental.setSections((List<Section>)makeList(Section.class, node.get("sections")));
            for(Section section : supplemental.getSections()) {
                section.setSupplemental(supplemental);
            }

            supplemental.setSequences((List<Sequence>)makeList(Sequence.class, node.get("sequences")));
            for(Sequence sequence : supplemental.getSequences()) {
                sequence.setSupplemental(supplemental);
            }

            return supplemental;
        }
    }

    public Bill makeBill(JsonNode node)
    {
        return node.isNull() ? null : (Bill)storage.get(node.asText(), Bill.class);
    }

    public Meeting makeMeeting(JsonNode node)
    {
        return node.isNull() ? null : (Meeting)storage.get(node.asText(), Meeting.class);
    }

    public Agenda makeAgenda(JsonNode node)
    {
        return node.isNull() ? null : (Agenda)storage.get(node.asText(), Agenda.class);
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

    public Action makeAction(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            Action action = new Action();
            action.setOid(node.get("id").asText());
            action.setDate(new Date(node.get("date").asLong()));
            action.setText(node.get("text").asText());
            action.setSession(node.get("year").asInt());
            action.setActive(node.get("active").asBoolean());
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
            vote.setActive(node.get("active").asBoolean());
            vote.setModifiedDate(makeDate(node.get("modified")));
            vote.setPublishDate(makeDate(node.get("published")));
            vote.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return vote;
        }
    }

    public Addendum makeAddendum(JsonNode node)
    {
        if (node.isNull()) {
            return null;
        }
        else {
            Addendum addendum = new Addendum(
                node.get("addendumId").asText(),
                node.get("weekOf").asText(),
                makeDate(node.get("published")),
                node.get("agendaNo").asInt(),
                node.get("year").asInt()
            );
            addendum.setMeetings((List<Meeting>)makeList(Meeting.class, node.get("meetings")));
            addendum.setActive(node.get("active").asBoolean());
            addendum.setModifiedDate(makeDate(node.get("modified")));
            addendum.setPublishDate(makeDate(node.get("published")));
            addendum.setDataSources(new HashSet<String>((List<String>)makeList(String.class, node.get("dataSources"))));
            return addendum;
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
                else if (Person.class.isInstance(item)) {
                    arrayNode.add(makeNode((Person)item));
                }
                else if (String.class.isInstance(item)) {
                    arrayNode.add((String)item);
                }
                else if (Bill.class.isInstance(item)) {
                    arrayNode.add(makeNode((Bill)item));
                }
                else if (Agenda.class.isInstance(item)) {
                    arrayNode.add(makeNode((Agenda)item));
                }
                else if (Addendum.class.isInstance(item)) {
                    arrayNode.add(makeNode((Addendum)item));
                }
                else if (Meeting.class.isInstance(item)) {
                    arrayNode.add(makeNode((Meeting)item));
                }
                else if (Action.class.isInstance(item)) {
                    arrayNode.add(makeNode((Action)item));
                }
                else if (Supplemental.class.isInstance(item)) {
                    arrayNode.add(makeNode((Supplemental)item));
                }
                else if (Sequence.class.isInstance(item)) {
                    arrayNode.add(makeNode((Sequence)item));
                }
                else if (Section.class.isInstance(item)) {
                    arrayNode.add(makeNode((Section)item));
                }
                else if (CalendarEntry.class.isInstance(item)) {
                    arrayNode.add(makeNode((CalendarEntry)item));
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

    public String makeNode(Meeting meeting)
    {
        if (meeting != null) {
            return storage.key(meeting);
        }
        else {
            return null;
        }
    }

    public String makeNode(Agenda agenda)
    {
        if (agenda != null) {
            return storage.key(agenda);
        }
        else {
            return null;
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

    public JsonNode makeNode(CalendarEntry entry)
    {
        if (entry != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("oid", entry.getOid());
            node.put("no", entry.getNo());
            node.put("bill", makeNode(entry.getBill()));
            node.put("subBill", makeNode(entry.getSubBill()));
            node.put("billHigh", entry.getBillHigh());
            node.put("motionDate", makeNode(entry.getMotionDate()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Section section)
    {
        if (section != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", section.getId());
            node.put("name", section.getName());
            node.put("type", section.getType());
            node.put("cd", section.getCd());
            node.put("calendarEntries", makeArrayNode(section.getCalendarEntries()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Sequence sequence)
    {
        if (sequence != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("no", sequence.getNo());
            node.put("id", sequence.getId());
            node.put("notes", sequence.getNotes());
            node.put("actCalDate", makeNode(sequence.getActCalDate()));
            node.put("releaseDateTime", makeNode(sequence.getReleaseDateTime()));
            node.put("calendarEntries", makeArrayNode(sequence.getCalendarEntries()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Supplemental supplemental)
    {
        if (supplemental != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", supplemental.getId());
            node.put("supplementalId", supplemental.getSupplementalId());
            node.put("calendarDate", makeNode(supplemental.getCalendarDate()));
            node.put("releaseDateTime", makeNode(supplemental.getReleaseDateTime()));
            node.put("sections", makeArrayNode(supplemental.getSections()));
            node.put("sequences", makeArrayNode(supplemental.getSequences()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Addendum addendum)
    {
        if (addendum != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("addendumId", addendum.getAddendumId());
            node.put("weekOf", addendum.getWeekOf());
            node.put("meetings", makeArrayNode(addendum.getMeetings()));
            node.put("year", addendum.getYear());
            node.put("agendaNo", addendum.getAgenda().getNumber());
            node.put("active", addendum.isActive());
            node.put("modified", makeNode(addendum.getModifiedDate()));
            node.put("published", makeNode(addendum.getPublishDate()));
            node.put("dataSources", makeArrayNode(addendum.getDataSources()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

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

    public JsonNode makeNode(Action action)
    {
        if (action != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", action.getOid());
            node.put("active", action.isActive());
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
            node.put("active", vote.isActive());
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
}
