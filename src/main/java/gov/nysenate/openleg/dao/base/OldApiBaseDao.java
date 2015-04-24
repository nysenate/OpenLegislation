package gov.nysenate.openleg.dao.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.config.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public abstract class OldApiBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(OldApiBaseDao.class);

    protected static final ObjectMapper mapper = new ObjectMapper();

    protected static final String getJsonDocumentTemplate = "/2.0/${doc_type}/${oid}.json";

    @Autowired
    Environment environment;

    /**
     * Attempts to retrieve a json document from the 1.9.2 api and map it to an instance of the specified class
     * @param docType String - The doctype of the requested document
     * @param oid String - The object id of the requested document
     * @param documentClass Class<T> - The class object for the return type
     * @param <T> A type that will contain the response data
     * @return T
     * @throws OldApiDocumentNotFoundEx if the response cannot be retrieved or does not contain mappable data
     */
    protected <T> T getDocument(String docType, String oid, Class<T> documentClass) throws OldApiDocumentNotFoundEx {
        String url = environment.getOldProdUrl() + StrSubstitutor.replace(getJsonDocumentTemplate,
                ImmutableMap.of("doc_type", docType, "oid", oid));
        try {
            String json = IOUtils.toString(new URL(url), "ISO-8859-15");
            Map<String, Object> resultMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            return extractDocument(resultMap, docType, documentClass);
        } catch (IOException | IllegalArgumentException ex) {
            throw new OldApiDocumentNotFoundEx(docType, oid, ex);
        }
    }

    private <T> T extractDocument(Map<String, Object> resultMap, String docType, Class<T> documentClass) {
        Object response = resultMap.get("response");
        if (response instanceof Map && ((Map) response).containsKey("results")) {
            Object results = ((Map) response).get("results");
            if (results instanceof List && !((List) results).isEmpty()) {
                Object result = ((List) results).get(0);
                if (result instanceof Map && ((Map) result).containsKey("data")) {
                    Object data = ((Map) result).get("data");
                    if (data instanceof Map && ((Map) data).containsKey(docType)) {
                        Object rawDoc = ((Map) data).get(docType);
                        return mapper.convertValue(rawDoc, documentClass);
                    }
                }
            }
        }
        throw new IllegalArgumentException("Could not locate document json in response");
    }
}
