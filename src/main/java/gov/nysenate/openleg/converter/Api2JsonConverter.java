package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

public class Api2JsonConverter
{
    protected static Logger logger = Logger.getLogger(Api2JsonConverter.class);

    protected final String encoding = "UTF-8";
    protected final JsonFactory jsonFactory;
    protected final ObjectMapper objectMapper;
    protected final PrettyPrinter prettyPrinter;

    public Api2JsonConverter()
    {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(Feature.INDENT_OUTPUT);
        this.jsonFactory = this.objectMapper.getJsonFactory();
        this.prettyPrinter = new DefaultPrettyPrinter();
    }

    public String toString(SenateResponse value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public void write(SenateResponse response, OutputStream out) throws IOException
    {
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode responseNode = objectMapper.createObjectNode();
        ObjectNode metadataNode = objectMapper.createObjectNode();
        metadataNode.put("totalresults", String.valueOf(response.getMetadataByKey("totalresults")));
        responseNode.put("metadata", metadataNode);

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (Result result : response.getResults()) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("otype", result.getOtype());
            node.put("oid", result.getOid());
            if (result.getOtype().equals("vote")) {
                node.put("url", "http://open.nysenate.gov/legislation/bill/"+((Vote)result.getObject()).getBill().getBillId());
            }
            else if (result.getOtype().equals("action")) {
                node.put("url", "http://open.nysenate.gov/legislation/bill/"+((Action)result.getObject()).getBill().getBillId());
            }
            else {
                node.put("url", "http://open.nysenate.gov/legislation/"+result.getOtype()+"/"+result.getOid());
            }
            ObjectNode data = objectMapper.createObjectNode();
            data.put(result.getOtype(), makeNode(result.getObject()));
            node.put("data", data);
            resultsArray.add(node);
        }
        responseNode.put("results", resultsArray);

        rootNode.put("response", responseNode);
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(rootNode);
        generator.close();
    }

    public JsonNode makeNode(IBaseObject object) throws IOException
    {
        if (object.getOtype().equals("bill")) {
            return makeNode((Bill)object);
        }
        else if (object.getOtype().equals("calendar")) {
            return makeNode((Calendar)object);
        }
        else if (object.getOtype().equals("meeting")) {
            return makeNode((Meeting)object);
        }
        else if (object.getOtype().equals("transcript")) {
            return makeNode((Transcript)object);
        }
        else if (object.getOtype().equals("vote")) {
            return makeNode((Vote)object);
        }
        else if (object.getOtype().equals("action")) {
            return makeNode((Action)object);
        }
        else {
            throw new RuntimeException("Invalid base object otype: "+object.getOtype());
        }
    }

