package gov.nysenate.openleg.service.spotcheck.senatesite.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.processor.base.ParseError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by PKS on 2/25/16.
 */
public abstract class SenateSiteJsonParser {

    private static final String LANGUAGE_NONE = "und";

    @Autowired protected ObjectMapper objectMapper;

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

    protected String getValue(JsonNode parentNode, String fieldName, String valueFieldName) {
        JsonNode undNode = parentNode.path(fieldName)
                .path(LANGUAGE_NONE);
        if (!undNode.isArray() || !undNode.elements().hasNext()) {
            return null;
        }
        JsonNode valueNode = undNode.elements().next()
                .path(valueFieldName);
        if (valueNode.isTextual() || valueNode.isNull()) {
            return valueNode.textValue();
        }
        return null;
    }

    protected String getValue(JsonNode parentNode, String fieldName) {
        return getValue(parentNode, fieldName, "value");
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
        JsonNode undNode = parentNode.path(fieldName).path(LANGUAGE_NONE);
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

    protected LocalDateTime parseUnixTimeValue(JsonNode parentNode, String fieldName) {
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

    protected LocalDateTime parseTimeStamp(JsonNode parentNode, String fieldName) {
        final DateTimeFormatter timeStampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String rawValue = getValue(parentNode, fieldName);
        if (rawValue == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(rawValue, timeStampFormatter);
        } catch (DateTimeException ex) {
            throw new ParseError("cannot convert value into LocalDateTime. field: " + fieldName + " value: " + rawValue, ex);
        }
    }

    protected <E extends Enum<E>> E getEnumValue(JsonNode parentNode, String fieldName, Class<E> enumClass) {
        String stringValue = getValue(parentNode, fieldName);
        try {
            return E.valueOf(enumClass, StringUtils.upperCase(stringValue));
        } catch (NullPointerException | IllegalArgumentException ex) {
            return null;
        }
    }
}
