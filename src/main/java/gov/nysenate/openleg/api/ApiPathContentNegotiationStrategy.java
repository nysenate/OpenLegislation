package gov.nysenate.openleg.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;

import static gov.nysenate.openleg.api.BaseCtrl.isApiPath;

/**
 * Negotiates every /api/3 request as JSON. The .pdf endpoints set their PDF Content-Type
 * themselves, which Spring writes without negotiating. Other paths negotiate on the Accept header.
 */
public class ApiPathContentNegotiationStrategy implements ContentNegotiationStrategy {
    private static final List<MediaType> JSON = List.of(MediaType.APPLICATION_JSON);

    private final ContentNegotiationStrategy acceptHeader = new HeaderContentNegotiationStrategy();

    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request != null && isApiPath(request)) {
            return JSON;
        }
        return acceptHeader.resolveMediaTypes(webRequest);
    }
}
