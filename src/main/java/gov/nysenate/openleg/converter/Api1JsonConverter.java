package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
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
import java.util.Date;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class Api1JsonConverter
{
    protected final String encoding = "UTF-8";
    protected final JsonFactory jsonFactory;
    protected final ObjectMapper objectMapper;

    public Api1JsonConverter()
    {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(Feature.INDENT_OUTPUT);
        this.jsonFactory = this.objectMapper.getJsonFactory();
    }

    public String toString(SenateResponse value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(BaseObject value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public void write(BaseObject object, OutputStream out) throws IOException
    {
        if (object.getOtype().equals("bill")) {
            write((Bill)object, out);
        }
        else if (object.getOtype().equals("calendar")) {
            write((Calendar)object, out);
        }
        else if (object.getOtype().equals("meeting")) {
            write((Meeting)object, out);
        }
        else if (object.getOtype().equals("transcript")) {
            write((Transcript)object, out);
        }
        else {
            throw new RuntimeException("Invalid base object otype: "+object.getOtype());
        }
    }

    public void write(SenateResponse response, OutputStream out) throws IOException
    {
        ArrayNode array = objectMapper.createArrayNode();
        for (Result result : response.getResults()) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("otype", result.getOtype());
            node.put("id", result.getOid());
            node.put("title", result.getTitle());
            if (result.getOtype().equals("action")) {
                node.put("billno", result.getFields().get("billno"));
            }
            else if (result.getOtype().equals("vote")) {
                node.put("billno", result.getFields().get("billno"));
                node.put("sponsor", result.getFields().get("sponsor"));
            }
            else if (result.getOtype().equals("bill")) {
                node.put("summary", result.getSummary());
                node.put("billno", result.getFields().get("billno"));
                node.put("year", result.getFields().get("year"));
                node.put("sponsor", result.getFields().get("sponsor"));
                node.put("committee", result.getFields().get("committee"));
            }
            else if (result.getOtype().equals("meeting")) {
                node.put("committee", result.getFields().get("committee"));
                node.put("location", result.getFields().get("location"));
                node.put("chair", result.getFields().get("chair"));
            }
            else if (result.getOtype().equals("transcript")) {
                node.put("location", result.getFields().get("location"));
            }
            else if (result.getOtype().equals("calendar")) {
                // No special fields.
            }
            array.add(node);
        }
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(array);
        generator.close();
    }

    protected void write(Calendar calendar, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("year", calendar.getYear());
        node.put("type", calendar.getType());
        node.put("sessionYear", calendar.getSession());
        node.put("no", calendar.getNo());
        node.put("id", calendar.getOid());
        node.put("supplementals", makeArrayNode(calendar.getSupplementals()));

        ObjectNode wrapper = objectMapper.createObjectNode();
        wrapper.put("meeting", node);
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(wrapper);
        generator.close();
    }

    protected void write(Meeting meeting, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("meetingDateTime", meeting.getMeetingDateTime().getTime()+"");
        node.put("meetday", meeting.getMeetday());
        node.put("location", meeting.getLocation());
        node.put("committeeName", meeting.getCommitteeName());
        node.put("committeeChair", meeting.getCommitteeChair());
        node.put("notes", meeting.getNotes());
        node.put("addendums", objectMapper.createArrayNode());
        node.put("bills", makeArrayNode(meeting.getBills()));

        ObjectNode wrapper = objectMapper.createObjectNode();
        wrapper.put("meeting", node);
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(wrapper);
        generator.close();
    }

    protected void write(Transcript transcript, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", transcript.getId());
        node.put("timestamp", transcript.getTimeStamp().toString());
        node.put("location", transcript.getLocation());
        node.put("session", transcript.getType());
        node.put("text", transcript.getTranscriptText());

        ArrayNode wrapper = objectMapper.createArrayNode();
        wrapper.add(node);
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(wrapper);
        generator.close();
    }

    protected void write(Bill bill, OutputStream out) throws IOException
    {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("year", bill.getSession());
        node.put("senateId", bill.getBillId());
        node.put("sponsor", bill.getSponsor().getFullname());
        node.put("title", bill.getTitle());
        node.put("summary", bill.getSummary());
        node.put("actions", makeArrayNode(bill.getActions()));
        node.put("votes", makeArrayNode(bill.getVotes()));

        ArrayNode cosponsors = objectMapper.createArrayNode();
        for (Person person : bill.getCoSponsors()) {
            ObjectNode cosponsor = objectMapper.createObjectNode();
            cosponsor.put("cosponsor", person.getFullname());
        }
        node.put("cosponsors", cosponsors);

        ArrayNode wrapper = objectMapper.createArrayNode();
        wrapper.add(node);
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        generator.writeTree(wrapper);
        generator.close();
    }

    protected JsonNode makeNode(Supplemental supplemental)
    {
        if (supplemental != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", supplemental.getId());
            node.put("calendarDate", makeNode(supplemental.getCalendarDate()));
            node.put("releaseDateTime", makeNode(supplemental.getReleaseDateTime()));
            node.put("supplementalId", supplemental.getSupplementalId());
            node.put("sections", makeArrayNode(supplemental.getSections()));
            node.put("sequences", makeArrayNode(supplemental.getSequences()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    protected JsonNode makeNode(Section section)
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

    protected JsonNode makeNode(Sequence sequence)
    {
        if (sequence != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("no", sequence.getNo());
            node.put("actCalDate", makeNode(sequence.getActCalDate()));
            node.put("relaseDateTime", makeNode(sequence.getReleaseDateTime()));
            node.put("notes", sequence.getNotes());
            node.put("calendarEntries", makeArrayNode(sequence.getCalendarEntries()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    protected JsonNode makeNode(CalendarEntry entry)
    {
        if (entry != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("no", entry.getNo());
            node.put("billHigh", entry.getBillHigh());
            node.put("motionDate", makeNode(entry.getMotionDate()));
            node.put("bill", makeNode(entry.getBill()));
            node.put("subBill", makeNode(entry.getSubBill()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    protected JsonNode makeNode(Bill bill)
    {
        if (bill != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("active", String.valueOf(bill.isActive()));
            node.put("year", bill.getYear());
            node.put("senateBillNo", bill.getBillId());
            node.put("title", bill.getTitle());
            node.put("lawSection", bill.getLawSection());
            node.put("sameAs", bill.getSameAs());
            node.put("frozen", "false");
            node.put("addmendments", makeArrayNode(bill.getAmendments()));
            node.put("summary", bill.getSummary());
            node.put("actClause", bill.getActClause());
            node.put("uniBill", String.valueOf(bill.isUniBill()));
            node.put("sponsor", makeNode(bill.getSponsor()));
            node.put("otherSponsors", makeArrayNode(bill.getOtherSponsors()));
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    protected String makeNode(Date date)
    {
        return (date != null) ? String.valueOf(date.getTime()) : null;
    }

    protected JsonNode makeNode(Person person)
    {
        if (person != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("fullname", person.getFullname());
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    protected JsonNode makeNode(Vote vote)
    {
        if (vote != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("timestamp", vote.getVoteDate().getTime()+"");
            node.put("ayes", vote.getAyes().size()+"");
            node.put("nays", vote.getNays().size()+"");
            node.put("abstains", vote.getAbstains().size()+"");
            node.put("excused", vote.getExcused().size()+"");

            ArrayNode voters = objectMapper.createArrayNode();
            for (String name : vote.getAyes()) {
                ObjectNode voter = objectMapper.createObjectNode();
                voter.put("name", name);
                voter.put("vote", "aye");
            }
            for (String name : vote.getNays()) {
                ObjectNode voter = objectMapper.createObjectNode();
                voter.put("name", name);
                voter.put("vote", "nay");
            }
            for (String name : vote.getAbstains()) {
                ObjectNode voter = objectMapper.createObjectNode();
                voter.put("name", name);
                voter.put("vote", "abstain");
            }
            for (String name : vote.getExcused()) {
                ObjectNode voter = objectMapper.createObjectNode();
                voter.put("name", name);
                voter.put("vote", "excused");
            }
            node.put("voters", voters);

            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    protected JsonNode makeNode(Action action)
    {
        if (action != null) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("action", action.getText());
            node.put("timestamp", action.getDate().getTime()+"");
            return node;
        }
        else {
            return objectMapper.createObjectNode().nullNode();
        }
    }

    protected ArrayNode makeArrayNode(Collection<? extends Object> list)
    {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        if (list != null) {
            for (Object item : list) {
                if (Vote.class.isInstance(item)) {
                    arrayNode.add(makeNode((Vote)item));
                }
                else if (String.class.isInstance(item)) {
                    arrayNode.add((String)item);
                }
                else if (Action.class.isInstance(item)) {
                    arrayNode.add(makeNode((Action)item));
                }
                else if (Person.class.isInstance(item)) {
                    arrayNode.add(makeNode((Person)item));
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
                else if (Bill.class.isInstance(item)) {
                    arrayNode.add(makeNode((Bill)item));
                }
                else {
                    throw new RuntimeException("Invalid array node type: "+item.getClass());
                }
            }
        }
        return arrayNode;
    }
}
