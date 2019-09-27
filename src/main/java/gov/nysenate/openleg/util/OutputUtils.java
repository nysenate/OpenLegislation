package gov.nysenate.openleg.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gov.nysenate.openleg.client.view.base.MapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

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
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private static ObjectMapper elasticsearchJsonMapper = jsonMapper.copy();
    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(MapView.class, new MapViewSerializer());
        // fixme: ES7 current 7.0.0 build should accept variable digit millis (.3 vs .300) but doesn't in arch pkg...
        // todo: Check if this fix comes through in a later build and remove the strict serializer if so.
        module.addSerializer(LocalDateTime.class, new StrictLocalDateTimeSerializer());
        elasticsearchJsonMapper.registerModule(module);
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
        return mapToJson(object, jsonMapper);
    }

    /**
     * Given an object, this method will attempt to serialize it into JSON
     * suitable for ElasticSearch indexing.
     * @param object Object
     * @return String - Json or empty string if failed.
     */
    public static String toElasticsearchJson(Object object){
        return mapToJson(object, elasticsearchJsonMapper);
    }

    private static String mapToJson(Object object, ObjectMapper objectMapper){
        try {
            return objectMapper.writeValueAsString(object);
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
