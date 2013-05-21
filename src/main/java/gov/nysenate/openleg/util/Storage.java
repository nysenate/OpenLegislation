package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

public class Storage {

    private final File storage;
    private final Logger logger;
    private final JsonFactory jsonFactory;
    private final ObjectMapper objectMapper;
    private final PrettyPrinter prettyPrinter;

    public HashMap<String, Object> memory;
    public HashSet<String> dirty;
    public String encoding = "UTF-8";
    public Boolean autoFlush;

    public static enum Status { NEW , MODIFIED, DELETED };

    public Storage(String storagePath) {
        this(storagePath, true);
    }

    public Storage(File storageDir) {
        this(storageDir, true);
    }

    public Storage(String storagePath, Boolean autoFlush) {
        this(new File(storagePath),autoFlush);
    }

    public Storage(File storageDir, Boolean autoFlush) {
        this.storage = storageDir;
        this.logger  = Logger.getLogger(this.getClass());
        this.memory  = new HashMap<String, Object>();
        this.dirty   = new HashSet<String>();
        this.autoFlush = autoFlush;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(Feature.INDENT_OUTPUT);
        this.jsonFactory = this.objectMapper.getJsonFactory();
        this.prettyPrinter = new DefaultPrettyPrinter();
    }

    public void clearCache() {
        this.memory.clear();
    }

    public Object get(String key, Class<?> cls) {
        return get(key, cls, true);
    }

    public Object restore(String key, Class<?> cls) {
        try {
            File file = new File(new File(storage, "unpublished"), key+".json");
            if (file.exists()) {
                Object value;
                logger.info("Unpublishing: "+file.getPath());
                if (cls == Bill.class) {
                    value = this.readBill(file);
                }
                else {
                    value = objectMapper.readValue(FileUtils.readFileToString(file,encoding), cls);
                }
                if (!file.delete()) {
                    logger.error("Unable to delete unpublished file: "+key);
                }
                return value;
            }
            else {
                logger.info("Can't restore non-existing file: "+file.getPath());
            }
        } catch (IOException e) {
            logger.error("Unexpected restoration error",e);
        }
        return null;
    }

    public Object get(String key, Class<?> cls, Boolean useCache) {
        if (useCache && memory.containsKey(key))
            return memory.get(key);
        else {
            //Attempt load from storage
            logger.debug("Loading: "+key);
            File file = storageFile(key);

            try {
                if (cls == Bill.class) {
                    return readBill(file);
                } else {
                    return objectMapper.readValue(FileUtils.readFileToString(file,encoding), cls);
                }
            } catch (org.codehaus.jackson.JsonParseException e) {
                logger.error("could not parse json", e);
            } catch (JsonMappingException e) {
                logger.error("could not map json", e);
            } catch (IOException e) {
                logger.debug("Storage Miss: "+file);
            }
        }

        return null;
    }

    public void set(String key, Object value) {
        memory.put(key, value);
        dirty.add(key);
    }

    public Boolean del(String key) throws IOException {
        // Deletions are always automatically flushed
        logger.debug("Deleting key: "+key);

        // Instead, move to storage/unpublished
        if (flushKey(key)) {
            FileUtils.moveFileToDirectory(storageFile(key), new File(storage, "unpublished"), true);
        }
        memory.remove(key);
        dirty.remove(key);
        return true;
    }

    public boolean flushKey(String key)
    {
        //Serialize the object.
        //Write object to file.
        if (!memory.containsKey(key)) {
            logger.error("Dirty entry "+key+" not found in memory.");
            return false;
        }

        File file = storageFile(key);

        try {
            FileUtils.forceMkdir(file.getParentFile());
            Object value = memory.get(key);
            if (value instanceof Bill) {
                writeBill((Bill)value);
            } else {
                JsonGenerator generator = this.jsonFactory.createJsonGenerator(file, JsonEncoding.UTF8);
                generator.setPrettyPrinter(this.prettyPrinter);
                objectMapper.writeValue(generator, value);
                generator.close();
            }
            return true;
        } catch (IOException e) {
            logger.error("Cannot open file for writing: "+file, e);
            return false;
        }
    }

    public void flush() {
        logger.info("Flushing "+dirty.size()+" objects.");
        for(String key : dirty) {
            flushKey(key);
        }
        dirty.clear();
    }

    public File storageFile(String key) {
        return new File(storage, key+".json");
    }

    /*
     * A - Assembly Bill
     * S - Senate Bill
     * L - Joint Resolution
     * E - Assembly Resolution
     * R - Senate Resolution
     * J - "Legislative Resolution" - how is that different than a joint resolution? "Honoring..."?
     * K - "Legislative Resolution" - proclamations of special days, weeks?
     */
    Pattern keyPattern = Pattern.compile("([ASLREJK][0-9]{1,5}[A-Z]?)-([0-9]{4})");

    public Bill getBill(String billNo)
    {
        String key = Bill.getKey(billNo);
        if (key != null) {
            return (Bill)this.get(key, Bill.class);
        }
        else {
            logger.error("Invalid bill key: "+key, new IllegalArgumentException(key));
            return null;
        }
    }

