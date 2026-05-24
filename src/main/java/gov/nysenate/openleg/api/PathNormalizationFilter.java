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
        String pathWithinApplication = getPathWithinApplication(request);

        if (hasDuplicateSlashes(pathWithinApplication)) {
            redirectToNormalizedPath(request, response, pathWithinApplication);
            return;
        }

        if (hasTrailingSlash(pathWithinApplication)) {
            forwardToTrimmedPath(request, response, pathWithinApplication);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static void redirectToNormalizedPath(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 String pathWithinApplication) {
        String normalizedPath = request.getContextPath() + collapseDuplicateAndTrailingSlashes(pathWithinApplication);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", buildRedirectLocation(request, normalizedPath));
    }

    private static void forwardToTrimmedPath(HttpServletRequest request,
                                             HttpServletResponse response,
                                             String requestPath) throws ServletException, IOException {
        String trimmedPath = removeTrailingSlash(requestPath);
        request.getRequestDispatcher(trimmedPath)
                .forward(request, response);
    }

    private static String buildRedirectLocation(HttpServletRequest request, String normalizedPath) {
        String redirectUri = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(normalizedPath)
                .build(false)
                .toUriString();
        return appendRawQueryString(redirectUri, request.getQueryString());
    }

    private static String getPathWithinApplication(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private static boolean hasDuplicateSlashes(String path) {
        return path.contains("//");
    }

    private static boolean hasTrailingSlash(String path) {
        // The root context path "/" is valid as-is and must not be trimmed.
        return path.length() > 1 && path.endsWith("/");
    }

    private static String appendRawQueryString(String path, String queryString) {
        // Preserve the incoming queryString exactly.
        return queryString == null ? path : path + "?" + queryString;
    }

    private static String collapseDuplicateAndTrailingSlashes(String path) {
        String normalized = MULTI_SLASH.matcher(path).replaceAll("/");
        if (hasTrailingSlash(normalized)) {
            normalized = removeTrailingSlash(normalized);
        }
        return normalized;
    }

    private static String removeTrailingSlash(String path) {
        return path.substring(0, path.length() - 1);
    }
}
