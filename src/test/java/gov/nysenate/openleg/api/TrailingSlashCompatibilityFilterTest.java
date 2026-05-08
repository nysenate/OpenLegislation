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
public class TrailingSlashCompatibilityFilterTest {

    private final TrailingSlashCompatibilityFilter filter = new TrailingSlashCompatibilityFilter();

    @Test
    public void forwardsDuplicateTrailingSlashToCanonicalPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws//");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals("/api/3/laws", response.getForwardedUrl());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void preservesQueryStringWhenForwarding() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws//");
        request.setQueryString("limit=10&offset=20");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals("/api/3/laws?limit=10&offset=20", response.getForwardedUrl());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void allowsSingleTrailingSlashToContinueThroughExistingSpringConfig() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/3/laws/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(response.getForwardedUrl());
        verify(chain).doFilter(request, response);
    }
}
