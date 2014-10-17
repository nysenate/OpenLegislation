package gov.nysenate.openleg.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OutputUtils serves as a simple utility class to convert Objects to string representations.
 * This is primarily useful during development and debugging.
 */
public abstract class OutputUtils
{
    private static final Logger logger = LoggerFactory.getLogger(OutputUtils.class);

    private static ObjectMapper jsonMapper = new ObjectMapper();
    static {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.registerModule(new GuavaModule());
        jsonMapper.registerModule(new JSR310Module());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    /**
     * Given an object, this method will attempt to serialize it into JSON.
     * @param object Object
     * @return String - Json or empty string if failed.
     */
    public static String toJson(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        }
        catch(JsonGenerationException ex){
            logger.error("Failed to generate json: " + ex.getMessage());
        }
        catch(JsonMappingException ex){
            logger.error("Failed to map json: " + ex.getMessage());
        }
        catch(Exception ex){
            logger.error("ObjectMapper exception: " + ex.getMessage());
        }
        return "";
    }
}
