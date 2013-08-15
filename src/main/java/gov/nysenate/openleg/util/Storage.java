package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/**
 * Simple file backed key-value store with that supports both published and unpublished
 * file modes. Buffers changes in memory to reduce file system access and increase performance.
 *
 * @author GraylinKim
 */
public class Storage
{

    public static void main(String[] args) {
        List<String> test = new ArrayList<String>();
        System.out.println(test.getClass().cast(test));
    }

    protected final Logger logger;

    /**
     * Represents the current status of a key in storage:
     *
     * <ul>
     *  <li>NEW - New and not yet flushed to file.</li>
     *  <li>MODIFIED - Modified since the last flush to file.</li>
     *  <li>UNMODIFIED - Currently unmodified since last flush to file.</li>
     *  <li>DELETED - Deleted since last flush to file (not yet deleted on file).</li>
     *  <li>UNKNOWN - Key is not known to storage, possibly because a previous delete was flushed.</li>
     * </ul>
     *
     * @author GraylinKim
     */
   public static enum Status { NEW, MODIFIED, DELETED, UNMODIFIED, UNKNOWN };

    /**
     * The base directory for this storage on the file system.
     */
    protected final File storageDir;

    /**
     * The directory for published documents on the file system.
     */
    protected final File publishedDir;

    /**
     * The directory for unpublished documents on the file system.
     */
    protected final File unpublishedDir;

    /**
     * Memory buffer for cache values. Used to prevent excessive file operations.
     */
    public HashMap<String, BaseObject> memory;

    /**
     * Tracks the set of currently dirty keys that need to be flushed to the file system
     */
    protected HashSet<String> dirty;


    public String encoding = "UTF-8";
    protected final JsonFactory jsonFactory;
    protected final ObjectMapper objectMapper;
    protected final PrettyPrinter prettyPrinter;


    /**
     * Create a new storage connection to the given file path.
     *
     * @param storagePath - Base file path for the storage on the file system
     */
    public Storage(String storagePath)
    {
        this(new File(storagePath));
    }

    /**
     * Create a new storage connection to the given directory.
     *
     * @param storageDir - Base directory for the storage on the file system
     */
    public Storage(File storageDir)
    {
        this.logger  = Logger.getLogger(this.getClass());

        this.storageDir = storageDir;
        this.publishedDir = new File(storageDir, "published");
        this.unpublishedDir = new File(storageDir, "unpublished");

        this.memory  = new HashMap<String, BaseObject>();
        this.dirty   = new HashSet<String>();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(Feature.INDENT_OUTPUT);
        this.jsonFactory = this.objectMapper.getJsonFactory();
        this.prettyPrinter = new DefaultPrettyPrinter();
    }

    /**
     * Get the current value of a key. First checks storage memory, then
     * falls back to the file system.
     *
     * @param key - The key to fetch the value for.
     * @param cls - The class interpret the value as.
     * @return - The object from storage.
     */
    public BaseObject get(String key, Class<? extends BaseObject> cls)
    {
        BaseObject value = null;
        if (memory.containsKey(key)) {
            logger.debug("Cache hit: "+key);
            value = memory.get(key);
        }
        else {
            logger.debug("Cache miss: "+key);
            File storageFile = getStorageFile(key);
            if (storageFile != null) {
                try {
                    if (cls == Bill.class) {
                        value = read(Bill.class, storageFile);
                    }
                    else {
                        value = objectMapper.readValue(FileUtils.readFileToString(storageFile,encoding), cls);
                    }
                    value.setBrandNew(false);
                } catch (org.codehaus.jackson.JsonParseException e) {
                    logger.error("could not parse json", e);
                } catch (JsonMappingException e) {
                    logger.error("could not map json", e);
                } catch (IOException e) {
                    logger.debug("Storage Miss: "+storageFile);
                }
            }
            else {
                logger.debug("Missing key: "+key);
            }
        }
        return value;
    }

    /**
     * Writes the new value to system memory. To propagate these changes to the file system
     * you must first flush the key (value.getOid()).
     *
     * @param value - The new value to store
     */
    public void set(BaseObject value)
    {
        String key = this.key(value);
        memory.put(key, value);
        dirty.add(key);
    }

    /**
     * @param value - The storage key for this object
     */
    public String key(BaseObject value)
    {
        return value.getYear()+"/"+value.getOtype()+"/"+value.getOid();
    }

    /**
     * Nullifies the key in storage memory. To propagate the deletion to the file system you
     * must flush the key.
     *
     * @param key - The key to delete
     */
    public void del(String key)
    {
        logger.debug("Deleting key: "+key);
        memory.put(key, null);
        dirty.add(key);
    }

