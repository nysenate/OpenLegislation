package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

/**
 * Under /api/3, answers with error responses the errors Spring MVC raises when no endpoint can take the
 * request: no endpoint matches the path (404), none supports the method (405), or none reads the request
 * body's Content-Type (415). Running ahead of Spring's resolvers, it also answers the 415 raised while
 * reading the body of an endpoint that declares no {@code consumes}. Other paths keep Spring's handling.
 */
public class ApiErrorResolver implements HandlerExceptionResolver, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(ApiErrorResolver.class);

    private final HttpMessageConverter<Object> jsonConverter;

    public ApiErrorResolver(HttpMessageConverter<Object> jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        ErrorCode errorCode = errorCode(ex);
        if (errorCode == null || !BaseCtrl.isApiPath(request)) {
            return null;
        }
        var springError = (org.springframework.web.ErrorResponse) ex;
        var output = new ServletServerHttpResponse(response);
        output.setStatusCode(springError.getStatusCode());
        output.getHeaders().putAll(springError.getHeaders());
        try {
            jsonConverter.write(new ErrorResponse(errorCode), MediaType.APPLICATION_JSON, output);
        } catch (IOException writeEx) {
            logger.debug("Couldn't write the error response for {}", ex.toString(), writeEx);
        }
        return new ModelAndView();
    }

    private static ErrorCode errorCode(Exception ex) {
        return switch (ex) {
            case NoHandlerFoundException ignored -> ErrorCode.NO_SUCH_ENDPOINT;
            case HttpRequestMethodNotSupportedException ignored -> ErrorCode.METHOD_NOT_ALLOWED;
            case HttpMediaTypeNotSupportedException ignored -> ErrorCode.UNSUPPORTED_MEDIA_TYPE;
            default -> null;
        };
    }

    /** Ahead of Spring's resolvers, which would send the error status for an HTML page. */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
