package gov.nysenate.openleg.service.spotcheck.senatesite.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.processor.base.ParseError;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

/**
 * Created by PKS on 2/25/16.
 */
public class JsonParser {

    @Autowired
    ObjectMapper objectMapper;

    protected <T> Optional<T> deserializeValue(JsonNode jsonNode, String fieldName, TypeReference<T> resultType) {
        String jsonValue = getValue(jsonNode, fieldName);
        return Optional.ofNullable(jsonValue).map(json -> {
            try {
                return objectMapper.readValue(json, resultType);
            } catch (IOException ex) {
                throw new ParseError("Error while attempting to map json. " +
                        "target_class: " + resultType.getType() + " field: " + fieldName + " value: " + json,
                        ex);
            }
        });
    }

    protected String getValue(JsonNode parentNode, String fieldName) {
        JsonNode undNode = parentNode.path(fieldName)
                .path("und");
        if (!undNode.isArray() || !undNode.elements().hasNext()) {
            return null;
        }
        JsonNode valueNode = undNode.elements().next()
                .path("value");
        if (valueNode.isTextual() || valueNode.isNull()) {
            return valueNode.textValue();
        }
        return null;
    }

    protected int getIntValue(JsonNode parentNode, String fieldName) {
        String rawValue = getValue(parentNode, fieldName);
        if (rawValue == null) {
            return 0;
        }
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException ex) {
            throw new ParseError("could not parse int value. field: " + fieldName + " value: " + rawValue, ex);
        }
    }

    protected List<String> getStringListValue(JsonNode parentNode, String fieldName){
        return getListValue(parentNode, fieldName, JsonNode::asText);
    }

    protected List<Integer> getIntListValue(JsonNode parentNode, String fieldName){
        return getListValue(parentNode, fieldName, JsonNode::asInt);
    }

    protected <T> List<T> getListValue(JsonNode parentNode, String fieldName,
                                       Function<JsonNode, T> valMapper) {
        JsonNode undNode = parentNode.path(fieldName).path("und");
        if(!undNode.isArray() || !undNode.elements().hasNext()){
            return new ArrayList<>();
        }

        List<T> valueList = new ArrayList<>();
        for (JsonNode node : undNode)
        {
            JsonNode valueNode = node.path("value");
            if (!valueNode.isNull()) {
                valueList.add(valMapper.apply(valueNode));
            }
        }
        return valueList;
    }


    protected boolean getBooleanValue(JsonNode parentNode, String fieldName) {
        String rawValue = getValue(parentNode, fieldName);
        if (rawValue == null) {
            return false;
        }
        if ("1".equals(rawValue) ^ "0".equals(rawValue)) {
            return "1".equals(getValue(parentNode, fieldName));
        }
        throw new ParseError("unexpected value for boolean. field: " + fieldName + " value: " + rawValue);
    }

    protected LocalDateTime getDateTimeValue(JsonNode parentNode, String fieldName) {
        String rawValue = getValue(parentNode, fieldName);
        if (rawValue == null) {
            return null;
        }
        try {
            long msvalue = Long.parseLong(rawValue);
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(msvalue), ZoneId.of("America/New_York"));
        } catch (DateTimeException | NumberFormatException ex) {
            throw new ParseError("cannot convert value into LocalDateTime. field: " + fieldName + " value: " + rawValue, ex);
        }
    }
}
