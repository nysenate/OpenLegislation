package gov.nysenate.openleg.spotchecks.openleg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains utility methods for querying an OpenLeg json API.
 */
@Service
public class JsonOpenlegDaoUtils {

    private final OpenLegEnvironment env;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegDaoUtils.class);

    @Autowired
    public JsonOpenlegDaoUtils(OpenLegEnvironment env, ObjectMapper objectMapper) {
        this.env = env;
        this.objectMapper = objectMapper;
    }

    /**
     * Query a paginated OpenLeg API and parse the results.
     *
     * The queried api must return a {@link ListViewResponse<T>}.
     *
     * @param viewClass {@link Class<T>} - view class for the objects being queried, used to deserialize json.
     * @param uriString String - URI for the queried api.  Doesn't need host, or key/limit/offset params.
     * @param limitOffset {@link LimitOffset} - limit the results
     * @param <T> {@link ViewObject} object that will be deserialized from the response
     * @return {@link PaginatedList<T>} paginated list of deserialized {@link ViewObject}s
     */
    public <T extends ViewObject> PaginatedList<T> queryForViewObjects(Class<T> viewClass, String uriString, LimitOffset limitOffset) {
        try {
            // Query for the json response
            URL url = buildUrlWithLimOff(uriString, limitOffset).build().toUri().toURL();
            JsonNode responseNode = objectMapper.readTree(url);
            JsonNode items = responseNode.path("result").path("items");
            if (!items.isArray()) {
                throw new OpenlegJsonRetrievalEx("Error parsing json for " + url + " : 'result'->'items' is not an array");
            }
            //
            List<T> viewList = new ArrayList<>(limitOffset.getLimit());
            for (Iterator<JsonNode> it = items.elements(); it.hasNext(); ) {
                JsonNode itemNode = it.next();
                T viewObj = objectMapper.treeToValue(itemNode, viewClass);
                viewList.add(viewObj);
            }
            JsonNode totalNode = responseNode.path("total");
            if (!totalNode.isInt()) {
                throw new OpenlegJsonRetrievalEx("Error parsing json for " + url + " : 'total' is not an int");
            }
            int total = totalNode.intValue();
            return new PaginatedList<>(total, limitOffset, viewList);
        } catch (IOException e) {
            throw new OpenlegJsonRetrievalEx("Error attempting to read JSON from openleg ref instance", e);
        }
    }

    /**
     * Query an openleg api for a single {@link ViewObject}
     *
     * @param viewClass {@link Class<T>} - view class for the object being queried, used to deserialize json.
     * @param uriString String - URI for the queried api.  Doesn't need host or key.
     * @param <T> {@link ViewObject} object that will be deserialized from the response
     * @return {@link T} deserialized {@link ViewObject}
     */
    public <T extends ViewObject> T queryForViewObject(Class<T> viewClass, String uriString) {
        try {
            URL url = buildUrl(uriString).build().toUri().toURL();
            JsonNode responseNode = objectMapper.readTree(url);
            JsonNode objectNode = responseNode.path("result");
            if (!objectNode.isObject()) {
                throw new OpenlegJsonRetrievalEx("Error parsing json for " + url + " : 'result' is not an object");
            }
            return objectMapper.treeToValue(objectNode, viewClass);
        } catch (IOException e) {
            throw new OpenlegJsonRetrievalEx("Error attempting to read JSON from openleg ref instance", e);
        }
    }

    /**
     * Generate a URL from the given uri string and {@link LimitOffset}.
     *
     * The host and key are added from current {@link OpenLegEnvironment} variables.
     * The limit/offset params are added from the passed in {@link LimitOffset}
     *
     * @param uriString String - uri
     * @param limitOffset {@link LimitOffset} - used to set limit/offset params
     * @return URL
     * @throws MalformedURLException if something is off
     */
    private UriComponentsBuilder buildUrlWithLimOff(String uriString, LimitOffset limitOffset) throws MalformedURLException {
        return buildUrl(uriString)
                .queryParam("limit", limitOffset.getLimit())
                .queryParam("offset", limitOffset.getOffsetStart());
    }

    /**
     * Generate a uri builder for the given uri and add the configured openleg url and key.
     *
     * @param uriString String - uir
     * @return UriComponentsBuilder
     */
    private UriComponentsBuilder buildUrl(String uriString) {
        return UriComponentsBuilder.fromHttpUrl(env.getOpenlegRefUrl() + uriString)
                .queryParam("key", env.getOpenlegRefApiKey());
    }
}
