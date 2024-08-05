package gov.nysenate.openleg.common.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OutputUtils serves as a simple utility class to convert Objects to string representations.
 * This is primarily useful during development and debugging.
 */
public final class OutputUtils {
    private static final Logger logger = LoggerFactory.getLogger(OutputUtils.class);
    public static final ObjectMapper basicJsonMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new GuavaModule()).registerModule(new JavaTimeModule());
    public static final ObjectMapper elasticsearchJsonMapper = basicJsonMapper.copy()
            .disable(SerializationFeature.INDENT_OUTPUT);
    public static final ObjectMapper failOnUnknownMapper = basicJsonMapper.copy()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private OutputUtils() {}

    /**
     * Given an object, this method will attempt to serialize it into JSON.
     * @param object Object
     * @return String - Json or empty string if failed.
     */
    public static String toJson(Object object) {
        try {
            return basicJsonMapper.writeValueAsString(object);
        }
        catch(JsonGenerationException ex) {
            logger.error("Failed to generate json: {}", ex.getMessage());
        }
        catch(JsonMappingException ex) {
            logger.error("Failed to map json: {}", ex.getMessage());
        }
        catch(Exception ex) {
            logger.error("ObjectMapper exception: {}", ex.getMessage());
        }
        return "";
    }
}