    public void saveBill(Bill bill)
    {
        String key = bill.getKey();
        if (key != null) {
            this.set(key, bill);
        }
        else {
            logger.error("Invalid bill key: "+bill.getSenateBillNo());
        }
    }

    public ObjectMapper mapper = new ObjectMapper();

    public ArrayNode listToArrayNode(List<? extends Object> list) {
        return arrayToArrayNode(list.toArray());
    }

    public ArrayNode arrayToArrayNode(Object[] list) {
        ArrayNode arrayNode = mapper.createArrayNode();
        for (Object item : list) {
            arrayNode.add(item.toString());
        }
        return arrayNode;
    }

    public ObjectNode personToObjectNode(Person person) {
        if (person == null) {
            return null;
        }

        ObjectNode node = mapper.createObjectNode();
        node.put("position", person.getPosition());
        node.put("fullname", person.getFullname());
        node.put("id", person.getId());
        node.put("branch", person.getBranch());
        node.put("contactInfo", person.getContactInfo());
        node.put("guid", person.getGuid());
        return node;
    }

    public Person jsonNodeToPerson(JsonNode node) {
        if (node.isNull()) {
            return null;
        }
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

    public ObjectNode actionToObjectNode(Action action) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", action.getId());
        node.put("active", action.getActive());
        node.put("bill", (action.getBill() != null) ? action.getBill().getSenateBillNo() : "");
        node.put("date", action.getDate().getTime());
        node.put("modified", action.getModified());
        node.put("sobiReferenceList", arrayToArrayNode(action.getSobiReferenceList().toArray()));
        node.put("text", action.getText());
        node.put("year", action.getYear());
        return node;
    }

    public Action jsonNodeToAction(JsonNode node) {
        Action action = new Action();
        action.setId(node.get("id").asText());
        action.setActive(node.get("active").asBoolean());
        action.setDate(new Date(node.get("date").asLong()));
        action.setModified(node.get("modified").asLong());
        action.setSobiReferenceList(new HashSet<String>(jsonNodeToListString(node.get("sobiReferenceList"))));
        action.setText(node.get("text").asText());
        action.setYear(node.get("year").asInt());
        return action;
    }

