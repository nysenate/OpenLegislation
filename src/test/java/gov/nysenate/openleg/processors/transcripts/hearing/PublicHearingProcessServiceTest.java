package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.processors.AbstractProcessServiceTest;
import gov.nysenate.openleg.processors.ProcessService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static gov.nysenate.openleg.legislation.committee.Chamber.ASSEMBLY;
import static gov.nysenate.openleg.legislation.committee.Chamber.SENATE;
import static gov.nysenate.openleg.legislation.transcripts.hearing.HearingHostType.COMMITTEE;
import static org.junit.Assert.assertEquals;

@IntegrationTest
public class PublicHearingProcessServiceTest extends AbstractProcessServiceTest {

    @Autowired
    private PublicHearingProcessService processService;
    @Autowired
    private PublicHearingDataService dataService;

    @Override
    protected ProcessService getProcessService() {
        return processService;
    }

    @Override
    protected String getName() {
        return "hearing_transcripts";
    }

    @Override
    protected boolean isTestFile(File file) {
        return file.getName().contains(" Test.");
    }

    @Test
    public void basicTest() {
        String filename = "1-30-20 Human Services Test.txt";
        processFiles(filename);
        var actualHearing = dataService.getPublicHearing(filename);

        var title = "JOINT LEGISLATIVE HEARING In the Matter of the 2020-2021 EXECUTIVE BUDGET ON HUMAN SERVICES";
        var address = """
                Hearing Room B
                Legislative Office Building
                Albany, New York""";
        var expectedHearing = new PublicHearing(filename, actualHearing.getText(), title, address,
                LocalDate.of(2020, 1, 30), LocalTime.of(9, 34), LocalTime.of(16, 34));
        expectedHearing.setId(actualHearing.getId());
        expectedHearing.setHosts(Set.of(new HearingHost(SENATE, COMMITTEE, "FINANCE"),
                new HearingHost(ASSEMBLY, COMMITTEE, "WAYS AND MEANS")));
        assertEquals(expectedHearing, actualHearing);
    }
}
