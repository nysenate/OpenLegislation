package gov.nysenate.openleg.common.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
            .disable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new SimpleModule().addKeySerializer(String.class, new ElasticsearchStringSerializer()));
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

    private static class ElasticsearchStringSerializer extends StdKeySerializers.StringKeySerializer {
        @Override
        public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
            var strValue = ((String) value);
            // Elasticsearch does not support empty Strings as keys.
            g.writeFieldName(strValue.isEmpty() ? "BLANK" : strValue);
        }
    }
}