    public ArrayNode makeArrayNode(Collection<? extends Object> list)
    {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        if (list != null) {
            for (Object item : list) {
                if (Bill.class.isInstance(item)) {
                    arrayNode.add(makeShortNode((Bill)item));
                }
                else if (Vote.class.isInstance(item)) {
                    arrayNode.add(makeShortNode((Vote)item));
                }
                else if (Action.class.isInstance(item)) {
                    arrayNode.add(makeShortNode((Action)item));
                }
                else if (Person.class.isInstance(item)) {
                    arrayNode.add(makeNode((Person)item));
                }
                else if (String.class.isInstance(item)) {
                    arrayNode.add((String)item);
                }
                else if (Meeting.class.isInstance(item)) {
                    arrayNode.add(makeNode((Meeting)item));
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

    public JsonNode makeNode(Calendar calendar)
    {
        if (calendar != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", calendar.getOid());
            node.put("no", String.valueOf(calendar.getNo()));
            node.put("sessionYear", String.valueOf(calendar.getSession()));
            node.put("year", String.valueOf(calendar.getYear()));
            node.put("type", String.valueOf(calendar.getType()));
            node.put("supplementals", makeArrayNode(calendar.getSupplementals()));
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
            node.put("calendarDate", (supplemental.getCalendarDate() == null ? null : String.valueOf(supplemental.getCalendarDate().getTime())));
            node.put("releaseDateTime", (supplemental.getReleaseDateTime() == null ? null : String.valueOf(supplemental.getReleaseDateTime().getTime())));
            node.put("sections", makeArrayNode(supplemental.getSections()));
            node.put("sequences", makeArrayNode(supplemental.getSequences()));
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
            node.put("actCalDate", String.valueOf(sequence.getActCalDate().getTime()));
            node.put("releaseDateTime", String.valueOf(sequence.getReleaseDateTime().getTime()));
            node.put("calendarEntries", makeArrayNode(sequence.getCalendarEntries()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(CalendarEntry entry)
    {
        if (entry != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("no", String.valueOf(entry.getNo()));
            node.put("bill", makeShortNode(entry.getBill()));
            node.put("subBill", makeShortNode(entry.getSubBill()));
            node.put("billHigh", entry.getBillHigh());
            node.put("motionDate", (entry.getMotionDate() == null ? null : String.valueOf(entry.getMotionDate().getTime())));
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
            node.put("absent", makeArrayNode(vote.getAbsent()));
            node.put("abstains", makeArrayNode(vote.getAbstains()));
            node.put("ayes", makeArrayNode(vote.getAyes()));
            node.put("ayeswr", makeArrayNode(vote.getAyeswr()));
            node.put("excused", makeArrayNode(vote.getExcused()));
            node.put("nays", makeArrayNode(vote.getNays()));
            node.put("bill", makeShortNode(vote.getBill()));
            node.put("voteDate", String.valueOf(vote.getVoteDate().getTime()));
            node.put("voteType", String.valueOf(vote.getVoteType()));
            node.put("description", vote.getDescription());
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
            node.put("date", String.valueOf(action.getDate().getTime()));
            node.put("id",  action.getOid());
            node.put("text", action.getText());
            node.put("bill", makeShortNode(action.getBill()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Bill bill)
    {
        if (bill != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("actClause", bill.getActClause());
            node.put("actions", makeArrayNode(bill.getActions()));
            node.put("active", String.valueOf(bill.isActive()));
            node.put("amendments", makeArrayNode(bill.getAmendments()));
            node.put("coSponsors", makeArrayNode(bill.getCoSponsors()));
            node.put("currentCommittee", bill.getCurrentCommittee());
            node.put("frozen", "false");
            node.put("fulltext", bill.getFulltext());
            node.put("law", bill.getLaw());
            node.put("lawSection", bill.getLawSection());
            node.put("memo", bill.getMemo());
            node.put("multiSponsors", makeArrayNode(bill.getMultiSponsors()));
            node.put("otherSponsors", makeArrayNode(bill.getOtherSponsors()));
            node.put("previousVersions", makeArrayNode(bill.getPreviousVersions()));
            node.put("sameAs", bill.getSameAs());
            node.put("senateBillNo", bill.getBillId());
            node.put("sponsor", makeNode(bill.getSponsor()));
            node.put("summary", bill.getSummary());
            node.put("title", bill.getTitle());
            node.put("uniBill", String.valueOf(bill.isUniBill()));
            node.put("votes", makeArrayNode(bill.getVotes()));
            node.put("year", bill.getSession());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Meeting meeting)
    {
        if (meeting != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("meetingDateTime", String.valueOf(meeting.getMeetingDateTime().getTime()));
            node.put("meetday", meeting.getMeetday());
            node.put("location", meeting.getLocation());
            node.put("committeeName", meeting.getCommitteeName());
            node.put("committeeChair", meeting.getCommitteeChair());
            node.put("bills", makeArrayNode(meeting.getBills()));
            node.put("notes", meeting.getNotes());
            node.put("addendums", objectMapper.createArrayNode());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeShortNode(Bill bill)
    {
        if (bill != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("actClause", bill.getActClause());
            node.put("active", String.valueOf(bill.isActive()));
            node.put("amendments", makeArrayNode(bill.getAmendments()));
            node.put("frozen", "false");
            node.put("lawSection", bill.getLawSection());
            node.put("otherSponsors", makeArrayNode(bill.getOtherSponsors()));
            node.put("sameAs", bill.getSameAs());
            node.put("senateBillNo", bill.getBillId());
            node.put("sponsor", makeNode(bill.getSponsor()));
            node.put("summary", bill.getSummary());
            node.put("title", bill.getTitle());
            node.put("uniBill", String.valueOf(bill.isUniBill()));
            node.put("year", bill.getYear());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeShortNode(Vote vote)
    {
        if (vote != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("absent", makeArrayNode(vote.getAbsent()));
            node.put("abstains", makeArrayNode(vote.getAbstains()));
            node.put("ayes", makeArrayNode(vote.getAyes()));
            node.put("ayeswr", makeArrayNode(vote.getAyeswr()));
            node.put("excused", makeArrayNode(vote.getExcused()));
            node.put("nays", makeArrayNode(vote.getNays()));
            node.put("voteDate", String.valueOf(vote.getVoteDate().getTime()));
            node.put("voteType", String.valueOf(vote.getVoteType()));
            node.put("description", vote.getDescription());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeShortNode(Action action)
    {
        if (action != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("date", String.valueOf(action.getDate().getTime()));
            node.put("text", action.getText());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Transcript transcript)
    {
        if (transcript != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("location", transcript.getLocation());
            node.put("timeStamp", String.valueOf(transcript.getTimeStamp().getTime()));
            node.put("transcriptText", transcript.getTranscriptText());
            node.put("type", transcript.getType());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    public JsonNode makeNode(Person person) {
        if (person != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("fullname", person.getFullname());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }
}
