package gov.nysenate.openleg.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

public class Storage {

    private final String storage;
    private final Logger logger;
    private final JsonFactory jsonFactory;
    private final ObjectMapper objectMapper;
    private final PrettyPrinter prettyPrinter;

    public HashMap<String, Status> changeLog;
    public HashMap<String, Object> memory;
    public HashSet<String> dirty;
    public String encoding = "UTF-8";
    public Boolean autoFlush;

    public static enum Status { NEW , MODIFIED, DELETED };

    public Storage(String filepath) {
        this(filepath, true);
    }

    public Storage(String filepath, Boolean autoFlush) {
        this.storage = filepath;
        this.logger  = Logger.getLogger(this.getClass());
        this.memory  = new HashMap<String, Object>();
        this.dirty   = new HashSet<String>();
        this.autoFlush = autoFlush;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.getSerializationConfig().enable(Feature.INDENT_OUTPUT);
        this.jsonFactory = this.objectMapper.getJsonFactory();
        this.prettyPrinter = new DefaultPrettyPrinter();

        // Load the changeLog from last time...
        this.changeLog = loadLog();
    }

    public void clearCache() {
        this.memory.clear();
    }

    public Object get(String key, Class<?> cls) {
        return get(key, cls, true);
    }

    public Object get(String key, Class<?> cls, Boolean useCache) {
        if (useCache && memory.containsKey(key))
            return memory.get(key);
        else {
            //Attempt load from storage
            File file = storageFile(key);
            try {
                return objectMapper.readValue(FileUtils.readFileToString(file,encoding), cls);
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

        // Log the change
        Status keyStatus = changeLog.get(key);
        if (keyStatus == null) {
            if (storageFile(key).exists()) {
                changeLog.put(key, Status.MODIFIED);
            } else {
                changeLog.put(key, Status.NEW);
            }
        } else if (keyStatus != Status.NEW) {
            changeLog.put(key, Status.MODIFIED);
        }
    }

    public Boolean del(String key) {
        // Deletions are always automatically flushed
        logger.debug("Deleting key: "+key);
        memory.remove(key);
        dirty.remove(key);

        // Log the change
        Status keyStatus = changeLog.get(key);
        if (keyStatus == Status.NEW) {
            changeLog.remove(key);
        } else {
            changeLog.put(key, Status.DELETED);
        }
        return storageFile(key).delete();
    }

    public void flush() {
        logger.info("Flushing "+dirty.size()+" objects.");
        for(String key : dirty) {
            //Serialize the object.
            //Write object to file.
            if (!memory.containsKey(key)) {
                logger.error("Dirty entry "+key+" not found in memory.");
                continue;
            }

            File file = storageFile(key);

            try {
                FileUtils.forceMkdir(file.getParentFile());
                JsonGenerator generator = this.jsonFactory.createJsonGenerator(file, JsonEncoding.UTF8);
                generator.setPrettyPrinter(this.prettyPrinter);
                objectMapper.writeValue(generator, memory.get(key));
                generator.close();
            } catch (IOException e) {
                logger.error("Cannot open file for writing: "+file, e);
            }
        }
        dirty.clear();
    }

    public HashMap<String, Status> loadLog() {
        changeLog = new HashMap<String, Status>();

        try {
            for(String line : FileUtils.readLines(storageFile("changeLog"))) {
                String[] parts = line.split("\t");
                changeLog.put(parts[0], Status.valueOf(parts[1]));
            }
        } catch (IOException e) {
            logger.info("No change log found. Creating new log.");
        }

        return changeLog;
    }

    public void saveLog() {
        try {
            File logFile = storageFile("changeLog");
            FileUtils.forceMkdir(logFile.getParentFile());

            StringBuffer out = new StringBuffer();
            for (Entry<String, Status> entry : changeLog.entrySet()) {
                out.append(entry.getKey()+"\t"+entry.getValue()+"\n");
            }

            FileUtils.write(logFile, out);
        } catch (IOException e) {
            logger.error("Could not open changeLog for writing", e);
        }
    }

    public void clearLog() {
        storageFile("changeLog").delete();
        changeLog.clear();
    }

    private File storageFile(String key) {
        return new File(storage, key+".json");
    }
}
