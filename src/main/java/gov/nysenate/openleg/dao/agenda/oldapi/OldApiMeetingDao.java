package gov.nysenate.openleg.dao.agenda.oldapi;

import gov.nysenate.openleg.client.view.oldapi.OldMeetingView;
import gov.nysenate.openleg.dao.base.OldApiBaseDao;
import gov.nysenate.openleg.dao.base.OldApiDocumentNotFoundEx;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaInfoAddendum;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.model.entity.CommitteeId;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Repository
public class OldApiMeetingDao extends OldApiBaseDao {

    public static final String docType = "meeting";

    /**
     * Attempts to get a committee meeting from the 1.9.2 api
     * @param committeeId - The Id of the committee that meets
     * @param meetingDate - The date of the committee meeting
     * @return OldMeetingView
     * @throws OldApiDocumentNotFoundEx if the meeting could not be retrieved
     */
    public OldMeetingView getMeeting(CommitteeId committeeId, LocalDate meetingDate) throws OldApiDocumentNotFoundEx {
        String oid = committeeId.getName().replaceAll("[ ,]+", "-") + "-" +
                meetingDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        return getDocument(docType, oid, OldMeetingView.class);
    }

}
