package gov.nysenate.openleg.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Temporarily normalizes duplicate trailing slashes for legacy API clients after the Spring 6 upgrade.
 */
@Component("trailingSlashCompatibilityFilter")
public class TrailingSlashCompatibilityFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(TrailingSlashCompatibilityFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.length() > 1 && path.endsWith("//")) {
            String normalizedPath = path.replaceFirst("/+$", "");
            String forwardPath = appendQueryString(normalizedPath, request.getQueryString());
            request.getRequestDispatcher(forwardPath).forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static String appendQueryString(String path, String queryString) {
        return queryString == null ? path : path + "?" + queryString;
    }
}
