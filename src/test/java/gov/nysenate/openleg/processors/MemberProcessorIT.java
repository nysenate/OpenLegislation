package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import java.io.IOException;

@Category(SillyTest.class)
public class MemberProcessorIT extends BaseTests {
    @Autowired
    private MemberProcessor memberProcessor;
    @Autowired
    private MemberService memberService;

    @Test
    public void testProcessMember() throws IOException, SAXException {
        // Insert test file path as needed. Then, you can use memberService to retrieve and test the data.
        // I recommend creating test files in "src/test/resources" under a new "members" folder.
        memberProcessor.process(null);
    }
}
