package gov.nysenate.openleg.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Normalizes slash sequences in incoming request URIs.
 *
 * Multiple consecutive slashes anywhere in the path → 301 permanent redirect to the
 * collapsed URL (duplicate slashes removed, any trailing slash also stripped).
 *
 * A single trailing slash with no duplicates → internal forward to the path without it,
 * replicating the transparent trailing-slash matching that Spring 6 removed.
 */
@Component("pathNormalizationFilter")
public class PathNormalizationFilter extends OncePerRequestFilter {

    private static final Pattern MULTI_SLASH = Pattern.compile("/{2,}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (requestUri.contains("//")) {
            String normalizedPath = normalizePath(requestUri);
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", buildRedirectLocation(request, normalizedPath));
            return;
        }

        if (requestUri.length() > 1 && requestUri.endsWith("/")) {
            String normalizedPath = requestUri.substring(0, requestUri.length() - 1);
            request.getRequestDispatcher(appendRawQueryString(normalizedPath, request.getQueryString()))
                    .forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static String buildRedirectLocation(HttpServletRequest request, String normalizedPath) {
        String redirectUri = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(normalizedPath)
                .build(false)
                .toUriString();
        return appendRawQueryString(redirectUri, request.getQueryString());
    }

    private static String appendRawQueryString(String path, String queryString) {
        return queryString == null ? path : path + "?" + queryString;
    }

    private static String normalizePath(String path) {
        String normalized = MULTI_SLASH.matcher(path).replaceAll("/");
        int normalizedLength = normalized.length();
        if (normalizedLength > 1 && normalized.charAt(normalizedLength - 1) == '/') {
            normalized = normalized.substring(0, normalizedLength - 1);
        }
        return normalized;
    }
}
