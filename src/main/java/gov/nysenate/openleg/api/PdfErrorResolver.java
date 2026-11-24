package gov.nysenate.openleg.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

/**
 * Makes the error responses of handlers that produce a PDF JSON, whatever media types the request accepts.
 * Their exception handlers return the JSON error envelope, but Spring writes an exception handler's body
 * only in a media type the request accepts. A request with {@code Accept: application/pdf} would otherwise
 * leave the exception unresolved and get Tomcat's HTML 500 page. Registered ahead of Spring's resolvers,
 * this only sets the Content-Type and leaves the handling to them.
 */
public class PdfErrorResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        if (handler instanceof HandlerMethod method && producesPdf(method)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }
        return null;
    }

    private static boolean producesPdf(HandlerMethod method) {
        RequestMapping mapping = method.getMethodAnnotation(RequestMapping.class);
        return mapping != null && Arrays.asList(mapping.produces()).contains(MediaType.APPLICATION_PDF_VALUE);
    }
}
