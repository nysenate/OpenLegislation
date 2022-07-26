package gov.nysenate.openleg.api;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("xFrameFilter")
public class XFrameFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest,
                                    HttpServletResponse httpResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        httpResponse.setHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
        filterChain.doFilter(httpRequest, httpResponse);
    }
}
