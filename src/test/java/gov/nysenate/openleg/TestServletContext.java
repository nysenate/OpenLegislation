package gov.nysenate.openleg;

import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
class TestServletContext extends MockServletContext {}