    /**
     * Clears out the storage memory. This operation does not affect changes written to
     * the file system. Make sure to flush first, all unwritten changes (including deletions!)
     * will be lost.
     */
    public void clear()
    {
        if (dirty.size() != 0) {
            logger.warn("Clearing storage with "+dirty.size()+" dirty keys.");
            dirty.clear();
        }
        else {
            logger.debug("Clearing storage of "+memory.size()+" keys.");
        }

        memory.clear();

    }

    /**
     * Clear out a specific key from storage memory. This operation does not affect changes
     * written to the file system. make sure to flush first, all unwritten changes (including
     * deletions!) will be lost.
     *
     * @param key - The key of the value to clear.
     */
    public void clear(String key)
    {
        if (dirty.contains(key)) {
            logger.warn("Clearing dirty key: "+key);
            dirty.remove(key);
        }
        memory.remove(key);
    }

    /**
     * Write all values in storage memory to file for long term storage.
     */
    public void flush()
    {
        logger.info("Flushing "+dirty.size()+" objects.");
        for(String key : dirty.toArray(new String[]{})) {
            flush(key);
        }
        dirty.clear();
    }

    /**
     * Write a specific value in storage memory to file for long term storage.
     *
     * @param key - The key of the value to write.
     */
    public void flush(String key)
    {
        logger.info("Flushing key: "+key);

        // Remove existing file record.
        FileUtils.deleteQuietly(getUnpublishedFile(key));
        FileUtils.deleteQuietly(getPublishedFile(key));

        // If the value wasn't deleted, write it to file
        BaseObject value = memory.get(key);
        if (value != null) {
            File storageFile = value.isPublished() ? getPublishedFile(key) : getUnpublishedFile(key);

            try {
                FileUtils.forceMkdir(storageFile.getParentFile());

                if (value instanceof Bill) {
                    write((Bill)value, storageFile);
                }
                else {
                    logger.info("Writing to: "+storageFile);
                    JsonGenerator generator = this.jsonFactory.createJsonGenerator(storageFile, JsonEncoding.UTF8);
                    generator.setPrettyPrinter(this.prettyPrinter);
                    objectMapper.writeValue(generator, value);
                    generator.close();
                }
            }
            catch (IOException e) {
                logger.error("Cannot open file for writing: "+storageFile, e);
            }
        }

        // Mark the key as clean by removing from the dirty set.
        dirty.remove(key);
    }

    /**
     * @param key - The key to get Status for
     * @return - The current Status of a key
     */
    public Status status(String key)
    {
        File storageFile = getStorageFile(key);
        if (dirty.contains(key)) {
            if (memory.get(key) == null) {
                return Status.DELETED;
            }
            else if (storageFile == null) {
                return Status.NEW;
            }
            else {
                return Status.MODIFIED;
            }
        }
        else if (storageFile == null) {
            return Status.UNKNOWN;
        }
        else {
            return Status.UNMODIFIED;
        }
    }

    /**
     * @return - Base directory of the storage on the file system.
     */
    public File getStorageDir()
    {
        return storageDir;
    }

    /**
     * @param key - The key to get a storage file for.
     * @return - The storage file. null if no such file exists.
     */
    protected File getStorageFile(String key)
    {
        File storageFile = getPublishedFile(key);
        if (storageFile.exists()) {
            logger.debug("Published storage file found for key: "+key);
            return storageFile;
        }

        storageFile = getUnpublishedFile(key);
        if (storageFile.exists()) {
           logger.debug("Unpublished storage file found for key: "+key);
           return storageFile;
        }

        return null;
    }

    /**
     * @param key - The key to fetch a file for.
     * @return - File for the published key.
     */
    protected File getPublishedFile(String key)
    {
        return new File(publishedDir, key + ".json");
    }

