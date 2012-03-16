package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.model.ISenateObject;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.EasyWriter;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

/**
 * JsonDao is the point of contact for writing, reading and deleting
 * any JSON file associated with a SenateObject.
 * 
 * Any time an object is written it can be assumed that the index
 * must be updated as well.  An EasyWriter is created to log
 * the absolute path of these changes.
 * 
 * TODO - a similar hook should be added for deleting... can consolidate
 * 			the reading/writing in CalendarParser and AgendaParser
 *
 */
public class JsonDao {
    private final Logger logger = Logger.getLogger(JsonDao.class);

    ObjectMapper mapper = null;

    String jsonDirectory;
    String logPath;

    public JsonDao(String jsonDirectory, String logPath) {
        this(jsonDirectory, logPath, Ingest.getMapper());
    }

    public JsonDao(String jsonDirectory, String logPath, ObjectMapper mapper) {
        this.jsonDirectory = jsonDirectory;
        this.logPath = logPath;
        this.mapper = mapper;
    }

    public void log(String data) {
        new EasyWriter(new File(logPath)).open().writeLine(data).close();
    }

    public void write(SenateObject obj) {
        if(!(obj.getYear()+"").matches("20(09|1[0-9])")) {
            return;
        }

        File yearDir = new File(TextFormatter.append(jsonDirectory,"/",obj.getYear()));
        File typeDir = new File(TextFormatter.append(jsonDirectory,"/",obj.getYear(),"/",obj.luceneOtype()));
        File newFile = new File(TextFormatter.append(jsonDirectory,"/",obj.getYear(),"/",obj.luceneOtype()
                ,"/",obj.fileSystemId() != null ? obj.fileSystemId() : obj.luceneOid(),".json"));

        if(!yearDir.exists()) {
            logger.info("creating directory: " + yearDir.getAbsolutePath());
            yearDir.mkdir();
        }
        if(!typeDir.exists()) {
            logger.info("creating directory: " + typeDir.getAbsolutePath());
            typeDir.mkdir();
        }

        logger.debug("Writing json to path: " + newFile.getAbsolutePath());
        try {
            BufferedOutputStream osw = new BufferedOutputStream(new FileOutputStream(newFile));
            JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(osw,JsonEncoding.UTF8);
            generator.setPrettyPrinter(new DefaultPrettyPrinter());
            mapper.writeValue(generator, obj);
            osw.close();

            log(newFile.getAbsolutePath());
        } catch (JsonGenerationException e) {
            logger.error("could not parse json", e);
        } catch (JsonMappingException e) {
            logger.error("could not parse json", e);
        } catch (IOException e) {
            logger.error("error reading file", e);
        }
    }

    public <T extends SenateObject> T load(String id, String year, String type, Class<T> clazz) {
        return load(TextFormatter.append(jsonDirectory,"/",year,"/",type,"/",id,".json"), clazz);
    }

    /**
     * @param path to json document
     * @param clazz class of object to be loaded
     * @return deserialized SenateObject of type clazz
     */
    public <T extends SenateObject> T load(String path, Class<T> clazz) {
        try {
            logger.info("Loading object at: " + path);
            File file = new File(path);
            if(!file.exists())
                return null;
            return mapper.readValue(file, clazz);
        } catch (org.codehaus.jackson.JsonParseException e) {
            logger.warn("could not parse json", e);
        } catch (JsonMappingException e) {
            logger.warn("could not map json", e);
        } catch (IOException e) {
            logger.warn("error with file", e);
        }

        return null;
    }


    public boolean delete(SenateObject so) {
        return delete(so.fileSystemId() != null ? so.fileSystemId() : so.luceneOid(), so.getYear() +"", so.luceneOtype());
    }

    public boolean delete(String id, String year, String type) {
        return delete(TextFormatter.append(jsonDirectory,"/",year,"/",type,"/",id,".json"));
    }

    public boolean delete(String path) {
        logger.info("Deleting file at: " + path);

        File file = new File(path);
        return file.delete();
    }

    @SuppressWarnings("unchecked")
    public <T extends SenateObject> T mergeSenateObject(T obj, Class<? extends ISenateObject> clazz) {
        File file = new File(TextFormatter.append(
                jsonDirectory,
                "/",obj.getYear(),
                "/",obj.luceneOtype(),
                "/",obj.fileSystemId() != null ? obj.fileSystemId() : obj.luceneOid(),".json"));

        if(file.exists()) {
            logger.info("Merging object with id: " + obj.luceneOid());
            try {
                T oldObject = (T) mapper.readValue(file, clazz);
                oldObject.setActive(obj.getActive());
                oldObject.merge(obj);
                obj = oldObject;


            } catch (JsonGenerationException e) {
                logger.warn("could not parse json", e);
            } catch (JsonMappingException e) {
                logger.warn("could not parse json", e);
            } catch (IOException e) {
                logger.warn("error reading file", e);
            }
        }

        return obj;
    }
}
