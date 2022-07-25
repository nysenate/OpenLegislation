package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

// TODO: Make into unit test. Stop extending.
@Category(SillyTest.class)
public class HearingPdfParserTest extends BaseTests {
    @Autowired
    private HearingDataService hearings;
}