    public ObjectNode voteToObjectNode(Vote vote) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", vote.getId());
        node.put("active", vote.getActive());
        node.put("modified", vote.getModified());
        node.put("sobiReferenceList", arrayToArrayNode(vote.getSobiReferenceList().toArray()));
        node.put("date", vote.getVoteDate().getTime());
        node.put("voteType", vote.getVoteType());
        logger.debug("READING VOTE DESCRIPTION: "+vote.getDescription());
        node.put("description", vote.getDescription());
        node.put("ayes", listToArrayNode(vote.getAyes()));
        node.put("ayeswr", listToArrayNode(vote.getAyeswr()));
        node.put("nays", listToArrayNode(vote.getNays()));
        node.put("abstains", listToArrayNode(vote.getAbstains()));
        node.put("absent", listToArrayNode(vote.getAbsent()));
        node.put("excused", listToArrayNode(vote.getExcused()));
        node.put("bill", (vote.getBill() != null) ? vote.getBill().getSenateBillNo() : "");
        node.put("year", vote.getYear());
        return node;
    }

    public Vote jsonNodeToVote(JsonNode node) {
        Vote vote = new Vote();
        vote.setId(node.get("id").asText());
        vote.setAbsent(jsonNodeToListString(node.get("absent")));
        vote.setAyes(jsonNodeToListString(node.get("ayes")));
        vote.setAyeswr(jsonNodeToListString(node.get("ayeswr")));
        vote.setNays(jsonNodeToListString(node.get("nays")));
        vote.setExcused(jsonNodeToListString(node.get("excused")));
        vote.setAbstains(jsonNodeToListString(node.get("abstains")));
        vote.setVoteDate(new Date(node.get("date").asLong()));
        vote.setModified(node.get("modified").asLong());
        vote.setActive(node.get("active").asBoolean());
        logger.debug("READING VOTE DESCRIPTION: "+node.get("description").asText());
        vote.setDescription(node.get("description").asText());
        vote.setVoteType(node.get("voteType").asInt());
        vote.setYear(node.get("year").asInt());
        return vote;
    }

    public List<String> jsonNodeToListString(JsonNode array) {
        List<String> list = new ArrayList<String>();
        Iterator<JsonNode> iter = array.getElements();
        while(iter.hasNext()) {
            list.add(iter.next().asText());
        }
        return list;
    }

    public Bill readBill(File file) throws JsonProcessingException, IOException
    {
        Iterator<JsonNode> iter;
        String contents = FileUtils.readFileToString(file,encoding);
        JsonNode node = mapper.readTree(contents);
        Bill bill = new Bill(node.get("senateBillNo").asText(),node.get("year").asInt());
        bill.setActClause(node.get("actClause").asText());
        bill.setActive(node.get("active").asBoolean());
        bill.setAmendments(jsonNodeToListString(node.get("amendments")));
        bill.setCurrentCommittee(node.get("currentCommittee").asText());
        bill.setFulltext(node.get("fulltext").asText());
        bill.setLaw(node.get("law").asText());
        bill.setLawSection(node.get("lawSection").asText());
        bill.setMemo(node.get("memo").asText());
        bill.setModified(node.get("modified").asLong());
        bill.setPastCommittees(jsonNodeToListString(node.get("pastCommittees")));
        bill.setPreviousVersions(jsonNodeToListString(node.get("previousVersions")));
        bill.setSameAs(node.get("sameAs").asText());
        bill.setSobiReferenceList(new HashSet<String>(jsonNodeToListString(node.get("sobiReferenceList"))));
        bill.setSponsor(jsonNodeToPerson(node.get("sponsor")));
        bill.setStricken(node.get("stricken").asBoolean());
        bill.setSummary(node.get("summary").asText());
        bill.setUniBill(node.get("uniBill").asBoolean());
        bill.setTitle(node.get("title").asText());

        List<Action> actions = new ArrayList<Action>();
        iter = node.get("actions").getElements();
        while(iter.hasNext()) {
            Action action = jsonNodeToAction(iter.next());
            // action.setBill(bill);
            actions.add(action);
        }
        bill.setActions(actions);

        List<Vote> votes = new ArrayList<Vote>();
        iter = node.get("votes").getElements();
        while(iter.hasNext()) {
            Vote vote = jsonNodeToVote(iter.next());
            // vote.setBill(bill);
            votes.add(vote);
        }
        bill.setVotes(votes);

        if (node.get("otherSponsors") != null && !node.get("otherSponsors").isNull()) {
            List<Person> otherSponsors = new ArrayList<Person>();
            iter = node.get("otherSponsors").getElements();
            while(iter.hasNext()) {
                otherSponsors.add(jsonNodeToPerson(iter.next()));
            }
            bill.setOtherSponsors(otherSponsors);
        }

        List<Person> cosponsors = new ArrayList<Person>();
        iter = node.get("coSponsors").getElements();
        while(iter.hasNext()) {
            cosponsors.add(jsonNodeToPerson(iter.next()));
        }
        bill.setCoSponsors(cosponsors);

        List<Person> multiSponsors = new ArrayList<Person>();
        iter = node.get("multiSponsors").getElements();
        while(iter.hasNext()) {
            multiSponsors.add(jsonNodeToPerson(iter.next()));
        }
        bill.setMultiSponsors(multiSponsors);

        return bill;
    }

    public void writeBill(Bill bill) throws IOException
    {
        logger.debug("Writing Bill: "+bill.getSenateBillNo());
        ObjectNode node = mapper.createObjectNode();
        node.put("actClause", bill.getActClause());
        node.put("active", bill.isActive());
        node.put("currentCommittee", bill.getCurrentCommittee());
        node.put("fulltext", bill.getFulltext());
        node.put("law", bill.getLaw());
        node.put("lawSection", bill.getLawSection());
        node.put("memo", bill.getMemo());
        node.put("modified", bill.getModified());
        node.put("sameAs", bill.getSameAs());

        ArrayNode otherSponsors = mapper.createArrayNode();
        for (Person otherSponsor : bill.getOtherSponsors()) {
            otherSponsors.add(personToObjectNode(otherSponsor));
        }
        node.put("otherSponsors", otherSponsors);

        ArrayNode multisponsors = mapper.createArrayNode();
        for (Person multisponsor : bill.getMultiSponsors()) {
            multisponsors.add(personToObjectNode(multisponsor));
        }
        node.put("multiSponsors", multisponsors);

        ArrayNode cosponsors = mapper.createArrayNode();
        for (Person cosponsor : bill.getCoSponsors()) {
            cosponsors.add(personToObjectNode(cosponsor));
        }
        node.put("coSponsors", cosponsors);

        ArrayNode actions = mapper.createArrayNode();
        for (Action action : bill.getActions()) {
            actions.add(actionToObjectNode(action));
        }
        node.put("actions", actions);

        node.put("sponsor", personToObjectNode(bill.getSponsor()));
        node.put("stricken", bill.isStricken());
        node.put("pastCommittees", listToArrayNode(bill.getPastCommittees()));
        node.put("previousVersions", listToArrayNode(bill.getPreviousVersions()));
        node.put("senateBillNo", bill.getSenateBillNo());
        node.put("summary", bill.getSummary());
        node.put("title", bill.getTitle());
        node.put("year", bill.getYear());
        node.put("uniBill", bill.isUniBill());
        node.put("sobiReferenceList", arrayToArrayNode(bill.getSobiReferenceList().toArray()));
        node.put("amendments", listToArrayNode(bill.getAmendments()));

        ArrayNode votes = mapper.createArrayNode();
        for (Vote vote : bill.getVotes()) {
            votes.add(voteToObjectNode(vote));
        }
        node.put("votes", votes);

        File file = new File(storage, bill.getYear()+"/bill/"+bill.getSenateBillNo()+".json");
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(file, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
    }
}
