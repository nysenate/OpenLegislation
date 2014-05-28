package gov.nysenate.openleg.util;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * OutputHelper serves as a simple utility class to convert Objects to string representations.
 * This is primarily useful during development and debugging.
 */
public class OutputHelper
{
    private static final Logger logger = Logger.getLogger(OutputHelper.class);

    private static ObjectMapper jsonMapper = new ObjectMapper();
    static {
        jsonMapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
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
