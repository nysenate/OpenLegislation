package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteCommittee;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class AgendaVoteView implements ViewObject
{
    private ListView<AgendaAttendanceView> attendanceList;
    private ListView<AgendaVoteBillView> votesList;

    public AgendaVoteView(AgendaVoteCommittee voteComm) {
        if (voteComm != null) {
            this.attendanceList = ListView.of(voteComm.getAttendance().stream()
                .map(a -> new AgendaAttendanceView(a)).collect(toList()));
            this.votesList = ListView.of(voteComm.getVotedBills().values().stream()
                .map(v -> new AgendaVoteBillView(v)).collect(Collectors.toList()));
        }
    }

    //Added for Json Deserialization
    protected AgendaVoteView() {}

    public ListView<AgendaAttendanceView> getAttendanceList() {
        return attendanceList;
    }

    public ListView<AgendaVoteBillView> getVotesList() {
        return votesList;
    }

    @Override
    public String getViewType() {
        return "agenda-vote";
    }
}