    /**
     * @param key - The key to fetch a file for.
     * @return - File for the unpublished key.
     */
    protected File getUnpublishedFile(String key)
    {
        return new File(unpublishedDir, key + ".json");
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

    private final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String dateToNode(Date date)
    {
        return date == null ? null : jsonDateFormat.format(date);
    }

    public Date nodeToDate(JsonNode node)
    {
        try {
            return node.isNull() ? null : jsonDateFormat.parse(node.asText());
        }
        catch (ParseException e) {
            logger.error("Invalid json date format: "+node.asText(), e);
            return null;
        }
    }

    public Bill getBill(String billId) {
        String[] parts = billId.split("-");
        return getBill(parts[0], Integer.parseInt(parts[1]));
    }

    public Bill getBill(String printNumber, int session)
    {
        String key = session+"/bill/"+printNumber+"-"+session;
        return (Bill)this.get(key, Bill.class);
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
        node.put("id", action.getOid());
        node.put("active", action.isActive());
        node.put("bill", (action.getBill() != null) ? action.getBill().getBillId() : null);
        node.put("date", action.getDate().getTime());
        node.put("modified", dateToNode(action.getModifiedDate()));
        node.put("published", dateToNode(action.getPublishDate()));
        node.put("dataSources", arrayToArrayNode(action.getDataSources().toArray()));
        node.put("text", action.getText());
        node.put("year", action.getYear());
        return node;
    }

    public Action jsonNodeToAction(JsonNode node) {
        Action action = new Action();
        action.setOid(node.get("id").asText());
        action.setActive(node.get("active").asBoolean());
        action.setDate(new Date(node.get("date").asLong()));
        action.setModifiedDate(nodeToDate(node.get("modified")));
        action.setPublishDate(nodeToDate(node.get("published")));
        action.setDataSources(new HashSet<String>(jsonNodeToListString(node.get("dataSources"))));
        action.setText(node.get("text").asText());
        action.setSession(node.get("year").asInt());
        return action;
    }

    public ObjectNode voteToObjectNode(Vote vote) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", vote.getId());
        node.put("active", vote.isActive());
        node.put("modified", dateToNode(vote.getModifiedDate()));
        node.put("published", dateToNode(vote.getPublishDate()));
        node.put("dataSources", arrayToArrayNode(vote.getDataSources().toArray()));
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
        node.put("bill", (vote.getBill() != null) ? vote.getBill().getBillId() : "");
        node.put("year", vote.getYear());
        return node;
    }

    public Vote jsonNodeToVote(JsonNode node) {
        Vote vote = new Vote();
        vote.setOid(node.get("oid").asText());
        vote.setAbsent(jsonNodeToListString(node.get("absent")));
        vote.setAyes(jsonNodeToListString(node.get("ayes")));
        vote.setAyeswr(jsonNodeToListString(node.get("ayeswr")));
        vote.setNays(jsonNodeToListString(node.get("nays")));
        vote.setExcused(jsonNodeToListString(node.get("excused")));
        vote.setAbstains(jsonNodeToListString(node.get("abstains")));
        vote.setVoteDate(new Date(node.get("date").asLong()));
        vote.setModifiedDate(nodeToDate(node.get("modified")));
        vote.setPublishDate(nodeToDate(node.get("published")));
        vote.setActive(node.get("active").asBoolean());
        logger.debug("READING VOTE DESCRIPTION: "+node.get("description").asText());
        vote.setDescription(node.get("description").asText());
        vote.setVoteType(node.get("voteType").asInt());
        vote.setSession(node.get("year").asInt());
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

    public Bill read(Class<Bill> cls, File file) throws JsonProcessingException, IOException
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
        bill.setModifiedDate(nodeToDate(node.get("modified")));
        bill.setPublishDate(nodeToDate(node.get("published")));
        bill.setPastCommittees(jsonNodeToListString(node.get("pastCommittees")));
        bill.setPreviousVersions(jsonNodeToListString(node.get("previousVersions")));
        bill.setSameAs(node.get("sameAs").asText());
        bill.setDataSources(new HashSet<String>(jsonNodeToListString(node.get("dataSources"))));
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

    public void write(Bill bill, File storageFile) throws IOException
    {
        logger.debug("Writing Bill: "+bill.getBillId());
        ObjectNode node = mapper.createObjectNode();
        node.put("actClause", bill.getActClause());
        node.put("active", bill.isActive());
        node.put("currentCommittee", bill.getCurrentCommittee());
        node.put("fulltext", bill.getFulltext());
        node.put("law", bill.getLaw());
        node.put("lawSection", bill.getLawSection());
        node.put("memo", bill.getMemo());
        node.put("modified", dateToNode(bill.getModifiedDate()));
        node.put("published", dateToNode(bill.getPublishDate()));
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
        node.put("senateBillNo", bill.getBillId());
        node.put("summary", bill.getSummary());
        node.put("title", bill.getTitle());
        node.put("year", bill.getSession());
        node.put("uniBill", bill.isUniBill());
        node.put("dataSources", arrayToArrayNode(bill.getDataSources().toArray()));
        node.put("amendments", listToArrayNode(bill.getAmendments()));

        ArrayNode votes = mapper.createArrayNode();
        for (Vote vote : bill.getVotes()) {
            votes.add(voteToObjectNode(vote));
        }
        node.put("votes", votes);

        JsonGenerator generator = this.jsonFactory.createJsonGenerator(storageFile, JsonEncoding.UTF8);
        generator.writeTree(node);
        generator.close();
    }
}
