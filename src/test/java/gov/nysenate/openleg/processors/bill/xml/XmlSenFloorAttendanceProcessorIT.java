package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.attendance.SqlSenateVoteAttendanceDao;
import gov.nysenate.openleg.legislation.attendance.VoteId;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.legislation.bill.BillVoteType.FLOOR;
import static org.junit.Assert.assertEquals;

@IntegrationTest
public class XmlSenFloorAttendanceProcessorIT extends BaseXmlProcessorTest {
    private static final String billDir = "processor/bill/", atdDir = billDir + "senFlAtd/";

    @Autowired
    private SqlSenateVoteAttendanceDao attendanceDao;

    @Test
    public void baseTest() {
        String attendance = "2023-05-09-12.05.44.214714_SENFLATD_00000.xml";
        final var voteId = new VoteId(LocalDate.of(2023, 5, 8), 2, FLOOR);
        final var expectedMembers = Set.of("RIVERA", "MAY", "OBERACKER", "GONZALEZ");
        processXmlFile(atdDir + attendance);
        var remoteMembers = attendanceDao.getAttendance(voteId).getRemoteMembers()
                .stream().map(SessionMember::getLbdcShortName).collect(Collectors.toSet());
        assertEquals(expectedMembers, remoteMembers);
    }
}
