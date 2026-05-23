package gov.nysenate.openleg.api;

import gov.nysenate.openleg.config.annotation.UnitTest;
import jakarta.servlet.FilterChain;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Category(UnitTest.class)
public class PathNormalizationFilterTest {

    private static final int MOVED_PERMANENTLY = 301;

    private final PathNormalizationFilter filter = new PathNormalizationFilter();

    // --- 301 redirect: multiple consecutive slashes ---

    @Test
    public void redirectsDoubleTrailingSlash() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws//");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws", response.getHeader("Location"));
    }

    @Test
    public void redirectsTripleTrailingSlash() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws///");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws", response.getHeader("Location"));
    }

    @Test
    public void redirectsDoubleSlashInMiddleOfPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api//3/laws");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws", response.getHeader("Location"));
    }

    @Test
    public void redirectsDoubleSlashAtStartOfPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "//api/3/laws");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws", response.getHeader("Location"));
    }

    @Test
    public void redirectsDoubleSlashesInMultiplePlaces() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api//3//laws//");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws", response.getHeader("Location"));
    }

    @Test
    public void preservesQueryStringOnRedirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws//");
        request.setQueryString("limit=10&offset=20");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws?limit=10&offset=20", response.getHeader("Location"));
    }

    @Test
    public void preservesRawEncodedQueryStringOnRedirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws//");
        request.setQueryString("term=a+b&redirect=%2Fapi%2F3%2Flaws%2F&empty=");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws?term=a+b&redirect=%2Fapi%2F3%2Flaws%2F&empty=",
                response.getHeader("Location"));
    }

    @Test
    public void preservesDoubleSlashInsideQueryParameterOnRedirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws//");
        request.setQueryString("redirect=https://example.com/foo//bar&limit=10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws?redirect=https://example.com/foo//bar&limit=10",
                response.getHeader("Location"));
    }

    @Test
    public void includesNonDefaultPortInRedirectUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api//3/laws");
        request.setServerPort(8080);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost:8080/api/3/laws", response.getHeader("Location"));
    }

    @Test
    public void omitsDefaultHttpPortFromRedirectUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api//3/laws");
        request.setScheme("http");
        request.setServerPort(80);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("http://localhost/api/3/laws", response.getHeader("Location"));
    }

    @Test
    public void omitsDefaultHttpsPortFromRedirectUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api//3/laws");
        request.setScheme("https");
        request.setServerPort(443);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(MOVED_PERMANENTLY, response.getStatus());
        assertEquals("https://localhost/api/3/laws", response.getHeader("Location"));
    }

    @Test
    public void redirectDoesNotPassThroughFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api//3/laws");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
    }

    // --- Internal forward: single trailing slash, no duplicates ---

    @Test
    public void forwardsSingleTrailingSlash() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals("/api/3/laws", response.getForwardedUrl());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void forwardsWithoutAppendingQueryString() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws/");
        request.setQueryString("limit=10&offset=20");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals("/api/3/laws", response.getForwardedUrl());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void preservesOriginalRequestParametersOnForward() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws/");
        request.setQueryString("term=a+b&redirect=%2Fapi%2F3%2Flaws%2F&empty=");
        request.addParameter("term", "a b");
        request.addParameter("redirect", "/api/3/laws/");
        request.addParameter("empty", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals("/api/3/laws", response.getForwardedUrl());
        assertEquals(1, request.getParameterValues("term").length);
        assertEquals("a b", request.getParameter("term"));
        assertEquals(1, request.getParameterValues("redirect").length);
        assertEquals("/api/3/laws/", request.getParameter("redirect"));
        assertEquals(1, request.getParameterValues("empty").length);
        assertEquals("", request.getParameter("empty"));
        verify(chain, never()).doFilter(request, response);
    }

    // --- Pass-through: clean paths ---

    @Test
    public void doesNotModifyCleanPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(response.getForwardedUrl());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void doesNotModifyRootPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(response.getForwardedUrl());
        verify(chain).doFilter(request, response);
    }
}
