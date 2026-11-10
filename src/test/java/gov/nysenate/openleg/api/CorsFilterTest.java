package gov.nysenate.openleg.api;

import gov.nysenate.openleg.config.annotation.UnitTest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class CorsFilterTest {

    @Test
    public void exposesRetryAfterToScriptsEvenOnARejection() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain rejectingChain = (request, res) -> {
            ((HttpServletResponse) res).setStatus(429);
            ((HttpServletResponse) res).setHeader("Retry-After", "1");
        };

        new CorsFilter().doFilter(new MockHttpServletRequest("GET", "/api/3/bills"), response, rejectingChain);

        assertEquals("*", response.getHeader("Access-Control-Allow-Origin"));
        assertEquals("Retry-After", response.getHeader("Access-Control-Expose-Headers"));
    }
}
