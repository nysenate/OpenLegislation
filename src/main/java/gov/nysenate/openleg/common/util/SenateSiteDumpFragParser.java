package gov.nysenate.openleg.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import gov.nysenate.openleg.spotchecks.model.SpotCheckContentType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckDataSource;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpFragment;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckDataSource.NYSENATE;

/**
 * Parses a {@link SenateSiteDumpFragment} from a json String and {@link SpotCheckRefType}.
 * The json string has the following mandatory strings: from, to, part, totalParts, session.
 */
@Service
public class SenateSiteDumpFragParser {

    @Autowired private ObjectMapper objectMapper;

    /**
     * <p>Parse a json string into a {@link SenateSiteDumpFragment}
     * <p>Throws <code>SenateSiteDumpFragParserException</code> if a required json value is missing.</p>
     * @param json The json string to parse.
     * @return {@link SenateSiteDumpFragment}
     * @throws IOException If there is an issue parsing the json
     * @see SenateSiteDumpId
     */
    public SenateSiteDumpFragment parseFragment(String json) throws IOException {
        JsonNode rootNode = objectMapper.readValue(json, JsonNode.class);

        int part = getRequiredNode(rootNode, "part").asInt();
        int totalParts = getRequiredNode(rootNode, "totalParts").asInt();

        LocalDateTime refDatetime = parseDateTimeFromNode(getRequiredNode(rootNode, "refDateTime"));

        int year = getRequiredNode(rootNode, "year").intValue();

        String contentType = getRequiredNode(rootNode, "contentType").textValue();
        SpotCheckRefType refType = getRefType(contentType);

        SenateSiteDumpId dumpId = createDumpId(refDatetime, totalParts, year, refType);
        return new SenateSiteDumpFragment(dumpId, part);
    }

    /**
     * Get a node from a rootNode and field name.
     * If the node does not exist, throw exception.
     */
    private JsonNode getRequiredNode(JsonNode rootNode, String fieldName) {
        if (rootNode.has(fieldName)) {
            return rootNode.get(fieldName);
        }
        else {
            throw SenateSiteDumpFragParserException.missingField(fieldName);
        }
    }

    /** Return a LocalDateTime parsed from a JsonNode if the node contains text. Otherwise return null. */
    private LocalDateTime parseDateTimeFromNode(JsonNode node) {
        if (node.asText().equals("")) {
            return null;
        }
        return LocalDateTime.parse(node.asText(), DateUtils.PUBLIC_WEBSITE_DUMP_DATETIME_FORMAT);
    }

    /**
     * Returns a {@link SenateSiteDumpId} based on passed in data
     */
    private SenateSiteDumpId createDumpId(LocalDateTime refDateTime, int totalParts,
                                          int year, SpotCheckRefType refType) {
        return new SenateSiteDumpId(refType, totalParts, year, refDateTime);
    }

    /**
     * A map of {@link SpotCheckContentType}s to their corresponding
     * {@link SpotCheckDataSource#NYSENATE} {@link SpotCheckRefType}
     *
     * TODO
     *  - The current NYSenate.gov dump script uses the {@link SpotCheckContentType#BILL} content type
     *  - when it should use {@link SpotCheckContentType#BILL_AMENDMENT}.
     *  - I cannot make more changes to that script at the moment, so for now I am explicitly mapping
     *  - {@link SpotCheckContentType#BILL} to {@link SpotCheckRefType#SENATE_SITE_BILLS}
     */
    private static final ImmutableMap<SpotCheckContentType, SpotCheckRefType> refTypeMap =
            ImmutableMap.<SpotCheckContentType, SpotCheckRefType>builder()
                    .putAll(Maps.uniqueIndex(
                            EnumSet.allOf(SpotCheckRefType.class).stream()
                                    .filter(refType -> NYSENATE.equals(refType.getDataSource()))
                                    .toList(),
                            SpotCheckRefType::getContentType
                    ))
                    .put(SpotCheckContentType.BILL, SpotCheckRefType.SENATE_SITE_BILLS)
                    .build();

    /**
     * Return the relevant ref type for the passed in content type
     * @param contentTypeString String
     * @return {@link SpotCheckRefType}
     */
    private SpotCheckRefType getRefType(String contentTypeString) {
        SpotCheckContentType contentType = SpotCheckContentType.valueOf(StringUtils.upperCase(contentTypeString));
        return Optional.ofNullable(refTypeMap.get(contentType))
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid nysenate.gov content type: " + contentType));
    }

    /** Exceptions */

    public static final class SenateSiteDumpFragParserException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 5133300734005459879L;

        private SenateSiteDumpFragParserException(String message) {
            super(message);
        }

        public static SenateSiteDumpFragParserException missingField(String missingField) {
            return new SenateSiteDumpFragParserException(
                    "Required field: " + missingField + ", is missing from json dump.");
        }

        public static SenateSiteDumpFragParserException nullField(String field) {
            return new SenateSiteDumpFragParserException(
                    "Required field: " + field + " is null");
        }
    }
}
